package no.hvl.dat110.node;

/**
 * @author tdoy
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Set;

import no.hvl.dat110.rpc.interfaces.ChordNodeInterface;
import no.hvl.dat110.util.Util;

public class Operations implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Message message;
	private ChordNodeInterface node;
	private String filecontent;
	private Set<Message> activenodeswithfile;
	
	public Operations(ChordNodeInterface node, Message message, Set<Message> activenodeswithfile) throws RemoteException {
		this.node = node;
		this.message = message;
		this.activenodeswithfile = activenodeswithfile;
	}
	
	public void performOperation() throws RemoteException {
		System.out.println(node.getNodeIP()+ ": "+message.getOptype().toString()+" processing...");

		OperationType optype = message.getOptype();
		
		switch(optype) {
			case READ: {
				this.read(new File(message.getFilepath()));
			}
			break;
			case WRITE: {
				this.write(new File(message.getFilepath()));
			}
			break;

			default: break;
		}
			
	}
	
	// multicast operation to other replicas
	public void multicastOperationToReplicas(Message message) throws AccessException, RemoteException {
		this.message = message;
		Set<Message> replicas = this.activenodeswithfile;
		replicas.remove(message);										// don't repeat the operation for the initiating process
		
		for(Message activenodes : replicas) {
			String nodeip = activenodes.getNodeIP();
			String nodeid = activenodes.getNodeID().toString();
			try {
				Registry registry = Util.locateRegistry(nodeip);		// locate the registry and see if the node is still active
				ChordNodeInterface node = (ChordNodeInterface) registry.lookup(nodeid);

				node.onReceivedUpdateOperation(message);
							
			} catch (NotBoundException e) {

				//e.printStackTrace();
			}
		}
	}
	
	public void multicastReadReleaseLocks() throws AccessException, RemoteException {
		
		Set<Message> replicas = this.activenodeswithfile;
		
		for(Message activenodes : replicas) {
			String nodeip = activenodes.getNodeIP();
			String nodeid = activenodes.getNodeID().toString();

			try {
				Registry registry = Util.locateRegistry(nodeip);					// locate the registry and see if the node is still active
				ChordNodeInterface node = (ChordNodeInterface) registry.lookup(nodeid);
				node.releaseLocks();				
			} catch (NotBoundException e) {

				//e.printStackTrace();
			}
		}	
	}
	
	private void read(File file) throws RemoteException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line, out = "";
			while((line=br.readLine())!= null) {
				out += line;
				out += "\n";
			}
			br.close();
			setFilecontent(out);
		} catch (IOException e) {
			
			//e.printStackTrace();
		}
	}
	
	private void write(File file) throws RemoteException {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(message.getNewcontent());
			bw.newLine();
			bw.close();
			int v = message.getVersion();
			message.setVersion(++v); 										// new version number after write operation
			node.getFilesMetadata().put(message.getFilename(), message);
									
		} catch (IOException e) {
			
			//e.printStackTrace();
		}
	}

	public String getFilecontent() throws RemoteException {
		
		performOperation();
		
		return filecontent;
	}

	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}

}
