package x3.player.mcu;

import EDU.oswego.cs.dl.util.concurrent.FutureResult;
import EDU.oswego.cs.dl.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mcu.mru.MruClient;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hwaseob on 2018-02-26.
 */
public class McuSession {
    final static Logger log = LoggerFactory.getLogger(McuSession.class);

    Request inviteRequest;
    ServerTransaction inbound;//inboundTr, inviteTr
    ClientTransaction outbound;
    McuSessionFactory fact;

    final static String transport = "udp";
//    static SipFactory sipFactory = null;
//
//    static MessageFactory messageFactory;
//
//    static HeaderFactory headerFactory;
//
//    static AddressFactory addressFactory;
////    static ListeningPoint udpListeningPoint = 5070;
////    String transport = "udp";
//    static SipProvider sipProvider;

//    public MrfcSession(Request inviteRequest,
//                       ServerTransaction inbound,
//                       ClientTransaction outbound) {
//        this.inviteRequest = inviteRequest;
//        this.inbound = inbound;
//        this.outbound = outbound;
    protected McuSession(McuSessionFactory fact) {
        this.fact = fact;
//        if (sipFactory == null)
//            try
//            {
//                sipFactory = SipFactory.getInstance();
//                headerFactory = sipFactory.createHeaderFactory();
//                addressFactory = sipFactory.createAddressFactory();
//                messageFactory = sipFactory.createMessageFactory();
//            } catch (PeerUnavailableException e)
//            {
//                e.printStackTrace();
//            }
    }

//    private static SipFactory getSipFactory() {
//        if (sipFactory == null)
//        {
//            sipFactory = SipFactory.getInstance();
//        }
//        return sipFactory;
//    }

    //create
    //cancel
    //bye
    //sessionDeclined
    //sessionCancelled
    //sessionCreated
    //sessionClosed

                    //create//processInbound
    protected void processInvite(//MrfcSessionFactory fact,
                                     RequestEvent e) {
//        SipProvider sipProvider;
//        sipProvider = (SipProvider) e.getSource();
        Request req = e.getRequest();
//        MrfcSession session = new MrfcSession();
        try
        {
//            System.out.println("mrfc: got an Invite sending Trying");
            Response res = fact.getMessageFactory().createResponse(Response.TRYING, req);
            ServerTransaction tr = e.getServerTransaction();
            if (tr == null)
            {
                tr = fact.getSipProvider().getNewServerTransaction(req);
//                log.info("processInvite getBranchId = "+tr.getBranchId());
                inbound = tr;
                inviteRequest = req;
//                tr.setApplicationData(this);
                tr.getDialog().setApplicationData(this);
            }
//            this.dialog = tr.getDialog();
//            this.inviteTid = tr;
//            this.inviteRequest = req;
//            System.out.println("mrfc: getDialog");
            tr.sendResponse(res);
//            Request req = e.getRequest();
//            String method = req.getMethod();
//            FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
//            ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
//            log.info(fr.getAddress() + "<--" + res.getStatusCode() + "<--" + to.getAddress());
            log.info(Utils.toString(res));

            String sdp=new String(req.getRawContent());
            String caller=((SipURI)((FromHeader)req.getHeader("From")).getAddress().getURI()).getUser();
            String callee=((SipURI)((ToHeader)req.getHeader("To")).getAddress().getURI()).getUser();
            MruClient mru=fact.getClient();
            FutureResult f;
            try
            {
                f = mru.offer("INBOUND",
                              tr.getDialog().getCallId().getCallId(),
                          caller,
                          callee,
                          sdp);
            } catch (NullPointerException e1)
            {
                throw e1;
            }
            res = fact.getMessageFactory().createResponse(Response.RINGING, req);
//            System.out.println("mrfc: createResponse");
            tr.sendResponse(res);
            log.info(Utils.toString(res));

            f.timedGet(1000L);
//            ToHeader toHeader=(ToHeader)req.getHeader(ToHeader.NAME);//To: <sip:whaworld@localhost>
//            SipURI sipURI = (SipURI)toHeader.getAddress().getURI();//sip:whaworld@localhost
//            String user=sipURI.getUser(); //whaworld  //SipURI
//            invite("ACS",//fromName
//                   "192.168.2.107",//fromSipAddress
//                   "ACService",//fromDisplayName
//                   req.getRequestURI(),
//                   toHeader,
//                   req.getRawContent());

//            this.ok = fact.getMessageFactory().createResponse(Response.OK, req);
////            Address address = addressFactory.createAddress("mrfc <sip:"+ "192.168.56.1" + ":" + udpListeningPoint.getPort() + ">");
//            Address address = fact.getAddressFactory().createAddress("mrfc <sip:" + "192.168.2.97" + ":" + fact.getSipProvider().getListeningPoint(transport).getPort() + ">");
//            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(address);
//            ok.addHeader(contactHeader);
//            ToHeader toHeader = (ToHeader) ok.getHeader(ToHeader.NAME);
//            toHeader.setTag("4321");
//            callerContentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");


            ClientTransaction outbound=invite(
                    "ACS",//fromName
//                     "192.168.56.1",//fromSipAddress
                    "192.168.2.97",//fromSipAddress
                    "ACService",//fromDisplayName
                    "whaworld",//toUser
                    "172.0.0.3",//toSipAddress
                    "whaworld",//toDisplayName
//                     "192.168.56.101:15060;transport=tcp",//peerHostPort
//                     "192.168.56.101:5060",//peerHostPort
                    "172.0.0.3:5060",//peerHostPort
                    "192.168.2.97",
                    req.getRawContent()
            );
//            System.out.println();
//            return new MrfcSession(req,
//                                   tr,
//                                   outbound);
        } catch (Exception ex)
        {
            ex.printStackTrace();
//            throw ex;
            //System.exit(0);
//            return null;
        }

    }

