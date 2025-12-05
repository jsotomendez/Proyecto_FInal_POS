# Sistema POS Web - Gestión Integral para Restaurantes y Retail

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.8-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Frontend-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-Deploy-0B0D0E?style=for-the-badge&logo=railway&logoColor=white)

## Descripción del Proyecto

Este proyecto es una **Plataforma de Punto de Venta (POS)** basada en la web, desarrollada como proyecto final para la asignatura de **Programación 3** de la carrera de Ingeniería de Sistemas en la Universidad de Córdoba.

El sistema está diseñado para centralizar y automatizar la gestión operativa de cadenas de restaurantes o comercios con múltiples sedes. Permite el control en tiempo real de inventarios, facturación electrónica, gestión de proveedores y análisis de ventas, resolviendo la problemática de la fragmentación de datos y la falta de trazabilidad en los negocios tradicionales.

---

## Problemática y Solución

### La Problemática
Las cadenas de restaurantes que operan con ventas presenciales y domicilios a menudo enfrentan:
* Información fragmentada en hojas de cálculo o sistemas desconectados.
* Errores humanos en el control de inventario (stock que no cuadra).
* Dificultad para consolidar reportes de ventas de múltiples sedes.
* Lentitud en el proceso de toma de pedidos y facturación.

### La Solución
Se desarrolló un sistema web robusto que garantiza:
1.  **Integridad de Datos:** Uso de una base de datos relacional normalizada para asegurar la consistencia.
2.  **Automatización:** El inventario se descuenta automáticamente con cada venta y se suma con cada compra.
3.  **Toma de Decisiones:** Un Dashboard gerencial con KPIs y gráficos en tiempo real.
4.  **Escalabilidad:** Arquitectura preparada para soportar múltiples sedes y usuarios concurrentes en la nube.

---

## Arquitectura de Software

El proyecto sigue una arquitectura **Monolítica Modular** basada en el patrón **MVC (Modelo-Vista-Controlador)**, aprovechando la inversión de control de Spring Framework.

### Capas del Sistema:
1.  **Capa de Presentación (Frontend):**
    * Renderizado del lado del servidor con **Thymeleaf**.
    * Estilizado con **Bootstrap 5** y una hoja de estilos personalizada (CSS).
    * Lógica dinámica en el cliente (Carrito de compras, Gráficos) usando **JavaScript (ES6)** y **Chart.js**.
    * Comunicación asíncrona mediante **Fetch API** para el módulo POS.

2.  **Capa de Lógica de Negocio (Backend):**
    * Controladores (`@Controller` para vistas HTML, `@RestController` para APIs JSON).
    * Servicios (`@Service`) que encapsulan las reglas de negocio (ej: validación de stock, cálculo de totales, transacciones).
    * **DTOs (Data Transfer Objects):** Para desacoplar las entidades de la base de datos de los datos enviados por la vista (ej: `PedidoDTO`, `DashboardDTO`).

3.  **Capa de Persistencia (Data Access):**
    * **Spring Data JPA:** Abstracción para el acceso a datos.
    * **Hibernate:** ORM (Object-Relational Mapping) para mapear clases Java a tablas MySQL.
    * Consultas avanzadas con **JPQL (Java Persistence Query Language)** y consultas nativas SQL para reportes complejos.

---

## Tecnologías y Herramientas

| Componente | Tecnología / Versión | Descripción |
| :--- | :--- | :--- |
| **Lenguaje** | Java 21 (LTS) | Última versión de soporte a largo plazo con características modernas. |
| **Framework** | Spring Boot 3.5.8 | Framework principal para inyección de dependencias y configuración automática. |
| **Base de Datos** | MySQL 8.0.44 | Motor de base de datos relacional robusto. |
| **Gestor de Dependencias** | Maven | Gestión de librerías y ciclo de vida de construcción. |
| **Frontend** | Thymeleaf + Bootstrap 5 | Motor de plantillas y framework CSS responsivo. |
| **Gráficos** | Chart.js | Librería JS para visualización de datos en el Dashboard. |
| **Despliegue** | Railway | Plataforma PaaS para el hosting de la aplicación y base de datos. |

