package lab5;

import java.util.concurrent.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Sub2 {

    public synchronized void run() throws InterruptedException {
        // Варіант 18, 2) C=В*МT+D*MX*a;

        System.out.println("Starting sub2...");
        long startTime = System.nanoTime();

        Vector B = new Vector("./sub2/B.txt").filledWithRandomValues();
        Vector D = new Vector("./sub2/D.txt").filledWithRandomValues();

        Matrix MT = new Matrix("./sub2/MT.txt").filledWithRandomValues();
        Matrix MX = new Matrix("./sub2/MX.txt").filledWithRandomValues();

        double fixed_a = 2;

        ExecutorService service = Executors.newFixedThreadPool(3);
        BlockingQueue<Vector> queueForVectors = new LinkedBlockingQueue<Vector>(2);

        Runnable task1 = () -> {
            try {
                queueForVectors.put(B.multiplyWithMatrix(MT));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable task2 = () -> {
            try {
                queueForVectors.put(D.multiplyWithDouble(fixed_a).multiplyWithMatrix(MX));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Callable<Vector> task3 = () -> {
            Vector first = queueForVectors.take();
            Vector second = queueForVectors.take();
            first.sumWithVector(second).saveToFile("./sub2/C.txt");
            return first;
        };

        service.execute(task1);
        service.execute(task2);

        try {
            System.out.println("C:");
            service.submit(task3).get().printToConsole();

            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Total execution time sub2 in millis: " + elapsedTime / 1000000);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

}
