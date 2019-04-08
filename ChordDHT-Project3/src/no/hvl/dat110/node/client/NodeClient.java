package no.hvl.dat110.node.client;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import no.hvl.dat110.node.NodeInformation;
import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Hash;
import no.hvl.dat110.util.Util;

public class NodeClient {

	public static void main(String[] args) {
		// Lookup(key) - Use this class as a client that is requesting for a new file and needs the identifier and IP of the node where the file is located
		// assume you have a list of nodes in the tracker class and select one randomly. We can use the Tracker class for this purpose
		
		String fileresource = "process2"+1;				// the name of the resource we want to lookup (+1 means replica 1)
		
		BigInteger keyid = Hash.hashOf(fileresource); 	// obtain the identifier for fileresource from the name space (0,1,...,2^mbit)
		
		System.out.println("Lookup keyID = "+keyid);
		System.out.println("======================");
		
		// try the trackers IP addresses
		
		Registry registry = Util.tryIPs();
		
		if(registry != null) {
			
			try {
				String haship = Hash.hashOf(Util.activeIP).toString();

				ChordNodeInterface entryNode = (ChordNodeInterface) registry.lookup(haship);
				
				if(entryNode != null) {
					ChordNodeInterface succOfentryNode = entryNode.findSuccessor(keyid);	// lookup the successor of entrynode recursively - remote calls
					NodeInformation succInfo = new NodeInformation(succOfentryNode);		// print out info about this node - this is the node with the keyid
					succInfo.printInfo();
				}
			}catch(RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
			
		}
		

	}
	
	

}
