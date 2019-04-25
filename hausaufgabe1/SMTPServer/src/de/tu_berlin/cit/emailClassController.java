package de.tu_berlin.cit;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.lang.Object;

//Imports for file operations
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;



public class emailClassController {


    //###################################################################
    //#########################  constants  #############################
    //###################################################################
    // IMPORTANT!!! IF YOU CHANGE ANY OF THESE VALUES THEN CHANGE THEM IN ALL FILES.
    private static final String TERMINATOR = "\r\n.\r\n";
    private static final boolean COMPLETE = true;

    public static final String [] theHailMaryArray = {"HELO", "MAIL FROM:", "RCPT TO:", "DATA", "QUIT", "HELP"};

    public static final int NEW = 0;
    public static final int HELO = 1;
    public static final int MAILFROM = 2;
    public static final int RCPTO = 3;
    public static final int DATA = 4;
    public static final int QUIT = 5;
    public static final int HELP = 6;

    public static final int MESS = 7;
    public static final int ERROR = 8;

    //###################################################################





    //###################################################################
    //########################  constructor  ############################
    //###################################################################

    public emailClassController(){
        emailStructList.add(new EmailStruct());
    }

    //###################################################################







    //###################################################################
    //######################  The actual class  #########################
    //###################################################################

    private List<EmailStruct> emailStructList = new ArrayList<EmailStruct>();

    private String hostName = "";
    private boolean helpFlag = false; 
    private boolean sentAckFlag;
    private boolean complete = false;
    
    //###################################################################


    



    //###################################################################
    //##########################  setters  ##############################
    //###################################################################
   
    public void setComplete(boolean newValue){
        this.complete = newValue;
    }

    public void setHostName(String newValue){
        this.hostName = newValue;
    }

    public void deactivateHelpFlag(){
        this.helpFlag = false;
    }

    public void setAckFlag(){
        this.sentAckFlag= true;
    }

    public void setLast(EmailStruct newValue){
        int listLength = this.emailStructList.size();
        this.emailStructList.set(listLength-1, newValue);
    }

    //###################################################################





    //###################################################################
    //##########################  getters  ##############################
    //###################################################################
   
    public boolean getComplete(){
        return this.complete;
    }

    public String getHostName(){
        return this.hostName;
    }

    public boolean getHelpFlag(){
        return helpFlag;
    }

    public boolean getAckFlag(){
        return sentAckFlag;
    }

    //###################################################################





    //###################################################################
    //#######################  List manager  ############################
    //###################################################################
   
    /**
     * add new struct to back of list
     */
    public void createNewMessage(){
        emailStructList.add(new EmailStruct());
    }

    /**
     * add new struct to back of list
     */
    public void createNewMessage(EmailStruct newMessage){
        emailStructList.add(newMessage);
    }

    /**
     * pop the last struct from the list and remove it.
     * @return lastMessage the very last message in the list
     */
    public EmailStruct popLastMessage(){
        int listLength = emailStructList.size();
        if(listLength == 0){
            return null;
        }
        EmailStruct lastMessage = emailStructList.remove(listLength-1);

        return lastMessage;
    }


    /**
     * get the last struct from the list.
     * @return lastMessage the very last message in the list
     */
    public EmailStruct getLastMessage(){
        int listLength = emailStructList.size();
        if(listLength == 0){
            return null;
        }
        EmailStruct lastMessage = emailStructList.get(listLength-1);

        return lastMessage;
    }



    /** 
     * set the state of the last message
     */
    public void setLastState(int state){
        int listLength = emailStructList.size();
        EmailStruct lastMessage = emailStructList.get(listLength-1);
        lastMessage.setState(state);
        emailStructList.set(listLength-1, lastMessage);
    }

    /**
     * get the state of the last message
     * @return state current state in last message
     */
    public int getLastState(){
        int listLength = emailStructList.size();
        EmailStruct lastMessage = emailStructList.get(listLength-1);

        return lastMessage.getState();
    }
    
    //###################################################################











    //###################################################################
    //##################  parse Message and logic  ######################
    //###################################################################
    
