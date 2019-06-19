import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public class WebOrderSystem {
	  	private Connection connection;
	    private Queue inQueue;
	    private Topic topic;
	    private Session session;
	    private MessageProducer messageProducer;
	    private MessageConsumer messageConsumer;
	    
	 
	  

	    public static void main(String[] args) {
	        System.out.println("starting activemq");
	        try {
	            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();
	            
	            conFactory.setTrustAllPackages(true);
	          
	            Connection connection = conFactory.createConnection();
	           
	            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	            Topic topic = session.createTopic("ORDER");
	            MessageConsumer messageConsumer = session.createConsumer(topic);
	            
	         
	            //ENDPOINT
	            messageConsumer.setMessageListener(new MessageListener() {
	                @Override
	                public void onMessage(Message message) {
	                    try {
	               
	                	  Order order = (Order) ((ObjectMessage) message).getObject();
	                	  String orderString = order.getFirstName() +","+ order.getLastName() +","+ order.getNumberOfSurfboards() +","+ order.getNumberOfDivingSuits()+","+ order.getCustomerID();
	                	  System.out.println(orderString);
	                    } catch (JMSException e) {
	                        e.printStackTrace();
	                    }
	                }
	            });
	                 
	          
	            connection.start();
	        } catch (JMSException e) {
	            e.printStackTrace();
	        }
	    }

	  
	    

}
