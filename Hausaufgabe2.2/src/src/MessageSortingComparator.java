package src;

import java.util.Comparator;

public class MessageSortingComparator implements Comparator<Message> { 
	  
    @Override
    public int compare(Message message1, Message message2) { 

        // for comparison 
        int TimeCompare = message1.getTimeOfSenderThread() - message2.getTimeOfSenderThread(); 
        int IDCompare = message1.getId() - message2.getId();
        
        // 2-level comparison using if-else block 
        if (TimeCompare == 0) { 
            return ((IDCompare == 0) ? TimeCompare : IDCompare); 
        } else { 
            return TimeCompare; 
        } 
    }
}








