package no.hvl.dat110.node;

/**
 * @author tdoy
 * dat110 - demo/exercise
 */

import java.rmi.RemoteException;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Util;

public class NodeInformation extends Thread{
	
	private ChordNodeInterface chordnode;
	
	public NodeInformation(ChordNodeInterface chordnode) {
		this.chordnode = chordnode;
	}
	
	public void run() {
		
		while(true) {
			try {
				printInfo();
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public synchronized void printInfo() {
		try {
			System.out.println("==================================");
			System.out.println("Node Identifier = "+chordnode.getNodeID());
			System.out.println("Node IP address = "+chordnode.getNodeIP());
			System.out.println("successor("+ chordnode.getNodeIP()+") = "+chordnode.getSuccessor().getNodeIP());
			if(chordnode.getPredecessor() == null)
				System.out.println("predecessor("+ chordnode.getNodeIP()+") = "+chordnode.getPredecessor());
			else
				System.out.println("predecessor("+ chordnode.getNodeIP()+") = "+chordnode.getPredecessor().getNodeIP());
			System.out.println("Current FingerTable for "+chordnode.getNodeIP()+" => "+Util.toString(chordnode.getFingerTable()));
			System.out.println("Current File keyids for "+chordnode.getNodeIP()+" => "+chordnode.getFileKey());
			System.out.println("==================================");
		}catch(RemoteException e) {
			//
		}
	}

}
