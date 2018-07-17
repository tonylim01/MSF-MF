package x3.player.mcu;

import EDU.oswego.cs.dl.util.concurrent.FutureResult;
import EDU.oswego.cs.dl.util.concurrent.TimeoutException;
import com.uangel.svc.util.LocalIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mcu.mru.Client;
import x3.player.mcu.mru.MediaSession;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.address.TelURL;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by hwaseob on 2018-02-26.
 */
public class Session implements Serializable {
    final static Logger log = LoggerFactory.getLogger(Session.class);

    String conferenceID;
    Request inviteRequest;
    //control plane
    ServerTransaction serverTransaction;
    ClientTransaction clientTransaction;
    SessionFactory fact;

    final static String transport = "udp";
    byte[] inbound_sdp;
    private String caller;
    private String callee;
    //user plane
    MediaSession inbound;//inbound_mru  userplain i o  is os  inbound_rtp
    MediaSession outbound;
    boolean closed = false;
    final int MAX_FORWARDS = 70;
    long sequenceNumber = 1;
    String toTag;

    protected Session(SessionFactory fact) {
        this.fact = fact;
        this.conferenceID = UUID.randomUUID().toString();

        gov.nist.javax.sip.Utils utils = new gov.nist.javax.sip.Utils();
        this.toTag = utils.generateTag();
    }

    public String getToTag() {
        return toTag;
    }

    public String dir(String callId) {
        if (callId.equals(inbound.getCallId()))
            return "INBOUND";
        else if (callId.equals(outbound.getCallId()))
            return "OUTBOUND";
        else
            return "?BOUND";
    }

    protected void processPrAck(final RequestEvent event) {
        try
        {
            Response ok = fact.getMessageFactory().createResponse(Response.OK, event.getRequest());
            log.info(Utils.toString(ok));
            serverTransaction.sendResponse(ok);
        } catch (ParseException | InvalidArgumentException | SipException e)
        {
            log.error("processPrAck", e);
        }
    }

    protected void processInvite(RequestEvent e) {
        log.info("RECEIVE INBOUND INVITE");
        Request req = e.getRequest();
        try
        {
            Response res = fact.getMessageFactory().createResponse(Response.TRYING, req);
            ServerTransaction tr = e.getServerTransaction();
            if (tr == null)
            {
                tr = fact.getSipProvider().getNewServerTransaction(req);
                serverTransaction = tr;
                inviteRequest = req;
                tr.getDialog().setApplicationData(this);
            }
            tr.sendResponse(res);
            log.info("INBOUND SEND TRYING 100 " +Utils.toString(res));

            String sdp = new String(req.getRawContent());
            //From: <sip:01020950212@sktims.net>;tag=5d8153a424385
//            caller = ((SipURI) ((FromHeader) req.getHeader("From")).getAddress().getURI()).getUser();
            SipURI from = (SipURI) ((FromHeader) req.getHeader("From")).getAddress().getURI();
//            log.info("getHost="+from.getHost());//sktims.net
//            log.info("getUser="+from.getUser());//01020950212
            caller = from.getUser();
            String fromTag=((FromHeader) req.getHeader("From")).getTag();


            //To: <tel:01020319012;phone-context=sktims.net>
//            callee = ((SipURI) ((ToHeader) req.getHeader("To")).getAddress().getURI()).getUser();
            TelURL to = (TelURL) ((ToHeader) req.getHeader("To")).getAddress().getURI();
//            log.info("getPhoneContext="+to.getPhoneContext());//sktims.net
//            log.info("getPhoneNumber="+to.getPhoneNumber());//01020319012
            callee = to.getPhoneNumber();

            ViaHeader viaHeader=(ViaHeader)req.getHeader("Via");
//            log.info("getHost="+viaHeader.getHost());
//            log.info("getPort="+viaHeader.getPort());
//            log.info("getRPort="+viaHeader.getRPort());
//            log.info("getBranch="+viaHeader.getBranch());

//            res = fact.getMessageFactory().createResponse(Response.RINGING/*180*/, req);
//            Header h1=fact.getHeaderFactory().createHeader("P-Asserted-Identity", "<sip:"+callee+"@sktims.net>");
//            res.addHeader(h1);
////            h1=fact.getHeaderFactory().createHeader("P-Charging-Vector", "");
//            h1=req.getHeader("P-Charging-Vector");
//            res.addHeader(h1);
////            h1=fact.getHeaderFactory().createHeader("P-Charging-Function-Addresses", "");
//            h1=req.getHeader("P-Charging-Function-Addresses");
//            res.addHeader(h1);
//            tr.sendResponse(res);
//            log.info(Utils.toString(res));

            inbound = new MediaSession("INBOUND",
                                       conferenceID,
                                /*callId*/tr.getDialog().getCallId().getCallId());
            inbound.setClient(fact.getClient());

            FutureResult f;
            try
            {
                f = inbound.offer(caller,
                                  callee,
                                  sdp);
            } catch (IOException e1)
            {
                inbound = null;
                log.error("inbound offer fail", e1);
                Response nok = fact.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, req);
                tr.sendResponse(nok);
                fact.getSessionLifeCycleListener().sessionCannotCreate(this, e1);
                return;
            }

            try
            {
                f.timedGet(1000L);
            } catch (InterruptedException | InvocationTargetException e1)
            {
                log.error("inbound offer fail", e1);
                Response nok = fact.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, req);
                tr.sendResponse(nok);
                onClosed();
                fact.getSessionLifeCycleListener().sessionCannotCreate(this, e1);
                return;
            }

