# Verificación final · Reto 7

Fecha: 11 de octubre de 2026 (America/Bogota).

- Proyecto Firebase: reto7jugandotriquionline; Cloud Firestore (default).
- Configuración Android: com.hernandovela.reto7, google-services.json local.
- Compilación assembleDebug y assembleDebugAndroidTest: correcta.
- Pruebas unitarias del tablero: correctas.
- node scripts/verify-cloud.mjs: PASS. Tres identidades, unión concurrente con un solo cupo disponible, rechazo del tercer jugador, turnos, casillas ocupadas, participantes, victoria y lectura desde otro cliente.
- OnlineSmokeInstrumentation en Small_Phone (emulator-5554): OK. Autenticación Android, creación y lectura desde Firestore, recreación de Activity y cierre de partida.

La prueba online usó clientes independientes contra la nube real. No se realizó una partida manual entre el emulador y un teléfono físico. Para jugar, instala app/build/outputs/apk/debug/app-debug.apk en el teléfono con Android 7 o posterior e Internet; crea la partida en un dispositivo y únete desde el otro.

Las verificaciones crean cuentas anónimas y salas identificadas como verificación; las salas quedan finalizadas o cerradas. La configuración Firebase permanece local y no se publica en GitHub.
