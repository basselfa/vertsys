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



public class EmailStruct {



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
    //######################  The actual class  #########################
    //###################################################################
    private int state = NEW;
  
    private String sender = null;
    private String receiver ; 
    private String data = "";


    // getters
    public int getState(){
        return state;
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

    
    // setters

    public int setState(int x){
        return state=x;
    }

    public void setSender(String newValue){
        this.sender = newValue;
    }
    
    public void setReceiver(String newValue){
        this.receiver = newValue;
    }

    public void setData(String newValue){
        this.data = newValue;
    }

}
