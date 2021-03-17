package lab5;

import java.util.concurrent.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Sub1 {
    public synchronized void run() throws InterruptedException {
        // Варіант 18, 1) MА= min(D+ В)*MD*MT+MX*ME;

        System.out.println("Starting sub1...");
        long startTime = System.nanoTime();

        Vector D = new Vector("./sub1/D.txt").filledWithRandomValues();
        Vector B = new Vector("./sub1/B.txt").filledWithRandomValues();

        Matrix MD = new Matrix("./sub1/MD.txt").filledWithRandomValues();
        Matrix MT = new Matrix("./sub1/MT.txt").filledWithRandomValues();
        Matrix MX = new Matrix("./sub1/MX.txt").filledWithRandomValues();
        Matrix ME = new Matrix("./sub1/ME.txt").filledWithRandomValues();

        double[] min_D_B = new double[1];

        ExecutorService service = Executors.newFixedThreadPool(4);
        BlockingQueue<double[]> queueForMin = new LinkedBlockingQueue<double[]>(1);
        BlockingQueue<Matrix> queueForMX = new LinkedBlockingQueue<Matrix>(1);
        BlockingQueue<Matrix> queueForMD = new LinkedBlockingQueue<Matrix>(1);

        Runnable task1 = () -> {
            min_D_B[0] = D.sumWithVector(B).findMin();
            try {
                queueForMin.put(min_D_B);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable task2 = () -> {
            try {
                queueForMX.put(MX.multiplyWithMatrix(ME));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable task3 = () -> {
            try {
                queueForMD.put(MD.multiplyWithMatrix(MT));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Callable<Matrix> task4 = () -> {
            Matrix mx = queueForMX.take();
            Matrix md = queueForMD.take();
            double[] min_d_b = queueForMin.take();
            mx.multiplyWithMatrix(md).multiplyWithDouble(min_d_b[0]).saveToFile("./sub1/MA.txt");

            return mx;
        };

        service.execute(task1);
        service.execute(task2);
        service.execute(task3);

        try {
            System.out.println("MA:");
            service.submit(task4).get().printToConsole();

            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Total execution time sub1 in millis: " + elapsedTime / 1000000);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

}
