Para poder probar nuestra app, primero de todo necesitarás tener un emulador de WearOs en Android Studio Proyect. Como recomendación hemos estado usando
la versión R, API level:30 (Android 11 Wear OS).

CONFIGURAR DISPOSITIVO:
1. Configurar tu emulador, una vez iniciado (Solo emulador) tienes que ir a la barra superior y en el icono en el que aparecen 3 puntos en vertical(Extended Controls)
   buscaremos en la ventana emergente que se abrirá el apartado "Location" y ahí tendremos que buscar la ubicación que deseemos tener como ubicación actual. Una vez 
   tengamos dicha ubicación en el panel de la derecha(Saved Points) clickaremos en dicha ubicación y en los botones de abajo pulsaremos el que dice "Set Location".

Todo el paso 1 es necesario ya que si no cogerá tu ubicación actual del emulador.

2. Volviendo a la ventana emergente Extended Controls, ahí buscaremos el apartado Microphone, y deberemos activar la opción que dice "Virtual Microphone uses host audio input",
   esto es necesario para poder hacer una búsqueda sobre el destino al que quieras ir. También hay que decir que cuando lo probé por primera vez funcionaba sin problemas, 
   las ultimas veces que lo probé no me captaba la voz, seguimos sin saber el porqué. Pero creando un nuevo emulador y haciendo todo de nuevo se soluciona.
   
3. Un ultimo cambio, un poco más opcional, que sirve para cambiar el idioma del emulador y que así si te reconozca la voz en Español. Busca en las apps de tu dispositivo 
   una llamada "Custom Locale" y seleccionar español, si no existe tienes que crearla escribiendo "es-ES". Si la app "Custom Locale" no te aparece tienes que activar el modo                  desarrollador, esto en cada dispositivo es diferente, pero suele ser hacer un numero de pulsaciones en "Serial Number", una vez activado, haz lo dicho anteriormente.
   
Hecho todo esto, la applicación estaría lista para funcionar.

EJECUTAR:
Como recomendación cuando sea la primera vez que ejecutes la app, primero clickar "¿Donde estoy?" ya que cargará mejor los mapas, que si directamente entramos en 
"Quiero ir a..." hay veces en las que no carga el mapa y se queda una pantalla en gris. También que si cuando estes intentando que el microfono te capte la voz 
esto a veces no funciona por culpa del emulador de Android Studio no sabemos bien de donde proviene el problema, pero la única solucion que conocemos es crear un nuevo emulador,
aún así también permite entrada por texto escrito, esto es de ayuda para los dispositivos que no dispongan de microfono.
