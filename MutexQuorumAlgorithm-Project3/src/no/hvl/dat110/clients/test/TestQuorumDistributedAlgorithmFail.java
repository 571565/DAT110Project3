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


class TestQuorumDistributedAlgorithmFail {
	
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
		
		// create new processes with unique processids and bind them in a registry
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
		// No operation (READ/WRITE) should be allowed with this setup (i.e. 5 processes are busy out of 10)
		p1.acquireLock();
		p2.acquireLock();
		p3.acquireLock();
		p4.acquireLock();
		p5.acquireLock();
		
		// build a message for one process
		Message rmessage = buildReadMessage("process6");
		
		// a read request comes to a particular process - go through the voting process
		boolean decision = p6.requestReadOperation(rmessage); 		// get the voters' decision
		Assertions.assertTrue(decision);							// this must fail (i.e. decision = false)		

	}
	
	private Message buildReadMessage(String procname) throws RemoteException {
		
		// build the operation to be performed - write/read
		Message message = new Message();
		message.setOptype(OperationType.READ);									// set the type of message - read
		message.setProcessStubName(procname);
		return message;
	}

}
