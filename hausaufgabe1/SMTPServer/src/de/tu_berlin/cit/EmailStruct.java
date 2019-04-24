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


//Imports for file operations
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;



public class EmailStruct {


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


    private boolean sentAckFlag = false ;
    private int state = NEW;
    private boolean helpFlag = false; 
    private String hostName = null;
    private String sender = null;
    private String receiver ; 
    private String data = "";

    public int getState(){
        return state;
    }
    public int setState(int x){
        return state=x;
    }

    public String getHostName(){
        return hostName;
    }

    public String getSender(){
        return sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public String getData(){
        return data;
    }

    public boolean getHelpFlag(){
        return helpFlag;
    }
    public void deactivateHelpFlag(){
        this.helpFlag= false;
    }
    public boolean getAckFlag(){
        return sentAckFlag;
    }
    public void setAckFlag(){
        this.sentAckFlag= true;
    }



    // Returns true if parseInput is complete for a process (QUIT was received)
    public static boolean parseInput(SelectionKey key, String inputString){

        // copy of the myEmailStruct (siehe Server code) to avoid repeated casting for each of the cases (HELO, DATA,..)
        List<EmailStruct> emailStructArray = (ArrayList<EmailStruct>) key.attachment();
        // TODO throw and catch needed to verify casting type
        EmailStruct emailStruct = emailStructArray.get(emailStructArray.size()-1);


        
        if (emailStruct.state == DATA){
            emailStruct.data = emailStruct.data + inputString;
            System.out.println("Message is getting received");
            // make sure  all data has been received
           
            System.out.println("data length is: "+emailStruct.data.length() + "Terminator length is: "+ TERMINATOR.length());
            if(emailStruct.data.length() >= TERMINATOR.length()) { // avoid error in the  substring method
           //     if(TERMINATOR.equals(emailStruct.data.substring(emailStruct.data.length() - 3))) {
            	System.out.println("Got into first if");
            	if(emailStruct.data.endsWith(TERMINATOR)) {
                    emailStruct.state = MESS; // State after getting the complete massage 
                    emailStruct.sentAckFlag = false ;
                    System.out.println("Message fully received.");
                }
            }

            // update attachment
            ((List<EmailStruct>) key.attachment()).set(emailStructArray.size()-1, emailStruct);

            // Transaction with client is not over, return not complete.
            return !COMPLETE;

        }else{

            int state = NEW;
            emailStruct.sentAckFlag = false ;

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
                    if(emailStruct.state != NEW){
                        // ERROR - Wrong use of protocol: Timing of HELO.
                        System.out.println("Wrong use of protocol: HELO is only sent at the start of a connection.");
                        emailStruct.state = ERROR;
                        return COMPLETE;
                    }
                    
                    // Extract host name + Verify format
                    String hostName = inputString.substring(theHailMaryArray[HELO-1].length()); 
                    
                    
                    
                 // The Format for HostName does not include "<" or ">"
                    if(hostName.length() <= theHailMaryArray[HELO-1].length()+1 ){
                        // ERROR - Wrong host format.
                        System.out.println("Wrong use of format: Wrong message format of HELO");
                        emailStruct.state = ERROR;
                        return COMPLETE;
                    }
                    
                    // Save to structure
                    emailStruct.hostName = hostName;
                    System.out.println("hostName in emailStruct"+ emailStruct.hostName); 
                    
                    
                    // Update state
                    emailStruct.state = HELO; 
                    emailStruct.sentAckFlag = false ;

                    //update attachment
                    ((List<EmailStruct>) key.attachment()).set(emailStructArray.size()-1, emailStruct);
                    
                    break;
                    
                case MAILFROM:
                    //code block
                    // check if a new message variable is needed and if current state coresponds to that expected from protocol.
                    if(emailStruct.state == MESS){
                        emailStruct = new EmailStruct();
                        emailStruct.hostName = emailStructArray.get(0).hostName;
                    }else if(emailStruct.state != HELO){
                        // ERROR - Wrong use of protocol: Timing of MAIL FROM.
                        System.out.println("Wrong use of protocol: MAIL FROM is only sent after HELO.");
                        emailStruct.state = ERROR;
                        return COMPLETE;
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
                    emailStruct.sender = sender; //.substring(0, sender.length()-1);
                    System.out.println("Sender: " + emailStruct.sender);
                    emailStruct.state = MAILFROM;
                    emailStruct.sentAckFlag = false ;
                    break;

                case RCPTO:
                    //code block
                    
                    // check if current state coresponds to that expected from protocol.
                    if(emailStruct.state != MAILFROM && emailStruct.state != HELO){
                        // ERROR - Wrong use of protocol: Timing of RCPTO.
                        System.out.println("Wrong use of protocol: \"RCPT TO:\" is only sent after MAIL FROM.");
                        emailStruct.state = ERROR;
                        return COMPLETE;
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
                    emailStruct.receiver = receiver; //.substring(1, receiver.length()-2);
                    System.out.println("receiver: " + emailStruct.receiver);
                    emailStruct.state = RCPTO;
                    emailStruct.sentAckFlag = false ;
                    break;

                case DATA:
                    //code block
                    // Confirm correctness of format
//                    if (!theHailMaryArray[DATA-1].equals(inputString)){
                    if (!inputString.equals("DATA\r\n")){
                    	throw new RuntimeException("Wrong format for DATA.");                                                                    
                    }

                    // Confirm correctness of state
                    if (emailStruct.state != RCPTO){
                        System.out.println("Wrong use of format: DATA can only be sent after \"RCPT TO.\"");
                        emailStruct.state = ERROR;
                        return COMPLETE;
                    }

                    // update status
                    System.out.println("DATA command was received...");
                    emailStruct.state = DATA;
                    emailStruct.sentAckFlag = false ;
                    break;

                case QUIT:
                    //code block
                    // Confirm correctness of format
//                    if (!theHailMaryArray[QUIT-1].equals(inputString)){
                	if (inputString.equals("QUIT")) {
                		throw new RuntimeException("Wrong format for QUIT.");                                                                    
                    }

                    // Confirm correctness of message in regards to state
                    if (emailStruct.state != MESS){
                        System.out.println("Wrong use of format: QUIT can only be sent after a message was completely sent.");
                        emailStruct.state = ERROR;
                        return COMPLETE;
                    }

                    // update state
                    emailStruct.state = QUIT;
                    emailStruct.sentAckFlag = false ;
                    // return complete to signal end of connection.
                    return COMPLETE;
                    
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
                    emailStruct.helpFlag = true;
                    break;

                default:
                    //code block
                    System.out.println("Mistake in Logic.  How did it all go so wrong?");
                    emailStruct.state = ERROR;
                    return COMPLETE;

            }
            
        }

        return !COMPLETE;
    }
