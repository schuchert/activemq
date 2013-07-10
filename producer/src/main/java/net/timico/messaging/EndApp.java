package net.timico.messaging;
 
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class EndApp
{
    public static String brokerURL = "tcp://localhost:61616";
 
    public static void main( String[] args ) throws Exception
    {
        // setup the connection to ActiveMQ
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
 
        EndProducer producer = new EndProducer(factory, "test");
        producer.run();
        producer.close();
    }
}
