package de.tu_berlin.cit.vs.jms.broker;

import java.security.MessageDigest;
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
import javax.jms.MessageProducer;
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
import de.tu_berlin.cit.vs.jms.common.UnWatchMessage;
import de.tu_berlin.cit.vs.jms.common.UnregisterMessage;
import de.tu_berlin.cit.vs.jms.common.WatchMessage;

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
    private ConcurrentHashMap<String, Stock> stocksTabelle = new ConcurrentHashMap<String, Stock>();
    private ConcurrentHashMap<String, Topic> topicsTabelle = new ConcurrentHashMap<String, Topic>();
    private ConcurrentHashMap<Topic, MessageProducer> topicsProducerTabelle = new ConcurrentHashMap<Topic, MessageProducer>();
 //   private ConcurrentHashMap<String, MessageProducer> TopicsMap = new ConcurrentHashMap<String, MessageProducer>();
    
    private ConcurrentHashMap<String, MessageConsumer> InputQueueMap = new ConcurrentHashMap<String, MessageConsumer>();
    
    private ConcurrentHashMap<String, MessageProducer> OutputQueueMap = new ConcurrentHashMap<String, MessageProducer>();
    private List<Stock> stocksList = new ArrayList<Stock>();
    List<Stock> stockList = new ArrayList<>();
    
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
	   			    MessageConsumer tmp = null;
					try {
						tmp = brokerSession.createConsumer(brokerSession.createQueue(client+ "InputQueue"));
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	   			   try {
					tmp.setMessageListener(listener);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   			   InputQueueMap.put(client,tmp);
	   			   try {
					OutputQueueMap.put(client,brokerSession.createProducer(brokerSession.createQueue(client + "OutputQueue")));
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   			 
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
	   				
	   				
	   				
					try {
						if(sell(sm.getStockName(),sm.getAmount())== -1 )
						{
							System.out.println("not enough Stocks");
						
						}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
	   			}
	   			else if (msgParsed instanceof RequestListMessage) 
	   			{
	   				RequestListMessage rlm = (RequestListMessage) msgParsed;
	   				ListMessage lm = new ListMessage(stockList);
	   				ObjectMessage tmp=null;
					try {
						tmp = brokerSession.createObjectMessage(lm);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   				try {
						OutputQueueMap.get(rlm.getClientName()).send(tmp);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} ; 
	   			}
	   			else if (msgParsed instanceof BuyMessage) 
	   			{
	   				BuyMessage bm = (BuyMessage) msgParsed;
	   				try {
						if(buy(bm.getStockName(),bm.getAmount())== -1 )
						{
							System.out.println("not enough Stocks");
						
						}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
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
    	brokerCF = new ActiveMQConnectionFactory( "tcp://localhost:61616");
        brokerConnection = brokerCF.createConnection();
        brokerConnection.start();
        brokerSession = brokerConnection.createSession(false, brokerSession.AUTO_ACKNOWLEDGE);
        registerQueue = brokerSession.createQueue("registerQueue");
        brokerConsumer = brokerSession.createConsumer(registerQueue);
        brokerConsumer.setMessageListener(listener);        
    	//initialize hash table for queue x2
        this.stockList= stockList;
        for(Stock stock : stockList) {
            /* TODO: prepare stocks as topics */
        	Topic topicTmp = brokerSession.createTopic(stock.getName()) ;
        	topicsTabelle.put(stock.getName(),topicTmp);
        	topicsProducerTabelle.put(topicTmp,brokerSession.createProducer(topicTmp));
        	stocksTabelle.put(stock.getName(),stock);
        	
        }
     
    }
    
    public void stop() throws JMSException {
        //TODO
    }
    
    public synchronized int buy(String stockName, int amount) throws JMSException {
    	Stock stock = stocksTabelle.get(stockName) ;
    
    	stock.setAvailableCount(stock.getAvailableCount()+ amount); 
    	return 0 ; 
        
    }
    
    /**
     * it adds amount to stock count of stockName
     * test the parameters before calling this method
     * @param stockName
     * @param amount
     * @return
     * @throws JMSException
     */
    public synchronized int sell(String stockName, int amount) throws JMSException {
        //TODO
    	Stock stock = stocksTabelle.get(stockName) ;
    	if (amount > stock.getAvailableCount() ) 
    		return -1;
    	
    	stock.setAvailableCount(stock.getAvailableCount()- amount); 
    	return 0 ; 
        
    }
    
    public synchronized List<Stock> getStockList() {
        

        /* TODO: populate stockList */

        return stockList;
    }
}