---

## Despliegue en la Nube (Railway)

Este proyecto está configurado para ser desplegado fácilmente en **Railway**.

1.  **Base de Datos:** Se utiliza el servicio de MySQL proporcionado por Railway.
2.  **Configuración:** La aplicación detecta automáticamente las variables de entorno (`MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, etc.) para la conexión en la nube.
3.  **Puerto Dinámico:** Se ha configurado `server.port=${PORT:8080}` para adaptarse al puerto asignado por la plataforma.

---

## Modelo de Datos (Base de Datos)

La base de datos `pos_db` consta de **15 tablas** interconectadas. Los puntos clave del modelo son:

* **Normalización:** Estructura optimizada para evitar redundancia.
* **Integridad Referencial:** Uso estricto de Llaves Foráneas (FK) para relacionar `Pedido` -> `Cliente`, `Sede`, `Usuario`, etc.
* **Manejo de Dinero:** Uso de tipos de datos `DECIMAL` para precisión financiera.
* **Auditoría:** Campos de fecha (`datetime`) automáticos mediante `@PrePersist` de JPA.

**Tablas Principales:**
* `pedido` & `orderitem`: Cabecera y detalle de ventas.
* `compra` & `purchaseitem`: Cabecera y detalle de abastecimiento.
* `inventario`: Tabla de cruce entre `sede` y `producto` para control de stock multi-sede.
* `factura` & `pago`: Gestión fiscal y financiera.

---

## Guía de Estilo (UI/UX)

Se implementó un sistema de diseño personalizado (`styles.css`) sobre Bootstrap para ofrecer una experiencia corporativa y profesional.

* **Paleta de Colores:**
    * 🟦 **Base:** `#2c3e50` (Azul Medianoche) - Usado en barras de navegación y encabezados para transmitir seriedad y confianza.
    * 🟧 **Acento:** `#e67e22` (Naranja Calabaza) - Usado en botones de acción principal (Guardar, Confirmar) para guiar al usuario.
    * ⬜ **Fondo:** `#f4f7f6` (Gris Hielo) - Para reducir la fatiga visual.
* **Tipografía:** *Segoe UI* / *Roboto* para una lectura moderna y limpia.
* **Feedback Visual:** Uso de modales para pagos y alertas de confirmación para acciones destructivas.

---

## Instrucciones de Instalación (Local)

### Prerrequisitos
1.  Tener instalado **Java JDK 21**.
2.  Tener instalado **MySQL Server 8.0**.
3.  Tener instalado un IDE (IntelliJ IDEA recomendado).

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/JoseG-SotoMendez/FinalProject.git](https://github.com/JoseG-SotoMendez/FinalProject.git)
    ```
2.  **Configurar Base de Datos:**
    * Abre el archivo `src/main/resources/application.properties`.
    * Verifica que el usuario y contraseña de MySQL coincidan con tu instalación local:
        ```properties
        spring.datasource.username=root
        spring.datasource.password=TU_CONTRASEÑA
        ```
    * Spring Boot creará la base de datos `pos_db` automáticamente al iniciar.

3.  **Cargar Datos Iniciales (Opcional pero recomendado):**
    * Una vez inicie la aplicación por primera vez (para que se creen las tablas), ejecuta el script SQL `datos_semilla.sql` (proporcionado en la documentación) en tu gestor de base de datos para poblar el sistema con productos, clientes y ventas históricas.

4.  **Ejecutar la Aplicación:**
    * Desde IntelliJ, ejecuta la clase `FinalProjectApplication.java`.
    * O usa la terminal: `./mvnw spring-boot:run`

5.  **Acceso:**
    * Abre tu navegador en: `http://localhost:8080/`

---

## Autores

* **José Gil Soto Méndez** - *Ingeniería de Sistemas*
* **Miguel Guerra Negrete** - *Ingeniería de Sistemas*
* **Andrés Luna Moreno** - *Ingeniería de Sistemas*

**Universidad de Córdoba** - Montería, Colombia  
*2025*