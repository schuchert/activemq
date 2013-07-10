package net.timico.messaging;
 
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
 
public class EndProducer
{
    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
 
    public EndProducer(ConnectionFactory factory, String queueName) throws JMSException
    {
        this.factory = factory;
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        producer = session.createProducer(destination);
    }
 
    public void run() throws JMSException
    {
        System.out.println("Sending terminate message");
        Message message = session.createTextMessage("terminate");
        producer.send(message);
    }
 
    public void close() throws JMSException
    {
        if (connection != null)
        {
            connection.close();
        }
    }
}
