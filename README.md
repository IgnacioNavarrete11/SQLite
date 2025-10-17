# Le Piqué del Nachez - App de Gestión

Este proyecto es una aplicación de Android diseñada para la gestión de un menú de restaurante, incluyendo funcionalidades para clientes y administradores. La app permite a los usuarios ver el menú, mientras que los administradores pueden gestionar tanto los platos como los usuarios del sistema.

## ✒️ Autores

*   **Ignacio** - *Desarrollo inicial y arquitectura*

## 🛠️ Entorno de Desarrollo

Aunque no puedo detectar la versión exacta de Android Studio, la aplicación está configurada con las siguientes especificaciones técnicas, las cuales son compatibles con las versiones modernas del IDE (como "Hedgehog" o "Iguana"):

*   **Lenguaje:** Java
*   **Base de Datos:** SQLite nativa de Android.
*   **SDK Mínimo:** Se recomienda API 24 (Android 7.0 Nougat) o superior.
*   **SDK Compilación/Objetivo:** API 33 (Android 13) o superior.

## 🚀 Cómo Funciona la Aplicación

El flujo de la aplicación es sencillo e intuitivo:

1.  **Pantalla de Carga (Splash Screen):** Al iniciar, una pantalla de bienvenida se muestra durante unos segundos.
2.  **Inicio de Sesión:** El usuario es dirigido a una pantalla de login donde puede ingresar sus credenciales. También hay una opción para navegar a la pantalla de registro si no tiene una cuenta.
3.  **Redirección por Rol:**
    *   Si el usuario es un **administrador** (ej. "admin"), es dirigido al `AdminActivity`, un panel de control avanzado.
    *   Si es un **usuario normal**, es dirigido al `MainMenuActivity`, donde puede visualizar el menú del restaurante.
4.  **Panel de Administrador (`AdminActivity`):** El administrador tiene acceso a dos funcionalidades principales:
    *   **Gestionar Platos:** Puede añadir, editar y eliminar platos del menú.
    *   **Gestionar Usuarios:** Puede navegar a una pantalla separada (`UserManagerActivity`) para ver, crear, editar y eliminar usuarios, así como asignarles el rol de administrador.
5.  **Menú Principal (`MainMenuActivity`):** El usuario estándar puede ver la lista completa de platos disponibles, organizados y formateados.
6.  **Cerrar Sesión:** Desde el menú principal, el usuario puede cerrar su sesión y volver a la pantalla de login.

## 🏗️ Arquitectura y Clases Principales

La aplicación sigue una arquitectura básica pero robusta, separando la interfaz de usuario, la lógica de negocio y el acceso a datos.

### Directorio `ui` (Interfaz de Usuario)

Contiene todas las `Activities` (pantallas) de la aplicación.

*   `MainActivity`: La actividad de entrada. Muestra un splash screen y redirige a `LoginActivity`.
*   `LoginActivity`: Gestiona el inicio de sesión del usuario.
*   `RegisterActivity`: Permite a los nuevos usuarios crear una cuenta.
*   `MainMenuActivity`: Muestra el menú de platos a los usuarios estándar.
*   `AdminActivity`: Panel de control para administradores, desde donde se gestionan platos y se accede a la gestión de usuarios.
*   `UserManagerActivity`: Permite al administrador realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los usuarios.
*   `FoodCursorAdapter` y `UserCursorAdapter`: Clases clave que actúan como puente entre los datos obtenidos de la base de datos (en un `Cursor`) y las `ListViews` que los muestran. Son responsables de inflar el layout de cada fila y poblarlo con los datos correspondientes.

### Directorio `data/db` (Base de Datos)

Centraliza toda la lógica relacionada con la base de datos SQLite.

*   **`TotalFoodContract.java` (El "Contrato"):**
    *   **Para qué sirve:** Es el "diccionario" o el esquema oficial de la base de datos. Su única función es definir de manera centralizada y sin errores los nombres de las tablas y columnas como constantes `public static final`.
    *   **Cómo funciona:** Evita errores de tipeo en el código (ej. "usrename" en lugar de "username") y hace que las consultas a la base de datos sean más legibles y fáciles de mantener. Si necesitas cambiar el nombre de una columna, solo lo haces en este archivo.

