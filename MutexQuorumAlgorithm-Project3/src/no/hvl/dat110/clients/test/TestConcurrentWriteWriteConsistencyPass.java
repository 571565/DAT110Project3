package no.hvl.dat110.clients.test;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.hvl.dat110.interfaces.ProcessInterface;
import no.hvl.dat110.mutexprocess.Config;
import no.hvl.dat110.mutexprocess.Message;
import no.hvl.dat110.mutexprocess.OperationType;
import no.hvl.dat110.mutexprocess.Operations;
import no.hvl.dat110.mutexprocess.ProcessContainer;


class TestConcurrentWriteWriteConsistencyPass {
	

	private ProcessInterface p1;
	private ProcessInterface p2;

	@BeforeEach
	void setUp() throws Exception {
		
		Random r = new Random();
		
		new ProcessContainer("process1", r.nextInt(10000));
		new ProcessContainer("process2", r.nextInt(10000));
		new ProcessContainer("process3", r.nextInt(10000));
		new ProcessContainer("process4", r.nextInt(10000));
		new ProcessContainer("process5", r.nextInt(10000));
		new ProcessContainer("process6", r.nextInt(10000));
		new ProcessContainer("process7", r.nextInt(10000));
		new ProcessContainer("process8", r.nextInt(10000));
		new ProcessContainer("process9", r.nextInt(10000));
		new ProcessContainer("process10", r.nextInt(10000));
		
		// To test the correctness - we will manually manipulate the processes
		// Get the registry  - running on localhost
		Registry registry = LocateRegistry.getRegistry(Config.PORT);
		// Look up the registry for each remote object
		p1 = (ProcessInterface) registry.lookup("process1");
		p2 = (ProcessInterface) registry.lookup("process2");

	}

	@Test
	void test() throws RemoteException, InterruptedException {
		
		// GOAL: A write/write operation must not leave the distributed data store in an inconsistent state
		// We must not have a read-write or a write-write conflict under concurrent operations
		
		client1 pr1 = new client1("process1", p1); 			// process1 receives write request from client1
		client2 pr2 = new client2("process2", p2); 			// process2 receives write request from client2
		
		pr1.start();
		pr2.start();
		
		pr1.join();
		pr2.join();
		
		System.out.println(pr1.isSucceeded()+" : "+pr2.isSucceeded());
		boolean result = pr1.isSucceeded() && pr2.isSucceeded();
		Assertions.assertFalse(result); 							// This must assert to false (pass) i.e. pr1 and pr2 = false (at least one must fail to avoid write/write conflicts)
		
	}
	
	private Message buildWriteMessage(String procname, String content) throws RemoteException {
		
		// build the operation to be performed - write/read
		Message message = new Message();
		message.setOptype(OperationType.WRITE);									// set the type of message - write
		message.setNewcontent(content); 										// content to write
		message.setProcessStubName(procname);
		
		return message;
	}
	
	private boolean performWriteOperation(ProcessInterface p, Message message) throws RemoteException {
		
		if(message.isAcknowledged()) {										// majority votes?
			p.acquireLock(); 												// acquire lock to CS and also increments localclock
			Operations op = new Operations(p, message);						// perform operation
			op.performOperation();											// on local resource
			p.multicastUpdateOrReadReleaseLockOperation(message); 			// multicast update operation to all other processes		
			p.releaseLocks(); 												// release locks to CS after operations
			
			return true;
		} else {
			return false;
		}
	}
	
	class client1 extends Thread {
		
		boolean succeeded;
		String process;
		ProcessInterface recprocess;
		client1(String rcvname, ProcessInterface recp) throws RemoteException{
			process = rcvname;
			recprocess = recp;
		}
		
		public void run() {
			try {
				Message wmessage = buildWriteMessage(process, "testing-process1");  						// build a message for one process
				
				boolean decision = recprocess.requestWriteOperation(wmessage);			// send request and get back the voters decision							// send a write request from p4					
				wmessage.setAcknowledged(decision); 									// put the voters' decision in a message
				recprocess.multicastVotersDecision(wmessage); 							// multicast the decision to others
				succeeded = performWriteOperation(recprocess, wmessage); 				// perform a write operation
				//Thread.sleep(1000);
			}catch(Exception e) {
				
			}
		}

		public boolean isSucceeded() {
			return succeeded;
		}
		
	}
	
	class client2 extends Thread {
		
		boolean succeeded = false;
		String process;
		ProcessInterface recprocess;
		
		client2(String rcvname, ProcessInterface recp) throws RemoteException{
			process = rcvname;
			recprocess = recp;
		}
		
		public void run() {
			try {
				Message wmessage = buildWriteMessage(process, "testing-process2");
				
				// a read request comes to a particular process
				boolean decision = recprocess.requestReadOperation(wmessage); 					// send request and get back the voters decision
				wmessage.setAcknowledged(decision); 											// put the voters' decision in a message
				recprocess.multicastVotersDecision(wmessage); 									// multicast the decision to others
				succeeded = performWriteOperation(recprocess, wmessage); 						// perform a write operation
				//Thread.sleep(5000);
			}catch(Exception e) {
				
			}
		}
		
		public boolean isSucceeded() {
			return succeeded;
		}
	}

}
