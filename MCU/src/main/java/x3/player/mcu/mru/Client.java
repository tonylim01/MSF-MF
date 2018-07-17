package x3.player.mcu.mru;

import EDU.oswego.cs.dl.util.concurrent.FutureResult;
import com.rabbitmq.client.*;
import com.rabbitmq.tools.json.JSONReader;
import com.rabbitmq.tools.json.JSONWriter;
import com.uangel.svc.util.TimedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by hwaseob on 2018-03-06.
 */
public class Client {
    final static Logger log = LoggerFactory.getLogger(Client.class);

    private ConnectionFactory factory;
    Connection connection;
    Channel channel;
    final String EXCHANGE_NAME = "";//default exchange
    final String q ="a2s_a2sd";
    Map<String, FutureResult> futureResultMap = Collections.synchronizedMap(new TimedHashMap<String, FutureResult>(60000/*1 min*/*10));
    int expir=5000;//5 seconds

    public Client() {
    }

    public ConnectionFactory getConnectionFactory() {
        return factory;
    }

    public void setConnectionFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    public void connect() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();
        log.info("rabbitmq is connected");

        channel.queueDeclare(q,
                             true,//durable
                             false,//exclusive
                             false,//autoDelete
                             null);
        channel.basicConsume(q,
                             true,
                             new DefaultConsumer(channel) {
                                 JSONReader r= new JSONReader();
                                 public void handleDelivery(String consumerTag,
                                                            Envelope envelope,
                                                            AMQP.BasicProperties properties,
                                                            byte[] rawBody) throws IOException {
                                     try
                                     {
                                         Map<String, Object> js = (Map<String, Object>) r.read(new String(rawBody));
                                         Map<String, Object> header = (Map<String, Object>) js.get("header");//properties.getHeaders();
                                         String msgFrom = String.valueOf(header.get("msgFrom"));
                                         Map<String, Object> body = (Map<String, Object>) js.get("body");
                                         String type = String.valueOf(header.get("type"));
                                         if ("mfmp_heartbeat_indi".equals(type))
                                         {
                                             Map<String, Object> hb = (Map<String, Object>) r.read(new String(rawBody));
//                                         if (heartbeatListener != null)
//                                         {
//                                             heartbeatListener.beat(h);
//                                         }
                                             beat(hb);
                                             return;
                                         }

                                         log.info("MCU<--" + msgFrom + " " + type + " " + new String(rawBody));
                                         String tid = String.valueOf(header.get("transactionId"));
                                         Integer rc = (Integer) header.get("reasonCode");
                                         FutureResult f = futureResultMap.remove(tid);
                                         if (f != null)
                                         {
                                             Map<String, Object> res = new HashMap<String, Object>();
                                             res.put("reasonCode", rc);
                                             res.put("body", js.get("body"));
                                             if (Integer.valueOf(0).equals(rc))
                                             {
                                                 f.set(res);
                                             } else
                                             {
                                                 f.setException(new ClientException(rc));
                                             }
                                         }

                                         if ("mfmp_service_start_res".equalsIgnoreCase(type) && body != null)
                                         {
                                             Integer aiif_id = (Integer) body.get("AIIF ID");
                                             String callId = String.valueOf(header.get("callId"));
                                             if (Integer.valueOf(0).equals(rc))
                                             {
                                                 onServiceStarted(callId,
                                                                  aiif_id);
                                             }
                                         }
                                     } catch (Exception e)
                                     {
                                        log.error(e.toString(), e);
                                     }
                                 }
                             });

    }

    public void close() {
        try
        {
            channel.close();
            connection.close();
        } catch (IOException e)
        {
            log.error(e.toString(), e);
        } catch (TimeoutException e)
        {
            log.error(e.toString(), e);
        }
    }

    protected FutureResult request(//String exchange,
                                   String routingKey,
                                   Map<String, Object> header,
                                   Map<String, Object> body) throws IOException {
        FutureResult f = new FutureResult();
        String tid = UUID.randomUUID().toString();
        futureResultMap.put(tid, f);
        header.put("transactionId", tid);

        Map<String, Object> m=new HashMap<>();
        m.put("header", header);
        if (body != null)
        {
            m.put("body", body);
        }

        try
        {
            channel.basicPublish(EXCHANGE_NAME,//exchange
                                 routingKey,//"amf_amfd",//"mru1_mrud",//routingKey
                                 new AMQP.BasicProperties
                                         .Builder()
                                         .expiration(""+expir)
                                         .build(),
                                 new JSONWriter().write(m).getBytes());
        } catch (NullPointerException e)
        {
            throw e;
        }

        return f;
    }

    public FutureResult offer(String dir,
                              String conference_id,
                              String callId,
                              String caller,
                              String callee,
                              String sdp) throws IOException {

        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_set_offer_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        Map<String,Object> body=new HashMap<>();
        if (sdp != null)
        {
            body.put("from_no", caller);
            body.put("to_no", callee);
            body.put("conference_id", conference_id);
            body.put("sdp", sdp);
        } else
        {
            body.put("from_no", caller);
            body.put("to_no", callee);
            body.put("conference_id", conference_id);
        }

        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "amf_amfd",//"mru1_mrud",//routingKey
                                 header,
                                 body);
        log.info("MCU-->MRU offer "+dir+ "\n" + header + "\n" + body);
        return f;
    }


    public FutureResult answer(String dir,
                               String callId) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_get_answer_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "amf_amfd",//"mru1_mrud",//routingKey
                                 header,
                                 null/*body.getBytes()*/);
        log.info("MCU-->MRU answer "+dir + "\n"+ header/*+"\n"+body*/);
        return f;
    }


    public FutureResult negoDone(String dir,
                                 String callId,
                                 String sdp) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_nego_done_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        Map<String,Object> body=new HashMap<>();
        if (sdp != null)
        {
            body.put("sdp", sdp);
        }

        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "amf_amfd",//"mru1_mrud",//routingKey
                                 header,
                                 body);
        log.info("MCU-->MRU negoDone "+dir+ "\n" + header + "\n" + body);
        return f;
    }

    public FutureResult hangup(String dir, String callId) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_hangup_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "amf_amfd",//"mru1_mrud",//routingKey
                                 header,
                                 null);//body.getBytes());
        log.info("MCU-->MRU hangup "+ dir + "\n"+ header);
        return f;
    }

    public FutureResult serviceStart(/*String dir,*/ String callId,
                                     String mdn) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_service_start_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        Map<String,Object> body=new HashMap<>();
        body.put("MDN", mdn);
        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "awf_awfd",//"mru1_mrud",//routingKey
                                 header,
                                 body);//body.getBytes());
        log.info("MCU-->ACSWF service start "+ /*dir +*/ "\n"+ header+"\n"+body);
        return f;
    }

    public FutureResult serviceStop(/*String dir,*/ String callId,
                                     String mdn) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_service_stop_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        Map<String,Object> body=new HashMap<>();
        body.put("MDN", mdn);
        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "awf_awfd",//"mru1_mrud",//routingKey
                                 header,
                                 body);//body.getBytes());
        log.info("MCU-->ACSWF service stop "+ /*dir +*/ "\n"+ header+"\n"+body);
        return f;
    }

    protected void onServiceStarted(String callId, int aiif_id) {
    }

    public FutureResult aiif_allocated(/*String dir,*/ String callId,
                                       String mdn,
                                       int aiif_id) throws IOException {
        Map<String,Object> header=new HashMap<>();
        header.put("type", "mfmp_service_start_req");
        header.put("callId", callId);
        header.put("msgFrom", q);
        Map<String,Object> body=new HashMap<>();
        body.put("MDN", mdn);
        body.put("AIIF ID", aiif_id);
        FutureResult f = request(//EXCHANGE_NAME,//exchange
                                 "amf_amfd",//"mru1_mrud",//routingKey
                                 header,
                                 body);//body.getBytes());
        log.info("MCU-->MRU service start "+ /*dir +*/ "\n"+ header+"\n"+body);
        return f;
    }

    public void beat(Map<String, Object> hb) {
    }


    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
        factory.setHost("172.16.0.45");
        factory.setUsername("hwaseob");
        factory.setPassword("uangel");
//        factory.setHost("192.168.2.115");//5672
//        factory.setUsername("mornbr");
//        factory.setPassword("mornbr");

        Client client = new Client();
        client.setConnectionFactory(factory);
//        client.setHeartbeatListener(new HeartbeatListener() {
//            @Override
//            public void beat(Map<String, Object> h) {
//                System.out.println(h);
//            }
//        });
        client.connect();
//        client.offer("INBOUND",
//                    "1234",
//                     "31ekf",
//                     "7687",
//                     "8233",
//                     "s=kwjef");


        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish("",//exchange
                             "mcu_mcud",//routingKey
                             new AMQP.BasicProperties
                                     .Builder()
                                     .build(),
                             "{\"session_total\":2000}".getBytes());
    }
}
