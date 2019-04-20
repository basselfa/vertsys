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



public class SMTPServerInput {

    public static final int NEW = 0;
    public static final int HELO = 1;
    public static final int MAILFROM = 2;
    public static final int RCPTTO = 3;
    public static final int DATA = 4;
    public static final int QUIT = 5;
    public static final int HELP = 6;
    public static final int MESS = 7;
    public static final int ERROR = 8;

    private int lastState = NEW; // describes the last fully completed task
    private int newState = NEW; // describes the task discovered by the parser



}