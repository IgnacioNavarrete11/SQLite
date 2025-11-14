# Le Piqué del Nachez - App de Gestión

Este proyecto es una aplicación de Android diseñada para la gestión de un menú de restaurante, incluyendo funcionalidades para clientes y administradores. La app permite a los usuarios ver el menú, mientras que los administradores pueden gestionar tanto los platos como los usuarios del sistema.

## ✒️ Autores

*   **Ignacio** - *Desarrollo inicial y arquitectura*
*   **Benjamin** - *Testing y cheqqueo*

## 🛠️ Entorno de Desarrollo

La aplicación está configurada con las siguientes especificaciones técnicas, las cuales son compatibles con las versiones modernas del IDE (como "Hedgehog" o "Iguana"):

*   **Lenguaje:** Java
*   **Base de Datos:** **100% Firebase**, utilizando **Cloud Firestore** para los datos (menú y usuarios) y **Firebase Authentication** para el registro y login.
*   **SDK Mínimo:** Se recomienda API 24 (Android 7.0 Nougat) o superior.
*   **SDK Compilación/Objetivo:** API 36 o superior.

## 🏗️ Arquitectura y Componentes Clave

La aplicación sigue una arquitectura que separa la interfaz de usuario, la lógica de negocio y el acceso a datos, todo conectado a los servicios de Firebase.

### Directorio `app` (Raíz del Módulo)

*   `google-services.json`: **¡El archivo más importante!** Este fichero contiene las "llaves" que conectan esta aplicación específica con tu proyecto de Firebase en la nube. **Nunca debe ser compartido públicamente.** Si clonas este proyecto en otro ordenador, necesitarás descargar tu propio archivo `google-services.json` desde la consola de Firebase y colocarlo en esta carpeta para que la app pueda conectarse a la base de datos.

### Directorio `ui` (Interfaz de Usuario)

Contiene todas las `Activities` (pantallas) de la aplicación.

*   `MainActivity`: La actividad de entrada. Muestra un splash screen y redirige a `LoginActivity`.
*   `LoginActivity`: Gestiona el inicio de sesión del usuario con **Firebase Authentication** y consulta su rol en **Firestore**.
*   `RegisterActivity`: Permite a los nuevos usuarios crear una cuenta en **Firebase Authentication** y guarda sus datos en **Firestore**.
*   `MainMenuActivity`: Muestra el menú de platos usando una `RecyclerView` conectada en tiempo real a Firestore.
*   `AdminActivity`: Panel de control para administradores. Gestiona los platos y permite el acceso a la gestión de usuarios, todo con `RecyclerView` y Firestore.
*   `UserManagerActivity`: Permite al administrador ver todos los usuarios y cambiar sus roles. Usa `RecyclerView` y se actualiza en tiempo real.

### Directorio `model` (Modelo de Datos)

Clases POJO (Plain Old Java Objects) que sirven como molde para los datos de Firestore.

*   `FoodItem.java`: Representa un plato del menú.
*   `User.java`: Representa a un usuario, con su nombre, email y rol.

### Directorio `adapter` (Adaptadores para RecyclerView)

Clases que conectan los datos de las listas (`foodList`, `userList`) con las `RecyclerView`.

*   `FoodAdapter.java`: Adaptador para mostrar la lista de platos.
*   `UserAdapter.java`: Adaptador para mostrar la lista de usuarios en el panel de administración.

## 🚀 Cómo Funciona la Aplicación

El flujo de la aplicación es sencillo e intuitivo:

1.  **Pantalla de Carga (Splash Screen):** Al iniciar, una pantalla de bienvenida se muestra durante unos segundos.
2.  **Registro y Login (Firebase Auth):** El usuario es dirigido a una pantalla de login donde puede ingresar sus credenciales. Si no tiene cuenta, puede navegar a la pantalla de registro. Todo el proceso es gestionado de forma segura por Firebase Authentication.
3.  **Redirección por Rol (Firestore):**
    *   Si el usuario tiene el rol de **"admin"** en la base de datos de Firestore, es dirigido al `AdminActivity`.
    *   Si es un **usuario normal**, es dirigido al `MainMenuActivity`.
4.  **Panel de Administrador (`AdminActivity`):** El administrador tiene acceso a dos funcionalidades principales, ambas en tiempo real:
    *   **Gestionar Platos:** Puede añadir, editar y eliminar platos del menú. Los cambios se guardan en Cloud Firestore y se reflejan instantáneamente para todos los usuarios.
    *   **Gestionar Usuarios:** Puede navegar a `UserManagerActivity` para ver la lista de todos los usuarios registrados y **cambiar su rol** (de "user" a "admin" y viceversa). Los cambios se guardan en Cloud Firestore.
5.  **Menú Principal (`MainMenuActivity`):** El usuario estándar puede ver la lista completa de platos disponibles, la cual se carga **directamente desde Cloud Firestore y se actualiza en tiempo real**.
6.  **Cerrar Sesión:** Desde ambas pantallas (admin y usuario), se puede cerrar la sesión.

## 👥 ¿Para Quién va Dirigido?

Este proyecto es ideal para:

*   **Pequeños Restaurantes o Cafeterías:** Que necesiten una solución digital, moderna y sin costo de servidor para administrar su menú.
*   **Estudiantes de Desarrollo Android:** Sirve como un excelente caso de estudio práctico que abarca conceptos modernos y esenciales:
    *   Navegación entre `Activities` con `Intent`.
    *   Integración completa con **Firebase (Authentication y Firestore)** para una solución 100% cloud.
    *   Implementación de lógica de roles (usuario vs. administrador) leída desde la nube.
    *   Uso de `RecyclerView` con adaptadores personalizados.
    *   Manejo de datos y actualizaciones en tiempo real.

## 💡 Posibles Mejoras a Futuro

La base del proyecto es sólida, moderna y puede expandirse con nuevas funcionalidades:

*   **Reglas de Seguridad en Firestore:** ¡El siguiente paso más importante! Definir reglas de seguridad en la consola de Firebase para garantizar que solo los usuarios autenticados puedan leer y que solo los administradores puedan escribir en las colecciones.
*   **Imágenes de Platos:** Añadir soporte para que el administrador pueda subir una foto para cada plato (usando **Firebase Storage**) y mostrarla en el menú.
*   **Sistema de Pedidos:** Crear una colección `orders` en Firestore que relacione usuarios y platos, permitiendo a los clientes hacer pedidos desde la app.
*   **Búsqueda y Filtros:** Implementar una barra de búsqueda en el menú para que los clientes puedan filtrar platos por nombre.

## Diagrama Flujo:

![Imagen de WhatsApp 2025-10-26 a las 00 11 01_33cd1d62](https://github.com/user-attachments/assets/7cb083eb-7762-4003-af7f-45f04f611c5e)

## Diagrama Clases:

![Imagen de WhatsApp 2025-10-26 a las 00 12 18_103d07ed](https://github.com/user-attachments/assets/0010d49c-de26-4659-80b6-12ad91daa523)

## Diagrama de secuencia:

![Imagen de WhatsApp 2025-10-27 a las 21 46 14_e65ece7e](https://github.com/user-attachments/assets/b6b36eb3-031a-4a16-ba09-148345ddac73)
