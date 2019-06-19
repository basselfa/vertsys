

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class BillingSystem {

	private final static int firstPrime = 241;
	private final static int secondPrime = 50511;
	private final static int someNumberE = 13;
	private final static int failPercent = 5;
	private static int hashInp; 
	

	
	public static void main(String[] args) {
		try {
            // Parse the string argument into an integer value.
	    	hashInp = Integer.parseInt(args[0]);
       
		    ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();
	        conFactory.setTrustAllPackages(true);
	        Connection connection = conFactory.createConnection();
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	       
	        Topic topic = session.createTopic("ORDER");
	        MessageConsumer messageConsumer = session.createConsumer(topic);
	        
	        Queue outQueue = session.createQueue("BILL_INV_ORDER");
	        MessageProducer messageProducer = session.createProducer(outQueue);
	        
	        messageConsumer.setMessageListener(new MessageListener() {
	            @Override
	            public void onMessage(Message message) {
	            	try {
						Order order = (Order) ((ObjectMessage) message).getObject();
						hashInp = Integer.parseInt(order.getCustomerID());
						Boolean validity = hash(hashInp);
						order.setValid(validity.toString());
						System.out.println("order validitiy: "+order.getValid());
						
						 ObjectMessage msg = session.createObjectMessage(order);
						 msg.setJMSCorrelationID(order.getOrderID());
				         messageProducer.send(msg);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	           
	            } 
	        });
	        
	        connection.start();
			} catch (JMSException | NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	    
	public static boolean hash(int inputInt) {
		return (((inputInt^someNumberE) %(firstPrime * secondPrime) << 7) % 100  > failPercent) ? true : false;
	}
	

	
	
	public static void test() {
		int pos = 0;
		int neg = 0;
		for(int i = 0; i <= 100000 ; i++) {
			if(hash(i)) {
				System.out.println("true");
				pos++;
			}else {
				System.out.println("false");
				neg++;
			}
		}
		System.out.println("pos: " + pos + ", neg: " + neg);
	}

}
