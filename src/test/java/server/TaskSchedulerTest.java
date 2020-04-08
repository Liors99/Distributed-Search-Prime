package server;

import static org.junit.jupiter.api.Assertions.*;

import data.BigInt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.CoordConsole;

import java.math.BigInteger;
import java.util.PriorityQueue;

class TaskSchedulerTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testParseInput() {
    	TaskScheduler TS = new TaskScheduler();
    	
    	WorkerRecord[] wrs = new WorkerRecord[5];
    	
    	for(int i = 0; i < wrs.length; i++) {
    		wrs[i] = new WorkerRecord();
    	}
    	

    	wrs[0].setScore(5);
    	wrs[1].setScore(5);
    	wrs[2].setScore(7);
    	wrs[3].setScore(5);
    	wrs[4].setScore(10);
    	
        TS.setTotalScore(new BigInt("32"));
        //BigInt[] results = TS.deriveRange(wR1, new BigInt("99"), new BigInt("3"));
        
        int lower = 3;
        int upper = lower;
        for(int i=0;i < wrs.length;i++) {
        	BigInt[] results = TS.deriveRange(wrs[i], new BigInt("99"), new BigInt(Integer.toString(lower)));
        	upper = lower + ((99-3)*wrs[i].getScore()/32);
        	
        	if(i == wrs.length-1) {
        		upper = 99;
        	}
        	
        	
        	
        	System.out.print(Integer.toString(lower) +" " + results[0].toString() +"   " + results[1].toString() + " " + Integer.toString(upper) );
        	System.out.println();
        	
        	lower = upper+1;
        	
        }
        
        
        

    }

    

    @Test
    void queueTaskTest(){
        TaskScheduler TS = new TaskScheduler();
        TS.setTotalScore(new BigInt("25"));
        
        WorkerRecord wR1 = new WorkerRecord();
        WorkerRecord wR2 = new WorkerRecord();
        WorkerRecord wR3 = new WorkerRecord();
        WorkerRecord wR4 = new WorkerRecord();

        wR1.setScore(5);
        wR2.setScore(10);
        wR3.setScore(5);
        wR4.setScore(7);


        TS.addToWorkerQueue(wR1);
        TS.addToWorkerQueue(wR2);
        TS.addToWorkerQueue(wR3);
        TS.addToWorkerQueue(wR4);
        
        
        PriorityQueue<WorkerRecord> pq = TS.getWorkerQueue();
       
        WorkerRecord wr = pq.remove();
        assertEquals(10,wr.getScore());
        wr = pq.remove();
        assertEquals(7,wr.getScore());
        wr = pq.remove();
        assertEquals(5,wr.getScore());
        wr = pq.remove();
        assertEquals(5,wr.getScore());
        
        

    }

    @Test
    void rescheduleTaskTest(){
        TaskScheduler TS = new TaskScheduler();
        TS.setTotalScore(new BigInt("25"));

        WorkerRecord wR1 = new WorkerRecord();
        WorkerRecord wR2 = new WorkerRecord();
        WorkerRecord wR3 = new WorkerRecord();
        WorkerRecord wR4 = new WorkerRecord();

        wR1.setScore(5);
        wR2.setScore(10);
        wR3.setScore(5);
        wR4.setScore(7);


        TS.addToWorkerQueue(wR1);
        TS.addToWorkerQueue(wR2);
        TS.addToWorkerQueue(wR3);
        TS.addToWorkerQueue(wR4);


        PriorityQueue<WorkerRecord> pq = TS.getWorkerQueue();

        WorkerRecord wr = pq.remove();
        WorkerRecord wR5 = new WorkerRecord();
        TS.putActiveWorker(wR5);
        TS.reschedule(wR5, wr);
        assertEquals(wR5.getWorkrange(),wr.getWorkrange());



    }

}