package x3.player.mcu;

import javax.sip.RequestEvent;
import javax.sip.address.URI;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ListIterator;

/**
 * Created by hwaseob on 2018-03-07.
 */
public class Utils {

    static public String toString(Request req) {
        FromHeader fr= (FromHeader) req.getHeader(FromHeader.NAME);
        ToHeader to= (ToHeader) req.getHeader(ToHeader.NAME);
        String method = req.getMethod();
        URI requestURI = req.getRequestURI();//sip:abcd@127.0.0.1
        String ver = req.getSIPVersion();//SIP/2.0
        StringBuffer headers = new StringBuffer();
        for (ListIterator i = req.getHeaderNames(); i.hasNext(); )
        {
            String key = String.valueOf(i.next());
//            Header h = req.getHeader(key);
//            headers.append(h.toString());
            for (ListIterator li=req.getHeaders(key); li.hasNext();)
            {
                Header h=(Header)li.next();
                headers.append(h.toString());
            }

        }
        return fr.getAddress() + "-->" + req.getMethod() + "-->"+to.getAddress()+"\n" +method + " " + requestURI + " " + ver + "\n" + headers;
    }

    static public String toString(Response res) {
        FromHeader fr= (FromHeader) res.getHeader(FromHeader.NAME);
        ToHeader to= (ToHeader) res.getHeader(ToHeader.NAME);

        String ver = res.getSIPVersion();//SIP/2.0
        int status = res.getStatusCode();
        String reason = res.getReasonPhrase();
        StringBuffer headers = new StringBuffer();
        for (ListIterator i = res.getHeaderNames(); i.hasNext(); )
        {
            String key = String.valueOf(i.next());
//            Header h = res.getHeader(key);
//            headers.append(h.toString());
            for (ListIterator li=res.getHeaders(key); li.hasNext();)
            {
                Header h=(Header)li.next();
                headers.append(h.toString());
            }
        }
        String body = null;
        byte[] content = res.getRawContent();
        if (content != null)
        {
            body = new String(content);
        }

        return (fr.getAddress() + "<--" + res.getStatusCode() + "<--"+to.getAddress())+"\n"+(ver + " " + status + " " + reason + "\n" + headers + "\n" + body);
    }

}
