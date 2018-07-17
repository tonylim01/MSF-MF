package x3.player.mcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mcu.mru.Client;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by hwaseob on 2018-02-26.
 */
public class SessionFactory implements Serializable {
    final static Logger log = LoggerFactory.getLogger(SessionFactory.class);

    private SipFactory sipFactory;

    private SipProvider sipProvider;

    private MessageFactory messageFactory;

    private HeaderFactory headerFactory;

    private AddressFactory addressFactory;

    private Client client;

    private SessionLifeCycleListener sessionLifeCycleListener = new NullSessionLifeCycleListener();

    private Properties config;

    public SessionFactory() {
    }

    public Session create(RequestEvent e) {
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

    public HeaderFactory getHeaderFactory() {
        if (headerFactory == null)
            try
            {
                headerFactory = getSipFactory().createHeaderFactory();
            } catch (PeerUnavailableException e)
            {
                log.error(e.toString(), e);
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
                log.error(e.toString(), e);
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
                log.error(e.toString(), e);
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

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }
}
