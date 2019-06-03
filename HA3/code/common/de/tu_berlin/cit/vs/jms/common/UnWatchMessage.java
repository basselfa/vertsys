package de.tu_berlin.cit.vs.jms.common;

import de.tu_berlin.cit.vs.jms.common.BrokerMessage.Type;

public class UnWatchMessage extends BrokerMessage {
	   private String clientName;
	   private String topicName;
	    
	    public UnWatchMessage(String clientName,String topicName) {
	        super(Type.SYSTEM_REGISTER);
	        
	        this.clientName = clientName;
	        this.topicName = topicName;
	    }
	    
	    public String getClientName() {
	        return clientName;
	    }
	    public String getTopicName() {
	        return topicName;
	    }
}