            List<String> headers=new LinkedList<>();
            for (ListIterator i = req.getHeaderNames(); i.hasNext(); )
            {
                String key = String.valueOf(i.next());
                for (ListIterator li=req.getHeaders(key); li.hasNext();)
                {
                Header h=(Header)li.next();
                    if (key.equals("Call-ID"))
                    {
                        continue;
                    }
                    if (key.equals("Record-Route"))
                    {
                        continue;
                    }
                    if (key.equals("Via"))
                    {
                        continue;
                    }
                    if (key.equals("Contact"))
                    {
                        continue;
                    }
                    if (h.toString().contains("acstest"))
                    {
                        continue;
                    }
                    String kv = h.toString();
                    headers.add(kv);
                }
            }
            //MCU-->MRU inbound answer
            try
            {
                FutureResult g = inbound.answer();
                Map<String, Object> h = (Map<String, Object>) g.timedGet(1000L);
                Map<String, String> body = (Map<String, String>) h.get("body");
                if (body != null)
                {
                    inbound_sdp = body.get("sdp").getBytes();
                }

                //183
//                res = fact.getMessageFactory().createResponse(Response.RINGING/*180*/, req);
//                res = fact.getMessageFactory().createResponse(Response./*RINGING*/SESSION_PROGRESS/*183*/, req);
//                ContentTypeHeader callerContentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
//                res.setContent(inbound_sdp, callerContentTypeHeader);
//                Header p_skt=fact.getHeaderFactory().createHeader("P-SKT-Error-Info", "T_MEMORING");
//                res.addHeader(p_skt);

//                log.info("p_headers="+p_headers);
//                for (Map.Entry<String,String> e1: p_headers.entrySet())
//                {
//                    Header h1=fact.getHeaderFactory().createHeader(e1.getKey(), e1.getValue());
//                    res.addHeader(h1);
//                }
//                log.info(Utils.toString(res));
//                tr.sendResponse(res);
            } catch (IOException | InterruptedException | InvocationTargetException e1)
            {
                log.error("inbound answer fail", e1);
                Response nok = fact.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, req);
                tr.sendResponse(nok);
                onClosed();
                fact.getSessionLifeCycleListener().sessionCannotCreate(this, e1);
                return;
            }

