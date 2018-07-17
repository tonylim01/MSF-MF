package x3.player.mcu;

import com.uangel.svc.util.LocalIP;
import gov.nist.javax.sip.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import x3.player.a2s.AppInstance;
//import x3.player.a2s.sip.SipSignal;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.ArrayList;

//import static x3.player.a2s.sip.Process.SipDefine.MAX_FORWARDS;//70
//import static x3.player.a2s.sip.Process.SipDefine.TRANSPORT;//UDP

public class OptionsProcessing {

    private static final Logger log = LoggerFactory.getLogger(OptionsProcessing.class);

    //    private SipSignal sipSignal;
    SipProvider sipProvider;
    final String TRANSPORT = "udp";
    final int MAX_FORWARDS = 70;

    private String toTags;
    private String fromTags;

    private String toIp;
    private String fromIp;
    private String fromName;
    private String toName;
//    private int toPort;
    private String peerHostPort;

    private CallIdHeader callIdHeader;

    private ContactHeader ccontactHeader;
    private AddressFactory af;
    private HeaderFactory hf;
    private long sequence;
    private int fromPort;

    public OptionsProcessing(SipProvider sipProvider) {
//        this.sipSignal = AppInstance.getInstance().getSipSignal();
        this.sipProvider = sipProvider;
    }

    private FromHeader createFromHeader() throws ParseException {

        SipURI fromAddress = this.af.createSipURI(this.fromName, this.fromIp/*host*/);
        Address fromNameAddress = this.af.createAddress(fromAddress);
//        fromNameAddress.setDisplayName(this.fromName);

        return this.hf.createFromHeader(fromNameAddress, this.fromTags);
    }

    private ToHeader createToHeader() throws ParseException {

        SipURI toAddress = this.af.createSipURI(this.toName, this.toIp);
        Address toNameAddress = this.af.createAddress(toAddress);
//        toNameAddress.setDisplayName(this.toName);

        return this.hf.createToHeader(toNameAddress, this.toTags);
    }

    private SipURI createSipURI() throws ParseException {

//        String host = this.toIp + ":" + this.toPort;//-->peerHostPort
        return this.af.createSipURI(this.toName, /*host*/peerHostPort);
    }

    private ArrayList createVia() throws ParseException, InvalidArgumentException {
        // Create ViaHeaders
        ArrayList viaHeaders = new ArrayList();

        String ipAddress = this/*.sipSignal.getSipProvider()*/.sipProvider.getListeningPoint(TRANSPORT).getIPAddress();
        ViaHeader viaHeader = this.hf.createViaHeader(ipAddress, this./*sipSignal.getSipProvider()*/sipProvider.getListeningPoint(TRANSPORT).getPort(), TRANSPORT, null);

        // add via headers
        viaHeaders.add(viaHeader);

        return viaHeaders;
    }

    private CSeqHeader createCSeqHeader(String method) throws ParseException, InvalidArgumentException {

        return this.hf.createCSeqHeader(this.sequence++, method);
    }

    private MaxForwardsHeader createMaxForwardsHeader() throws InvalidArgumentException {

        return this.hf.createMaxForwardsHeader( MAX_FORWARDS/*70*/);
    }

    private void createContactHeader() throws ParseException {

        // Create the contact name address.
        SipURI contactURI = this.af.createSipURI(this.fromName, this.fromIp);
        contactURI.setPort(this.fromPort);
        Address contactAddress = this.af.createAddress(contactURI);

        // Add the contact address.
        contactAddress.setDisplayName(this.fromName);

        this.ccontactHeader = this.hf.createContactHeader(contactAddress);
    }


    public void options() {//OPTIONS
        String method = Request.OPTIONS;
        SipFactory sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Request req;

        try
        {
            this.hf = sipFactory.createHeaderFactory();
            this.af = sipFactory.createAddressFactory();
            MessageFactory mf = sipFactory.createMessageFactory();

            Utils utils = new Utils();
            this.fromTags = utils.generateTag();

            this.callIdHeader = this.getCallIdHeader();

            req = mf.createRequest(this.createSipURI(),
                                   method,
                                   this.callIdHeader,
                                   this.createCSeqHeader(method),
                                   this.createFromHeader(),
                                   this.createToHeader(),
                                   this.createVia(),
                                   this.createMaxForwardsHeader());

            this.createContactHeader();

            req.addHeader(this.ccontactHeader);

            // Create the client transaction.
//            logger.debug("out_request : " + req);
            log.debug(x3.player.mcu.Utils.toString(req));
            ClientTransaction cliInviteTid;
            cliInviteTid = this./*sipSignal.getSipProvider().*/sipProvider.getNewClientTransaction(req);
            cliInviteTid.sendRequest();

        } catch (Exception e)
        {
//            System.out.println(ex.getMessage());
//            ex.printStackTrace();
            log.error("options fail", e);
        }
    }

    void setToTags(String toTags) {
        this.toTags = toTags;
    }

    public void setFromTags(String fromTags) {
        this.fromTags = fromTags;
    }

    public void setToIp(String toIp) {
        this.toIp = toIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

//    public void setToPort(int toPort) {
//        this.toPort = toPort;
//    }

    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }
//    {
//        this.callIdHeader = callIdHeader;
//    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public CallIdHeader getCallIdHeader() {
        return this./*sipSignal.getSipProvider().*/sipProvider.getNewCallId();
    }

    public static void main(String[] args) throws Exception {
        SipSignal sip = new SipSignal();
        String localIP = LocalIP.getLocalAddress().toString().substring(1);
        log.info("Local IP = "+localIP);
        sip.setHost(localIP);
        int port=5070;
        sip.setPort(port);
        sip.init();

        OptionsProcessing opt=new OptionsProcessing(sip.getSipProvider());
        opt.setToName("CSCF_AAA");
        opt.setFromName("ACS");
        opt.setToIp("192.168.7.81");
        opt.setFromIp("sktims.net");
//        opt.setToPort(5070);
        opt.setPeerHostPort("sktims.net");
        opt.setFromPort(5060);
        opt.setSequence(12);
        opt.options();
/*
OPTIONS sip:CSCF_AAA@192.168.7.81:5070 SIP/2.0
Content-Length: 0
Call-ID: d7ba02440a84d40e3417f340be60651c@192.168.2.97
CSeq: 12 OPTIONS
From: <sip:ACS@sktims.net>;tag=955a69e
To: <sip:CSCF_AAA@192.168.7.81>
Via: SIP/2.0/UDP 192.168.2.97:5070;branch=z9hG4bK-373738-71f3f5038982c1c893593ecccc651596
Max-Forwards: 70
Contact: "ACS" <sip:ACS@sktims.net:5060>

 */
    }

    public String getPeerHostPort() {
        return peerHostPort;
    }

    public void setPeerHostPort(String peerHostPort) {
        this.peerHostPort = peerHostPort;
    }
}