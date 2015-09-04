package edu.uiuc.cs425;
import org.apache.thrift.TException;
import edu.uiuc.cs425.ClientInitializer;

public class DistributedGrepClient {
        
    public static void main(String [] args) {
        
        //Initialize one by one?
        
       ClientInitializer a = new ClientInitializer("localjost");
        a.client.StartProcessing(a, args);
        a.clientClose();
    }

}

