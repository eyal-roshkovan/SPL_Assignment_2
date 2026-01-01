package scheduling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TiredThreadTest {

    private TiredThread worker;

    @BeforeEach
    void setUp() {
        worker = new TiredThread(1, 1.0);
        worker.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        worker.shutdown();
        worker.join(1000);
    }

    @Test
    void testTaskExecutionUpdatesStats() throws InterruptedException {
        Runnable task = (() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });


        long startTime = System.nanoTime();
        worker.addTimeIdle(startTime - worker.getIdleStartTime());
        Runnable taskToExecute = () -> {
            try {
                worker.setBusy(true);
                task.run();
            } finally {
                long endTime = System.nanoTime();
                worker.addTimeUsed(endTime - startTime);
                worker.setIdleStartTime(endTime);
                worker.setBusy(false);
            }
        };
        worker.newTask(taskToExecute);

        Thread.sleep(20);
        assertTrue(worker.isBusy(), "Worker should be busy executing");

        Thread.sleep(150);
        assertFalse(worker.isBusy(), "Worker should be idle now");
        assertTrue(worker.getTimeUsed() > 0, "Time used should have increased");
    }

    @Test
    void testQueueCapacityAndExceptions() throws InterruptedException {
        final AtomicInteger signal = new AtomicInteger(0);

        worker.newTask(() -> {
            while (signal.get() == 0) {

            }
        });

        Thread.sleep(100);

        assertDoesNotThrow(() -> worker.newTask(() -> {}),
                "Should be able to buffer exactly one task while busy");

        assertThrows(IllegalStateException.class, () -> {
            worker.newTask(() -> {});
        }, "Should fail because worker is busy AND queue is full");

        signal.set(1);
        Thread.sleep(1000);
    }

    @Test
    void testCompareTo() throws InterruptedException {
        TiredThread otherWorker = new TiredThread(2, 1.5);

        assertEquals(0, worker.compareTo(otherWorker), "Should be equal at start");
        Runnable task = (() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });


        long startTime = System.nanoTime();
        worker.addTimeIdle(startTime - worker.getIdleStartTime());
        Runnable taskToExecute = () -> {
            try {
                worker.setBusy(true);
                task.run();
            } finally {
                long endTime = System.nanoTime();
                worker.addTimeUsed(endTime - startTime);
                worker.setIdleStartTime(endTime);
                worker.setBusy(false);
            }
        };
        worker.newTask(taskToExecute);
        Thread.sleep(150);
        assertTrue(worker.compareTo(otherWorker) > 0, "Busy worker should be 'more tired'");
    }
}