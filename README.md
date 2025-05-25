# YiGO - Generación del APK

Este documento explica paso a paso cómo generar el archivo APK (Android Package) de la aplicación YiGO desde Android Studio, para que pueda ser instalado en cualquier dispositivo Android.

## Requisitos previos

Antes de generar el APK, asegúrate de cumplir con lo siguiente:

- Tener Android Studio instalado en tu equipo.
- Haber clonado o abierto el proyecto YiGO correctamente.
- Asegurarte de que el proyecto no presenta errores de compilación.

## Pasos para generar el APK

### 1. Abrir el proyecto en Android Studio

Inicia Android Studio y abre la carpeta del proyecto de YiGO. Espera a que cargue por completo y sincronice las dependencias.

### 2. Verificar que todo funcione correctamente

Revisa que no existan errores en el código. Puedes hacer esto ejecutando la aplicación en un emulador o dispositivo físico para asegurarte de que todo esté funcionando antes de generar el APK.

### 3. Generar el APK

Dirígete al menú superior y sigue la siguiente ruta:

Build > Build Bundle(s) / APK(s) > Build APK(s)

Android Studio comenzará a compilar el proyecto. Esto puede tardar unos segundos dependiendo del tamaño del proyecto y la capacidad del equipo.

### 4. Localizar el APK generado

Una vez finalice la compilación, Android Studio mostrará una notificación en la parte inferior derecha con el mensaje "APK(s) generated successfully". Desde allí puedes hacer clic en "locate" para abrir la carpeta donde se generó el archivo.

También puedes encontrarlo manualmente en la siguiente ruta dentro del proyecto:

<nombre_del_proyecto>/app/build/outputs/apk/debug/app-debug.apk

Este archivo `.apk` es el que puedes instalar en cualquier dispositivo Android.

### 5. Instalar el APK en un dispositivo Android

Para instalar el APK en un celular Android:

1. Copia el archivo APK al dispositivo (vía cable USB, Bluetooth o servicios en la nube).
2. Abre el archivo desde el administrador de archivos del dispositivo.
3. Si es la primera vez que instalas una app externa, el sistema te pedirá habilitar la opción de “Instalar apps desconocidas”.
4. Activa la opción en la configuración y regresa para completar la instalación.

---

## Notas importantes

- El APK generado de esta manera es de tipo **debug**, útil para pruebas o distribución interna.
- Es recomendable probar el APK en varios dispositivos para asegurar su compatibilidad.

---
