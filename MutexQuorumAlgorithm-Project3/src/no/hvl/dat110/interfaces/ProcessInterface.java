package no.hvl.dat110.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import no.hvl.dat110.mutexprocess.Message;

public interface ProcessInterface extends Remote {
	
	public int getProcessID() throws RemoteException;
	
	public Message onMessageReceived(Message message) throws RemoteException;
	
	public boolean majorityAcknowledged() throws RemoteException;
	
	public void incrementclock() throws RemoteException;
	
	public boolean requestWriteOperation(Message message) throws RemoteException;
	
	public boolean requestReadOperation(Message message) throws RemoteException;
	
	public void acquireLock() throws RemoteException;
	
	public void releaseLocks() throws RemoteException;
	
	public int getVersion() throws RemoteException;
	
	public void setVersion(int version) throws RemoteException;
	
	public String getFilename() throws RemoteException;
	
	public void onReceivedUpdateOperation(Message message) throws RemoteException;

	public void onReceivedVotersDecision(Message message) throws RemoteException;

	public void multicastUpdateOrReadReleaseLockOperation(Message message) throws RemoteException;

	public void multicastVotersDecision(Message message) throws RemoteException;

}
