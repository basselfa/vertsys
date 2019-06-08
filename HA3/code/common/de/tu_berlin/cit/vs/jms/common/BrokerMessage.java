package de.tu_berlin.cit.vs.jms.common;

import java.io.Serializable;


public abstract class BrokerMessage implements Serializable {
    private Type type;
    
    public BrokerMessage(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
    
    public enum Type {
        SYSTEM_REGISTER,		//send name of queues for client
        SYSTEM_UNREGISTER,
        SYSTEM_ERROR,
        SYSTEM_WATCH,
        SYSTEM_UNWATCH,
        SYSTEM_UPDATEMESSAGE,
        SYSTEM_REQUEST_LIST,
        
        STOCK_LIST,
        STOCK_BUY,
        STOCK_SELL
    }
}
