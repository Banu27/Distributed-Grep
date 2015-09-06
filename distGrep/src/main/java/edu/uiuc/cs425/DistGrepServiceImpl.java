package edu.uiuc.cs425;

import org.apache.thrift.TException;

import edu.uiuc.cs425.DistributedGrep.Iface;

public class DistGrepServiceImpl implements Iface {

	public void startProcessing(String pattern) throws TException {
		// TODO Auto-generated method stub

	}

	public boolean isAlive() throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getProgress() throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void doneProcessing() throws TException {
		// TODO Auto-generated method stub
		return;
	}

}
