# User Service - Central de Autenticación y Usuarios

Este repositorio contiene el **User-Service**, un componente crítico e independiente diseñado bajo una arquitectura de microservicios para resolver la gestión de identidades, autenticación segura y el ciclo de vida de los usuarios en la plataforma.

El servicio está construido con un enfoque en el alto rendimiento, aplicando separación estricta de capas, validaciones de negocio en el dominio y una estrategia de seguridad robusta basada en tokens de un solo uso.

---

## Tecnologías y Arquitectura

El diseño de este microservicio se apoya en un stack moderno y robusto dentro del ecosistema de Java, enfocado en mantener un código limpio y mantenible:

* **Java 21 & Spring Boot 3.x:** Uso de las últimas características del lenguaje y el framework para un desarrollo eficiente.
* **Spring Security & JWT (JSON Web Tokens):** Seguridad stateless para proteger los endpoints y gestionar los roles de acceso de manera descentralizada.
* **MapStruct:** Biblioteca de mapeo de tipos segura para generar conversores entre entidades y DTOs sin escribir código repetitivo.
* **MySQL & Spring Data JPA:** Persistencia de datos relacional con consultas optimizadas y gestión del historial de estados de los usuarios.
* **Jakarta Validation & Custom Validators:** Validaciones estrictas en la capa de entrada mediante anotaciones personalizadas para aplicar reglas de negocio complejas desde el ingreso de los datos.
* **Docker & Docker Compose:** Contenerización del servicio y su base de datos para despliegues ágiles y entornos aislados.
* **Testing Suite (JUnit 5 & Mockito):** Arquitectura probada mediante tests de controladores con `MockMvc` aislando el contexto de seguridad.

---

## Funcionalidades del Sistema

* **Registro de Usuarios:** Validación estricta de campos obligatorios, formato de correo, contraseñas de alta seguridad y control de mayoría de edad.
* **Control de Estado de Cuentas:** Soporte nativo para estados activos, inactivos y baneados.
* **Historial de Estados:** Registro automático de auditoría que guarda cada cambio de estado de un usuario junto con la fecha exacta del evento.
* **Lógica de Bloqueo Integrada:** Capacidad para procesar suspensiones automáticas de cuentas basadas en el comportamiento o reportes del sistema.
* **Consultas Dinámicas:** Filtrado avanzado a la base de datos utilizando JPA Specifications (Criteria API).
* **Procesamiento Asíncrono:** Tareas programadas (Scheduled Tasks) para el mantenimiento autónomo del sistema.
* **Configuración CORS:** Políticas de seguridad implementadas de forma nativa para permitir la comunicación cruzada y segura con clientes frontend.

---

## Instalación y Ejecución

### Requisitos Previos
* **Docker** (Recomendado para levantar todo el entorno)
* **Java 21**
* Instancia de **MySQL** activa (si no se utiliza Docker Compose)

### Pasos para iniciar
1. Clonar el repositorio.
2. Configurar la conexión a la base de datos en el archivo `application.yml`.
3. Levantar el servicio usando el wrapper de Gradle:
   ```bash
   ./gradlew bootRun
   ```