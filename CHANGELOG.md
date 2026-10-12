# Historial de versiones

## 1.2.0 · build 4 · 12 de octubre de 2026

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



## 1.1.0 · build 3 · 12 de octubre de 2026

- Código único de ocho caracteres para crear y entrar a partidas; formato ABCD-2345.
- Botón Copiar código y texto seleccionable para compartir por WhatsApp.
- Nombre del jugador obligatorio, guardado en el dispositivo; nombres de ambos participantes visibles en tablero, turnos y resultado.
- Fondo, tablero Canvas, colores y botones del Reto 6, con disposición horizontal al girar.
- Versión y número de build visibles en todas las pantallas.
- Nueva colección Firestore games_v2 con reglas que validan código, nombres, cupos, turnos y resultado; games conserva compatibilidad con 1.0.x.
- Pruebas de códigos/nombres, reglas de nube y dos identidades Android.

## 1.0.1 · build 2

- APK de distribución sin depuración ni marcas testOnly.
- SDK Android 36 estable, versión identificable del archivo para compartir.

## 1.0.0 · build 1

- Lista de partidas y juego para dos dispositivos mediante Firebase.
