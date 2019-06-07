package de.tu_berlin.cit.vs.jms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import de.tu_berlin.cit.vs.jms.common.BuyMessage;
import de.tu_berlin.cit.vs.jms.common.ListMessage;
import de.tu_berlin.cit.vs.jms.common.RegisterMessage;
import de.tu_berlin.cit.vs.jms.common.RequestListMessage;
import de.tu_berlin.cit.vs.jms.common.SellMessage;
import de.tu_berlin.cit.vs.jms.common.Stock;
import de.tu_berlin.cit.vs.jms.common.UnWatchMessage;
import de.tu_berlin.cit.vs.jms.common.UnregisterMessage;
import de.tu_berlin.cit.vs.jms.common.UpdatesMessage;
import de.tu_berlin.cit.vs.jms.common.WatchMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


public class JmsBrokerClient {
	private static String clientName;	//I added this because it was missing somehow
	ActiveMQConnectionFactory clientCF = null;
    Connection clientConnection = null;
    Session clientSession = null;
    
    Queue registerQueue = null;
    MessageProducer registerProducer = null;
    
    Queue inputQueue = null;
    MessageProducer inputProducer = null;
    
    Queue outputQueue = null;
    MessageConsumer outputConsumer = null;
    private ConcurrentHashMap<String, MessageConsumer> topicsTabelle = new ConcurrentHashMap<String, MessageConsumer>();
    private List<Stock> stocksList = null ;

    
    private final MessageListener listener = new MessageListener() {
        @Override
        public void onMessage(Message msg) {
            if(msg instanceof ObjectMessage) {
            	Object msgParsed = null;
            	
	   			 try 
	   			 {
	   			     msgParsed = ((ObjectMessage) msg).getObject();
	   			
	   			 } 
	   			 catch (JMSException ex) 
	   			 {
	   			     System.err.println("Error  " + ex.getMessage());
	   			 }
	   			if(msgParsed instanceof ListMessage)
	            {
	   				ListMessage lm =  (ListMessage) msgParsed;
	   				stocksList= lm.getStocks();
	   				System.out.println("server sent ListMessage");
	   			
	   				
	   			}
	   			else if(msgParsed instanceof UpdatesMessage)
	            { 
	   				UpdatesMessage um = (UpdatesMessage) msgParsed ; 
	   				System.out.println("server sent UpdatesMessage : "+ um.getUpdatesMessage()  );
	   				
	   			} 
            }
        }
    };
    
	
    public JmsBrokerClient(String clientName) throws JMSException {
        this.clientName = clientName;
        
        /* TODO: initialize connection, sessions, consumer, producer, etc. */
        
    	 clientCF = new ActiveMQConnectionFactory("tcp://localhost:61616");
    	 clientCF.setTrustAllPackages(true);
         clientConnection = clientCF.createConnection();
        clientConnection.start();
         clientSession = clientConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        registerQueue = clientSession.createQueue("registerQueue");
        registerProducer = clientSession.createProducer(registerQueue);
        RegisterMessage mr = new RegisterMessage(clientName);
        ObjectMessage msg = clientSession.createObjectMessage(mr);
        registerProducer.send(msg);
        
        String tmp =  clientName + "InputQueue" ;
         setInputQueue(tmp);
        
        tmp =  clientName + "OutputQueue" ; 
        setOutputQueue(tmp);
        
    }
    //all receiving in the function can be synchronous
 
    
    //new function register: sends a message to the server with its name (this one should be done)
    public void register() throws JMSException {
    	RegisterMessage registerMessage = new RegisterMessage(this.clientName);
    	ObjectMessage msg = clientSession.createObjectMessage(registerMessage);
    	registerProducer.send(msg);
    }
    
    //new function unregister
    public void unregister() throws JMSException {
        //TODO
    	RegisterMessage unregisterMessage = new RegisterMessage(this.clientName);
    	ObjectMessage msg = clientSession.createObjectMessage(unregisterMessage);
    	inputProducer.send(msg);
    }
    
    /**
     * ask for a list of all available stocks and all their info.
     * @throws JMSException
     */
    public void requestList() throws JMSException {
        //TODO
    	RequestListMessage requestListMessage = new RequestListMessage(this.clientName);
    	ObjectMessage msg = clientSession.createObjectMessage(requestListMessage);
    	registerProducer.send(msg);
    }
    