*   **`FoodDbHelper.java` (El "Ayudante"):**
    *   **Para qué sirve:** Es el guardián y administrador principal de la base de datos. Hereda de `SQLiteOpenHelper`, una clase de Android que facilita enormemente el trabajo con SQLite.
    *   **Cómo funciona:**
        1.  **Creación (`onCreate`):** Se ejecuta automáticamente la primera vez que se accede a la base de datos. Aquí se escriben las sentencias `CREATE TABLE` usando las constantes del `Contract`.
        2.  **Actualización (`onUpgrade`):** Si incrementas el número de `DATABASE_VERSION`, este método se ejecuta automáticamente. Es el lugar ideal para `ALTER TABLE` o, como en este caso, para borrar las tablas antiguas y volver a crearlas (una estrategia común durante el desarrollo).
        3.  **Métodos CRUD:** Contiene todos los métodos públicos para interactuar con la base de datos (`addUser`, `checkUser`, `getAllFoodItems`, etc.), encapsulando la lógica SQL y devolviendo los datos de una manera limpia al resto de la app.

## 👥 ¿Para Quién va Dirigido?

Este proyecto es ideal para:

*   **Pequeños Restaurantes o Cafeterías:** Que necesiten una solución digital sencilla para administrar su menú sin depender de sistemas complejos o costosos.
*   **Estudiantes de Desarrollo Android:** Sirve como un excelente caso de estudio práctico que abarca conceptos fundamentales:
    *   Múltiples Activities y navegación con `Intent`.
    *   Uso de `ListView` con `CursorAdapter` para mostrar datos de manera eficiente.
    *   Gestión completa de una base de datos SQLite con `SQLiteOpenHelper` y un `Contract`.
    *   Implementación de lógica de roles (usuario vs. administrador).
    *   Uso de diálogos (`AlertDialog`) para la edición de datos.

## 💡 Posibles Mejoras a Futuro

La base del proyecto es sólida y puede expandirse con nuevas funcionalidades:

*   **Imágenes de Platos:** Añadir soporte para que el administrador pueda subir una foto para cada plato y mostrarla en el menú.
*   **Sistema de Pedidos:** Crear una tabla `orders` que relacione usuarios y platos, permitiendo a los clientes hacer pedidos desde la app.
*   **Búsqueda y Filtros:** Implementar una barra de búsqueda en el menú para que los clientes puedan filtrar platos por nombre o categoría.
*   **Migración a `RecyclerView`:** Reemplazar las `ListViews` por `RecyclerView`, que es el componente moderno y más eficiente en Android para mostrar listas.
*   **Uso de una Arquitectura Moderna:** Refactorizar el código para usar patrones como **MVVM (Model-View-ViewModel)** con **Android Jetpack (ViewModel, LiveData, Room)**. Room, en particular, es una capa de abstracción sobre SQLite que reduce drásticamente el código repetitivo y es menos propenso a errores.
*   **Hash de Contraseñas:** En lugar de guardar las contraseñas como texto plano, utilizar una librería de criptografía como `javax.crypto` o las utilidades de `androidx.security` para guardar un hash seguro.

