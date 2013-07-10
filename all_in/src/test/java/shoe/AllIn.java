package shoe;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AllIn {
    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "test";
    public static final int MESSAGES_TO_SEND = 10;
    private BrokerService broker;
    private volatile boolean shutdown;
    private int messagesReceived;

    @Before
    public void startBroker() throws Exception {
        broker = new BrokerService();
        broker.addConnector(BROKER_URL);
        broker.start();
    }

    @Test
    public void smoke() throws Exception {
        produce();

        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(QUEUE_NAME);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new SimpleConsumer());
        while(!shutdown) {
            Thread.sleep(10);
        }
        assertThat(messagesReceived, is(MESSAGES_TO_SEND + 1));
    }

    private void produce() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(destination);

        for(int i = 1; i <= MESSAGES_TO_SEND; ++i) {
            Message message = session.createTextMessage("Hello World!" + i);
            producer.send(message);
        }
        producer.send(session.createTextMessage("terminate"));
    }

    @After
    public void stopBroker() throws Exception {
        broker.stop();
    }

    class SimpleConsumer implements MessageListener {
        @Override
        public void onMessage(Message message) {
            try {
                String body = ((TextMessage)message).getText();
                ++messagesReceived;
                if("terminate".equals(body)) {
                    shutdown = true;
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
