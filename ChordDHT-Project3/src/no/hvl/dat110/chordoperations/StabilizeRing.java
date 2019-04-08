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


public class StabilizeRing extends Thread {
	
	private Node node;
	
	public StabilizeRing(Node node) {
		this.node = node;
	}
	
	public void run() {
		while (true) {
			
			try {
				stabilize();
				Thread.sleep(1000);
			}catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void stabilize() throws RemoteException, NotBoundException {
		
		System.out.println("Stabilizing ring from "+node.getNodeIP()+"...");
		ChordNodeInterface succ = node.getSuccessor();						// get the successor of node
		
		ChordNodeInterface succnode = null;
		ChordNodeInterface predsucc = null;
		Registry registry = Util.locateRegistry(succ.getNodeIP());

		if(registry == null)
			return;
		
		succnode = (ChordNodeInterface) registry.lookup(succ.getNodeID().toString());	// confirm the successor is alive
		predsucc = succnode.getPredecessor(); 							// get the predecessor of the successor of this node
		
		BigInteger nodeID = node.getNodeID();
		BigInteger succID = succnode.getNodeID();

		BigInteger predsuccID = null;
		
		if(predsucc != null) {
			predsuccID = predsucc.getNodeID();
			
			if(predsuccID.compareTo(node.getNodeID())==0)			// this is important for 2 ring members - if (predsucc(node) == node)
				return;

			//predID.compareTo(nodeID)==1 && predID.compareTo(succID)==-1
			boolean cond = Util.computeLogic(predsuccID, nodeID.add(new BigInteger("1")), succID.add(new BigInteger("1")));
			//System.out.println(cond+" = "+predID+" is btw "+nodeID.add(new BigInteger("1"))+" and "+succID.add(new BigInteger("1")));
			if(cond) {
				node.setSuccessor(predsucc);
											
				try {
					predsucc.notifySuccessor(node);			// notify successor (predsucc) that it has a new predecessor (node)
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}	
			}
		}
		
		System.out.println("Finished stabilizing chordring from "+node.getNodeIP());
	}
}
