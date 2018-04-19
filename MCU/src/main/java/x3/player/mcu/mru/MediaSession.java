package x3.player.mcu.mru;

import EDU.oswego.cs.dl.util.concurrent.FutureResult;

import java.io.IOException;

/**
 * Created by hwaseob on 2018-03-15.
 */
public class MediaSession {
    private String conferenceID;
    private String callId;//=callId
    private String dir;
    private Client client;

//    public MruSession() {
//    }

    public MediaSession(String dir,
                        String conferenceID,
                        String callId) {
        this.dir = dir;
        this.conferenceID = conferenceID;
        this.callId = callId;
    }

    public FutureResult offer(String caller,
                              String callee,
                              String sdp) throws IOException {
        try
        {
            Thread.sleep(1000L);
        } catch (InterruptedException e)
        {
//            e.printStackTrace();
        }
//        throw new IOException("df");
        return client.offer(dir,
                            conferenceID,
                            callId,
                            caller,
                            callee,
                            sdp);
    }

    public FutureResult answer() throws IOException {
        return client.answer(dir,
                             callId);
    }

    public FutureResult negoDone(String sdp) throws IOException {
        return client.negoDone(dir,
                               callId,
                               sdp);
    }

    public FutureResult hangup() throws IOException {
        return client.hangup(dir,
                             callId);
//        MruClient mru = fact.getClient();
//        try
//        {
//            FutureResult f = mru.hangup(dir ? "INBOUND"
//                                                : "OUTBOUND",
//                                        tr.getDialog().getCallId().getCallId());
//            f.timedGet(1000L);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        } catch (TimeoutException e)
//        {
//            e.printStackTrace();
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        } catch (InvocationTargetException e)
//        {
//            e.printStackTrace();
//        }
    }

    public String getConferenceID() {
        return conferenceID;
    }

    public void setConferenceID(String conferenceID) {
        this.conferenceID = conferenceID;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
