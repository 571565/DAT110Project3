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
import no.hvl.dat110.mutexprocess.ProcessContainer;


class TestQuorumDistributedAlgorithmPass {
	
	//private SimulateReplicaUpdates sr;
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
		// Get the registry  - running on local machine's IP
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
	void test() throws Exception {
		
		// we will manually manipulate the processes and acquire locks so that such processes can deny requests
		// for N = 10 processes, we need 6 processes to grant permission (N/2 + 1) for any READ or WRITE operation
		// No operation (READ/WRITE) should be allowed with this setup (i.e. 5 processes are busy)
		p1.acquireLock();
		p2.acquireLock();
		p3.acquireLock();
		p4.acquireLock();
		p5.acquireLock();
		
		// build a message for one process
		Message wmessage = buildWriteMessage("process9");
		Message rmessage = buildReadMessage("process6");
		
		// a read request comes to a particular process
		boolean decision = p6.requestReadOperation(rmessage);	
		Assertions.assertFalse(decision);					// this must assert to false (i.e pass)		
		
		System.out.println();
		
		// Release all the manually claimed CS locks + p6' lock
		p1.releaseLocks();
		p2.releaseLocks();
		p3.releaseLocks();
		p4.releaseLocks();
		p5.releaseLocks();	
		// release locks from other processes not released because we did not perform any real operation
		p6.releaseLocks();
		p7.releaseLocks();
		p8.releaseLocks();
		p9.releaseLocks();
		p10.releaseLocks();
		
		// a write request comes to a particular process
		decision = p9.requestWriteOperation(wmessage);
		Assertions.assertTrue(decision); 					// this must assert to true (i.e pass)

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

}
