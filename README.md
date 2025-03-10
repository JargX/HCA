La herramienta "Herramienta_Copia_Archivos" permite realizar una copia automatizada de los archivos y configuraciones más relevantes de un usuario en Windows, incluyendo carpetas personales (Descargas, Escritorio, Documentos, Videos, Imágenes), 
accesos directos de red, configuraciones de Microsoft Office y perfiles de Firefox. También exporta ciertas claves del registro importantes para configuraciones de red y Office.

**Instrucciones para el despliegue:**

- Coloca las librerías de JavaFX descargadas desde el sitio oficial y el JRE en la carpeta:

```
C:\HCA_Beta_Deploy
```

La estructura debe quedar así:

```
C:.
├───HCA_Beta_Deploy    <-- coloca aquí las librerías de JavaFX y JRE
└───Herramienta_Copia_Archivos
    ├───.settings
    ├───bin
    │   └───Principal
    ├───jre
    │   ├───lib
    │   │   └───security
    │   ├───bin
    │   ├───include
    │   │   └───win32
    │   └───conf
    ├───output
    └───src
        └───Principal

Para ejecutar correctamente el programa, asegúrate de que la carpeta `HCA_Beta_Deploy` contenga las versiones adecuadas de JavaFX (`javafx-sdk`) y JRE-OpenJDK necesarias para la ejecución de la aplicación. 
Esto garantiza la compatibilidad y evita errores relacionados con módulos faltantes al momento de ejecutar la herramienta.

