import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import java.security.SecureRandom;

/**this class describes the connection between the queues and topics
 *also enricher, message translator and aggregator*/

public class Routes {
	static int orderID=0; 
	
	//creates new order with data from call center message
    private static Processor callAdapterTranslator = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            String[] parts = exchange.getIn().getBody(String.class).split(",");
            String customerID = parts[0];
            String fullName = parts[1];
            String[] name = fullName.split(" ");
            String firstName = name[0];
            String lastName = name[1];
            String numberOfSurfboards = parts[2];
            String numberOfDivingSuits = parts[3];
            orderID++;
  
            exchange.getOut().setBody(new Order(customerID, firstName, lastName, numberOfSurfboards, numberOfDivingSuits, Integer.toString(orderID)));
        }
    };

	//creates new order with data from web message
    private static Processor WebAdapterTranslator = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            String[] parts = exchange.getIn().getBody(String.class).split(",");
            String firstName = parts[0];
            String lastName = parts[1];
            String numberOfSurfboards = parts[2];
            String numberOfDivingSuits = parts[3];
            String customerID = parts[4];
            orderID++;
            
            exchange.getOut().setBody(new Order(customerID, firstName, lastName, numberOfSurfboards, numberOfDivingSuits, Integer.toString(orderID)));
        }
    };

    public static class ResultAggregation implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
           
        	 if (oldExchange == null) {return newExchange;}
     		 
     		 else {	
     			 Boolean inv, bill;
     			 
     			 Order oldOrder = (Order)oldExchange.getIn().getBody();
     			 Order newOrder = (Order)newExchange.getIn().getBody();
     			 System.out.println("oldorderID: "+ oldOrder.getOrderID());
     			 System.out.println("neworderID: "+ newOrder.getOrderID());
     			 if(	oldOrder.getValid().equals("true") && newOrder.getValid().equals("true") ) {
     				 System.out.println("order was considered valid");
     				 oldExchange.getIn().setHeader("validationResult", true);
     			 }
     			 else { 
     				 System.out.println("order was NOT considered valid");
     				 oldExchange.getIn().setHeader("validationResult", false);
     			 }
     			 return oldExchange ;
     		 }         
        }
    }
  
    // if the code doesn't
    public static class EnrichAggregation implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        	return newExchange;
        }
    }

    public static void main(String[] args) throws Exception {

    	 DefaultCamelContext ctxt = new DefaultCamelContext();
         ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
         activeMQComponent.setTrustAllPackages(true);
         ctxt.addComponent("activemq", activeMQComponent);

        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	   //read call centre orders from file ->process->send to topic
            	   from("file:orders?noop=false")
            	 
                   .split(body().tokenize("\n"))
                   //ADAPTER/TRANSLATOR
                   .enrich("direct:enrich", new EnrichAggregation())
                   //TO DO: CONTENT ENRICHER -> ADD total num items AND OrderID
                   .to("activemq:topic:ORDER");	//PUB&SUB
            	   
            	   //reads webOrders from queue->send to topic
            	   from("activemq:queue:webOrder")
                   //ADAPTER/TRANSLATOR
            	   .process(WebAdapterTranslator)
                   //TO DO: CONTENT ENRICHER -> ADD total num items AND OrderID
                   .to("activemq:topic:ORDER");	//PUB&SUB
            	   
            	   //enricher
            	   from("direct:enrich")
            	   .process(callAdapterTranslator);
            	   
            	   //read orders from bill and inv queue->send to result
            	   from("activemq:queue:BILL_INV_ORDER") 
            	   			//AGGREGATOR: sets validation result
	                       .aggregate(header("JMSCorrelationID"),new ResultAggregation())
	                       .completionSize(2)
	                       .to("activemq:queue:RESULTORDER");
            	   
            	   //read from result queue->appropriate list
            	   from("activemq:queue:FINISHEDORDER")
            	   		//CONTENT BASED ROUTER: differentiates between valid and unvalid results
            	   		.choice()
            	   		.when(header("validOrder").isEqualTo("true"))
            	   			.log("the Order is valid")
            	   			.to("file:validOrders?noop=false")
            	   		.otherwise()
            	   			.log("the Order is not valid")
            	   			.to("file:unvalidOrders?noop=false")
            	   .end();
            }
        };

        ctxt.addRoutes(route);

        ctxt.start();
        System.in.read();
        ctxt.stop();
    }
}
