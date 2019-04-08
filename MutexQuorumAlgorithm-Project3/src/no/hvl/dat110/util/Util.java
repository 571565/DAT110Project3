package no.hvl.dat110.util;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import no.hvl.dat110.interfaces.ProcessInterface;
import no.hvl.dat110.mutexprocess.Config;


public class Util {
	
	
	public static Registry locateRegistry() {

		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(Config.PORT);			
			registry.list();
		} catch (RemoteException e) {
			registry = null;
		}
		
		return registry;
	}
	
	public static ProcessInterface registryHandle(String stubID) throws AccessException, RemoteException, NotBoundException {
		
		ProcessInterface process = null;
		
		Registry registry = locateRegistry();
		
		process = (ProcessInterface) registry.lookup(stubID);
		
		return process;
		
	}
	
	public static int numOfReplicas() {
		
		return getProcessReplicas().size();				//
	}
	
	public static List<String> getProcessReplicas() {
		List<String> replicas = new ArrayList<String>();
		// assume we have 10 replicas
		replicas.add("process1");
		replicas.add("process2");
		replicas.add("process3");
		replicas.add("process4");
		replicas.add("process5");
		replicas.add("process6");
		replicas.add("process7");
		replicas.add("process8");
		replicas.add("process9");
		replicas.add("process10");
		
		return replicas;
	}

}
