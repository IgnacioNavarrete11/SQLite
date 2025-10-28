# Le Piqué del Nachez - App de Gestión

Este proyecto es una aplicación de Android diseñada para la gestión de un menú de restaurante, incluyendo funcionalidades para clientes y administradores. La app permite a los usuarios ver el menú, mientras que los administradores pueden gestionar tanto los platos como los usuarios del sistema.

## ✒️ Autores

*   **Ignacio** - *Desarrollo inicial y arquitectura*
*   **Benjamin** - *Testing y cheqqueo*

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
        4.  **Seguridad:** Implementa el hasheo de contraseñas utilizando la librería `jbcrypt`. Las contraseñas nunca se guardan como texto plano. Al registrar un usuario, su contraseña se convierte en un hash seguro antes de almacenarse. Al iniciar sesión, la contraseña ingresada se compara con el hash almacenado, garantizando una autenticación segura.

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

## Diagrama Flujo: https://sl1nk.com/LQ0lB
## Diagrama Clases: https://sl1nk.com/8uaDP
## Diagrama de secuencia: https://sl1nk.com/8SxH2

