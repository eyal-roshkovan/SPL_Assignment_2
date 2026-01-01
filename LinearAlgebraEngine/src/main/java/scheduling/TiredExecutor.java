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
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new TiredThread(i, Math.random() + 0.5);
            workers[i].start();
            idleMinHeap.offer(workers[i]);
        }
    }

    public void submit(Runnable task) {
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

    public synchronized String getWorkerReport() {
        double averageFatigue = 0.0;
        StringBuilder report = new StringBuilder();
        for(TiredThread worker: workers){
            String workerReport = String.format("Worker %d: Time Used = %d ns, Time Idle = %d ns, Fatigue = %,2f\n",
                    worker.getWorkerId(),
                    worker.getTimeUsed(),
                    worker.getTimeIdle(),
                    worker.getFatigue());
            report.append(workerReport);
        }

        for(TiredThread worker: workers)
            averageFatigue += worker.getFatigue();

        averageFatigue /= workers.length;
        report.append(String.format("Average Fatigue: %.2f\n", averageFatigue));

        double fairness = 0.0;
        for(TiredThread worker: workers)
            fairness += Math.pow(worker.getFatigue() - averageFatigue, 2);

        report.append("Fairness value: ").append(String.format("%.2f\n", fairness));
        return report.toString();
    }
}