## Diagrama Flujo: https://www.plantuml.com/plantuml/dpng/pPJFQXin4CRlUehfdXAewTtWaYMkIS1RIgXllKoaiQrMAosIrY5zcpv2wQrNlrZ7Okrg5LmClSHW7KRVV9e_F_OcHFInRuru4xVK4dG6jHK2PNGULg57hvteMAEbxj16g9Pkesqjezxf-5m11bZMOyN2EJL_l2FJaK-2nVnkhAXLMtZKXKU3sZPa-v6eQMeXPZDOcFwhWqzUsuFSm0HIKP1iTOWUbVCmcmanT02Jzrpb5ExjnbD0w8VmCqXdMHve_nDByMSd4HvTtF-oK0sPlagpqJkCn28XbZNB3mrEGMv9Vbi5ybSz7nPRUWiTXl3alBe-PQLXy1ycmpCF51sh8VgUXAAHBhVN8RLtCly_ovfbbfvu8f5ilBhEmWc9uzpvHb7MSTO-hyowIE3rPXl1hLDoqzDX_jou9GEA0FETVB7IMN2TrCvIWDk7rHoaQpj34OEewzJ1i8PfgislyViB6v3eDwWmBwtEqQWDPrhaAzxcqH0-Ej-YILoWYLbu38u0_fq1HtfInDIXeG2AJY3PP9rdvXHb-2H9ElAX0lyk9ccKpFeKEBV-T5aWS2hXdtHT_CmQJRZadQOfc2ll2l1AopIdSdYSc08Z4ZcQeTYItV-6xlYrA1WqkjKH5OPaqpG5WyqXETlqW9uhYajra5KPWvppmNld6S3Rd9fxkzl_C9fVF95uvlvY4Bapvpt9YE7L87Wv6yN0XwrLG-HTzeN-swWBrKiyqW9k-EXRymS0
## Diagrama Clases: https://www.plantuml.com/plantuml/dpng/jLNDRjGm4BxdAKmvi0jiL74fYb3L_IagKYjAMyhPcmofLSUEx4dB2dWg7e4Nmqxix7hTQ2krd3OxtvUPB_EDnqSQgSAcuiaBl84AICqf4qcYRvcegQ8LoJdLclnC29dH_BPKiX75N7AfoBSRXc3ZKXMWkjYC6vA9JPMIzrtevVx-mVl5GV8xIMe3qXB8tZH7TiUGWTwpgLsHCrFQ8QisJCY45CiJu3MeZ7oKibYuVotuXaWnLq0HHZEZYiEu3r-0RYeO-O06REeBo5Sv1tNDuBv5Z29V-bIM3zG6_Af12Lf1HeuBXbVm0tletBntBrKHGKiKRRgCp1f4APxm8HGHgXhRTDkIBw1a6a4zdsgVyVc4-unEU-3q6OWcqgxkR8rF29NEoAPX3gS5hT5BcJTAIpNjGayGooKj-hBRKgT5zS1at1b9vxIqTQxzupk78LsUqfLiC2DN_j51jUKvmLcN-hmF3SaCXjXaZ2HQky-eQ1F53RNeZcPQodCsq-PpAeDZlw2cDrJ3ng4FCcyS-enSVZbb2FuL1-HIe9dPZCoxXwvea6JaeF6spZ2XXsdHoXrTecAYV4l2tt56bb9oeCBHynl8R-CNOYBJhRsZ0RW4d7A-CNKPwHnOo_BWA4rJ0tCfoj3tAucqJM6_MD4SNV_IrBP78nMepPzuM8uDVTMJxPHo8DfyMaKZlis_cn_Ox8OesEtHURNOeAKst9B3hlHUsY4-aRomcwKRpZwofiwOA3Q9wyxRhivb8z2-pGJ67VfVKctknqWD1FLx8XBqNT6o0e7-s9jUR4NysBPQF3XY0iThxex662Mlo0ae43cZEfdMzLnMDUrBa0-_9fFWyXxW1BVi42cwrmPuqHqon6kxjulqm0fDacvXVAv1kFLagU4gtDoggNF7qQ8BQfWRxrzlpILIX80R3ODrntwIr2ndV_-895uBhsqDV-u7yFwSstmBg44Kpl30DJAP76wt8FGhPeHEFPuHTJ2mikN4tOYTZEeDas8Zak1-IjF3TIDZQDt3BUqHlbKeIdq4ecWg_Wy0
## Diagrama de secuencia: https://www.plantuml.com/plantuml/dpng/dLJDQjmm4BxhAQO-jOU-G61Bp9A5W5N2Xirzi2QE6jbQHdBQFawVeRTUysBrU9Mrh2wb58B3pdppyttZpzSX8iUnT-fTVAAUuEZG3afX6pt38Op8rYjrd42sjKSS8bHwGjHjj2ysVgy00-Xw3MXyLo2Q0h6dpeP8l0Bjwp_dQ1h6zQnoM6twObXT80w1MEE07V6EiUjfY3Fke7TARJQmS-CN3tTi1wbmO0W-umjrsDhNdmDiDaeb8U3Z9z3r5TmEKtC4F1uLocIC92BeMW3D19XLVLlg_GTbACSjHKsTjsxcrXrJG6YP34qBeQDGOhREjiz9vagfvZps0Pqro60m-f35soTgdqM6QHM5Bi8swmulhp-aB2Y0fSI6MTYf063wawAR02Wn6oAZyJo5tohg2vmbQoYdAf7vSGxMfs2zDA4u7XyiVRt3JjPTqdAkYJfoXmoFwOwhReAGWFm_5pYFHfY7kLVAJSaawHLeBtGOEWfnllTuoeH4eEXEBf0eaL0mUExHPL9e_PzQQ7sAQhtqaUrttflRI7rY88dB32P1Ndz1wtjhi9YOcjDWb11OtzyEnHkGDwzkc3rVbHMjPwOsOgYKD5riP0XEvcQVEQTyTHC4EL2LtjDzATlEyvx2wEBQFqtkd_tYdzIYSJGWF7hkHoVnCbykK2crJ_DB_onnD2PppqAGlja9w1M0OD-yJp7jVN_ORc_kx-LiIwz7CT3JV4j36P7aiuNcfMEKawlJEUSR0QVaiavnHQ3X32hlrlZEZr5EDbDCFmq5Gp1_2HH09cMzIBabPcG85CG-bShid9EjBtySIo7g2q88n_L6cePnlP3MVziezrIsazR_jfJM5xOI_QwdnzYxtm00