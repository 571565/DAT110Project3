package no.hvl.dat110.node.client.test;

/**
 * project/exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.hvl.dat110.file.FileManager;
import no.hvl.dat110.node.Message;
import no.hvl.dat110.rpc.StaticTracker;
import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Hash;
import no.hvl.dat110.util.Util;

public class NodeClientTester {

	public NodeClientTester(String file) {
		testcase(file);
	}
	
	private void testcase(String filename) {
		
		// Lookup(key) - Use this class as a client that is requesting for a new file and needs the identifier and IP of the node where the file is located
		// assume you have a list of nodes in the tracker class and select one randomly. We can use the Tracker class for this purpose

		
		System.out.println("Original Lookup File = "+filename);
		System.out.println("Hashvalue: " + Hash.hashOf(filename));
		System.out.println("======================");
		
		// try the trackers IP addresses
		
		Registry registry = Util.tryIPs();
		
		if(registry != null) {
			
			try {
				String haship = Hash.hashOf(Util.activeIP).toString();
				//String haship = Hash.customHash(Util.activeIP).toString();

				ChordNodeInterface entrynode = (ChordNodeInterface) registry.lookup(haship);
				
				if(entrynode != null) {
					FileManager fm = new FileManager(entrynode, StaticTracker.N);
					Set<Message> activenodesset = fm.requestActiveNodesForFile(filename);
					List<Message> activenodes = new ArrayList<Message>(activenodesset);
					printNodes(activenodes);
				}
			}catch(RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
			
		}	
	}
	
	private void printNodes(List<Message> activenodes) {
		
		for(Message message : activenodes) {
			System.out.println(message.getNodeIP()+": "+message.getNodeID());
			System.out.println(message.getFilename()+": "+message.getVersion());
			System.out.println("=================");
		}
	}
	
	public static void main(String[] args) {
		// Lookup(key) - Use this class as a client that is requesting for a new file and needs the identifier and IP of the node where the file is located
		// assume you have a list of nodes in the tracker class and select one randomly. We can use the Tracker class for this purpose
		
		String file1 = "process1"+0;				// the name of the resource we want to lookup
		
		new NodeClientTester(file1);
	

	}
	
	

}
