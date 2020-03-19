package server;

import static org.junit.jupiter.api.Assertions.*;

import data.BigInt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.CoordConsole;

import java.math.BigInteger;

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
        WorkerRecord wR1 = new WorkerRecord();
        WorkerRecord wR2 = new WorkerRecord();
        WorkerRecord wR3 = new WorkerRecord();
        WorkerRecord wR4 = new WorkerRecord();
        WorkerRecord wR5 = new WorkerRecord();

        wR1.setScore(5);
        wR2.setScore(5);
        wR3.setScore(5);
        wR4.setScore(5);
        wR5.setScore(5);
        TS.setTotalScore(new BigInt("25"));
        BigInt[] results = TS.deriveRange(wR1, new BigInt("99"), new BigInt("3"));
        for (BigInt res : results) {
            System.out.println(res);
        }

    }

    @Test
    void scheduleTaskTest(){
        TaskScheduler TS = new TaskScheduler();
        TS.setTotalScore(new BigInt("25"));

        WorkerRecord wR1 = new WorkerRecord();
        WorkerRecord wR2 = new WorkerRecord();
        WorkerRecord wR3 = new WorkerRecord();
        WorkerRecord wR4 = new WorkerRecord();
        WorkerRecord wR5 = new WorkerRecord();

        wR1.setScore(5);
        wR2.setScore(5);
        wR3.setScore(5);
        wR4.setScore(5);
        wR5.setScore(5);

        TS.addToWorkerQueue(wR1);
        TS.addToWorkerQueue(wR2);
        TS.addToWorkerQueue(wR3);
        TS.addToWorkerQueue(wR4);
        TS.addToWorkerQueue(wR5);

        TS.scheduleTask(new BigInt("100"), new BigInt("101"));

    }

    @Test
    void scheduleTaskTest2(){
        TaskScheduler TS = new TaskScheduler();
        TS.setTotalScore(new BigInt("25"));

        WorkerRecord wR1 = new WorkerRecord();
        WorkerRecord wR2 = new WorkerRecord();
        WorkerRecord wR3 = new WorkerRecord();
        WorkerRecord wR4 = new WorkerRecord();

        wR1.setScore(5);
        wR2.setScore(10);
        wR3.setScore(5);
        wR4.setScore(5);


        TS.addToWorkerQueue(wR1);
        TS.addToWorkerQueue(wR2);
        TS.addToWorkerQueue(wR3);
        TS.addToWorkerQueue(wR4);


        TS.scheduleTask(new BigInt("100"), new BigInt("101"));

    }


}