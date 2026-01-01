package scheduling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TiredExecutorTest {
    private TiredExecutor executor;
    private final int NUM_THREADS = 4;

    @BeforeEach
    void setUp() {
        executor = new TiredExecutor(NUM_THREADS);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdown();
    }

    @Test
    void testSubmitAllBlocksUntilFinished() {
        AtomicInteger counter = new AtomicInteger(0);
        int numTasks = 10;
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(100); // Simulate work
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // This should take roughly (10 tasks / 4 threads) * 100ms = ~300ms
        executor.submitAll(tasks);

        // If submitAll is working correctly, counter must be exactly numTasks
        // immediately after the method returns.
        assertEquals(numTasks, counter.get(), "submitAll should not return until all tasks finish");
    }

    @Test
    void testTaskDistribution() throws InterruptedException {
        // Submit exactly as many tasks as there are threads
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            });
        }

        // At this point, the idleMinHeap should be empty.
        // The next submit should block until one of the first 4 tasks finishes.
        long startTime = System.currentTimeMillis();

        executor.submit(() -> {});

        long duration = System.currentTimeMillis() - startTime;

        // Duration should be at least 200ms because it had to wait for a worker to become idle
        assertTrue(duration >= 200, "Submit should have blocked waiting for an idle worker");
    }

    @Test
    void testWorkerReportFormatting() {
        executor.submit(() -> {});

        // Give it a tiny bit of time to run
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        String report = executor.getWorkerReport();

        assertNotNull(report);
        assertTrue(report.contains("Worker 0"), "Report should contain worker IDs");
        assertTrue(report.contains("Fatigue"), "Report should contain fatigue metrics");
    }
}