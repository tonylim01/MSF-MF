package x3.player.mcu;

import com.uangel.svc.util.TimedHashMap;
import gov.nist.javax.sip.stack.SIPTransactionStack;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import x3.player.mcu.mru.Client;

import javax.sip.*;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SipSignal implements SipListener, SessionLifeCycleListener {
    final static Logger log = LoggerFactory.getLogger(SipSignal.class);

    SipProvider sipProvider;
    ListeningPoint udpListeningPoint;
    private String host;
    private int port=5070;
    String transport = "udp";
    Properties properties = new Properties();

    SessionFactory fact;
    private Client client;

//    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//    JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000/*timeout*/);
    JedisPool pool;
    ThreadPoolExecutor executor;
    Map<String, Session> sessionMap = Collections.synchronizedMap(new TimedHashMap<>(60000*60*24));


    public SipSignal() {
    }

    public Map<String, Session> getSessionMap() {
        return sessionMap;
    }

    public void init() throws
            InvalidArgumentException,
            TransportNotSupportedException,
            PeerUnavailableException,
            ObjectInUseException,
            TooManyListenersException {
//        executor=new ThreadPoolExecutor(
//                10,
//                100,
//                1000L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(),
//                new BasicThreadFactory.Builder()
//                        .namingPattern("McuSipSignal-%d")
//                        .daemon(true)
//                        .priority(Thread.MAX_PRIORITY)
//                        .build()
//        );
//        executor.allowCoreThreadTimeOut(true);

        SipFactory sipFactory = null;
        SipStack sipStack;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        properties.setProperty("javax.sip.STACK_NAME", "mcu");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "LOG4J");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "mcu_debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "mcu_log.txt");
//        properties.setProperty("gov.nist.javax.sip.AUTOMATIC_DIALOG_SUPPORT", "OFF");
//        properties.setProperty("javax.sip.OUTBOUND_PROXY", "")

        sipStack = sipFactory.createSipStack(properties);

        try
        {
            udpListeningPoint = sipStack.createListeningPoint(host,//"192.168.2.97",//myAddress,
                                                              port,
                                                              transport);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(),e);
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e1)
            {
            }
            System.exit(1);
        }


        sipProvider = sipStack.createSipProvider(udpListeningPoint);
        sipProvider.addSipListener(this);

        fact = new SessionFactory();
        fact.setSipFactory(sipFactory);
        fact.setSipProvider(sipProvider);
        fact.setSessionLifeCycleListener(this);
        fact.setClient(client);
        log.info(SipSignal.class.getName()+" is initialized");
    }

    public Properties properties() {
        return properties;
    }

    public SipProvider getSipProvider() {
        return sipProvider;
    }

    public SessionFactory getSessionFactory() {
        return fact;
    }

    public void processRequest(RequestEvent e) {
        log.debug("processRequest");
        Request req = e.getRequest();
        String method = req.getMethod();
        URI requestURI = req.getRequestURI();//sip:abcd@127.0.0.1
        String ver = req.getSIPVersion();//SIP/2.0
        StringBuffer headers = new StringBuffer();
        for (ListIterator i = req.getHeaderNames(); i.hasNext(); )
        {
            String key = String.valueOf(i.next());
            Header h = req.getHeader(key);
            headers.append(h.toString());
        }
        String body = null;
        byte[] content = req.getRawContent();
        if (content != null)
        {
            body = new String(content);
        }

        String dir="";
        if (e.getDialog() != null)
        {

            String callId=e.getDialog().getCallId().getCallId();
            Session s=sessionMap.get(callId);
            dir="INBOUND";
            if (s != null)
            {
                dir=s.dir(callId);
            }
        }
        if (Request.OPTIONS.equals(method))
        {
            log.debug(Utils.toString(e.getRequest()) + "\n" + body);
        } else
        {
            log.info(dir+" "+method+" "+Utils.toString(e.getRequest()) + "\n" + body);
        }
        if (Request.INVITE.equals(method))
        {
            processInvite(e);
        } else if (Request.ACK.equals(method))
        {
            processAck(e);
        } else if (Request.CANCEL.equals(method))
        {
            processCancel(e);
        } else if (Request.BYE.equals(method))
        {
            processBye(e);
        } else if (Request.PRACK.equals(method))
        {
            processPrAck(e);
        } else if (Request.OPTIONS.equals(method))
        {
            returnOk(e);
        } else if (Request.UPDATE.equals(method))
        {
            returnOk(e);
        }
        /*
v=0
o=toto 542 479 IN IP4 192.168.2.97
s=Talk
c=IN IP4 192.168.2.97
t=0 0
m=audio 7078 RTP/AVP 124 111 110 0 8 101
a=rtpmap:124 opus/48000
a=fmtp:124 useinbandfec=1; usedtx=1
a=rtpmap:111 speex/16000
a=fmtp:111 vbr=on
a=rtpmap:110 speex/8000
a=fmtp:110 vbr=on
a=rtpmap:101 telephone-event/8000
a=fmtp:101 0-15
m=video 9078 RTP/AVP 103 99
a=rtpmap:103 VP8/90000
a=rtpmap:99 MP4V-ES/90000
a=fmtp:99 profile-level-id=3
         */
    }

    public void returnOk(final RequestEvent e) {
        Request req=e.getRequest();
        try
        {
            Response ok = fact.getMessageFactory().createResponse(Response.OK, req);
            ServerTransaction tr = e.getServerTransaction();
            if (tr == null)
            {
                tr = fact.getSipProvider().getNewServerTransaction(req);
            }
            tr.sendResponse(ok);
            String method = req.getMethod();
            if (!Request.OPTIONS.equals(method))
            {
                log.info(Utils.toString(ok));
            }

        } catch (ParseException e1)
        {
            log.error(e1.toString(), e1);
        } catch (TransactionAlreadyExistsException e1)
        {
            log.error(e1.toString(), e1);
        } catch (TransactionUnavailableException e1)
        {
            log.error(e1.toString(), e1);
        } catch (InvalidArgumentException e1)
        {
            log.error(e1.toString(), e1);
        } catch (SipException e1)
        {
            log.error(e1.toString(), e1);
        }

    }

    public void processInvite(final RequestEvent e) {
        log.debug("processInvite");
//        executor.execute(()->{
                Session s=fact.create(e);
        sessionMap.put(s.inbound.getCallId(),
                       s);
        sessionMap.put(s.outbound.getCallId(),
                       s);
//        });

    }

    public void processAck(RequestEvent e) {
        log.debug("processAck");
    }

    void processCancel(RequestEvent e) {
        log.debug("processCancel");

        ((Session) e.getServerTransaction().getDialog().getApplicationData()).cancel(e);

    }

    void processBye(final RequestEvent event) {
        log.debug("processBye");
        try
        {
            String callId=event.getServerTransaction().getDialog().getCallId().getCallId();
            Session s=sessionMap.get(callId);
//            executor.execute(()->{
//                Session s=(Session) event.getServerTransaction().getDialog().getApplicationData();
                if (s != null)
                        s.close(event);
//            });
            /*
 BYE sip:alice@10.1.3.33 SIP/2.0
 Via: SIP/2.0/TCP 192.168.10.20;branch=z9hG4bKnashds8
 Max-Forwards: 70
 To: Alice <sip:alice@atlanta.com>;tag=1928301774
 From: Bob <sip:bob@biloxi.com>;tag=a6c85cf
 Call-ID: a84b4c76e66710@pc33.atlanta.com
 CSeq: 231 BYE
 Content-Length: 0
             */
        } catch (NullPointerException e)
        {
            log.error(e.toString(), e);
            throw e;
        }

    }


    void processPrAck(final RequestEvent event) {
        log.debug("processPrAck");
        try
        {
//            executor.execute(()->{
            ((Session) event.getServerTransaction().getDialog().getApplicationData()).processPrAck(event);
        } catch (NullPointerException e)
        {
            log.error("processPrAck", e);
            throw e;
        }

    }

    public void processResponse(ResponseEvent event) {
        Response res = event.getResponse();
        String ver = res.getSIPVersion();//SIP/2.0
        int status = res.getStatusCode();
        String reason = res.getReasonPhrase();
        StringBuffer headers = new StringBuffer();
        for (ListIterator i = res.getHeaderNames(); i.hasNext(); )
        {
            String key = String.valueOf(i.next());
            Header h = res.getHeader(key);
            headers.append(h.toString());
        }
        String body = null;
        byte[] content = res.getRawContent();
        if (content != null)
        {
            body = new String(content);
        }

        String callId=event.getDialog().getCallId().getCallId();
        Session s=sessionMap.get(callId);
        String dir="";
        if (s != null)
        {
            dir=s.dir(callId);
        }
        log.info("processResponse "+dir+" "+res.getReasonPhrase()+" "+res.getStatusCode()+"\n" + ver + " " + status + " " + reason + "\n" + headers + "\n" + body);
        //100 Trying    state = null
        //180 Ringing   state = Early Dialog
        //200 Ok        state = Confirmed Dialog

        if (s == null)
        {
            log.error("SESSION NOT FOUND");
            return;
        }

        CSeqHeader cseq = (CSeqHeader) res.getHeader(CSeqHeader.NAME);

        if (Request.INVITE.equals(cseq.getMethod()))
        {


            if (300 <= res.getStatusCode() && res.getStatusCode() <= 699)
            {
//                log.info("RECEIVE OUTBOUND INVITE DECLINED "+res.getStatusCode());
//                try
//                {
//                    Session s= (Session) event.getClientTransaction().getDialog().getApplicationData();
//                    if (s != null)
//                    {
                        s.inviteDeclined(event);
//                    }
//                } catch (NullPointerException e)
//                {
//                    log.error(e.toString(), e);
//                }

            } else if (Response.OK == res.getStatusCode())
            {
//                String callId=event.getDialog().getCallId().getCallId();
//                Session s=sessionMap.get(callId);
//                if (s != null)
//                {
//                log.info("RECEIVE OUTBOUND INVITE 200 OK");
                s.inviteAccepted(event);
//                }

            } else if (res.getStatusCode() == Response.RINGING/*180*/ || res.getStatusCode() == Response.SESSION_PROGRESS/*183*/)
            {
//                log.info("RECEIVE OUTBOUND RINGING 180");
//                String callId=event.getDialog().getCallId().getCallId();
//                Session s=sessionMap.get(callId);
//                if (s != null)
//                {
                s.ringing(event);
//                }

            }
        }


    }

    public void processTimeout(TimeoutEvent timeoutEvent) {
        log.debug("processTimeout");
    }

    public void processIOException(IOExceptionEvent ioExceptionEvent) {
        log.debug("processIOException");
    }

    public void processTransactionTerminated(TransactionTerminatedEvent event) {
    }

    public void processDialogTerminated(DialogTerminatedEvent event) {
    }


    public void sessionDeclined(Session s) {
        log.info("sessionDeclined");
    }

    public void sessionCancelled(Session s) {
        log.debug("sessionCancelled");
    }

    public void sessionCreated(final Session s) {
        log.info("sessionCreated inbound callId="+s.getInboundTr().getDialog().getCallId().getCallId()+"----outbound callId="+s.getOutboundTr().getDialog().getCallId().getCallId());
        sessionMap.put(s.inbound.getCallId(),
                       s);
//        Jedis jedis = pool.getResource();
//        try
//        {
//            Map<String, String> m = new HashMap<String, String>();
//            m.put("inbound", s.getInboundTr().getDialog().getCallId().getCallId());
//            m.put("outbound", s.getOutboundTr().getDialog().getCallId().getCallId());
//
//            jedis.hmset("session:" + m.get("inbound"),
//                        m);
//            jedis.hmset("session:" + m.get("outbound"),
//                        m);
//        } finally
//        {
//            jedis.close();
//        }
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;)
                {
                    //log.info("SLEEP 15s");
                    try
                    {
                        Thread.sleep(15*1000);
                    } catch (InterruptedException e)
                    {
                        log.error("SEND OUTBOUND UPDATE fail", e);
                    }

                    if (sessionMap.containsKey(s.outbound.getCallId())) {
                        Dialog d = /*outBoundTr*//*outbound*/s.clientTransaction.getDialog();

                        try
                        {
                            Request u = d.createRequest(Request.UPDATE);
                            Header h;
                            h = fact.getHeaderFactory().createHeader("Session-Expires", "180;refresher=uac");
                            u.addHeader(h);
                            h = fact.getHeaderFactory().createHeader("Supported", "timer");
                            u.addHeader(h);
                            h = fact.getHeaderFactory().createHeader("Supported", "100rel");
                            u.addHeader(h);

                            Header h1 = fact.getHeaderFactory().createHeader("To", " <tel:"+s.getCallee()+";phone-context=sktims.net>;tag="+s.getToTag());
                            u.removeHeader("To");
                            u.addHeader(h1);

                            ClientTransaction ct = fact.getSipProvider().getNewClientTransaction(u);
                            d.sendRequest(ct);
                            log.info("SEND OUTBOUND UPDATE caller="+s.getCaller()+", callee="+s.getCallee()+" "+Utils.toString(u));
                        } catch (SipException e)
                        {
                            log.error("SEND OUTBOUND UPDATE fail", e);
                        } catch (ParseException e)
                        {
                            log.error("SEND OUTBOUND UPDATE fail", e);
                        }
                    } else
                    {
                        log.info("OUTBOUND UPDATE stop. caller="+s.getCaller()+", callee="+s.getCallee());
                        break;
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void sessionCannotCreate(Session s, Exception e) {
        log.error("sessionCannotCreate", e);
    }

    public void sessionClosed(Session s) {
        log.info("sessionClosed inbound="+s.getInboundTr().getDialog().getCallId().getCallId()+"--|-- outbound"+s.getOutboundTr().getDialog().getCallId().getCallId());
        sessionMap.remove(s.inbound.getCallId());
        sessionMap.remove(s.outbound.getCallId());
//        Jedis jedis = pool.getResource();
//        try
//        {
//            jedis.del("session:" + s.getInboundTr().getDialog().getCallId().getCallId());
//            jedis.del("session:" + s.getOutboundTr().getDialog().getCallId().getCallId());
//        } finally
//        {
//            jedis.close();
//        }
    }

    public void recover() {
        //todo:
        //redis
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

//    public static void main(String[] args) throws Exception {
//        McuSipSignal sip = new McuSipSignal();
//        sip.init();
//
//
////        sip.invite(
////                "ACS",//fromName
////                "192.168.56.1",//fromSipAddress
////                "ACService",//fromDisplayName
////                "whaworld",//toUser
////                "192.168.56.101",//toSipAddress
////                "whaworld",//toDisplayName
//////                     "192.168.56.101:15060;transport=tcp",//peerHostPort
//////                     "192.168.56.101:5060",//peerHostPort
////                "192.168.56.101:15060",//peerHostPort
////                "192.168.56.1",
////                "111222".getBytes()
////                //req.getRawContent()
////        );
//    }
}
