package no.hvl.dat110.rpc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import no.hvl.dat110.chordoperations.CheckPredecessor;
import no.hvl.dat110.chordoperations.JoinRing;
import no.hvl.dat110.chordoperations.LeaveRing;
import no.hvl.dat110.chordoperations.StabilizeRing;
import no.hvl.dat110.chordoperations.UpdateSuccessor;
import no.hvl.dat110.file.FileManager;
import no.hvl.dat110.chordoperations.FixFingerTable;
import no.hvl.dat110.node.Node;
import no.hvl.dat110.node.NodeInformation;
import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;

/**
 * @author tdoy
 * exercise/demo - dat110
 */

public class ChordNodeContainer {
	
	// the 'server' for the ChordNode instance
	
	// joinRing()
	// createRing() if joinRing() fails
	// bind to the registry
	// keep running
	
	private String nodename = "process1";
	private ChordNodeInterface chordnode;
	private long ttl;
	private boolean loopforever;
	
	public ChordNodeContainer(String nodename, long ttl, boolean loopforever) throws Exception {
		this.nodename = nodename;
		this.ttl = ttl;
		this.loopforever = loopforever;
		createNodeContainer();
		stabilizationProtocols();
	}
	
	private void createNodeContainer() throws RemoteException, UnknownHostException {
		
		// create a registry to hold the chordnode stub and start it on the port located in the StaticTracker class
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(StaticTracker.PORT);
			System.out.println("Registry "+registry.list());			// the method registry.list() must be invoked for the exception to work
			
		} catch(RemoteException e) {
			try {
				registry = LocateRegistry.createRegistry(StaticTracker.PORT);
				System.out.println("Registry created...");				
			} catch (RemoteException e1) {
				//
			}
			//e.printStackTrace();
		}
		
		chordnode = new Node(nodename);		// create a new Chord Node
		
		try {
			registry.rebind(chordnode.getNodeID().toString(), chordnode);
			System.out.println("Registry "+registry.list());
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}
	
	private void stabilizationProtocols() throws InterruptedException, IOException {
		
		// attempt to join the ring
		JoinRing join = new JoinRing(chordnode);
		join.join();
			
		// print out info about the node periodically
		NodeInformation nodeinfo = new NodeInformation(chordnode);
		nodeinfo.start();
		
		// create a local file and distribute to other nodes for cooperative mirroring
		System.out.println("creating local file and distributing to existing nodes");
		FileManager fm = new FileManager(chordnode, StaticTracker.N);
		fm.createLocalFile();
		fm.createReplicaFiles(nodename);					// use the node's address as the file name
		//fm.distributeReplicaFiles(); 						// send the files to the replicas once
		fm.start(); 										// send the files to the replicas occasionally			

		/**
		 *  Chord's stabilization protocols
		 */
		
		// Schedule updateSuccessor => run this thread periodically to set the first successor pointer to the correct node
		UpdateSuccessor updatesucc = new UpdateSuccessor(chordnode);
		updatesucc.start();
		
		// Scheduler to stabilize ring => we run this thread periodically to stabilize the ring
		StabilizeRing stabilize = new StabilizeRing((Node) chordnode);
		stabilize.start();
		
		// Scheduler to update fingers => schedule updateFinger to run periodically
		FixFingerTable updatefingers = new FixFingerTable(chordnode);
		updatefingers.start();
		
		// Scheduler to check predecessor => schedule check predecessor to run periodically
		CheckPredecessor checkpred = new CheckPredecessor((Node) chordnode);
		checkpred.start();
		
		// leave the ring after ttl secs
		LeaveRing leavering = new LeaveRing(chordnode, ttl, loopforever);
		leavering.start();
		
		nodeinfo.join();
		fm.join();
		updatesucc.join();
		stabilize.join();
		updatefingers.join();
		checkpred.join();
		leavering.join();
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		// 
	}

}
