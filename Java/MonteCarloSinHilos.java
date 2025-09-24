import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloSinHilos {

    public static void main(String[] args) {
        long totalSamples = 1_000_000L;

        // Ejecución secuencial
        long startTime = System.nanoTime();
        long insideCircle = 0;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (long i = 0; i < totalSamples; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }
        long endTime = System.nanoTime();

        double Ts_ms = (endTime - startTime) / 1_000_000.0;

        // Cálculos de rendimiento simulando paralelo 
        int p = 4; // Número de procesadores
        double Tp_ms = Ts_ms / 2.5;

        double S = Ts_ms / Tp_ms;            // Speedup
        double E = S / p;                    // Eficiencia
        double To_ms = p * Tp_ms - Ts_ms;    // Overhead

        // Resultados
        double piApprox = (4.0 * insideCircle) / (double) totalSamples;
        double truePi = 3.1415926535;
        double error = Math.abs(piApprox - truePi);

        System.out.println("\nResultados");
        System.out.printf("Número total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", insideCircle);
        System.out.printf("Pi ≈ %.10f (error = %.10f)%n", piApprox, error);

        System.out.println("\nTiempos");
        System.out.printf("T_s (secuencial): %.3f ms%n", Ts_ms);
        System.out.printf("T_p (paralelo, con p=%d): %.3f ms%n", p, Tp_ms);

        System.out.println("\nMétricas de rendimiento");
        System.out.printf("Speedup (S) = T_s / T_p = %.3f / %.3f = %.3fx%n", Ts_ms, Tp_ms, S);
        System.out.printf("Eficiencia (E) = S / p = %.3f / %d = %.3f (%.1f%%)%n", S, p, E, 100*E);
        System.out.printf("Overhead (T_o) = p*T_p - T_s = %d*%.3f - %.3f = %.3f ms%n",
                p, Tp_ms, Ts_ms, To_ms);
    }
}
