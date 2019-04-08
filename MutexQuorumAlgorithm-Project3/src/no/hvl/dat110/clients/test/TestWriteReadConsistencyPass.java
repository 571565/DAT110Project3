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


class TestWriteReadConsistencyPass {
	

	private ProcessInterface p1;
	private ProcessInterface p2;
	private ProcessInterface p3;
	private ProcessInterface p4;
	private ProcessInterface p5;
	private ProcessInterface p6;
	private ProcessInterface p7;
	private ProcessInterface p8;
	private ProcessInterface p9;
	private ProcessInterface p10;

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
		p3 = (ProcessInterface) registry.lookup("process3");
		p4 = (ProcessInterface) registry.lookup("process4");
		p5 = (ProcessInterface) registry.lookup("process5");
		p6 = (ProcessInterface) registry.lookup("process6");
		p7 = (ProcessInterface) registry.lookup("process7");
		p8 = (ProcessInterface) registry.lookup("process8");
		p9 = (ProcessInterface) registry.lookup("process9");
		p10 = (ProcessInterface) registry.lookup("process10");

	}

	@Test
	void test() throws RemoteException {
		
		// GOAL: A write operation must not leave the data store in an inconsistent state
		// We must not have a read-write or a write-write conflict
		
		// we will manually manipulate the processes and acquire locks so that such processes can deny requests
		// for N = 10 processes, we need 6 processes to grant permission (N/2 + 1) for any READ or WRITE operation
		// NR + NW > N:  infact (NR + NW) = N + 2 . We should not have read-write or write-write conflicts
		// A WRITE operation should be allowed with this setup (i.e. no process is busy)

		
		
		Message wmessage = buildWriteMessage("process4");  // build a message for one process
		
		boolean decision = p4.requestWriteOperation(wmessage);			// send a write request and get the voters decision
		wmessage.setAcknowledged(decision); 							// put the voters' decision in a message
		p4.multicastVotersDecision(wmessage); 							// multicast the decision to others

		boolean succeed = performWriteOperation(p4, wmessage);
		
		Assertions.assertTrue(succeed); 					// must pass		
		
		
		// GOAL: A read operation must produce exactly the same copy of data from any of the replicas after a write operation
		
		Message rmessage = buildReadMessage("process6");
		
		// a read request comes to a particular process
		decision = p6.requestReadOperation(rmessage); 		// and get the voters decision				
		rmessage.setAcknowledged(decision); 				// put the voters' decision in a message
		p6.multicastVotersDecision(rmessage); 				// multicast the decision to others
		
		succeed = performReadOperation(p6, rmessage); 		// perform a read operation
		
		Assertions.assertTrue(succeed); 					// must pass
		
		// most important is that the previous write operation must leave the data in a consistent state - all versions must be the same
		Assertions.assertEquals(p1.getVersion(), p2.getVersion());
		Assertions.assertEquals(p1.getVersion(), p3.getVersion());
		Assertions.assertEquals(p1.getVersion(), p4.getVersion());
		Assertions.assertEquals(p1.getVersion(), p5.getVersion());
		Assertions.assertEquals(p1.getVersion(), p6.getVersion());
		Assertions.assertEquals(p1.getVersion(), p7.getVersion());
		Assertions.assertEquals(p1.getVersion(), p8.getVersion());
		Assertions.assertEquals(p1.getVersion(), p9.getVersion());
		Assertions.assertEquals(p1.getVersion(), p10.getVersion());
		
	}
	
	private Message buildWriteMessage(String procname) throws RemoteException {
		
		// build the operation to be performed - write/read
		Message message = new Message();
		message.setOptype(OperationType.WRITE);									// set the type of message - write
		message.setNewcontent("testing-process1"); 								// content to write
		message.setProcessStubName(procname);
		
		return message;
	}
	
	private Message buildReadMessage(String procname) throws RemoteException {
		
		// build the operation to be performed - write/read
		Message message = new Message();
		message.setOptype(OperationType.READ);									// set the type of message - write
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
	
	private boolean performReadOperation(ProcessInterface p, Message message) throws RemoteException {

		if(message.isAcknowledged()) {												// majority acknowledged?
			p.acquireLock(); 												// acquire lock to CS and also increments localclock
			Operations op = new Operations(p, message);						// perform operation
			op.performOperation();											// on local resource
			p.multicastUpdateOrReadReleaseLockOperation(message); 			// multicast release lock to replica processes that voted
			p.releaseLocks(); 												// release locks to CS after operations
			
			return true;
		} else {
			return false;
		}
	}

}
