package de.tu_berlin.cit.vs.jms.common;


public class RequestListMessage extends BrokerMessage {
	private String clientName;
    public RequestListMessage(String clientName) {
        super(Type.STOCK_LIST);
        this.clientName = clientName;
    }
    public String getClientName() {
        return clientName;
    }
    
}
