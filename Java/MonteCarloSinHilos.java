import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class MonteCarloSinHilos {

    // 1. Variable en memoria compartida
    private static final AtomicLong globalCount = new AtomicLong(0);

    // Tarea que ejecuta cada hilo
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
                // Generar un punto aleatorio (x, y) en el rango [0, 1)
                double x = rnd.nextDouble();
                double y = rnd.nextDouble();
                // Verificar si el punto está dentro del círculo (x² + y² <= 1)
                if (x * x + y * y <= 1.0) {
                    localCount++;
                }
            }

            // 2. ¡SECCIÓN CRÍTICA!
            // Múltiples hilos intentan actualizar global_count al mismo tiempo.
            long newTotal = globalCount.addAndGet(localCount);
            System.out.printf("Hilo %d: añadiendo %d puntos al total (acumulado: %d).%n",
                    threadId, localCount, newTotal);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime(); // Inicio

        long totalSamples = 1_000_000L;
        int numThreads = 4;
        long samplesPerThread = totalSamples / numThreads;

        List<Thread> threads = new ArrayList<>();

        // Crear y lanzar hilos
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(new MonteCarloTask(samplesPerThread, i));
            threads.add(t);
            t.start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread t : threads) {
            t.join();
        }

        // 3. Todos los hilos han terminado. El resultado final está en la variable compartida.
        double piApprox = (4.0 * globalCount.get()) / (double) totalSamples;
        double truePi = 3.1415926535;
        double error = Math.abs(piApprox - truePi);

        // Resultados
        System.out.printf("\nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", globalCount.get());
        System.out.printf("Aproximación de pi: %.10f%n", piApprox);
        System.out.printf("Error: %.10f%n", error);

        long endTime = System.nanoTime(); // Fin
        double durationSeconds = (endTime - startTime) / 1_000_000_000.0; // En segundos
        System.out.printf("Tiempo de ejecución: %.3f segundos%n", durationSeconds);
    }
}
