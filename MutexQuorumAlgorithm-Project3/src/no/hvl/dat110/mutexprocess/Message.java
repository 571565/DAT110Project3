package no.hvl.dat110.mutexprocess;

import java.io.Serializable;
import java.rmi.RemoteException;


public class Message implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private int clock;
	private int processID;
	private String processStubName;
	private OperationType optype;
	private boolean acknowledged = false;
	
	private String filename;
	private String newcontent;
	private int version;
	
	public Message() throws RemoteException {
		super();
	}

	public int getClock() {
		return clock;
	}
	
	public void setClock(int clock) {
		this.clock = clock;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public OperationType getOptype() {
		return optype;
	}

	public void setOptype(OperationType optype) {
		this.optype = optype;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public String getProcessStubName() {
		return processStubName;
	}

	public void setProcessStubName(String processStubName) {
		this.processStubName = processStubName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getNewcontent() {
		return newcontent;
	}

	public void setNewcontent(String newcontent) {
		this.newcontent = newcontent;
	}
	
}
