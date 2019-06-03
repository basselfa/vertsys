package de.tu_berlin.cit.vs.jms.common;

import java.util.List;

import de.tu_berlin.cit.vs.jms.common.BrokerMessage.Type;

public class UpdatesMessage extends BrokerMessage {
	String um = null ;
    
    public UpdatesMessage(String um) {
       super(Type.SYSTEM_UPDATEMESSAGE);
       this.um = um ;
       
    }
    
    public String getUpdatesMessage() {
        return um;
    }
}
