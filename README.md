# Calculos
Hilo 3: +196403 (acumulado: 589006) 
Hilo 2: +196310 (acumulado: 785316) 
Hilo 0: +196295 (acumulado: 392603) 
Hilo 1: +196308 (acumulado: 196308) 

Resultados 
Puntos totales: 1000000 
Dentro del círculo (paralelo): 785316 
Pi ≈ 3.1412640000 (error = 0.0003286535) 

Tiempos 
T_s (secuencial): 8.749 ms (0.008749 s) 
T_p (paralelo, con p=4): 24.724 ms (0.024724 s) 

Métricas de rendimiento 
Speedup (S) = T_s / T_p = 8.749 / 24.724 = 0.354x 
Eficiencia (E) = S / p = 0.354 / 4 = 0.088 (8.8%) 
Overhead (T_o) = p*T_p - T_s = 4*24.724 - 8.749 = 90.146 ms.

## Nota
Con 1e6 muestras, el paralelismo salió más lento por el overhead de creación/sincronización de hilos (S < 1).