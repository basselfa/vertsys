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
import de.tu_berlin.cit.vs.jms.common.UpdatesMessage;
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
    private ConcurrentHashMap<Topic, Integer> topicsCounter = new ConcurrentHashMap<Topic, Integer>();
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
	   			   System.out.println("client sent register");
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
	   			 System.out.println("client sent unregister");
	   				
	   			}
	   			else if (msgParsed instanceof SellMessage) 
	   			{
	   				SellMessage sm = (SellMessage) msgParsed;
	   				Boolean flagTopicHasConsumers  = true ;
	   				
	   				try {
						sell(sm.getStockName(),sm.getAmount()) ; 
						
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   				
	   				String um ="client "+ sm.getClientName()+" sold  "+ sm.getAmount() +" of Stocks "+  stocksTabelle.get(sm.getStockName()).getStockCount() +
	   						" paid " +stocksTabelle.get(sm.getStockName()).getPrice() + " per Stock with "
	   						+ stocksTabelle.get(sm.getStockName()).getAvailableCount() +  " Stocks are left" ;
	   						System.out.println(um);
	   						MessageProducer stockProducer =  topicsProducerTabelle.get(topicsTabelle.get(sm.getStockName()));
	   						if (topicsCounter.contains(topicsTabelle.get(sm.getStockName())) == false ||  topicsCounter.get(topicsTabelle.get(sm.getStockName())) == 0) 
	   							flagTopicHasConsumers = false ;
		   		       try {
		   		    	   if ( flagTopicHasConsumers  = true)
							stockProducer.send(brokerSession.createObjectMessage(new UpdatesMessage(um)));
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
	   			}

	   			else if (msgParsed instanceof BuyMessage) 
	   			{	Boolean flagTopicHasConsumers  = true ;
	   				BuyMessage bm = (BuyMessage) msgParsed;
	   				String ums = null ;
	   				MessageProducer stockProducer = null ;
	   				try {
						if(buy(bm.getStockName(),bm.getAmount())== -1 )
						{
							ums ="not enough Stocks" ;
							stockProducer =  OutputQueueMap.get(bm.getClientName()) ;
						}
						else
						{
							 ums ="client "+ bm.getClientName()+" bought  "+ bm.getAmount() +" of Stocks "+  stocksTabelle.get(bm.getStockName()).getStockCount() +
			   						" got " +stocksTabelle.get(bm.getStockName()).getPrice() + "per Stock with "+ stocksTabelle.get(bm.getStockName()).getAvailableCount() +  " Stocks are left" ;	
			   				 stockProducer =  topicsProducerTabelle.get(topicsTabelle.get(bm.getStockName()));
			   				if (topicsCounter.contains(topicsTabelle.get(bm.getStockName())) == false || topicsCounter.get(topicsTabelle.get(bm.getStockName())) == 0) 
			   					flagTopicHasConsumers = false ;
						}
						System.out.println(ums);
						try {
							System.out.println(flagTopicHasConsumers);
							if ( flagTopicHasConsumers  = true)
		   		    		stockProducer.send(brokerSession.createObjectMessage(new UpdatesMessage(ums)));
		   		    	} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   				
	   			}
	   			else if (msgParsed instanceof RequestListMessage) 
	   			{
	   				System.out.println("client sent RequestListMessage");
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
	   			else if (msgParsed instanceof WatchMessage) 
	   			{
	   				System.out.println("client sent WatchMessage");
	   				WatchMessage wm = (WatchMessage) msgParsed;
	   				Topic tmp = topicsTabelle.get(wm.getTopicName() );
	   				if (topicsCounter.contains(tmp) == false) 
	   				topicsCounter.put(tmp, 1 ) ;
	   				else
	   					topicsCounter.put(tmp, (topicsCounter.get(tmp)+ 1) ) ;
	   			}
	   			else if (msgParsed instanceof UnWatchMessage) 
	   			{
	   				UnWatchMessage uwm = (UnWatchMessage) msgParsed;
	   				System.out.println("client sent UnWatchMessage");
	   				Topic tmp = topicsTabelle.get(uwm.getTopicName() );
	   				if (topicsCounter.contains(tmp) == true) 
	   					topicsCounter.put(tmp, (topicsCounter.get(tmp)- 1) ) ;
		   				
		   					
	   			}
            }
        }
    };
    
    
    
    public SimpleBroker(List<Stock> stockList) throws JMSException {
        /* TODO: initialize connection, sessions, etc. */
    	brokerCF = new ActiveMQConnectionFactory( "tcp://localhost:61616");
    	brokerCF.setTrustAllPackages(true);
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
    	// closing all producers for topics
    	for (Stock stock : stocksList) {
    		
    		topicsProducerTabelle.get(topicsTabelle.get(stock.getName())).close();
		}
    	// closing all producers  for client Qeues
		for (Stock stock : stocksList) {
	    		
	    		topicsProducerTabelle.get(topicsTabelle.get(stock.getName())).close();
			}
		// closing all  producers consumers for client Qeues
		for ( MessageConsumer tmp : InputQueueMap.values() )
		{
			tmp.close();
		}
		
	
		
    	brokerConsumer.close();
        brokerSession.close();
        brokerConnection.close();
        System.out.println("bye bye");
       
        java.lang.System.exit(0) ;
        
    }
    
    public synchronized int buy(String stockName, int amount) throws JMSException {
    	Stock stock = stocksTabelle.get(stockName) ;
    	if (amount > stock.getAvailableCount() ) 
    		return -1;
    	stock.setAvailableCount(stock.getAvailableCount()- amount); 
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
    	
    	
    	stock.setAvailableCount(stock.getAvailableCount() + amount); 
    	return 0 ; 
        
    }
    
    public synchronized List<Stock> getStockList() {
        

        /* TODO: populate stockList */

        return stockList;
    }
}