    // Returns true if parseInput is complete for a process (QUIT was received)
    public static emailClassController parseInput(emailClassController emailClass, String inputString){

        // get message list length
        int messageLength = emailClass.emailStructList.size();
        // TODO throw and catch needed to verify casting type
        EmailStruct tempEmailStruct = emailClass.emailStructList.get(messageLength-1);


        
        if (tempEmailStruct.getState() == DATA){
            tempEmailStruct.setData(tempEmailStruct.getData() + inputString);
            System.out.println("Message is getting received");
            // make sure  all data has been received
           
            System.out.println("data length is: "+tempEmailStruct.getData().length() + "Terminator length is: "+ TERMINATOR.length());
            if(tempEmailStruct.getData().length() >= TERMINATOR.length()) { // avoid error in the  substring method
           //     if(TERMINATOR.equals(tempEmailStruct.data.substring(tempEmailStruct.data.length() - 3))) {
            	System.out.println("Got into first if");
            	if(tempEmailStruct.getData().endsWith(TERMINATOR)) {
                    tempEmailStruct.setState(MESS); // State after getting the complete massage 
                    emailClass.sentAckFlag = false ;
                    System.out.println("Message fully received.");
                }
            }

            // // update attachment
            // ((List<EmailStruct>) key.attachment()).set(emailStructArray.size()-1, emailStruct);

            // Update emailClass
            emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

        }else{

            int state = NEW;
            emailClass.sentAckFlag = false ;

            for(int i=0 ; i < theHailMaryArray.length ; i++){
                if(inputString.length() < theHailMaryArray[i].length())
                    continue;
                //TODO Verify if the new Command suits the Protocol and handle the error  
                if(inputString.startsWith(theHailMaryArray[i])){
                    //changing hail mary
                    state = i+1;
                }
            }

            
            switch(state){
                case HELO:
                    if(tempEmailStruct.getState() != NEW){
                        // ERROR - Wrong use of protocol: Timing of HELO.
                        System.out.println("Wrong use of protocol: HELO is only sent at the start of a connection.");
                        tempEmailStruct.setState(ERROR);
                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        // Transaction with client is not over, return the emailClass.
                        return emailClass;

                    }
                    
                    // Extract host name + Verify format
                    String hostName = inputString.substring(theHailMaryArray[HELO-1].length()); 
                    
                    
                    
                 // The Format for HostName does not include "<" or ">"
                    if(hostName.length() <= theHailMaryArray[HELO-1].length()+1 ){
                        // ERROR - Wrong host format.
                        System.out.println("Wrong use of format: Wrong message format of HELO");
                        tempEmailStruct.setState(ERROR);

                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);


                        emailClass.complete = true;

                        // Transaction with client is not over, return the emailClass.
                        return emailClass;

                    }
                    
                    // Save to structure
                    // tempEmailStruct.setHostName(hostName);
                    emailClass.hostName = hostName;
                    System.out.println("hostName in emailStruct"+ emailClass.hostName); 
                    
                    
                    // Update state
                    tempEmailStruct.setState(HELO); 
                    emailClass.sentAckFlag = false ;

                    // //update attachment
                    // ((List<EmailStruct>) key.attachment()).set(emailStructArray.size()-1, emailStruct);
                    
                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);


                    break;
                    
                case MAILFROM:
                    //code block
                    // check if a new message variable is needed and if current state coresponds to that expected from protocol.
                    if(tempEmailStruct.getState() == MESS){
                        
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        emailClass.createNewMessage();

                        // get message list length
                        messageLength = emailClass.emailStructList.size();

                        tempEmailStruct = emailClass.emailStructList.get(messageLength-1);
                        
                        
                        // emailStruct.hostName = emailStructArray.get(0).hostName;
                    }else if(tempEmailStruct.getState() != HELO){
                        // ERROR - Wrong use of protocol: Timing of MAIL FROM.
                        System.out.println("Wrong use of protocol: MAIL FROM is only sent after HELO.");
                        tempEmailStruct.setState(ERROR);

                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        return emailClass;
                    }

                    // Extract sender's email adress + Verify format
                    String sender = inputString.substring(theHailMaryArray[MAILFROM-1].length());
//                    if(sender.charAt(0) != '<' || sender.charAt(sender.length() - 1) != '>'){
//                        // ERROR - Wrong email format.
//                        System.out.println("Wrong use of format: Wrong message format of email-address");
//                        emailStruct.state = ERROR;
//                        return COMPLETE;
//                    }
                    
                    // Save to structure
                    tempEmailStruct.setSender(sender); //.substring(0, sender.length()-1);
                    System.out.println("Sender: " + tempEmailStruct.getSender());
                    tempEmailStruct.setState(MAILFROM);
                    emailClass.sentAckFlag = false ;

                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                    break;

                case RCPTO:
                    //code block
                    
                    // check if current state coresponds to that expected from protocol.
                    if(tempEmailStruct.getState() != MAILFROM && tempEmailStruct.getState() != HELO){
                        // ERROR - Wrong use of protocol: Timing of RCPTO.
                        System.out.println("Wrong use of protocol: \"RCPT TO:\" is only sent after MAIL FROM.");
                        tempEmailStruct.setState(ERROR);

                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        emailClass.complete = true;
                    }

                    // Extract receiver's email adress + Verify format
                    String receiver = inputString.substring(theHailMaryArray[RCPTO-1].length());
//                    if(receiver.charAt(0) != '<' || receiver.charAt(receiver.length() - 1) != '>'){
//                        // ERROR - Wrong email format.
//                        System.out.println("Wrong use of format: Wrong message format of email-address");
//                        emailStruct.state = ERROR;
//                        return COMPLETE;
//                    }
                    // Save to structure
                    tempEmailStruct.setReceiver(receiver); //.substring(1, receiver.length()-2);
                    System.out.println("receiver: " + tempEmailStruct.getReceiver());
                    tempEmailStruct.setState(RCPTO);
                    emailClass.sentAckFlag = false ;

                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                    break;

                case DATA:
                    //code block
                    // Confirm correctness of format
//                    if (!theHailMaryArray[DATA-1].equals(inputString)){
                    if (!inputString.equals("DATA\r\n")){
                    	throw new RuntimeException("Wrong format for DATA.");                                                                    
                    }

                    // Confirm correctness of state
                    if (tempEmailStruct.getState() != RCPTO){
                        System.out.println("Wrong use of format: DATA can only be sent after \"RCPT TO.\"");
                        tempEmailStruct.setState(ERROR);
                        
                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        emailClass.complete = true;

                        return emailClass;
                    }

                    // update status
                    System.out.println("DATA command was received...");
                    tempEmailStruct.setState(DATA);
                    emailClass.sentAckFlag = false ;

                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                    break;

                case QUIT:
                    //code block
                    // Confirm correctness of format
//                    if (!theHailMaryArray[QUIT-1].equals(inputString)){
                	if (inputString.equals("QUIT")) {
                		throw new RuntimeException("Wrong format for QUIT.");                                                                    
                    }

                    // Confirm correctness of message in regards to state
                    if (tempEmailStruct.getState() != MESS){
                        System.out.println("Wrong use of format: QUIT can only be sent after a message was completely sent.");
                        tempEmailStruct.setState(ERROR);

                        // Update emailClass
                        emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                        emailClass.complete = true;

                        return emailClass;

                    }

                    // update state
                    tempEmailStruct.setState(QUIT);
                    emailClass.sentAckFlag = false ;
                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                    // raise end of connection flag.
                    emailClass.complete = true;

                    return emailClass;

                case HELP:
                    //code block

                    //check format of messsage
//                    if (!theHailMaryArray[HELP-1].equals(inputString)){
                    if (inputString.equals("HELP")){
                    	System.out.println("Hail Mary: "+ theHailMaryArray[HELP-1]);
                    	System.out.println("Format in: "+ inputString);
                    	
                        throw new RuntimeException("Wrong format for HELP.");                                                                    
                    }

                    // raise help flag
                    emailClass.helpFlag = true;
                    break;

                default:
                    //code block
                    System.out.println("Mistake in Logic.  How did it all go so wrong?");
                    tempEmailStruct.setState(ERROR);

                    // Update emailClass
                    emailClass.emailStructList.set(messageLength-1, tempEmailStruct);

                    // raise end of connection flag.
                    emailClass.complete = true;


            }
            
        }

        // Transaction with client is not over, return the emailClass.
        return emailClass;

    }

    //###################################################################






    
}