package no.hvl.dat110.rpc.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import no.hvl.dat110.node.Message;

public interface MutexInterface extends Remote {
	
	public Message onMessageReceived(Message message) throws RemoteException;
	
	public boolean majorityAcknowledged() throws RemoteException;
	
	public void incrementclock() throws RemoteException;
	
	public boolean requestWriteOperation(Message message) throws RemoteException;
	
	public boolean requestReadOperation(Message message) throws RemoteException;
	
	public void acquireLock() throws RemoteException;
	
	public void releaseLocks() throws RemoteException;
	
	public void onReceivedVotersDecision(Message message) throws RemoteException;

	public void multicastUpdateOrReadReleaseLockOperation(Message message) throws RemoteException;

	public void multicastVotersDecision(Message message) throws RemoteException;

	public void onReceivedUpdateOperation(Message message) throws RemoteException;
	
	public void setActiveNodesForFile(Set<Message> messages) throws RemoteException;

}
