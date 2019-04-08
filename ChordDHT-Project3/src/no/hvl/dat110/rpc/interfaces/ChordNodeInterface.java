package no.hvl.dat110.rpc.interfaces;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.hvl.dat110.node.Message;

public interface ChordNodeInterface extends Remote, MutexInterface {
	
	public BigInteger getNodeID() throws RemoteException;
	
	public void setNodeID(BigInteger nodeID) throws RemoteException;
	
	public String getNodeIP() throws RemoteException;

	public void setNodeIP(String nodeIP) throws RemoteException;

	public ChordNodeInterface getSuccessor() throws RemoteException;
	
	public void setSuccessor(ChordNodeInterface successor) throws RemoteException;
	
	public ChordNodeInterface getPredecessor() throws RemoteException;
	
	public void setPredecessor(ChordNodeInterface predecessor) throws RemoteException;
	
	public List<ChordNodeInterface> getFingerTable() throws RemoteException;
	
	public void addToFingerTable(ChordNodeInterface finger) throws RemoteException;
	
	public void removeFromFingerTable(ChordNodeInterface finger) throws RemoteException;
	
	public void setFingerTable(List<ChordNodeInterface> fingerTable) throws RemoteException;

	public Set<BigInteger> getFileKey() throws RemoteException;
	
	public void addToFileKey(BigInteger fileKey) throws RemoteException;
	
	public void removeFromFileKey(BigInteger fileKey) throws RemoteException;
	
	public ChordNodeInterface findSuccessor(BigInteger keyID) throws RemoteException;
	
	public void notifySuccessor(ChordNodeInterface node) throws RemoteException;
	
	public Map<BigInteger, Message> getFilesMetadata() throws RemoteException;
	
	public void createFileInNodeLocalDirectory(String srcfile, BigInteger destID) throws RemoteException;
	
}
