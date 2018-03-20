package x3.player.mcu.mru;

import EDU.oswego.cs.dl.util.concurrent.FutureResult;
import EDU.oswego.cs.dl.util.concurrent.TimeoutException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by hwaseob on 2018-03-15.
 */
public class MruSession {
    private String conferenceID;
    private String sessionID;
    private String dir;
    private MruClient client;

//    public MruSession() {
//    }

    public MruSession(String dir,
                      String conferenceID,
                      String sessionID) {
        this.dir = dir;
        this.conferenceID = conferenceID;
        this.sessionID = sessionID;
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
        throw new IOException("df");
//        return client.offer(dir,
//                            conferenceID,
//                            sessionID,
//                            caller,
//                            callee,
//                            sdp);
    }

    public FutureResult answer() throws IOException {
        return client.answer(dir,
                             sessionID);
    }

    public FutureResult negoDone(String sdp) throws IOException {
        return client.negoDone(dir,
                               sessionID,
                               sdp);
    }

    public FutureResult hangup() throws IOException {
        return client.hangup(dir,
                            sessionID);
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

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public MruClient getClient() {
        return client;
    }

    public void setClient(MruClient client) {
        this.client = client;
    }
}
