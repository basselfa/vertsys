package de.tu_berlin.cit.vs.jms.common;


public class BuyMessage extends BrokerMessage {
    private String stockName;
    private int amount;
    private String clientName;
    
    public BuyMessage(String clientName,String stockName, int amount) {
        super(Type.STOCK_BUY);
        this.clientName = clientName;
        this.stockName = stockName;
        this.amount = amount;
    }
    public String getClientName() {
        return clientName;
    }

    public String getStockName() {
        return stockName;
    }

    public int getAmount() {
        return amount;
    }
}
