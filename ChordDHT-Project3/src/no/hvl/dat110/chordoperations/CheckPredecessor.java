package no.hvl.dat110.chordoperations;

/**
 * @author tdoy
 * dat110 - demo/exercise
 */
import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import no.hvl.dat110.node.Node;
import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Util;

public class CheckPredecessor extends Thread {
	
	private Node node;
	
	
	public CheckPredecessor(Node node) {
		this.node = node;
	}
	
	public void run() {
		
		while(true) {			
			try {
				checkpred();
				Thread.sleep(1000);
			} catch (InterruptedException | RemoteException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void checkpred() throws RemoteException {
		System.out.println("Checking the predecessor for Node: "+node.getNodeIP());
		BigInteger predID = null;
		
		try {
			predID = node.getPredecessor().getNodeID();
			Registry registry = Util.locateRegistry(node.getPredecessor().getNodeIP());
			ChordNodeInterface predNode = (ChordNodeInterface) registry.lookup(predID.toString());
			if(predNode == null) {
				node.setPredecessor(null);		// object not available remove predecessor
				//System.out.println("predecessor for Node: "+node.getNodeIP()+" = "+node.getPredecessor());
				return;
			}

		} catch (NullPointerException | RemoteException | NotBoundException e) {		
			node.setPredecessor(null);		// if error occurs - predecessor can't be reached.. set the reference to null
			//e.printStackTrace();
		}

	}
}
