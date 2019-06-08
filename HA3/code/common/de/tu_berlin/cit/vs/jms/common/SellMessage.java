package de.tu_berlin.cit.vs.jms.common;


public class SellMessage extends BrokerMessage {
    private String stockName;
    private int amount;
    private String clientName;
    public SellMessage(String clientName,String stockName, int amount) {
        super(Type.STOCK_SELL);
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
