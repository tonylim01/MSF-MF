package x3.player.mcu;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import x3.player.mcu.mru.MruClient;

import javax.sip.*;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class McuSipSignal implements SipListener, SessionLifeCycleListener {
    final static Logger log = LoggerFactory.getLogger(McuSipSignal.class);

    //    MessageFactory mf;
//    MessageFactory messageFactory;
//
//    HeaderFactory headerFactory;
//
//    AddressFactory addressFactory;

    SipProvider sipProvider;
    ListeningPoint udpListeningPoint;
    //    String transport = "tcp";
    private int port=5070;
    String transport = "udp";
//    Dialog outBoundDialog;//outBoundDialog
//    ClientTransaction outBoundTr;
//    ServerTransaction inviteTid;//inBound inbound
//    private Request inviteRequest;
//    Response ok;

    McuSessionFactory fact;
    private MruClient client;

//    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//    JedisPool pool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000/*timeout*/);
    JedisPool pool;
    ThreadPoolExecutor executor;

    public void init() throws
            InvalidArgumentException,
            TransportNotSupportedException,
            PeerUnavailableException,
            ObjectInUseException,
            TooManyListenersException {
        executor=new ThreadPoolExecutor(
                10,
                100,
                1000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new BasicThreadFactory.Builder()
                        .namingPattern("McuSipSignal-%d")
                        .daemon(true)
                        .priority(Thread.MAX_PRIORITY)
                        .build()
        );
//        executor.allowCoreThreadTimeOut(true);

        SipFactory sipFactory = null;
        SipStack sipStack;
//        sipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "mcu");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "LOG4J");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "mcu_debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "mcu_log.txt");
//        properties.setProperty("gov.nist.javax.sip.AUTOMATIC_DIALOG_SUPPORT", "OFF");

//        try {
        sipStack = sipFactory.createSipStack(properties);
//            System.out.println("sipStack = " + sipStack);
//        } catch (PeerUnavailableException e) {
//            e.printStackTrace();
//            System.err.println(e.getMessage());
//            if (e.getCause() != null)
//                e.getCause().printStackTrace();
//            System.exit(0);
//        }

//        try {
//        headerFactory = sipFactory.createHeaderFactory();
//        addressFactory = sipFactory.createAddressFactory();
//        messageFactory = sipFactory.createMessageFactory();
//            headerFactory = sipFactory.createHeaderFactory();
//            addressFactory = sipFactory.createAddressFactory();
//            mf = sipFactory.createMessageFactory();
        /*ListeningPoint*/

        try
        {
            udpListeningPoint = sipStack.createListeningPoint("0.0.0.0",//myAddress,
                                                              port,
                                                              transport);
        } catch (InvalidArgumentException e)
        {
            log.error(e.toString(),e);
            System.exit(1);
        }

//            MrfcSipSignal listener = this;

        /*SipProvider*/
        sipProvider = sipStack.createSipProvider(udpListeningPoint);
//        System.out.println("udp provider " + sipProvider);
        sipProvider.addSipListener(this);

//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            ex.printStackTrace();
////            usage();
//        }
        fact = new McuSessionFactory();
        fact.setSipFactory(sipFactory);
        fact.setSipProvider(sipProvider);
        fact.setSessionLifeCycleListener(this);
        fact.setClient(client);
//        mru=new MruClient();
//        mru.setConnectionFactory();
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
//            System.out.println();
            headers.append(h.toString());
        }
        String body = null;
        byte[] content = req.getRawContent();
        if (content != null)
        {
            body = new String(content);
        }
        log.debug("processRequest\n" + method + " " + requestURI + " " + ver + "\n" + headers + "\n" + body);
        log.info(Utils.toString(e.getRequest()));
//        log.info("processRequest "+method);
//        e.getServerTransaction();//null
//        e.getSource();//SipProvider
//        log.info("method = " + method);//INVITE
//        req.getHeader("Via");//Via: SIP/2.0/UDP 127.0.0.1:5070;branch=z9hG4bK.oQ4cK~KsL;rport=59689;received=127.0.0.1
//        req.getContentEncoding();//null

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
        }
