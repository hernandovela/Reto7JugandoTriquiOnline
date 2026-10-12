# Triqui Online 1.2.0 · build 4

- Popup personalizado de victoria, derrota o empate para cada jugador.
- Sonido victory.wav para el ganador y defeat.wav para el perdedor. Empate sin sonido de victoria/derrota.
- Seguir jugando registra la aceptación del jugador; la siguiente ronda comienza solo cuando ambos aceptan.
- El tablero se vacía, X comienza y se conservan código y nombres.
- Salir cierra la partida para ambos, incluso después de terminar una ronda.
- Botón Ver resultado / seguir jugando para volver a abrir el popup.
- El sonido se reproduce una vez por ronda y jugador, evitando repeticiones por reconexión o giro.
- Reglas Firestore para votos independientes y reinicio atómico; no se pueden suplantar votos ni reabrir una sala cerrada.

Ambos jugadores deben actualizar a 1.2.0 para aceptar la revancha. Las partidas 1.1 existentes se pueden continuar con las nuevas apps sin cambiar el código; el contador de ronda se inicializa como 1.

Para escuchar los sonidos, usa el volumen multimedia del dispositivo. El cierre o paso a segundo plano libera MediaPlayer.
