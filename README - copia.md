<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
# SQLite

=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

## Diagrama Flujo: https://img.plantuml.biz/plantuml/png/jLVDRXit4BuRy3jCVR6DeWtGetHm15T9ZLQf4JUvCylHYWsNt99SzGz-c1npoA7ehPS2rOjbkEHAv8hhE8Vg8EoIypyVFyu-jOuPrpNozEGBjy46ePLCgDEJqnCdd4JuLh3Qi8O1HtWckvyrV0LtmZgafMTQSrYqhPVkxVY7sOqIbT1UNZA_EwUrLOVM6GOhihsedDWAzpjSt94lkxdq0alR8kr_0nRhJd5j_U8BjiMQ0OCVT7tGctilBNaJJcW5PulEeNAYOfNO_QtWrWXLYPR9C_Zpz0J0O8lC-IU0sQ8AGXmblB8TCq9VpSDU-1ThkBWrQCaoW_ENMEy--C0Tdby2rKV1XHMNGHGW2IAKXd8c5QgEqT5x-Ct2AfeaXs4lZIzAnyYWqaqhqJ5OQzDqqg_DCy7NJ0hE35IiQVkI7VPy8l_z6mH2pZz-VpNaeFHbAakMLjgdQG2DqIOpXD9YbdNKu6--GqbD-vfn_ifYhZJu7we1-9jmskhf2CZYqlkUGHUp9nKgsSZiH3Ia_PDcri7PsEFP_4XoBPIm6oZq8lpU66HlntuL5-kiBFb2w2BykX5qPlh4XVAmf9AQ617bBcmVEoZjiXBSXuV4PWcDDwgcPsPAK1n1WJp0bbMx3magGaUkm_vszuxajIqtP6X5D6RmbmwDu8cnGXyEYAWsMBqTOI9wDyYHZYUJECJ09c98eaYLX1hK4kD5L1pY4NR16w4AvsDfeT_o3M9Scwj2QQT04PmU0RaWc4CbUqrUzeyFO4bw8Ym2wTwpukEIEgAjJ7fFNBD-UNHAmd_EMNloa6IZloY8rrq2bXI11pX7bhCbE6T1FgUx8xBsGDesezUUCj6ic69raMDBB7iKT-GTMo2onUulneLvikI2kl1aAGLLXXIZoYHeKX0sk_TESnqJE6QbbrWnKtygcYCU6eFbMjDTg4WNwg5iWwa72AYFOD-QXJExTwdd-m9SfzhwvJd4Si6jXq6nEFlISq6dMTSQL8TR7O2JE9uzfz0zhV4CKAFhnyyXDpIHfFkZSeuGEPd77Kga6S5dqgl4MuZEtoFpm00HZ10PCI1XioEY_eCqsidYfDbrBMMEXGG_cU90K7km1eUPXI7wIgkrC4qO50eqUHttQJu8rdh1QR8yn4-35ax4_uWC7isTUHEl1Oqn3Eu8aXHckN7JGq1Eu8cJwQD6vdkod3XXT7ooo-A8W8U1xmNDUVl1rVU7nkKLgkvpIBX1jVl7KoLDZtHDpl-tmXnVVt5W9wXfZvGGocTNSkMr7gXaCEE_E7J_vV6KLkW3vYC0
## Diagrama Clases: https://img.plantuml.biz/plantuml/png/rLdDRkCs4Bu7o3kmkuSwJR8eiEYY2BR1ofAS2B2jrB9Jz6JG4kCGAvCgHILrsts0ddlhhQ_HQrwiG_t_KBQptItGFTWnv-Dm-CrmPiXz5qiiPB89Zu--aFTaGr0KOigEZuwFulUKHLZW3V937CVer-CZX8ROVxyMF661oKCkthurM20u3IulqSlHrwDlHoCjQk6aW150lj42B2nnhWD0MeZ9cHJObxacmxHI61S14Ia43KEOemOD8VXZDlRozUitRvHL7zIU8W3WDK4lJ1nX513aC4d47Vv5_Rs84omeHuE5S_82vKQdkowd6Rwa3rHI4h_892WdnuWYasyYB7F45jMfoQ48lNg5JC0Jj58Vi6QE0K46-P1zrLLFm1-brbBJEV9knePtlVJCcMrFbtDdOhkBERf0bCa2T8euCmN1aWo6G6H8K8mVIE0my3hpYIT1SW9J7i24OivoMJFMOTId68tvkcR33bkRm9gn9A1oJdwMYvW8XY7CBf2T3tL1Dw3g4Vog1QqIAJbB5mBvCFrLIOFLDGaZ8a0quZomyf-zXBHtVuf2fLcPEUZAEBD8B0NVTcGDrXOnRgnUCbRdr0Ws5LLdgPSdX2KbUHpDo9h649w74D_1VYptzWRJyDzwfx3cipX8vCgVwICDQGqNr1dRO-GvkeDH8y2HB35c8c8kyh4A68AfjvGyfaif7C37-LXD7LuPGP2BHo77ahArCeopDdyPmHFC81yf1k8EnmV5E4vNSoJPn1eEutl-gEJAlrnO5CEX61GJD718AeLq1omW8P4LCEHiZMZml9EqxPAjFLsAceoaxl59SPD4HGaSmv08cUrmr_dIwNjEm2GmlmeOfMrFm1I-xGiM9TyJB0S6GhfIJn0eMIC8r80-0DBZg1TdZay1MgVt5Br6EloifrD1r6fQlik2mnFvEGxRnnQLGu_2ZkATVZGRd4nHkxrfhlB9Mflk1cfkfkmkn9aBntWBv3lvG0Nn4o48yxSZBZPOPjXfibaHaOrK93FogBmwK4qa52paPzzd-SwK9lWwGmfq1Lrd19I0QgbSKW-7KxIYB4XLgG_q01-WPAVMVMuDE8e0tAn6s-uHs9EUUeZyN-nzLxek8bNVmCF5WSUgMyzl4Ec-CrAyxyTK4ZSYB3ynchZT4xNb0QdYjJn_kOPpP1bpOsXuzlBMddcEEwttu3Nfr9ZO8FAaW7nTIEV6LMkiO4ZLgAVVvwxbUiYoqS9R63F7BLavHNwQTYnoXvDGfi7JIY2d26TrOf3fJukGQW3FyWKHKSrU-IlA6pkbPSLvI30hrxed_lkTgdPCJg80DzD_EUiP5fMKzjMJ6e366H-jLJIshOaqmh0gWLdmxlI4wKuSowWv0WTbVYzMREmf8B4lQAGAz1aAUBA2UrWag5yXVE1dpSMs7A0RrTw9K9cajjWqjrbDwYONPBURh5BnfpIiun5TQrU2UjWSMZKcorgQ7LrrfBDpkMf7DcUBQ2rme86UgJKH3uDRS6gwDVLR9S7iTrDLpQodFw5WSlHJGjGCejuQdlx6Ah6XrYA5nIfxGDgZX158s0bKw3cNEFLFyLhHJYHL0ii1QVAglMqqwcvNNRVUfecNaO13Dtj76GwRNS2K8m152O7aTay42R3OLqbiM6khMGdQbAU_9FLnnW_3hNvZ4uLa0uK3eo70JHuc6nRNC8taRxfJRsvCvxPNGwY4E1nhSwKIckvuCPccqkN2isS7m6uCp_l1dLdBQyExteEr9uOpteDnlALXJPof1WP1iZHdjZ6tb-akUY2MFRPx83qjqd_j3_NgXo3KGunmI9K67d-SaqQkQns2ispFd3atywnwxlJdp37tQJF11LVkxCTzhfmOLn17CvtBFv4lYxRbI_HT-G_DxB5XmaxjD05UspDxQZf63N5yf7bE_EtylF4Ks0jgzFkzgFQ3KI-mqPRseZGDtCvTr1jRHL3hWg5WcdkR1gMvIXyVTJk_Gc5rMTpf5AZnDwxdcCxJ7zE6Oxe9-QlSYFIGzW7AKxndtONdDfPhtjtEpozRc-Wy-tKXpMl5eV8MfRh-lAkg3wNnKUToqzNM2-cuk_NMUbahOXTfLOOIgLwy9Oaru2h3wi7j6_Rk6TsRs3w3TWR4rBYrhunk73R-Kq0HqumKrNUfoxykWfeEBv1e45CeAD3CWlH4Dw_ZZ_PKrMPhPtQYj3Al6tsw8Ahmsg3Gxnr16whUR4uKnUy82v9D-0y0
## Diagrama de secuencia: https://img.plantuml.biz/plantuml/png/XPJ1RjD048RlaV8EWxcqKeiK7oC5rJGDHQeGQXiuJxpJP62zQ-sk4-1jU01EF49VZDcjZUrGnCMMyl-py-q_urpuW2uqbPvENeGTLGIrHZRJoNGIE6Y2bMu-Mm-Au8xAXap92ETmnpvGXR2oLa5Hrz6_iLyXwqspETpILdpEmjetwDXcyFA6jxlmHZSqdM0PhEiaG0_hsNHI2msNNAC9RJcveWoyv_0jUcwBiIS2B3VNf6joKLyjh-KKCWe4vncgV0vNcYiswE23nl27wQqZCXrKLYYnP0ag7pTCof2e-3zHFY9QBFuA1XQBgAndSFvQhZY7UriYR9eGhEbII6oynq0fXDkYyxhMiFRaJaw7oaVKhEIY9LQrzPqI8ve3AfKA6daOhEWCQlJ-36Igh4y7pPuIZS---1hzBlaFrgbZSKlXa-E06qrBb1TwYbXZazgyOYEjGo-rqC75vL1q1D3VwjwY3v2r6QAosL1zOCD-5qyT7143Iy6FPz_5diVYGeflhDcUbvhBB_3uKnPy44CSSr5qHh-pXpZeAyMoBqj6RRUnPtI8PIrhjkJ77_Ah1KgUOPlBr27hOB9HtQKr3-mgBFdnbnbLjO83QWixehXY4Ybf2XIftWdw2QkZAUOfwBml-FUW1YOPrDF1lTJoE7lmooIfutdbmwpQURMtJUFgHMrHTSY-It00M6Wzaf-zK7lM-uRs5ih6-HZtiqHjnZUoEGYYD_A5V4U58tkHjkL2FbdvN_mD
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
