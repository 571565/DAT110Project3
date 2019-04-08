package no.hvl.dat110.chordoperations;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Set;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Util;

/**
 * @author tdoy
 *
 */
public class LeaveRing extends Thread {
	
	/**
	 * When a node n leaves, it should notify its successor s and hand over all its keys to s. 
	 * At this point, s must set its predecessor ps to the predecessor pn of n.
	 * n should also inform its predecessor pn so that it can update its references. 
	 * At this point, pn must set its successor pointer to s. 
	 * In both cases, we assume that n sends its predecessor to s, and the last node in its successor list to p.
	 */
	
	private ChordNodeInterface chordnode;
	private long ttl;
	private boolean loopforever;
	
	public LeaveRing(ChordNodeInterface chordnode, long ttl, boolean loopforever) {
		this.chordnode = chordnode;
		this.ttl = ttl;
		this.loopforever = loopforever;
	}
	
	public void run() {
		while (true) {
			
			try {
				Thread.sleep(ttl);
				if(!loopforever)
					updatesandleave();
			}catch(InterruptedException | RemoteException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void updatesandleave() throws RemoteException {
		System.out.println("Leaving the ring...");
		System.out.println("Attempting to update successor and predecessor before leaving the ring...");

		ChordNodeInterface prednode = chordnode.getPredecessor();				// get the predecessor
		
		ChordNodeInterface succnode = chordnode.getSuccessor();					// get the successor
		
		ChordNodeInterface prednodestub = Util.registryHandle(prednode);		// get the prednode stub
		
		ChordNodeInterface succnodestub = Util.registryHandle(succnode);		// get the succnode stub
		
		Set<BigInteger> keyids = chordnode.getFileKey();						// get the keys for chordnode
		
		
		if(succnodestub != null) {
			for(BigInteger keyid : keyids) {
				succnodestub.addToFileKey(keyid); 								// add chordnode's keys to its successor's
			}
			succnodestub.setPredecessor(prednodestub); 							// set prednode as the predecessor of succnode
		}
		if(prednodestub != null) {
			prednodestub.setSuccessor(succnodestub);							// set succnode as the successor of prednode			
		} 
		
		System.exit(0);
	}
	
}
