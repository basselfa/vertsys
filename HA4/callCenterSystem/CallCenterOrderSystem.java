import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
 

public class CallCenterOrderSystem {
	
	
    public static void main(String[] args) throws Exception {
//    	if(args.length == 0) {
//			System.out.println("wrong arguments");
//			return;
//		}
    	   Boolean counter=true;
    	   int numCreatedFiles=0;
    	   List<String> lines = Arrays.asList("1,Bib Test,0,1");
    	   List<String> lines2 = Arrays.asList("2,Bla Blu,1,2");
    	   while(true) {
    		   numCreatedFiles++;
	           Path file = Paths.get("orders/order"+numCreatedFiles);
	           try {
	        	   if(counter==true) {
	        		   Files.write(file, lines, Charset.forName("UTF-8"));
	        		   counter=false;
	        	   }
	        	   else {Files.write(file, lines2, Charset.forName("UTF-8"));
	        	   		counter=true;}
	               System.out.println("New Order created");
	               Thread.sleep(10000);
	            
	         
	               
	           }catch (Exception e){
	               System.out.println(e.toString());
	           }
    	   }
     
      
    }

}
