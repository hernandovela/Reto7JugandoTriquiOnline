# Reto 7 · Jugando Triqui Online · 1.3.0

Android nativo Java, basado en el Reto 6 de Hernando Vela. Usa Authentication anónimo y Cloud Firestore (default) del proyecto reto7jugandotriquionline.

## Instalar y jugar

Ambos jugadores deben instalar TriquiOnline-Reto7-1.3.0.apk. Usa WhatsApp > Adjuntar > Documento para enviarlo. El destinatario descarga el archivo y lo abre desde Mis archivos > Descargas. Esta versión mantiene paquete y firma de 1.0.1 para actualizarla sin desinstalar.

1. Escribe tu nombre (1 a 24 caracteres).
2. Crea una partida. Se genera un código como ABCD-2345.
3. Pulsa Copiar código y pégalo en WhatsApp para el otro jugador.
4. El otro jugador escribe su nombre, pega el código y pulsa Ingresar a la partida.
5. Ambos ven los nombres de X y O. X comienza; el tablero solo acepta el turno correspondiente.

Cualquiera puede crear partidas y copiar el código. La lista muestra salas esperando, pero para ingresar a la partida de otra persona hay que escribir el código. Puedes reanudar tus salas desde la lista. Los códigos no constituyen una contraseña privada: las salas siguen siendo listables para usuarios autenticados.

La app guarda el nombre y la partida actual al cerrar o girar. Salir cierra la partida para ambos. Al terminar, cada jugador ve su popup de victoria, derrota o empate. Ganador y perdedor oyen sonidos distintos. Pulsa Seguir jugando: la revancha comienza cuando ambos aceptan, conserva el código y muestra la nueva ronda. Salir cierra la sala para ambos. Los dispositivos solo necesitan Internet, pueden estar en redes distintas.

## Android Studio

Abre este proyecto y usa el JDK incorporado de Android Studio y SDK Android 36. minSdk 24 (Android 7).

- app/google-services.json contiene la configuración local del paquete com.hernandovela.reto7 y no se publica en Git.
- FirebaseConfig inicializa el SDK explícitamente; Gradle copia la configuración como asset.
- gradlew.bat assembleDistribution testDebugUnitTest genera el APK firmado en app/build/outputs/apk/distribution/app-distribution.apk.
- La variante distribution usa la misma clave local de las versiones anteriores; no es una publicación en Play Store.

La versión visible proviene de BuildConfig.VERSION_NAME y VERSION_CODE, definidos en app/build.gradle.kts. Consulta CHANGELOG.md.

## Firebase y compatibilidad

Authentication anónimo habilitado y reglas firestore.rules publicadas. Las versiones 1.1 usan games_v2; los documentos usan el código normalizado como ID y almacenan hostName/guestName. Una transacción crea cada código sin sobrescribir otra sala. La unión también es transaccional para evitar un tercer jugador.

Las reglas mantienen games para los APK 1.0.x. Ambas personas deben actualizar a 1.1.0 para utilizar códigos y nombres; las partidas antiguas no se migran ni se borran.

## Verificación

- gradlew.bat testDebugUnitTest: reglas del tablero, normalización y validación de códigos y nombres.
- node scripts/verify-cloud.mjs: dos cupos, unión concurrente, nombres compartidos, rechazo de cambios de identidad/código, turnos y victoria contra la nube real.
- Prueba Android OnlineSmokeInstrumentation: dos usuarios, entrada con código formateado, nombres en pantalla, giro y jugadas sincronizadas.

Las pruebas crean salas identificadas como verificación y las finalizan o cierran. Las cuentas anónimas de prueba permanecen en Firebase.

La versión 1.2.0 muestra el resultado y reproduce el sonido una vez por ronda y jugador; Ver resultado / seguir jugando permite volver a abrir el popup sin repetir el sonido. Ambas personas deben actualizar a 1.2.0 para aceptar revanchas. Las salas 1.1 existentes admiten la nueva función con ronda inicial 1.

## Marcador y sonidos · 1.3.0
Ganadas y perdidas se muestran debajo del nombre de cada jugador. El marcador es por partida/código, se guarda en Firebase y continúa en las revanchas. Cada victoria incrementa una sola vez al ganador y equivale a una pérdida del rival. Los empates no cuentan como derrota. Las rondas anteriores a 1.3 no almacenaban ganadores históricos y no se reconstruyen: el contador comienza en cero cuando faltan campos.
Cada X confirmada reproduce human_move.wav y cada O confirmada reproduce computer_move.wav en los clientes conectados. El sonido no se repite por snapshots duplicados, carga inicial, giro o reinicio del tablero. Los sonidos de resultado de 1.2 permanecen. Ambos jugadores deben actualizar a 1.3.0; las nuevas reglas requieren actualizar el marcador junto con la jugada.
