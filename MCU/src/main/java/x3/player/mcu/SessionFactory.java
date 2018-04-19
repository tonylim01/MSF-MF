package x3.player.mcu;

import x3.player.mcu.mru.Client;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/**
 * Created by hwaseob on 2018-02-26.
 */
public class SessionFactory {

    private SipFactory sipFactory;

    private SipProvider sipProvider;

    private MessageFactory messageFactory;

    private HeaderFactory headerFactory;

    private AddressFactory addressFactory;
    //    static ListeningPoint udpListeningPoint = 5070;
//    String transport = "udp";
    private Client client;

//    private ListeningPoint udpListeningPoint;
    private SessionLifeCycleListener sessionLifeCycleListener = new NullSessionLifeCycleListener();

    public SessionFactory() {
    }

    public Session create(RequestEvent e) {
//        return MrfcSession.create(this, e);
        Session s = new Session(this);
        s.processInvite(e);
        return s;
    }

    public SipFactory getSipFactory() {
        return sipFactory;
    }

    public void setSipFactory(SipFactory sipFactory) {
        this.sipFactory = sipFactory;
    }

    public SipProvider getSipProvider() {
        return sipProvider;
    }

    public void setSipProvider(SipProvider sipProvider) {
        this.sipProvider = sipProvider;
    }

//    public ListeningPoint getUdpListeningPoint() {
//        return udpListeningPoint;
//    }
//
//    public void setUdpListeningPoint(ListeningPoint udpListeningPoint) {
//        this.udpListeningPoint = udpListeningPoint;
//    }

    public HeaderFactory getHeaderFactory() {
        if (headerFactory == null)
            try
            {
                headerFactory = getSipFactory().createHeaderFactory();
            } catch (PeerUnavailableException e)
            {
                e.printStackTrace();
            }

        return headerFactory;
    }

    public MessageFactory getMessageFactory() {
        if (messageFactory == null)
            try
            {
                messageFactory = getSipFactory().createMessageFactory();
            } catch (PeerUnavailableException e)
            {
                e.printStackTrace();
            }

        return messageFactory;
    }

    public AddressFactory getAddressFactory() {
        if (addressFactory == null)
            try
            {
                addressFactory = getSipFactory().createAddressFactory();
            } catch (PeerUnavailableException e)
            {
                e.printStackTrace();
            }

        return addressFactory;
    }

    public SessionLifeCycleListener getSessionLifeCycleListener() {
        return sessionLifeCycleListener;
    }

    public void setSessionLifeCycleListener(SessionLifeCycleListener sessionLifeCycleListener) {
        if (sessionLifeCycleListener == null)
        {
            this.sessionLifeCycleListener = new NullSessionLifeCycleListener();
            return;
        }
        this.sessionLifeCycleListener = sessionLifeCycleListener;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
