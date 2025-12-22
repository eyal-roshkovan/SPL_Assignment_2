package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new TiredThread(i, Math.random() + 0.5);
            workers[i].start();
            idleMinHeap.offer(workers[i]);
        }

    }

    public void submit(Runnable task) {
        // TODO
        try{
            TiredThread lazyWorker = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable wrappedTask = ()->{
                try{
                    task.run();
                }
                finally {
                    idleMinHeap.offer(lazyWorker);
                    synchronized (inFlight) {
                        if(inFlight.decrementAndGet() == 0)
                            inFlight.notifyAll();
                    }
                }
            };
            lazyWorker.newTask(wrappedTask);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
    }

    public void shutdown() throws InterruptedException {
        // TODO
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        return null;
    }
}
