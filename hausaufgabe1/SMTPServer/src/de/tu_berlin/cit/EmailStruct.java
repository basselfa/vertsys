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

//Imports for file operations
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;



public class EmailStruct {


    private static final String TERMINATOR = "\r\n.\r\n";
    private static final boolean COMPLETE = true;

    public static final String [] theHailMaryArray = {"HELO", "MAIL FROM:", "RCPT TO:", "DATA", "QUIT"};

    public static final int NEW = 0;
    public static final int HELO = 1;
    public static final int MAILFROM = 2;
    public static final int RCPTO = 3;
    public static final int DATA = 4;
    public static final int QUIT = 5;

    public static final int HELP = 6;
    public static final int MESS = 7;
    public static final int ERROR = 8;



    private int state = NEW;
    private String hostName = null;
    private String sender = null;
    private String[] receiver ; 
    private String data = "";

    public int getState(){
        return state;
    }

    public String getHostName(){
        return hostName;
    }

    public String getSender(){
        return sender;
    }

    public String[] getReceiver(){
        return receiver;
    }

    public String getData(){
        return data;
    }





    // Returns true if parseInput is complete for a process (QUIT was received)
    public boolean parseInput(SelectionKey key, String inputString, SMTPServer myServer){

        // make a copy of the last struct in the keyAttachment -- Ich konnte es nicht anders machen.
        EmailStruct[] emailStructArray = (EmailStruct []) key.attachment();
        EmailStruct emailStruct = emailStructArray[emailStructArray.length - 1];
        
        
        if (emailStruct.state == DATA){
            emailStruct.data.concat(inputString);
            // make sure  all data has been recieved
            if(emailStruct.data.length() >= TERMINATOR.length()) {
                if(TERMINATOR.equals(emailStruct.data.substring(emailStruct.data.length() - 3))) {
                    emailStruct.state = MESS;
                }
            }
            // Transaction with client is not over, return not complete.
            return !COMPLETE;

        }else{

            int state = NEW;

            for(int i=0 ; i < theHailMaryArray.length ; i++){
                if(inputString.length() < theHailMaryArray[i].length())
                    continue;
                
                if(inputString.startsWith(theHailMaryArray[i])){
                    //changing hail mary
                    state = i+1;
                }
            }

            
            switch(state){
                case HELO:
                    //THIS A DRAFT OF THE CODE ONLY !!!                
                    //TO DO: Extract host name + Verify format
                    String hostName = inputString.substring(theHailMaryArray[HELO-1].length()); 
                    if(hostName.charAt(0) != '<' || hostName.charAt(hostName.length() - 1) != '>'){
                        //error
                    }
                    
                    //TO DO: Save to structute
                    emailStruct.hostName = hostName.substring(1, hostName.length()-2);

                    //TO DO: Send Ack 
                    SMTPServer.sendMessages(HELO,key.channel());
                    //TOver DO: Update state 
                    break;
                    
                case MAILFROM:
                    //code block
                    break;
                case RCPTO:
                    //code block
                    break;
                case DATA:
                    //code block
                    break;
                case QUIT:
                    //code block
                    break;
                case HELP:
                    //code block
                    break;
                default:
                    //code block

            }
            
        }

        


        return false;
    }

}