//            /*ClientTransaction outbound = */invite(
//                "ACS",//fromName
//                "192.168.2.97",//fromSipAddress
//                "ACService",//fromDisplayName
//                "whaworld",//toUser
//                "172.0.0.2",//toSipAddress
//                "whaworld",//toDisplayName
//                "172.0.0.2:5060",//peerHostPort
//                "192.168.2.97",
//                req.getRawContent()
//        );
//            String toIP="192.168.2.97";
//            String fromIP="10.10.10.163";
//            /*ClientTransaction outbound = */invite(
//                "ACS",//fromName
//                fromIP,//fromSipAddress
//                "ACService",//fromDisplayName
//                "hwaseob",//toUser
//                toIP,//toSipAddress
//                "hwaseob",//toDisplayName
//                toIP+":5060",//peerHostPort
//                fromIP,
//                req.getRawContent()
//        );
//            String toIP="192.168.2.97";
//            String fromIP="10.10.10.163";
            String localIP = fact.getSipProvider().getListeningPoint(transport).getIPAddress();
//            invite(
//                    "ACS",//fromName
//                    localIP,//fromSipAddress
//                    "ACService",//fromDisplayName
//                    callee,//toUser
//                    callee,//toSipAddress
//                    null,//toDisplayName
//                    callee + ":5060",//peerHostPort
//                    localIP, //host    contactUrl=fromName + host
//                    req.getRawContent());
/*
INVITE sip:192.168.7.91@192.168.7.81:5070 SIP/2.0
Content-Length: 274^M
Via: SIP/2.0/UDP 192.168.5.22:5060;branch=z9hG4bK.SfwlRobpL;rport=5060;received=192.168.5.22^M
From: "alberto" <sip:alberthanbs@192.168.5.22>;tag=I~7RjEtrg^M
To: <sip:192.168.7.91@192.168.7.81>^M
CSeq: 20 INVITE^M
Call-ID: Kj5TL8P5KZ^M
Max-Forwards: 70^M
Supported: replaces^M
Allow: INVITE^M
Content-Type: application/sdp^M
Contact: "alberto" <sip:alberthanbs@192.168.5.22;transport=udp>;+sip.instance="<urn:uuid:a08ebc91-ee6f-4b0f-bef2-ad47e65fd6bb>"^M
User-Agent: Linphone Desktop/4.1.1 (belle-sip/1.6.3)^M

*/

            invite(
                    caller,//fromName
                    fact.getConfig().getProperty("CSCF_PHONE_CONTEXT"),//localIP,//fromSipAddress
                    null,//"ACService",//fromDisplayName
                    fromTag,
                    callee,//toUser
                    fact.getConfig().getProperty("CSCF_PHONE_CONTEXT"),//callee,//toSipAddress
                    null,//toDisplayName
                    //viaHeader.getHost()+":"+viaHeader.getPort(),
                    fact.getConfig().getProperty("CSCF_PHONE_CONTEXT"),//callee + ":5060",//peerHostPort
                    localIP, //host    contactUrl=fromName + host
                    req.getRawContent(),
                    headers);

        } catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }

    }

    /*ClientTransaction*/void invite(//outbound
                                     String fromName,
                                     String fromSipAddress,
                                     String fromDisplayName,
                                     String fromTag,
                                     String toUser,
                                     String toSipAddress,
                                     String toDisplayName,
                                     String peerHostPort,
                                     String host,
                                     byte[] content,
                                     List<String> headers
    ) {

        // Create the request.
        Request req = null;
        try
        {
            // create Request URI
//            SipURI requestURI = fact.getAddressFactory().createSipURI(toUser, peerHostPort);
            TelURL requestURI = fact.getAddressFactory().createTelURL(toUser);
            requestURI.setPhoneContext(peerHostPort/*"sktims.net"*/);
            SipURI fromAddress = fact.getAddressFactory().createSipURI(fromName, fromSipAddress);
            Address fromNameAddress = fact.getAddressFactory().createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
//            gov.nist.javax.sip.Utils utils = new gov.nist.javax.sip.Utils();
//            String fromTags = utils.generateTag();
            FromHeader fromHeader = fact.getHeaderFactory().createFromHeader(fromNameAddress, fromTag);

            // create To Header
//            SipURI toAddress = fact.getAddressFactory().createSipURI(toUser, toSipAddress);
            TelURL toAddress = fact.getAddressFactory().createTelURL(toUser);
            toAddress.setPhoneContext(toSipAddress/*"sktims.net"*/);
            Address toNameAddress = fact.getAddressFactory().createAddress(toAddress);
            if (toDisplayName != null)
            {
                toNameAddress.setDisplayName(toDisplayName);
            }
            ToHeader toHeader = fact.getHeaderFactory().createToHeader(toNameAddress, null/*tag*/);

            CallIdHeader callId = fact.getSipProvider().getNewCallId();

            CSeqHeader cSeq = fact.getHeaderFactory().createCSeqHeader(sequenceNumber++, Request.INVITE);
            // Create ViaHeaders
            ArrayList viaHeaders = new ArrayList();
            String ipAddress = fact.getSipProvider().getListeningPoint(transport).getIPAddress();
            ViaHeader viaHeader = fact.getHeaderFactory().createViaHeader(ipAddress,
                                                                          fact.getSipProvider().getListeningPoint(transport).getPort(),
                                                                          transport,
                                                                          null/*branch*/);
            viaHeaders.add(viaHeader);
            // Create a new MaxForwardsHeader
            MaxForwardsHeader maxForwards = fact.getHeaderFactory().createMaxForwardsHeader(MAX_FORWARDS);

            req = fact.getMessageFactory().createRequest(requestURI,
                                                         Request.INVITE,
                                                         callId,
                                                         cSeq,
                                                         fromHeader,
                                                         toHeader,
                                                         viaHeaders,
                                                         maxForwards);


            SipURI contactUrl = fact.getAddressFactory().createSipURI(fromName, host);
            contactUrl.setPort(fact.getSipProvider().getListeningPoint(transport).getPort());
            contactUrl.setLrParam();

            // Create the contact name address.
            SipURI contactURI = fact.getAddressFactory().createSipURI(fromName, host);
            contactURI.setPort(fact.getSipProvider().getListeningPoint(transport)
                                       .getPort());

            Address contactAddress = fact.getAddressFactory().createAddress(contactURI);

            // Add the contact address.
            //contactAddress.setDisplayName(fromName);

            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(contactAddress);
            req.addHeader(contactHeader);

            for (String kv: headers)
            {
                int colon=kv.indexOf(":");
                String key=kv.substring(0,colon);
                String val=kv.substring(colon + 1).trim();
                Header h=fact.getHeaderFactory().createHeader(key,val);
                req.addHeader(h);
            }

        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(), e);
        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        }


        ClientTransaction tr = null;
        try
        {
            //log.info(Utils.toString(req) + "\n");
            tr = fact.getSipProvider().getNewClientTransaction(req);
            this.clientTransaction = tr;
            tr.getDialog().setApplicationData(this);
        } catch (TransactionUnavailableException e)
        {
            log.error(e.toString(), e);
            return;
        }

        outbound = new MediaSession("OUTBOUND",
                                    conferenceID,
                             tr.getDialog().getCallId().getCallId());
        outbound.setClient(fact.getClient());
        String sdp/*inbound_offer_sdp*/ = new String(content);//sdp for INBOUND
        try
        {
            FutureResult f = outbound.offer(//"OUTBOUND",
                                            //tr.getDialog().getCallId().getCallId(),
                                            caller,
                                            callee,
                                            null);
            f.timedGet(1000L);
            f = outbound.answer(/*"OUTBOUND",
                           tr.getDialog().getCallId().getCallId()*/);
            Map<String, Object> res = (Map<String, Object>) f.timedGet(1000L);
            Map<String, String> body = (Map<String, String>) res.get("body");
            if (body != null)
            {
                sdp/*outbound_answer_sdp*/ = body.get("sdp");
            }
        } catch (IOException | InterruptedException | InvocationTargetException e)
        {
            log.error("outbound offer fail", e);
            try
            {
                Response nok = fact.getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, inviteRequest);
                serverTransaction.sendResponse(nok);
            } catch (ParseException e1)
            {
                log.error(e.toString(), e);
            } catch (SipException e1)
            {
                log.error(e.toString(), e);
            } catch (InvalidArgumentException e1)
            {
                log.error(e.toString(), e);
            }
            onClosed();
            fact.getSessionLifeCycleListener().sessionCannotCreate(this, e);
            return;
        }

        ContentTypeHeader contentTypeHeader = null;
        try
        {
            contentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
            req.setContent(sdp.getBytes(), contentTypeHeader);
        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        }

        // send the request out.
        try
        {
//            log.info("OUTBOUND INVITE");
            log.info("SEND OUTBOUND INVITE "+Utils.toString(req) + "\n" + sdp);
            tr.sendRequest();
            tr.getDialog().setApplicationData(this);
        } catch (SipException e)
        {
            log.error("outbound invite fail", e);
        }

    }

    public void ringing(ResponseEvent event) {
        try
        {
            Response res;
            res = fact.getMessageFactory().createResponse(Response.RINGING/*180*/, inviteRequest);
            Header h1= null;
            h1 = fact.getHeaderFactory().createHeader("P-Asserted-Identity", "<sip:"+callee+"@sktims.net>");
            res.addHeader(h1);
//            h1=fact.getHeaderFactory().createHeader("P-Charging-Vector", "");
            h1=event.getResponse().getHeader("P-Charging-Vector");
            res.addHeader(h1);
//            h1=fact.getHeaderFactory().createHeader("P-Charging-Function-Addresses", "");
            h1=event.getResponse().getHeader("P-Charging-Function-Addresses");
            res.addHeader(h1);
//            log.info("SEND INBOUND RINGING 180");
//            Header to=res.getHeader("To");
//            h1 = fact.getHeaderFactory().createHeader("To", to.);
//            res.addHeader(h1);

//            TelURL toAddress = fact.getAddressFactory().createTelURL(callee);
//            toAddress.setPhoneContext("sktims.net");
//            Address toNameAddress = fact.getAddressFactory().createAddress(toAddress);
//            log.info("toTag = "+toTag);
//            ToHeader toHeader = fact.getHeaderFactory().createToHeader(toNameAddress, toTag);
//            res.addHeader(toHeader);

            h1 = fact.getHeaderFactory().createHeader("To", " <tel:"+callee+";phone-context=sktims.net>;tag="+toTag);
            res.removeHeader("To");
            res.addHeader(h1);


//            log.info("SEND INBOUND RINGING 180 "+Utils.toString(res));
            serverTransaction.sendResponse(res);
            log.info("SEND INBOUND RINGING 180"+Utils.toString(res));
        } catch (ParseException e)
        {
            log.error("INBOUND RINGING 180 fail", e);
        } catch (InvalidArgumentException e)
        {
            log.error("INBOUND RINGING 180 fail", e);
        } catch (SipException e)
        {
            log.error("INBOUND RINGING 180 fail", e);
        }
    }

    public ServerTransaction getInboundTr() {
        return serverTransaction;
    }

    public ClientTransaction getOutboundTr() {
        return clientTransaction;
    }

    public void inviteDeclined(ResponseEvent event) {
        //MCU<-- 300~699 <--outbound


        //inbound<-- 300~699 <--MCU
        try
        {
            Response res = event.getResponse();
            Response nok = fact.getMessageFactory().createResponse(/*Response.DECLINE*/res.getStatusCode(), inviteRequest);
            log.info("RELAY INBOUND "+res.getStatusCode()+Utils.toString(res));
            serverTransaction/*inviteTid*/.sendResponse(nok);
        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(), e);
        } catch (SipException e)
        {
            log.error(e.toString(), e);
        }

        onClosed();
        fact.getSessionLifeCycleListener().sessionDeclined(this);
    }

    public void inviteAccepted(ResponseEvent event) {
        log.info("OUTBOUND 200 OK RECEIVED");
        Response res = event.getResponse();
        Dialog d =/*outBoundTr*/clientTransaction.getDialog();
        byte[] outbound_sdp = res.getRawContent();//sdp for OUTBOUND
        //MCU-->CSCF:outbound SIP:ACK
        try
        {
            Request ack = d.createAck(((CSeqHeader) res.getHeader(CSeqHeader.NAME)).getSeqNumber());
            Header h=fact.getHeaderFactory().createHeader("Session-Expires", "180;refresher=uac");
            ack.addHeader(h);
            log.info("SEND OUTBOUND ACK");
            d.sendAck(ack);
            log.info(Utils.toString(ack));
        } catch (InvalidArgumentException e)
        {
            log.error("200 OK fail", e);
        } catch (SipException e)
        {
            log.error("200 OK fail", e);
        } catch (ParseException e)
        {
            log.error("200 OK fail", e);
        }
        //outbound: MCU-->MRU Negodone_req
        try
        {
            FutureResult f = outbound.negoDone(//"OUTBOUND",
                                               //d.getCallId().getCallId(),
                                               new String(outbound_sdp));
            f.timedGet(1000L);
        } catch (IOException | InterruptedException | InvocationTargetException e)
        {
            log.error("outbound negoDone fail", e);
        }

        //CSCF<--MCU:inbound SIP:200/OK
        try
        {
            Response ok = fact.getMessageFactory().createResponse(Response.OK, inviteRequest);
            Address address = fact.getAddressFactory().createAddress("mrfc <sip:" + fact.getSipProvider().getListeningPoint(transport).getIPAddress() + ":" + fact.getSipProvider().getListeningPoint(transport).getPort() + ">");
            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(address);
            ok.addHeader(contactHeader);

            byte[] sdp = inbound_sdp == null ? outbound_sdp : inbound_sdp;
            ContentTypeHeader callerContentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
            ok.setContent(sdp/*res.getRawContent()*/, callerContentTypeHeader);
//            log.info("200 OK");
//            log.info("SEND INBOUND 200 OK");

//            TelURL toAddress = fact.getAddressFactory().createTelURL(callee);
//            toAddress.setPhoneContext("sktims.net");
//            Address toNameAddress = fact.getAddressFactory().createAddress(toAddress);
//            ToHeader toHeader = fact.getHeaderFactory().createToHeader(toNameAddress, toTag);
//            ok.addHeader(toHeader);

            Header h1 = fact.getHeaderFactory().createHeader("To", " <tel:"+callee+";phone-context=sktims.net>;tag="+toTag);
            ok.removeHeader("To");
            ok.addHeader(h1);

            log.info("SEND INBOUND 200 OK "+Utils.toString(ok) + "\n" + new String(sdp));
            serverTransaction/*inviteTid*/.sendResponse(ok);

            //inbound: MCU-->MRU negoDone
            try
            {
                FutureResult f = inbound.negoDone(/*"INBOUND",
                                              inbound.getDialog().getCallId().getCallId()*/
                                                  null);
                f.timedGet(1000L);
            } catch (IOException | InterruptedException | InvocationTargetException e)
            {
                log.error("inbound negoDone fail", e);
            }

            fact.getClient().serviceStart(inbound.getCallId(),
                                          caller/*"01012341234"*/);
            fact.getSessionLifeCycleListener().sessionCreated(this);
        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(), e);
        } catch (SipException e)
        {
            log.error(e.toString(), e);
        } catch (IOException e)
        {
            log.error(e.toString(), e);
        }
    }

    public void cancel(RequestEvent event) {
        //inbound-->cancel-->MCU

        //inbound<--OK<--MCU
        //inbound<-487 REQUEST_TERMINATED<--MCU
        try
        {
            Request req = event.getRequest();
            Response res = fact.getMessageFactory().createResponse(Response.OK, req);
            ServerTransaction tr = event.getServerTransaction();
            tr.sendResponse(res);
            log.info(Utils.toString(res));

            if (tr.getDialog().getState() != DialogState.CONFIRMED)
            {
                res = fact.getMessageFactory().createResponse(Response.REQUEST_TERMINATED/*487*/, inviteRequest);
                /*inviteTid*/
                serverTransaction.sendResponse(res);
                log.info(Utils.toString(res));
            }
            //todo:

        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(), e);
        } catch (SipException e)
        {
            log.error(e.toString(), e);
        }

        //MCU-->CANCEL-->outbound
        if (clientTransaction != null)
        {
            try
            {
                Request cancel =/*outBoundTr*/null;
                cancel = clientTransaction.createCancel();
                ClientTransaction ct = fact.getSipProvider().getNewClientTransaction(cancel);
                ct.sendRequest();
                log.info(Utils.toString(cancel));
            } catch (SipException e)
            {
                log.error(e.toString(), e);
            }
        }

        onClosed();
        fact.getSessionLifeCycleListener().sessionCancelled(this);
    }

    public void close(RequestEvent event) {//bye
        closed = true;
        Request req = event.getRequest();
        String callId = event.getServerTransaction().getDialog().getCallId().getCallId(); //fromWhich
        boolean dir = (callId.equals(serverTransaction.getDialog().getCallId().getCallId()));
        //dir == true, inbound
        //else outbound
        try
        {
            Response res = fact.getMessageFactory().createResponse(Response.OK, req);
            ServerTransaction tr = event.getServerTransaction();
            tr.sendResponse(res);
            log.info(Utils.toString(res));

        } catch (ParseException e)
        {
            log.error(e.toString(), e);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(), e);
        } catch (SipException e)
        {
            log.error(e.toString(), e);
        }

        try
        {
            Transaction tr;
            if (dir)
            {
                tr = clientTransaction;
            } else
            {
                tr = serverTransaction;
            }

            Dialog d = /*outBoundTr*//*outbound*/tr.getDialog();
            Request byeRequest = d.createRequest(Request.BYE);
            ClientTransaction ct = fact.getSipProvider().getNewClientTransaction(byeRequest);
            d.sendRequest(ct);
            log.info(Utils.toString(byeRequest));
        } catch (SipException e)
        {
            log.error(e.toString(), e);
        }

        onClosed();
        fact.getSessionLifeCycleListener().sessionClosed(this);
    }

    public boolean isClosed() {
        return closed;
    }

    protected void onClosed() {
        try
        {
            if (inbound != null)
            {
                inbound.hangup().timedGet(1000L);
            }
        } catch (IOException | InterruptedException | InvocationTargetException e)
        {
            log.error("inbound hangup fail", e);
        }

        try
        {
            if (outbound != null)
            {
                outbound.hangup().timedGet(1000L);
            }
        } catch (IOException | InterruptedException | InvocationTargetException e)
        {
            log.error("outbound hangup fail", e);
        }

        try
        {
            fact.getClient().serviceStop(inbound.getCallId(),
                                         caller/*"01012341234"*/);
        } catch (IOException e)
        {
            log.error(e.toString(), e);
        }
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public static void main(String[] args) throws Exception {
        SipSignal sip = new SipSignal();
        sip.setHost("localhost");
        sip.setPort(5060);
//        sip.setPool(pool);
        sip.setClient(new Client() {
            @Override
            public FutureResult offer(String dir, String conference_id, String callId, String caller, String callee, String sdp) throws IOException {
                FutureResult f=new FutureResult();
                f.set("");
                return f;
            }
        });
        sip.init();
        Properties config=new Properties();
        sip.getSessionFactory().setConfig(config);

        SessionFactory sf=sip.getSessionFactory();
        Session s=new Session(sf);



        String fromName="ACS";
        String fromSipAddress = "1.1.1.1";
        String fromDisplayName = null;
        String toUser = "";
        String toSipAddress = "1.1.1.1";
        String toDisplayName = null;
        String peerHostPort = "2.2.2.2:5060";
        String host="3.3.3.3";
        byte[] content = "abc".getBytes();
//        s.invite(
//                                     /*String*/ fromName,
//                                     /*String*/ fromSipAddress,
//                                     /*String*/ fromDisplayName,
//                                     /*String*/ toUser,
//                                     /*String*/ toSipAddress,
//                                     /*String*/ toDisplayName,
//                                     /*String*/ peerHostPort,
//                                     /*String*/ host,
//                                     /*byte[]*/ content
//        );

    }
}
