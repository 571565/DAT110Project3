package no.hvl.dat110.chordoperations;

/**
 * @author tdoy
 * dat110 - demo/exercise
 */

import java.math.BigInteger;
import java.rmi.RemoteException;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;


public class UpdateSuccessor extends Thread {
	
	private ChordNodeInterface chordnode;
	
	public UpdateSuccessor(ChordNodeInterface chordnode) {
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
		System.out.println("Updating the successor for the Node: "+ chordnode.getNodeIP());
		BigInteger succid = chordnode.getNodeID().add(new BigInteger("1")); 					// get the succid of (nodestub+1)	
		//System.out.println("keyid: "+chordnode.getNodeID()+".findsuccessor."+succid);
		ChordNodeInterface succnodestub = chordnode.findSuccessor(succid);						// finds the successor (succ(nodestub+1) of this node(remote call)
		ChordNodeInterface predsucc = succnodestub.getPredecessor();							// get the predecessor of the successor of this node
		
		try {
			if(chordnode.getNodeIP().equals(predsucc.getNodeIP())){
				return;
			} else {
				chordnode.getFingerTable().set(0, predsucc);									// update the first successor (entry) of the finger table				
				chordnode.setSuccessor(predsucc); 												// update the immediate successor (same as FT[0]	
				predsucc.notifySuccessor(chordnode); 											// notify succnodestub of this node as its predecessor
				update();
			}
			
		}catch(Exception e) 
		{
			//
		}
		
	}

}
