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
         
        Controller controller  = new Controller(nodeID);
        
        controller.startGrep();
        
        controller.WaitForServiceToStop();
        
        controller.CloseClients();
        
    }
}
