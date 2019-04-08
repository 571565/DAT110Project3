package no.hvl.dat110.chordoperations;

/**
 * @author tdoy
 * dat110 - demo/exercise
 */

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Hash;
import no.hvl.dat110.util.Util;

public class JoinRing {
	
	private ChordNodeInterface chordnode;

	public JoinRing(ChordNodeInterface chordnode) throws RemoteException {
		this.chordnode = chordnode;
	}
	
	public void join() throws RemoteException {
		
		//ChordNodeInterface node = chordnode;
		chordnode.setPredecessor(null);					// set predecessor to nil - No predecessor for now
				
		Registry registry = Util.tryIPs();			// try the trackers IP addresses

		if(registry != null) {
			try {
				String haship = Hash.hashOf(Util.activeIP).toString();
				//String haship = Hash.customHash(Util.activeIP).toString();

				ChordNodeInterface randomNode = (ChordNodeInterface) registry.lookup(haship);
				
				System.out.println("JoinRing-randomNode = "+randomNode.getNodeIP());
				// call remote findSuccessor function. The result will be the successor of this randomNode
				ChordNodeInterface chordnodeSuccessor = randomNode.findSuccessor(chordnode.getNodeID());
				
				// insert this node between randomNode and randomNodeSuccessor
				chordnode.setSuccessor(chordnodeSuccessor);	
				
				// notify chordnodeSuccessor of a new predecessor
				chordnodeSuccessor.notifySuccessor(chordnode);
				
				System.out.println(chordnode.getNodeIP()+" is between "+randomNode.getNodeIP()+" | "+chordnodeSuccessor.getNodeIP());			
				
			} catch (Exception e) {
				// if it fails create a new ChordRing
				// e.printStackTrace();
				createRing(chordnode);
			}
		} else {
			
			createRing(chordnode);		// no node is available, create a new ring
		}
	}
	
	private void createRing(ChordNodeInterface node) throws RemoteException {
		
		// set predecessor to nil - No predecessor for now
		node.setPredecessor(null);
		
		// set the successor to itself
		node.setSuccessor(node);
		
		System.out.println("New ring created. Node = "+node.getNodeIP()+" | Successor = "+node.getSuccessor().getNodeIP()+
				" | Predecessor = "+node.getPredecessor());
		
	}

}
