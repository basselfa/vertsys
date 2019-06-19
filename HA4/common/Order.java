import java.io.Serializable;

public class Order implements Serializable {
    private String customerID;
    private String firstName;
    private String lastName;
    private String overallItems;
    private String numberOfSurfboards;
    private String numberOfDivingSuits;
    private String orderID;
    private String valid;
    private String validationResult;

    //TO DO: after enrich --> String orderID will be deleted from the order Constructor 
    public Order(String customerID, String firstName, String lastName, String numberOfSurfboards, String numberOfDivingSuits,String orderID){
        this.orderID= orderID;
    	this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numberOfSurfboards = numberOfSurfboards;
        this.numberOfDivingSuits = numberOfDivingSuits;
    }



    public Order(String customerID, String firstName, String lastName, String overallItems, String numberOfSurfboards, String numberOfDivingSuits, String orderID, String valid, String validationResult) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.overallItems = overallItems;
        this.numberOfDivingSuits = numberOfDivingSuits;
        this.numberOfSurfboards = numberOfSurfboards;
        this.orderID = orderID;
        this.valid = valid;
        this.validationResult = validationResult;
    }


    @Override
    public String toString() {
        return "Order{" +
                "customerID='" + customerID + '\'' +
                "firstName='" + firstName + '\'' +
                "lastName='" + lastName + '\'' +
                "overallItems='" + overallItems + '\'' +
                "numberOfSurfboards='" + numberOfSurfboards + '\'' +
                "numberOfDivingSuits='" + numberOfDivingSuits + '\'' +
                "orderID='" + orderID + '\'' +
                "valid='" + valid + '\'' +
                ", validationResult=" + validationResult +
                '}';
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
   
    public String getOverallItems() {
        return overallItems;
    }

    public String getNumberOfSurfboards() {
        return numberOfSurfboards;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getValid() {
        return valid;
    }
   

    public String getValidationResult() {
        return validationResult;
    }

    public String getNumberOfDivingSuits() {
        return numberOfDivingSuits;
    }

    public void setOverallItems(String overallItems) {
        this.overallItems = overallItems;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
    public void setValidResult(String valid) {
        this.validationResult=valid;
       
    }
}
