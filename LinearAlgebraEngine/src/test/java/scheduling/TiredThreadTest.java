package scheduling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TiredThreadSimpleTest {

    private TiredThread worker;

    @BeforeEach
    void setUp() {
        // Initialize worker with a 2.0 fatigue factor
        worker = new TiredThread(1, 1.0);
        worker.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        // Safely shut down and wait for thread to finish
        worker.shutdown();
        worker.join(1000);
    }

    @Test
    void testTaskExecutionUpdatesStats() throws InterruptedException {
        // Run a task for 100ms
        worker.newTask(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });

        // Small sleep to let the thread pick up the task
        Thread.sleep(20);
        assertTrue(worker.isBusy(), "Worker should be busy executing");

        // Wait for task to finish
        Thread.sleep(150);
        assertFalse(worker.isBusy(), "Worker should be idle now");
        assertTrue(worker.getTimeUsed() > 0, "Time used should have increased");
    }

    @Test
    void testQueueCapacityAndExceptions() throws InterruptedException {
        // We use AtomicInteger so we can change the value from the test thread
        // while the worker thread reads it.
        final AtomicInteger signal = new AtomicInteger(0);

        // 1. Task 1: Worker takes this immediately and enters the while loop.
        worker.newTask(() -> {
            while (signal.get() == 0) {

            }
        });

        // Give the worker time to "take" the task and enter the loop
        Thread.sleep(100);

        // 2. Task 2: Worker is busy in the loop, so this task fills the queue.
        assertDoesNotThrow(() -> worker.newTask(() -> {}),
                "Should be able to buffer exactly one task while busy");

        // 3. Task 3: Queue is full. This MUST throw.
        assertThrows(IllegalStateException.class, () -> {
            worker.newTask(() -> {});
        }, "Should fail because worker is busy AND queue is full");

        // 4. Release the worker so it can finish Task 1 and Task 2
        signal.set(1);
    }

    @Test
    void testCompareTo() throws InterruptedException {
        TiredThread otherWorker = new TiredThread(2, 1.5);

        // At the start, both have 0 timeUsed, so fatigue is equal
        assertEquals(0, worker.compareTo(otherWorker), "Should be equal at start");

        // Run a task on our worker to increase its fatigue
        worker.newTask(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });
        Thread.sleep(150); // wait for completion

        // Now worker has fatigue (Time * 2.0), otherWorker has 0
        assertTrue(worker.compareTo(otherWorker) > 0, "Busy worker should be 'more tired'");
    }
}