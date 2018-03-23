package x3.player.mcu;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import x3.player.mcu.mru.MruClient;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Properties config=new Properties();
        FileInputStream in=new FileInputStream("config/mcu.properties");
        try
        {
            config.load(in);
        } finally
        {
            in.close();
        }
        int port=Integer.parseInt(config.getProperty("port", "5070"));
        log.info("port = "+port);
        String redis_host = config.getProperty("redis.host", "localhost");
        int redis_port=Integer.parseInt(config.getProperty("redis.port", "6379"));
        log.info("redis = "+redis_host+":"+redis_port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(jedisPoolConfig,
                                       redis_host,
                                       redis_port,
                                       1000/*timeout*/);

        MruClient mru = new MruClient();
        String mq_host = config.getProperty("rabbitmq.host", "localhost");
        String user=config.getProperty("rabbitmq.user");
        String passwd=config.getProperty("rabbitmq.passwd");
        log.info("rabbitmq = "+mq_host+", "+user+":"+passwd);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(mq_host);
        if (user != null)
        {
            factory.setUsername(user);
        }
        if (passwd != null)
        {
            factory.setPassword(passwd);
        }
        mru.setConnectionFactory(factory);
        mru.connect();

        McuSipSignal sip = new McuSipSignal();
        sip.setPort(port);
        sip.setPool(pool);
        sip.setClient(mru);
        sip.init();
        log.info("MCU start");
    }
}
