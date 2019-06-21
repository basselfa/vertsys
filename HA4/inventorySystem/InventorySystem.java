

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class InventorySystem {
	
	private static int totalNumberSurfboards;
	private static int totalNumberDivingSuits;
	private static int numberOrderedDivingSuits;
	private static int numberOrderedSurfboards;
	
	public static void  main(String[] args) {
	    try {
	    	if(args.length == 0) {
				System.out.println("wrong arguments");
				return;
			}
        // Parse the string argument into an integer value.
	    totalNumberSurfboards = Integer.parseInt(args[0]);
	    totalNumberDivingSuits = Integer.parseInt(args[1]);
     
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
					String validity = setOrderValidity(order);
					System.out.println("Order validity is: "+validity);
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

	
	
	public static String setOrderValidity(Order order) {
		numberOrderedDivingSuits= Integer.parseInt(order.getNumberOfDivingSuits());
		numberOrderedSurfboards=Integer.parseInt(order.getNumberOfSurfboards());
		
		if(numberOrderedDivingSuits <= totalNumberSurfboards && numberOrderedDivingSuits <= totalNumberDivingSuits ) {
			
			order.setValid("true");
			totalNumberSurfboards = totalNumberSurfboards - numberOrderedSurfboards;
			totalNumberDivingSuits =totalNumberDivingSuits - numberOrderedDivingSuits;
		
		}
		else {
			
			order.setValid("false");
		
		}
		return order.getValid();
		
	}

}