    ClientTransaction invite(//outbound
            String fromName,
            String fromSipAddress,
            String fromDisplayName,
//                URI requestURI,
//                ToHeader toHeader,
            String toUser,
            String toSipAddress,
            String toDisplayName,
            String peerHostPort,
            String host,
            byte[] content
    ) {

        // Create the request.
        Request req = null;
        try
        {
            // create Request URI
            SipURI requestURI = fact.getAddressFactory().createSipURI(toUser, peerHostPort);
            SipURI fromAddress = fact.getAddressFactory().createSipURI(fromName, fromSipAddress);
            Address fromNameAddress = fact.getAddressFactory().createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
            FromHeader fromHeader = fact.getHeaderFactory().createFromHeader(fromNameAddress, "12345");

            // create To Header
            SipURI toAddress = fact.getAddressFactory().createSipURI(toUser, toSipAddress);
            Address toNameAddress = fact.getAddressFactory().createAddress(toAddress);
            toNameAddress.setDisplayName(toDisplayName);
            ToHeader toHeader = fact.getHeaderFactory().createToHeader(toNameAddress, null/*tag*/);

            CallIdHeader callId = fact.getSipProvider().getNewCallId();

            CSeqHeader cSeq= fact.getHeaderFactory().createCSeqHeader(1L, Request.INVITE);
            // Create ViaHeaders
            ArrayList viaHeaders = new ArrayList();
            String ipAddress = fact.getSipProvider().getListeningPoint(transport).getIPAddress();
            ViaHeader viaHeader = fact.getHeaderFactory().createViaHeader(ipAddress,
                                                                          fact.getSipProvider().getListeningPoint(transport).getPort(),
                                                                          transport,
                                                                          null/*branch*/);
            viaHeaders.add(viaHeader);
            // Create a new MaxForwardsHeader
            MaxForwardsHeader maxForwards = fact.getHeaderFactory().createMaxForwardsHeader(70);

            req = fact.getMessageFactory().createRequest(requestURI,
                                                         Request.INVITE,
                                                         callId,
                                                         cSeq,
                                                         fromHeader,
                                                         toHeader,
                                                         viaHeaders,
                                                         maxForwards);

//        String host = "192.168.56.1";

            SipURI contactUrl = fact.getAddressFactory().createSipURI(fromName, host);
            contactUrl.setPort(fact.getSipProvider().getListeningPoint(transport).getPort());
            contactUrl.setLrParam();

            // Create the contact name address.
            SipURI contactURI = fact.getAddressFactory().createSipURI(fromName, host);
            contactURI.setPort(fact.getSipProvider().getListeningPoint(transport)
                                       .getPort());

            Address contactAddress = fact.getAddressFactory().createAddress(contactURI);

            // Add the contact address.
            contactAddress.setDisplayName(fromName);

            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(contactAddress);
            req.addHeader(contactHeader);

            // Create ContentTypeHeader
//            ContentTypeHeader contentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
//            req.setContent(content, contentTypeHeader);
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }


        ClientTransaction tr = null;
        try
        {
            tr = fact.getSipProvider().getNewClientTransaction(req);
            this.outbound = tr;
//            tr.setApplicationData(this);
            tr.getDialog().setApplicationData(this);
//            log.info("************ state = "+tr.getDialog().getState());
//            tr.getState();
//            inviteTid = tr;
        } catch (TransactionUnavailableException e)
        {
            e.printStackTrace();
            return null;
        }

        MruClient mru=fact.getClient();
        String caller="";
        String callee="";
        FutureResult f= null;
        String sdp=new String(content);//sdp for INBOUND
        try
        {
            f = mru.offer("OUTBOUND",
                          tr.getDialog().getCallId().getCallId(),
                                     caller,
                                     callee);
            f.timedGet(1000L);
            f=mru.answer("OUTBOUND",
                         tr.getDialog().getCallId().getCallId());
            Map<String,Object> res = (Map<String,Object>)f.timedGet(1000L);
            //if (res != null)
            Map<String, String> body = (Map<String, String>) res.get("body");
            if (body != null)
            {
                sdp = body.get("sdp");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        ContentTypeHeader contentTypeHeader = null;
        try
        {
            contentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
            req.setContent(sdp.getBytes(), contentTypeHeader);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

//        System.out.println("inviteTid = " + cliInviteTid);
        // send the request out.
        try
        {
            tr.sendRequest();
            log.info(Utils.toString(req));
//            outBoundDialog = tr.getDialog();
//            outBoundTr = tr;
//            log.info("************** state = "+tr.getDialog().getState());
//            return tr;
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        return tr;
    }

    public ServerTransaction getInboundTr() {
        return inbound;
    }

    public ClientTransaction getOutboundTr() {
        return outbound;
    }

    public void inviteDeclined(ResponseEvent event) {
        //MCU<-- 300~699 <--outbound

        //MCU--> ACK -->outbound
        try
        {
            Response res = event.getResponse();
            Dialog d=/*outBoundTr*/outbound.getDialog();
            Request ack = d.createAck(((CSeqHeader) res.getHeader(CSeqHeader.NAME)).getSeqNumber());
            d.sendAck(ack);
            log.info(Utils.toString(ack));
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
        }
//                System.out.println("Sending ACK");

        //inbound<-- 300~699 <--MCU
        try
        {
            Response res = event.getResponse();
            Response nok = fact.getMessageFactory().createResponse(/*Response.DECLINE*/res.getStatusCode(), inviteRequest);
//            Address address = fact.getAddressFactory().createAddress("mrfc <sip:" + "192.168.2.97" + ":" + fact.getSipProvider().getListeningPoint(transport).getPort() + ">");
//            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(address);
//            nok.addHeader(contactHeader);

//            ContentTypeHeader callerContentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
//            nok.setContent(res.getRawContent(), callerContentTypeHeader);
//                    ok.addHeader(headerFactory.createContentTypeHeader("application", "sdp"));
            inbound/*inviteTid*/.sendResponse(nok);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        fact.getSessionLifeCycleListener().sessionDeclined(this);
    }

    public void inviteAccepted(ResponseEvent event) {
        Response res = event.getResponse();
        Dialog d=/*outBoundTr*/outbound.getDialog();
        byte[] sdp=res.getRawContent();//sdp for OUTBOUND
        //MCU-->CSCF:outbound SIP:ACK
        try
        {
            Request ack = d.createAck(((CSeqHeader) res.getHeader(CSeqHeader.NAME)).getSeqNumber());
            d.sendAck(ack);
            log.info(Utils.toString(ack));
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
        }
        //outbound: MCU-->MRU Negodone_req
        MruClient mru=fact.getClient();
        try
        {
            FutureResult f=mru.negoDone("OUTBOUND",
                                        d.getCallId().getCallId(),
                                        new String(sdp));
            f.timedGet(1000L);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        //CSCF<--MCU:inbound SIP:200/OK
        //MCU-->MRU inbound answer
        try
        {
            FutureResult g = mru.answer("INBOUND",
                         inbound.getDialog().getCallId().getCallId());

            Map<String,Object> h = (Map<String,Object>)g.timedGet(1000L);
            Map<String, String> body = (Map<String, String>) h.get("body");
            if (body != null)
            {
            sdp = body.get("sdp").getBytes();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
//            f.timedGet(1000L);
        try
        {
            Response ok = fact.getMessageFactory().createResponse(Response.OK, inviteRequest);
//            Address address = addressFactory.createAddress("mrfc <sip:"+ "192.168.56.1" + ":" + udpListeningPoint.getPort() + ">");
            Address address = fact.getAddressFactory().createAddress("mrfc <sip:" + "192.168.2.97" + ":" + fact.getSipProvider().getListeningPoint(transport).getPort() + ">");
            ContactHeader contactHeader = fact.getHeaderFactory().createContactHeader(address);
            ok.addHeader(contactHeader);

            ContentTypeHeader callerContentTypeHeader = fact.getHeaderFactory().createContentTypeHeader("application", "sdp");
            ok.setContent(/*res.getRawContent()*/sdp, callerContentTypeHeader);
//                    ok.addHeader(headerFactory.createContentTypeHeader("application", "sdp"));
            inbound/*inviteTid*/.sendResponse(ok);

            //inbound: MCU-->MRU negoDone
            try
            {
                FutureResult f=mru.negoDone("INBOUND",
                                            inbound.getDialog().getCallId().getCallId());
                f.timedGet(1000L);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (TimeoutException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }

            fact.getSessionLifeCycleListener().sessionCreated(this);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
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
//            log.info("processCancel getBranchId = "+tr.getBranchId());
            tr.sendResponse(res);
            log.info(Utils.toString(res));

//            Response response = messageFactory.createResponse(Response.OK, request);
//            serverTransactionId.sendResponse(response);
            if (tr.getDialog().getState() != DialogState.CONFIRMED) {
                res = fact.getMessageFactory().createResponse(Response.REQUEST_TERMINATED/*487*/, inviteRequest);
                /*inviteTid*/inbound.sendResponse(res);
                log.info(Utils.toString(res));
            }
            //todo:

        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        //MCU-->CANCEL-->outbound
        try
        {
            Request cancel=/*outBoundTr*/null;
            cancel = outbound.createCancel();
            ClientTransaction ct = fact.getSipProvider().getNewClientTransaction(cancel);
            ct.sendRequest();
            log.info(Utils.toString(cancel));
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        fact.getSessionLifeCycleListener().sessionCancelled(this);
    }

    public void close(RequestEvent event) {//bye
        Request req = event.getRequest();
        String callId=event.getServerTransaction().getDialog().getCallId().getCallId(); //fromWhich
        boolean dir = (callId.equals(inbound.getDialog().getCallId().getCallId()));
        //dir == true, inbound
        //else outbound
        try
        {
            Response res = fact.getMessageFactory().createResponse(Response.OK, req);
            ServerTransaction tr = event.getServerTransaction();
            tr.sendResponse(res);
            log.info(Utils.toString(res));

            MruClient mru=fact.getClient();
            try
            {
                FutureResult f=mru.hangup(dir ? "INBOUND"
                                              : "OUTBOUND",
                                          tr.getDialog().getCallId().getCallId());
                f.timedGet(1000L);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (TimeoutException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        try
        {
//            String callId=event.getServerTransaction().getDialog().getCallId().getCallId(); //fromWhich
            Transaction tr;
//            if (callId.equals(inbound.getDialog().getCallId().getCallId()))
            if (dir)
            tr = outbound;
            else
            tr = inbound;

            Dialog d = /*outBoundTr*//*outbound*/tr.getDialog();
            Request byeRequest = d.createRequest(Request.BYE);
            ClientTransaction ct = fact.getSipProvider().getNewClientTransaction(byeRequest);
//            dialog = cliInviteTid.getDialog();
//            log.info("state = " + d.getState());
            d.sendRequest(ct);
            log.info(Utils.toString(byeRequest));
//            log.info("state = " + d.getState());

            MruClient mru=fact.getClient();
            try
            {
                FutureResult f=mru.hangup(dir ? "OUTBOUND"
                                              : "INBOUND",
                                          tr.getDialog().getCallId().getCallId());
                f.timedGet(1000L);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (TimeoutException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        } catch (SipException e)
        {
            e.printStackTrace();
        }

        fact.getSessionLifeCycleListener().sessionClosed(this);
    }

//    private Transaction fromWhich(String callId) {
//
//    }
}
