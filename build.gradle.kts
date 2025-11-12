// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Se usa la declaración con "id"
    // sirve descargando las herramientas necesarias dadas por google
    //para poder hacer la conexión a firebase configurando la conexión
    //con Apply false decidiendo que solo se descargue.

    //NO OLVIDAR INSTALAR Y AÑADIR LA LIBRERIA DE FIREBASE
    //y el json de firebase en el proyecto que es en este caso
    //google-services.json
    id("com.google.gms.google-services") version "4.4.1" apply false
}