# Validación 1.3.0 · build 5

12 de octubre de 2026 · America/Bogota.

- Compilación distribution, debug y androidTest correcta; 15 pruebas unitarias sin fallos.
- Firebase: incremento único de ganadas, rechazo de modificación manual del puntaje, conservación del marcador al reiniciar la ronda y reglas de códigos, turnos, victoria y revancha correctas.
- Android: sonidos X y O comprobados por recursos diferentes; tras ganar X una ronda y O otra, ambos muestran Ganadas: 1 / Perdidas: 1. Popups, sonidos de victoria/derrota y revancha siguen funcionando.
- WAV de X y O diferentes (SHA-256 distintos).
- Marcador en tres líneas inspeccionado en Small_Phone.
- Firma del APK verificada e instalación sobre la versión anterior correcta.

La verificación usa dos identidades en un emulador. La audición en el teléfono físico requiere volumen multimedia y ambos APK actualizados. Los resultados anteriores a 1.3 no se reconstruyen porque no se almacenaron por ronda.
