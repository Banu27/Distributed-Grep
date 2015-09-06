package edu.uiuc.cs425;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

/** This class is responsible for creating the service impl class and 
 * bind it to a specific port **/
public final class CommServer {

	private int    						m_nPort;
	private DistGrepServiceImpl         m_oImpl;
	private TServer 					m_oServer;
	public CommServer(int nPort)
	{
		this.m_nPort 	= nPort; 
		m_oImpl			= null;
		m_oServer       = null;
	}
	
	public int Initialize()
	{
		try 
		{
			TNonblockingServerTransport transport = new TNonblockingServerSocket(m_nPort);
			TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);
			args.transportFactory(new TFramedTransport.Factory());
			args.protocolFactory(new TBinaryProtocol.Factory());
			
			m_oImpl = new DistGrepServiceImpl();
			
			args.processor(new DistributedGrep.Processor(m_oImpl));
			
			args.selectorThreads(Constants.NETWORK_THREAD_COUNT);
			args.workerThreads(Constants.WORKER_THREAD_COUNT);
			
			m_oServer = new TThreadedSelectorServer(args);
			
		} catch (TTransportException e) {
			e.printStackTrace();
			return Constants.FAILURE;
		}
		return Constants.SUCCESS;
	}
	
	// this might require threading to avoid blocking the current thread
	public void StartService()
	{
		m_oServer.serve();
	}
	
	
}
