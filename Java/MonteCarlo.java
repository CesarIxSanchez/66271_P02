import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class MonteCarlo {

    // Acumulador para la versión paralela
    private static final AtomicLong globalCount = new AtomicLong(0);

    // Tarea por hilo
    static class MonteCarloTask implements Runnable {
        private final long numSamples;
        private final int threadId;
        MonteCarloTask(long numSamples, int threadId) {
            this.numSamples = numSamples;
            this.threadId = threadId;
        }
        @Override
        public void run() {
            long localCount = 0;
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            for (long i = 0; i < numSamples; i++) {
                double x = rnd.nextDouble();
                double y = rnd.nextDouble();
                if (x * x + y * y <= 1.0) localCount++;
            }
            long newTotal = globalCount.addAndGet(localCount);
            System.out.printf("Hilo %d: +%d (acumulado: %d)%n", threadId, localCount, newTotal);
        }
    }

    // Ejecución secuencial
    private static Result runSequential(long totalSamples) {
        long t0 = System.nanoTime();
        long inside = 0;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (long i = 0; i < totalSamples; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            if (x * x + y * y <= 1.0) inside++;
        }
        long t1 = System.nanoTime();
        double ms = (t1 - t0) / 1_000_000.0;
        return new Result(inside, ms);
    }

    // Ejecución paralela
    private static Result runParallel(long totalSamples, int numThreads) throws InterruptedException {
        globalCount.set(0);

        long t0 = System.nanoTime();

        long base = totalSamples / numThreads;
        long rem  = totalSamples % numThreads;

        List<Thread> threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            long samplesForThisThread = base + (i == numThreads - 1 ? rem : 0);
            Thread t = new Thread(new MonteCarloTask(samplesForThisThread, i));
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();

        long t1 = System.nanoTime();
        double ms = (t1 - t0) / 1_000_000.0;
        return new Result(globalCount.get(), ms);
    }

    // Ayuda para devolver par
    private record Result(long count, double timeMs) {}

    public static void main(String[] args) throws InterruptedException {
        long totalSamples = 1_000_000L;
        int numThreads = 4;

        // Secuencial
        Result seq = runSequential(totalSamples);
        double Ts_ms = seq.timeMs;

        // Paralelo
        Result par = runParallel(totalSamples, numThreads);
        double Tp_ms = par.timeMs;

        // Métricas
        double S  = Ts_ms / Tp_ms; // Speedup
        double E  = S / numThreads;  // Eficiencia
        double To_ms = numThreads * Tp_ms - Ts_ms; // Overhead

        // Pi
        double piApprox = (4.0 * par.count) / (double) totalSamples;
        double truePi = 3.1415926535;
        double error = Math.abs(piApprox - truePi);

        // Salida
        System.out.println("\nResultados");
        System.out.printf("Puntos totales: %d%n", totalSamples);
        System.out.printf("Dentro del círculo (paralelo): %d%n", par.count);
        System.out.printf("Pi ≈ %.10f (error = %.10f)%n", piApprox, error);

        System.out.println("\nTiempos");
        System.out.printf("T_s (secuencial): %.3f ms (%.6f s)%n", Ts_ms, Ts_ms / 1000.0);
        System.out.printf("T_p (paralelo, con p=%d): %.3f ms (%.6f s)%n", numThreads, Tp_ms, Tp_ms / 1000.0);

        System.out.println("\nMétricas de rendimiento");
        System.out.printf("Speedup (S) = T_s / T_p = %.3f / %.3f = %.3fx%n", Ts_ms, Tp_ms, S);
        System.out.printf("Eficiencia (E) = S / p = %.3f / %d = %.3f (%.1f%%)%n", S, numThreads, E, 100*E);
        System.out.printf("Overhead (T_o) = p*T_p - T_s = %d*%.3f - %.3f = %.3f ms%n",
                numThreads, Tp_ms, Ts_ms, To_ms);
    }
}
