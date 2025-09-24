import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloSinHilos {

    public static void main(String[] args) {
        long startTime = System.nanoTime();  // Inicio

        long totalSamples = 1_000_000L;
        long insideCircle = 0;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (long i = 0; i < totalSamples; i++) {
            // Generar un punto aleatorio (x, y) en el rango [0, 1)
            double x = rnd.nextDouble(); 
            double y = rnd.nextDouble(); 
            // Verificar si el punto está dentro del círculo (x² + y² <= 1)
            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }

        // Pi
        double piApprox = (4.0 * insideCircle) / (double) totalSamples;
        double truePi = 3.1415926535;
        double error = Math.abs(piApprox - truePi);

        // Resultados
        System.out.printf("\nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", insideCircle);
        System.out.printf("Aproximación de pi: %.10f%n", piApprox);
        System.out.printf("Error: %.10f%n", error);

        long endTime = System.nanoTime();  // Fin
        double durationSeconds = (endTime - startTime) / 1_000_000_000.0; // En segundos
        System.out.printf("Tiempo de ejecución: %.6f segundos%n", durationSeconds);
    }
}
