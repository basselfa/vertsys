import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public class WebOrderSystem {
	  	private Connection connection;
	    private Queue inQueue;
	    private Topic topic;
	    private Session session;
	    private MessageProducer messageProducer;
	    private MessageConsumer messageConsumer;
	    
	 
	  
	    //liest daten aus argumente
    	//erstellt daraus einen string
    	//schickt den string durch queue (newWebOrders) an routes
    	// <First Name, Last Name, Number of ordered surfboards, Number of ordered diving suits, Customer-ID>
	    public static void main(String[] args) {
	    	
	    	//check valit input
	    	if (args.length != 5) {
	    		System.out.println("wrong number of arguments.");
    			return;
	    	}
	    	
	    	for (int i=2 ; i < args.length ; i++) {
	    		if (!(args[i].matches("[0-9]+") && args[i].length() > 0)) {
		    		System.out.println("wrong format for number inputs.");
	    			return;
	    		}
	    	}
	    	//generates string for new incoming order
	        String newOrder = "";
	        for(int i=0 ; i < args.length ; i++) {
	        	newOrder = newOrder + args[i] + ",";
	        }
        	newOrder = newOrder + args[args.length -1];

	        
	    	//send string to routes for further processing
	        try {
	            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();

	            conFactory.setTrustAllPackages(true);
	          
	            Connection connection = conFactory.createConnection();
		        System.out.println(Session.AUTO_ACKNOWLEDGE);

	            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	            Queue queue = session.createQueue("webOrder");
	            MessageProducer messageProducer = session.createProducer(queue);
	            TextMessage msg = session.createTextMessage(newOrder);
	            messageProducer.send(msg);
	          
	            connection.start();
	        } catch (JMSException e) {
	            e.printStackTrace();
	        }
	    }
}
