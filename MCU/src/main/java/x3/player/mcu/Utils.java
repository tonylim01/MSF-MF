package x3.player.mcu;

import javax.sip.RequestEvent;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * Created by hwaseob on 2018-03-07.
 */
public class Utils {

    static public String toString(Request req) {
//        log.debug("processInvite");
//        Request req = e.getRequest();
//        String method = req.getMethod();
        FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
        ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
        return (fr.getAddress() + "-->" + req.getMethod() + "-->"+to.getAddress());
//        fact.create(e);
    }

    static public String toString(Response res) {
//        log.debug("processInvite");
//        Request req = e.getRequest();
//        String method = req.getMethod();
//        FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
//        ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
//        return (fr.getAddress() + "-->" + method + "-->"+to.getAddress());
//        fact.create(e);
        FromHeader fr= (FromHeader) res.getHeader(FromHeader.NAME);
        ToHeader to= (ToHeader) res.getHeader(ToHeader.NAME);
        return (fr.getAddress() + "<--" + res.getStatusCode() + "<--"+to.getAddress());
    }

}
