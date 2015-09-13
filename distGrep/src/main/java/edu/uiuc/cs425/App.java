package edu.uiuc.cs425;

/**
 * Hello world!
 *
 */
public class App 
{
	
    public static void main( String[] args ) throws InterruptedException
    {
        int nodeID = Integer.parseInt(args[0]);
        String searchDir = args[1];
         
        Controller controller  = new Controller(nodeID,searchDir);
        
        if(controller.StartServer() == Commons.FAILURE)
        {
        	System.out.println("Failed to bring the distributed grep service up. Shutting down ...");
        	System.exit(Commons.FAILURE);
        }
        
        if(Commons.FAILURE == controller.SetupConnections())
        {
        	System.out.println("Connection amoung VMs failed, Shutting down ...");
        	System.exit(Commons.FAILURE);
        }
        
        
        if(Commons.FAILURE == controller.startGrep())
        {
        	System.out.println("Failed to start grepping, Shutting down ...");
        	System.exit(Commons.FAILURE);
        }
        
        controller.WaitForServiceToStop();
        
        controller.CloseClients();
        
    }
}
