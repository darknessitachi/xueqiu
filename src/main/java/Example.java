import java.util.HashMap;
import java.util.Map;

public class Example {  
  
	public static Map<Integer,Boolean> test =   
	        new HashMap<Integer, Boolean>(); 
	
	public static Example example = new Example();  
      
	 
	
	
      
    private Example()  
    {  
        test.put(1, true);  
    }  
      
    public static Example getInstance()  
    {  
        return example;  
    }  
}  