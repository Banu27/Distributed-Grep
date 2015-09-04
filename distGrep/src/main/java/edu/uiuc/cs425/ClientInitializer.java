package edu.uiuc.cs425;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class ClientInitializer {

	TTransport transport;
public	DistributedGrep.Client client;

 public	ClientInitializer(String args)
	{
		try {
			transport = new TSocket(args, 9090);
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			client = new DistributedGrep.Client(protocol);
		} catch (TException x) {
			x.printStackTrace();
		}
		
	}
	
	void clientClose()
	{
		transport.close();
	}
}

