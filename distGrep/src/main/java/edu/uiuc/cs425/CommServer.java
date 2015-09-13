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
public final class CommServer implements Runnable{

	private int    						m_nPort;
	private DistGrepServiceImpl         m_oImpl;
	private TServer 					m_oServer;
	private int							m_nNodeIndex;
	private Thread 						m_oSerThread;
	
	
	public CommServer(int nPort, int nNodeID)
	{
		m_nPort 		= nPort; 
		m_oImpl			= null;
		m_oServer       = null;
		m_nNodeIndex    = nNodeID;
		m_oSerThread	= null;
	}
	
	public void setMasterProxy(CommClient masterProxy) {
		
		m_oImpl.setMasterProxy(masterProxy);		
	}
	
	public void setControllerProxy(Controller controllerProxy) {
		
		m_oImpl.setControllerProxy(controllerProxy);
		
	}
	
	public void setSearchDir(String path)
	{
		m_oImpl.setSearchDir(path);
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
			m_oImpl.setNodeID(m_nNodeIndex);
			args.processor(new DistributedGrep.Processor(m_oImpl));
			
			args.selectorThreads(Commons.NETWORK_THREAD_COUNT);
			args.workerThreads(Commons.WORKER_THREAD_COUNT);
			
			m_oServer = new TThreadedSelectorServer(args);
			
		} catch (TTransportException e) {
			e.printStackTrace();
			return Commons.FAILURE;
		}
		return Commons.SUCCESS;
	}
	
	// this might require threading to avoid blocking the current thread
	public void StartService()
	{
		m_oSerThread = new Thread(this);
		m_oSerThread.start();
	}
	
	public void WaitForServiceToStop()
	{
		try {
			m_oSerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		m_oServer.serve();		
	}
}
