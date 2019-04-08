package no.hvl.dat110.chordoperations;

/**
 * @author tdoy
 * dat110 - demo/exercise
 */

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.List;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Hash;

public class FixFingerTable extends Thread {
	
	private ChordNodeInterface chordnode;
	
	public FixFingerTable(ChordNodeInterface chordnode) {
		this.chordnode = chordnode;
	}
	
	public void run() {
		
		while(true) {
			try {
				update();
				Thread.sleep(1000);
			} catch (RemoteException | InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void update() throws RemoteException {
		
		System.out.println("Fixing the FingerTable for the Node: "+ chordnode.getNodeIP());
		int s = Hash.sbit;
		//int m = Hash.mbit;

		List<ChordNodeInterface> fingers = chordnode.getFingerTable();

		BigInteger modulos = Hash.addressSize();			// we can't go beyond our address space 2^mbit
	
		for(int i=0; i<s; i++) {

			BigInteger nextsuccID = new BigInteger("2");
			nextsuccID = nextsuccID.pow(i);
			//System.out.println("nextsuccID: "+nextsuccID);
			
			BigInteger succnodeID = chordnode.getNodeID().add(nextsuccID);
			succnodeID = succnodeID.mod(modulos);								// do succ(n + 2^(i-1)) mod 2^mbit
			
			//System.out.println("nodeID: "+chordnode.getNodeID()+" | succID: "+succnodeID);
			
			ChordNodeInterface succnode = null;
			try {
				succnode = chordnode.findSuccessor(succnodeID);
			} catch (RemoteException e) {
				//e.printStackTrace();
			}

			if(succnode != null) {
				try {
					fingers.set(i, succnode);
				}catch(IndexOutOfBoundsException e) {
					fingers.add(i, succnode);			// first time initialization
				}					
			}
		}

		//System.out.println("FingerTable for "+chordnode.getNodeIP()+" => "+Util.toString(fingers));
	}

}
