# Validación 1.2.0 · build 4

12 de octubre de 2026 · America/Bogota.

- assembleDistribution, assembleDebug, assembleDebugAndroidTest y 12 pruebas unitarias: correctos.
- Firebase real: PASS. Validación de turnos, nombres, código y victoria; no reinicia con un voto ni permite suplantar votos; dos aceptaciones reinician con mismo código y ronda +1; salir impide reabrir la sala.
- Android: OK. Popup Ganaste y recurso victory.wav; popup Perdiste y recurso defeat.wav; revancha de dos usuarios en el mismo código, seguida de victoria del otro jugador.
- El test consulta al cliente que reinicia la ronda y espera sincronización antes de comprobar el resultado.
- APK distribution firmado e instalado sobre 1.1.0 correctamente. Popup inspeccionado en Small_Phone.
- Reglas publicadas para games_v2; games mantiene la compatibilidad anterior. Salas 1.1 existentes usan ronda inicial 1.

La prueba Android usa dos identidades en un emulador; la audición del teléfono físico y la partida manual entre dos dispositivos requieren ambos APK 1.2.0.
