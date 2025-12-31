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
                long startTime = System.nanoTime();
                lazyWorker.addTimeIdle(startTime - lazyWorker.getIdleStartTime());
                try{
                    lazyWorker.setBusy(true);
                    task.run();
                }
                finally {
                    long endTime = System.nanoTime();
                    lazyWorker.addTimeUsed(endTime - startTime);
                    lazyWorker.setIdleStartTime(endTime);
                    lazyWorker.setBusy(false);
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

        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks) {
            submit(task);
        }
        synchronized (inFlight) {
            while (inFlight.get() != 0)
            {
                try {
                    inFlight.wait();
                }
                catch(InterruptedException e) {
                    break;
                }
            }
        }

    }

    public void shutdown() throws InterruptedException {
        // TODO
        double sum = 0;
        double avg = 0;
        double fairness = 0;
        for (TiredThread worker : workers)
            worker.shutdown();

        for (TiredThread worker : workers) {
            worker.join();
            sum += worker.getFatigue();
        }
        avg = sum / workers.length;
        for(TiredThread worker : workers)
            fairness += Math.pow(worker.getFatigue() - avg, 2);

        System.out.println("Fairness: " + fairness);
    }
    /*
    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder();
        report.append("Worker Activity Report:\n");
        for(TiredThread worker : workers){
            report.append(String.format(
                    "Worker %d: Fatigue=%.2f, Time Used=%d ns, Time Idle=%d ns\n",
                    worker.getWorkerId(),
                    worker.getFatigue(),
                    worker.getTimeUsed(),
                    worker.getTimeIdle()
            ));
        }
        return report.toString();
    }
     */

    public synchronized String getWorkerReport() {
        // return readable statistics for each worker
        StringBuilder ret = new StringBuilder();
        for(TiredThread worker: workers){
            String report = String.format("Worker %d: Time Used = %d ns, Time Idle = %d ns, Fatigue = %,2f\n",
                    worker.getWorkerId(),
                    worker.getTimeUsed(),
                    worker.getTimeIdle(),
                    worker.getFatigue());
            ret.append(report);
        }
        double averageFatigue = 0.0;
        for(TiredThread worker: workers){
            averageFatigue += worker.getFatigue();
        }
        averageFatigue /= workers.length;
        ret.append(String.format("Average Fatigue: %.2f\n", averageFatigue));
        double fairness = 0.0;
        for(TiredThread worker: workers){
            fairness += Math.pow(worker.getFatigue() - averageFatigue, 2);
        }
        ret.append("Fairness value: " + String.format("%.2f\n", fairness));
        return ret.toString();
    }
}