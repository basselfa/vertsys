

import javax.jms.*;


import org.apache.activemq.ActiveMQConnectionFactory;

public class ResultSystem {
	 public static void main(String[] args)  {
	       
	        try {
//	        	if(args.length == 0) {
//					System.out.println("wrong arguments");
//					return;
//				}
	            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();
	            
	            conFactory.setTrustAllPackages(true);
	          
	            Connection connection = conFactory.createConnection();
	            
	            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	            Queue inQueue = session.createQueue("RESULTORDER");
	       
	            Queue outQueue = session.createQueue("FINISHEDORDER");
	            MessageConsumer messageConsumer = session.createConsumer(inQueue);
	            MessageProducer messageProducer = session.createProducer(outQueue);
	         
	            messageConsumer.setMessageListener(new MessageListener() {
	                @Override
	                public void onMessage(Message message) {
	                    try {
	                    	
	                    	Order order = (Order) ((ObjectMessage) message).getObject();	
	                    if(message.getBooleanProperty("validationResult")==true) {
	                    	
	                    	order.setValidResult("true");
	                    	System.out.print("NEW VALID ORDER:");
	                    	}
	                    else if(message.getBooleanProperty("validationResult")==false) {
	                    	order.setValidResult("false");
	                    	System.out.print("NEW INVALID ORDER:");
	                    	
	                    }
	                	  String orderString = "First Name: "+order.getFirstName() +", Last Name: "+ order.getLastName() +",Number Surfboards: "+ order.getNumberOfSurfboards() +",Number DivingSuits: "+ order.getNumberOfDivingSuits()+", TotalNumber: "+order.getOverallItems()+",OrderID: "+order.getOrderID()+",CustomerID: "+ order.getCustomerID();
	              
	                	  TextMessage msg = session.createTextMessage(orderString);
	                	  	 
	                	  msg.setStringProperty("validOrder", order.getValidationResult());
	                	  messageProducer.send(msg);
	                	  System.out.println (orderString);
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
