package no.hvl.dat110.mutexprocess;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import no.hvl.dat110.interfaces.ProcessInterface;


public class ProcessContainer {
	
	public ProcessContainer(String procName, int procId) {
		try {
			// create registry and start it if not started 
			Registry registry = null;
			
			try {
				registry = LocateRegistry.getRegistry(Config.PORT);			
				registry.list();
			} catch (RemoteException e) {
				registry = LocateRegistry.createRegistry(Config.PORT);
			}
			
			// Make a new instance of the implementation class
			ProcessInterface proc = new MutexProcess(procId, procName);
			
			// bind the remote object (stub) in the registry			
			registry.rebind(procName, proc);
			
			System.out.println(procName+ " process registry is running");
		}catch(Exception e) {
			System.err.println("Process Container: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Random r = new Random();
		
		new ProcessContainer("process1", r.nextInt(10000));
		new ProcessContainer("process2", r.nextInt(10000));
		new ProcessContainer("process3", r.nextInt(10000));
	}

}
