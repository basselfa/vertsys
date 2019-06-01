package de.tu_berlin.cit.vs.jms.broker;

import java.util.ArrayList;
import java.util.HashMap;
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
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import de.tu_berlin.cit.vs.jms.common.BrokerMessage;
import de.tu_berlin.cit.vs.jms.common.BuyMessage;
import de.tu_berlin.cit.vs.jms.common.ListMessage;
import de.tu_berlin.cit.vs.jms.common.RegisterMessage;
import de.tu_berlin.cit.vs.jms.common.RequestListMessage;
import de.tu_berlin.cit.vs.jms.common.SellMessage;
import de.tu_berlin.cit.vs.jms.common.Stock;
import de.tu_berlin.cit.vs.jms.common.UnregisterMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 
 * 
 * 
 */
public class SimpleBroker {
    /* TODO: variables as needed */
	ActiveMQConnectionFactory brokerCF = null;
    Connection brokerConnection =null;
    Session brokerSession = null;
    Queue registerQueue = null;
    MessageConsumer brokerConsumer = null ;
    private ConcurrentHashMap<String, Topic> TopicsMap = new ConcurrentHashMap<String, Topic>();
    private ConcurrentHashMap<String, Queue> InputQueueMap = new ConcurrentHashMap<String, Queue>();
    private ConcurrentHashMap<String, Queue> OutputQueueMap = new ConcurrentHashMap<String, Queue>();
  
    
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
	   			if (msgParsed instanceof RegisterMessage) 
	   			{
	   			   RegisterMessage rm = (RegisterMessage) msgParsed;
	   			   String client = rm.getClientName();
	   			   System.out.println("Received: " + client);
	   			   InputQueueMap.put(client,brokerSession.createConsumer(brokerSession.createQueue(client+ "InputQueue")));
	   			   OutputQueueMap.put(client,brokerSession.createProducer(brokerSession.createQueue(client + "OutputQueue")));
	   			 
	   			}
	   			else if (msgParsed instanceof UnregisterMessage) 
	   			{
	   				UnregisterMessage urm = (UnregisterMessage) msgParsed;
	   				String client = urm.getClientName();
	   			    System.out.println("Received: " + client);
	   			    InputQueueMap.remove(client+ "InputQueue");
	   			    OutputQueueMap.remove(client + "OutputQueue");
	   				
	   			}
	   			else if (msgParsed instanceof SellMessage) 
	   			{
	   				SellMessage sm = (SellMessage) msgParsed;
	   			}
	   			else if (msgParsed instanceof RequestListMessage) 
	   			{
	   				RequestListMessage rlm = (RequestListMessage) msgParsed;
	   			}
	   			else if (msgParsed instanceof ListMessage) 
	   			{
	   				ListMessage lm = (ListMessage) msgParsed;
	   			}
	   			else if (msgParsed instanceof BuyMessage) 
	   			{
	   				BuyMessage bm = (BuyMessage) msgParsed;
	   			}
	   			else if (msgParsed instanceof WatchMessage) 
	   			{
	   				WatchMessage wm = (WatchMessage) msgParsed;
	   			}
	   			else if (msgParsed instanceof UnWatchMessage) 
	   			{
	   				UnWatchMessage uwm = (UnWatchMessage) msgParsed;
	   			}
            }
        }
    };
    
    
    
    public SimpleBroker(List<Stock> stockList) throws JMSException {
        /* TODO: initialize connection, sessions, etc. */
    	brokerCF = new ActiveMQConnectionFactory("vm://localhost");
        brokerConnection = brokerCF.createConnection();
        brokerConnection.start();
        brokerSession = brokerConnection.createSession(false, brokerSession.AUTO_ACKNOWLEDGE);
        registerQueue = brokerSession.createQueue("registerQueue");
        brokerConsumer = brokerSession.createConsumer(registerQueue);
        brokerConsumer.setMessageListner(listener);        
    	//initialize hash table for queue x2
        for(Stock stock : stockList) {
            /* TODO: prepare stocks as topics */
        	TopicsMap.put(stock,brokerSession.createTopic(stock.getName()));
        }
     
    }
    
    public void stop() throws JMSException {
        //TODO
    }
    
    public synchronized int buy(String stockName, int amount) throws JMSException {
        //TODO
        return -1;
    }
    
    public synchronized int sell(String stockName, int amount) throws JMSException {
        //TODO
        return -1;
    }
    
    public synchronized List<Stock> getStockList() {
        List<Stock> stockList = new ArrayList<>();

        /* TODO: populate stockList */

        return stockList;
    }
}
