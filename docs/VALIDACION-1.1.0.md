# Validación de la versión 1.1.0 · build 3

12 de octubre de 2026, America/Bogota.

- Compilación debug, distribution y androidTest: correcta.
- 12 pruebas unitarias: 8 del tablero y 4 de códigos/nombres, cero fallos.
- Prueba contra Firebase real: PASS. Código único de 8 caracteres, dos cupos, unión concurrente, nombres de participantes, rechazo de cambios de código y nombre del rival, turnos, casillas ocupadas y victoria.
- Android Small_Phone: OK. Dos identidades anónimas, unión mediante código formateado en minúsculas, nombres visibles, giro horizontal y jugadas sincronizadas.
- Firma APK verificada para Android 7 y posteriores; instalación de distribution sobre 1.0.1 correcta.
- Vista del tablero y pantalla inicial inspeccionadas en el emulador; fondo, colores, botones y tablero Canvas del Reto 6.
- Firebase conserva games para 1.0.x y valida games_v2 para 1.1.0. No se borraron partidas antiguas.

Ambos dispositivos deben instalar 1.1.0 para jugar mediante los nuevos códigos. La prueba Android usa dos identidades en un emulador; no sustituye una partida manual entre emulador y teléfono físico.
