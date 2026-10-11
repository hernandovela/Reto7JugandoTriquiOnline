# Reto 7 · Jugando Triqui Online

Extensión del proyecto Reto 6 de Hernando Vela. Android nativo Java, tablero Canvas y recursos gráficos originales. Dos dispositivos conectados al mismo proyecto Firebase juegan por Internet.

## Configuración Firebase

Proyecto: reto7jugandotriquionline.

1. En Firebase, registra una aplicación Android con paquete `com.hernandovela.reto7`.
2. Descarga google-services.json y guárdalo en `app/google-services.json`. Gradle lo incluye automáticamente en los assets y la app selecciona el cliente cuyo paquete es `com.hernandovela.reto7`. También admite `app/src/main/assets/firebase.properties` como alternativa. Esta app inicializa Firebase explícitamente; no necesita el plugin google-services.
3. Authentication > Métodos de acceso > habilita Anónimo.
4. Crea Cloud Firestore en modo producción, edición Standard, base (default).
5. Publica el contenido de `firestore.rules` en la pestaña Reglas. También puedes ejecutar `firebase deploy --only firestore:rules --project reto7jugandotriquionline` con Firebase CLI y tu sesión autorizada.

No se incluyen credenciales inventadas ni reglas de acceso público. Las reglas validan participantes, dos cupos, turno, una sola casilla vacía modificada y resultado real.

## Android Studio y dos dispositivos

Abre la carpeta del proyecto en Android Studio. Usa el JDK incorporado y el SDK Android 37 instalado. Sincroniza Gradle y ejecuta app en un AVD con Internet. Instala el mismo APK en el teléfono del segundo jugador (Android 7 o posterior).

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

1. Ambos abren la aplicación con Internet.
2. El primer jugador crea una partida y espera como X.
3. El segundo ve esa partida en la lista y pulsa Unirse; juega como O.
4. X comienza. El tablero solo acepta jugadas del jugador al que corresponde el turno.
5. Victoria, empate o salida se reflejan en ambos dispositivos. Para otra partida, vuelven a la lista y crean una nueva.

El teléfono puede estar en otra red; no se utiliza localhost ni una IP del emulador. Firestore media todas las operaciones. Las transacciones evitan el tercer jugador y movimientos simultáneos. Al perder conexión, el tablero se bloquea hasta recibir datos del servidor. Cerrar la app conserva la partida; Salir la cierra para ambos. Las salas abandonadas al cerrar la app quedan disponibles para reanudar; no hay eliminación automática.

## Validación

`gradlew.bat assembleDebug testDebugUnitTest`

Las pruebas heredadas validan las reglas locales del tablero. La verificación de Firebase con dos dispositivos requiere configurar el proyecto y publicar las reglas; no debe considerarse completada solo porque compila el APK.

Con google-services.json, Authentication anónimo y las reglas publicadas, ejecuta `node scripts/verify-cloud.mjs`. Verifica tres identidades independientes, competencia por el segundo cupo, rechazo de un tercer jugador, turno incorrecto, sobrescritura de casilla, cambio de participante, victoria y lectura desde el otro cliente. Crea una partida de verificación identificada por su nombre; queda finalizada o cerrada en la base.

`gradlew.bat connectedDebugAndroidTest` ejecuta la prueba Android contra Firebase: inicio anónimo, creación y lectura del servidor, recreación de Activity y cierre de una sala de prueba. Requiere un emulador o dispositivo conectado y Firebase configurado. La prueba antigua de sonidos del modo contra la computadora fue reemplazada porque sus controles no existen en la modalidad online.

Documentación: [transacciones Firestore](https://firebase.google.com/docs/firestore/manage-data/transactions) y [autenticación anónima Android](https://firebase.google.com/docs/auth/android/anonymous-auth).