//        System.out.println();
//        req.getMethod()
//        new String(req.getRawContent());
//        try {
//            Response res = mf.createResponse(Response.RINGING,
//                                                              req);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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

    public void processInvite(final RequestEvent e) {
        log.debug("processInvite");
//        Request req = e.getRequest();
//        String method = req.getMethod();
//        FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
//        ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
//        log.info(fr.getAddress() + "-->" + method + "-->"+to.getAddress());
//        log.info(Utils.toString(e.getRequest()));
        executor.execute(()->{
                fact.create(e);
        });

    }

    public void processAck(RequestEvent e) {
        log.debug("processAck");
    }

    void processCancel(RequestEvent e) {
        log.debug("processCancel");

//        Request req = e.getRequest();
//        String method = req.getMethod();
//        FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
//        ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
//        log.info(fr.getAddress() + "-->" + method + "-->"+to.getAddress());
//        log.info(Utils.toString(e.getRequest()));
        ((McuSession) e.getServerTransaction().getDialog().getApplicationData()).cancel(e);

    }

    void processBye(final RequestEvent event) {
        log.debug("processBye");
        try
        {
            executor.execute(()->{
                ((McuSession) event.getServerTransaction().getDialog().getApplicationData()).close(event);
            });
            //todo:
            //failover applicationData == null
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
            e.printStackTrace();
            throw e;
        }

    }

    public void processResponse(ResponseEvent event) {
        log.debug("processResponse");
        Response res = event.getResponse();
        String ver = res.getSIPVersion();//SIP/2.0
        int status = res.getStatusCode();
        String reason = res.getReasonPhrase();
//        res.getRawContent();
        StringBuffer headers = new StringBuffer();
        for (ListIterator i = res.getHeaderNames(); i.hasNext(); )
        {
            String key = String.valueOf(i.next());
            Header h = res.getHeader(key);
//            System.out.println();
            headers.append(h.toString());
        }
        String body = null;
        byte[] content = res.getRawContent();
        if (content != null)
        {
            body = new String(content);
        }

//        log.info("processResponse");
        log.debug("processResponse\n" + ver + " " + status + " " + reason + "\n" + headers + "\n" + body);

        log.info(Utils.toString(res));
//        log.info("************ state = " + event.getDialog().getState());
        //100 Trying    state = null
        //180 Ringing   state = Early Dialog
        //200 Ok        state = Confirmed Dialog

        CSeqHeader cseq = (CSeqHeader) res.getHeader(CSeqHeader.NAME);

//        ClientTransaction tid = event.getClientTransaction();
//        System.out.println("tid ====== "+tid);

//        try
//        {
        if (Request.INVITE.equals(cseq.getMethod()))
        {
//            int decline=Response.DECLINE;//603
            //REQUEST_TERMINATED = 487
            //if (Response.DECLINE/*603*/ == res.getStatusCode())
//            if (Response.REQUEST_TERMINATED == res.getStatusCode())
//            {
//                //noop
//                return;
//            }
            if (300 <= res.getStatusCode() && res.getStatusCode() <= 699)
            {
                try
                {
                    ((McuSession) event.getClientTransaction().getDialog().getApplicationData()).inviteDeclined(event);
                } catch (NullPointerException e)
                {
                    //throw e;
                    e.printStackTrace();
//                    return;
                }

            } else if (Response.OK == res.getStatusCode())
            {
                ((McuSession) event.getClientTransaction().getDialog().getApplicationData()).inviteAccepted(event);
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
//        log.info("processTransactionTerminated");
//        Transaction tr = (event.isServerTransaction()) ? event.getServerTransaction() : event.getClientTransaction();
//        String branchId = tr.getBranchId();
//        log.info("processTransactionTerminated branchId=" + branchId);
    }

    public void processDialogTerminated(DialogTerminatedEvent event) {
//        log.info("processDialogTerminated");
//        Dialog dialog = event.getDialog();
//        String dialogId = dialog.getDialogId();
//        String callId = dialog.getCallId().getCallId();
//        log.info("processDialogTerminated callId=" + callId);
    }


    public void sessionDeclined(McuSession s) {
        log.info("sessionDeclined");
    }

    public void sessionCancelled(McuSession s) {
        log.debug("sessionCancelled");
    }

    public void sessionCreated(McuSession s) {
//        log.debug("sessionCreated");
        log.info("sessionCreated inbound callId="+s.getInboundTr().getDialog().getCallId().getCallId()+"----outbound callId="+s.getOutboundTr().getDialog().getCallId().getCallId());

        Jedis jedis = pool.getResource();
        try
        {
            Map<String, String> m = new HashMap<String, String>();
            m.put("inbound", s.getInboundTr().getDialog().getCallId().getCallId());
            m.put("outbound", s.getOutboundTr().getDialog().getCallId().getCallId());

            jedis.hmset("session:" + m.get("inbound"),
                        m);
            jedis.hmset("session:" + m.get("outbound"),
                        m);
        } finally
        {
            jedis.close();
        }
    }

    @Override
    public void sessionCannotCreate(McuSession s, Exception e) {
        log.error("sessionCannotCreate", e);
    }

    public void sessionClosed(McuSession s) {
//        log.debug("sessionClosed");
        log.info("sessionClosed inbound="+s.getInboundTr().getDialog().getCallId().getCallId()+"--|-- outbound"+s.getOutboundTr().getDialog().getCallId().getCallId());

        Jedis jedis = pool.getResource();
        try
        {
            jedis.del("session:" + s.getInboundTr().getDialog().getCallId().getCallId());
            jedis.del("session:" + s.getOutboundTr().getDialog().getCallId().getCallId());
        } finally
        {
            jedis.close();
        }
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

    public MruClient getClient() {
        return client;
    }

    public void setClient(MruClient client) {
        this.client = client;
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
