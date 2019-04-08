package no.hvl.dat110.mutexprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import no.hvl.dat110.interfaces.ProcessInterface;
//import no.hvl.dat110.shared.SharedResource;
import no.hvl.dat110.util.Util;

public class Operations {
	
	private Message message;
	private ProcessInterface process;
	private String filecontent;
	
	public Operations(ProcessInterface p, Message message) throws RemoteException {
		this.process = p;
		this.message = message;
	}
	
	public void performOperation() throws RemoteException {
		System.out.println(process.getProcessID()+ ": "+message.getOptype().toString()+" processing...");

		OperationType optype = message.getOptype();
		
		switch(optype) {
			case READ: {
				this.read(new File(process.getFilename()));
			}
			break;
			case WRITE: {
				this.write(new File(process.getFilename()));
			}
			break;

			default: break;
		}
			
	}
	
	// multicast updates operation to other replicas
	public void multicastOperationToReplicas(Message message) throws AccessException, RemoteException {
		this.message = message;
		List<String> replicas = Util.getProcessReplicas();
		
		replicas.remove(message.getProcessStubName());			// don't repeat the operation for the initiating process

		for(int i=0; i<replicas.size(); i++) {
			String stub = replicas.get(i);
			
			try {
				ProcessInterface p = Util.registryHandle(stub);
				//this.process = p;
				p.onReceivedUpdateOperation(message); 			// perform operation on the remote data storage copy
				//this.performOperation();						
				//System.out.println(message.getProcessStubName()+": "+message.getOptype().toString()+" multicast to "+stub);
				
			} catch (NotBoundException e) {

				e.printStackTrace();
			}
		}
	}
	
	public void multicastReadReleaseLocks() throws AccessException, RemoteException {
		
		List<String> replicas = Util.getProcessReplicas();
		for(int i=0; i<replicas.size(); i++) {
			
			String stub = replicas.get(i);
			try {
				ProcessInterface p = Util.registryHandle(stub);
				p.releaseLocks();				
			} catch (NotBoundException e) {

				e.printStackTrace();
			}
		}	
	}
	
	public void read(File file) throws RemoteException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while((line=br.readLine())!= null) {
				this.setFilecontent(line);
			}
			br.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void write(File file) throws RemoteException {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(message.getNewcontent());
			bw.close();
			int v = message.getVersion();
			process.setVersion(++v); 						// new version number after write operation
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public String getFilecontent() {
		
		return filecontent;
	}

	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}

}
