package edu.uiuc.cs425;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import edu.uiuc.cs425.DistributedGrep.Iface;



/** This class implements the proxy to the thrift service "DistributedGrep"**/
public final class CommClient implements Iface {
	
	
	private String 						m_sIP;
	private int    						m_nPort;
	private DistributedGrep.Client      m_oProxy;
	private TSocket 					m_oTransport;
	
	
	public CommClient(String sIP, int nPort)
	{
		this.m_sIP 		= sIP;
		this.m_nPort 	= nPort; 
		this.m_oProxy   = null;
	}
	
	/** The Initialize() method is expected to be called by all the proxy objects
	 *  before any service calls are made. The methods establishes the connection
	 *  with the remote server. 
	 *  Returns Constants.SUCCESS on success and Constants.FAILURE on failure**/
	public int Initialize()
	{
		// thrift calls
		try
	    {
		  m_oTransport = new TSocket(m_sIP, m_nPort);
		  m_oTransport.open();
	      TProtocol protocol = new TBinaryProtocol(m_oTransport);
	      m_oProxy = new DistributedGrep.Client(protocol);     
	      
	    }  
	    catch (TException e)
	    {
	      e.printStackTrace();
	      return Constants.FAILURE;
	    }
		System.out.println("Connection established with " + m_sIP + ":" + Integer.toString(m_nPort));
		return Constants.SUCCESS;
	}
	
	/** The Close() method is called once all the required operations 
	 * are done and the proxy to the service is not needed anymore 
	 * **/
	public void Close()
	{
		try
		{
			m_oTransport.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Connection closed with " + m_sIP + ":" + Integer.toString(m_nPort));
	}
	

	// ALL the methods below act as wrappers to the actual RPC calls 
	// they just forward the call to the thrift proxy object
	public void startProcessing(String pattern) throws TException {
		
		try
		{
			m_oProxy.startProcessing(pattern);
		}
		catch (TException e)
		{
			e.printStackTrace();
		}

	}

	public boolean isAlive() throws TException {
		boolean ret = false;
		try
		{
			ret = m_oProxy.isAlive();
		}
		catch (TException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	// redo the method signature
	public int getProgress() throws TException {
		int ret = 0;
		try
		{
			ret = m_oProxy.getProgress();
		}
		catch (TException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public void doneProcessing() throws TException {
		
		try
		{
			m_oProxy.doneProcessing();
		}
		catch (TException e)
		{
			e.printStackTrace();
		}
	}

}
