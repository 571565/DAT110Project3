package no.hvl.dat110.node;

import java.io.Serializable;
import java.math.BigInteger;
import java.rmi.RemoteException;

public class Message implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private int clock;
	private BigInteger nodeID;
	private String nodeIP;
	private OperationType optype;
	private boolean acknowledged = false;
	private String filepath;
	
	private BigInteger filename;
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

	public BigInteger getNodeID() {
		return nodeID;
	}

	public void setNodeID(BigInteger nodeID) {
		this.nodeID = nodeID;
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

	public String getNodeIP() {
		return nodeIP;
	}

	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public BigInteger getFilename() {
		return filename;
	}

	public void setFilename(BigInteger filename) {
		this.filename = filename;
	}

	public String getNewcontent() {
		return newcontent;
	}

	public void setNewcontent(String newcontent) {
		this.newcontent = newcontent;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}