    public void buy(String stockName, int amount) throws JMSException {
        //TODO
    	BuyMessage buyMessage = new BuyMessage(clientName ,stockName,amount);
    	ObjectMessage msg = clientSession.createObjectMessage(buyMessage);
    	registerProducer.send(msg);
    }
    
    public void sell(String stockName, int amount) throws JMSException {
        //TODO
    	SellMessage sellMessage = new SellMessage(clientName , stockName,amount);
    	ObjectMessage msg = clientSession.createObjectMessage(sellMessage);
    	registerProducer.send(msg);
    }
    
    public void watch(String stockName) throws JMSException {	
    	createNewTopic(stockName);
    	WatchMessage wm = new WatchMessage(this.clientName,stockName);
    	ObjectMessage msg = clientSession.createObjectMessage(wm);
    	inputProducer.send(msg);
    	//send message to server
    	
    }
    
    public void unwatch(String stockName) throws JMSException {
    	topicsTabelle.get(stockName).close();
    	topicsTabelle.remove(stockName);
    	UnWatchMessage uwm = new UnWatchMessage(this.clientName,stockName);
    	ObjectMessage msg = clientSession.createObjectMessage(uwm);
    	inputProducer.send(msg);
    	
    }
    
    public void quit() throws JMSException {
        //TODO
    	UnregisterMessage unregisterMessage = new UnregisterMessage(this.clientName);
    	ObjectMessage msg = clientSession.createObjectMessage(unregisterMessage);
    	inputProducer.send(msg);
    	for (Stock stock : stocksList) {
    		if ( topicsTabelle.get(stock.getName()) != null )
    			topicsTabelle.get(stock.getName()).close();
		}
    	inputProducer.close();
    	outputConsumer.close();
    	registerProducer.close();
        clientSession.close();
        clientConnection.close();
        
    	
    }
    
    public void setInputQueue(String queueName) throws JMSException {
    
      	inputQueue = clientSession.createQueue(queueName);
        inputProducer = clientSession.createProducer(inputQueue);
    }
    
    public void setOutputQueue(String queueName) throws JMSException {
    	outputQueue = clientSession.createQueue(queueName);
        outputConsumer = clientSession.createConsumer(outputQueue);
        outputConsumer.setMessageListener(listener);
    }
    
    public void createNewTopic(String topicName) throws JMSException {
        //TODO
    	Topic topic = clientSession.createTopic(topicName);
    	MessageConsumer topicConsumer = clientSession.createConsumer(topic);
    	
	    topicsTabelle.put(topicName,topicConsumer);
	    topicConsumer.setMessageListener(listener);
    	
    }  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the client name:");
            clientName = reader.readLine();
            
            JmsBrokerClient client = new JmsBrokerClient(clientName);
            
            boolean running = true;
            client.requestList(); // to intialise sockets list
            while(running) {
                System.out.println("Enter command:");
                String[] task = reader.readLine().split(" ");
                
                synchronized(client) {
                    switch(task[0].toLowerCase()) {
                        case "quit":
                            client.quit();
                            System.out.println("Bye bye");
                            running = false;
                            break;
                        case "list":
                            client.requestList();
                            break;
                        case "buy":
                            if(task.length == 3) {
                                client.buy(task[1], Integer.parseInt(task[2]));
                            } else {
                                System.out.println("Correct usage: buy [stock] [amount]");
                            }
                            break;
                        case "sell":
                            if(task.length == 3) {
                                client.sell(task[1], Integer.parseInt(task[2]));
                            } else {
                                System.out.println("Correct usage: sell [stock] [amount]");
                            }
                            break;
                        case "watch":
                            if(task.length == 2) {
                                client.watch(task[1]);
                            } else {
                                System.out.println("Correct usage: watch [stock]");
                            }
                            break;
                        case "unwatch":
                            if(task.length == 2) {
                                client.unwatch(task[1]);
                            } else {
                                System.out.println("Correct usage: watch [stock]");
                            }
                            break;
                        default:
                            System.out.println("Unknown command. Try one of:");
                            System.out.println("quit, list, buy, sell, watch, unwatch");
                    }
                }
            }
            
        } catch (JMSException | IOException ex) {
            Logger.getLogger(JmsBrokerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
