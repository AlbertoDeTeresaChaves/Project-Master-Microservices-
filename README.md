# PROYECTO DE MICROSERVICIOS

Este es un proyecto que he desarrollado a lo largo del curso a medida que iba aprendiendo e implementando diferentes tecnologías de nivel empresarial. Para poder utilizar el contenido agregado, al final de este README encontrarás un tutorial detallado sobre cómo importarlo y probarlo.

## Contenidos aprendidos e implementados 💻

*   **15-Factor Methodology** (Principios de diseño de aplicaciones en la nube)
*   **Spring Boot & Spring Cloud** (Ecosistema principal del proyecto)
*   **Spring Security, OAuth2 & OpenID Connect** (Aseguramiento de APIs)
*   **Keycloak** (Proveedor de identidad y gestión de accesos IAM)
*   **Spring Cloud Functions & Spring Cloud Streams** (Arquitectura dirigida por eventos)
*   **Resilience4j** (Tolerancia a fallos y resiliencia)
*   **Docker & Kubernetes** (Contenerización y orquestación)
*   **Grafana Stack** (Alloy, Loki y Grafana para observabilidad)
*   **Helm** (Gestor de paquetes para Kubernetes)

---

### DIAGRAMA DE ARQUITECTURA
<img width="906" height="617" alt="Diagrama General de la Arquitectura" src="https://github.com/user-attachments/assets/d35595cf-076f-4d63-92bd-5bbe90ea7dcb" />

---

## Arquitectura y Componentes del Proyecto

A continuación, se detalla cada apartado del proyecto para comprender su funcionamiento y cómo soluciona las limitaciones inherentes a una arquitectura tradicional.

### 1. Arquitectura Monolítica vs. Arquitectura de Microservicios

#### ¿Qué es una Arquitectura Monolítica?
Consiste en un único artefacto o servidor que contiene toda la funcionalidad del negocio (Cuentas, Tarjetas, Préstamos). Es un enfoque común y efectivo para proyectos pequeños o fases iniciales (MVP).
*   **Ventajas ✅**
    *   Desarrollo inicial sencillo y rápido.
    *   Menor complejidad de infraestructura.
    *   Alto rendimiento local (sin latencia de red entre módulos).
*   **Desventajas ❌**
    *   Dificultad extrema para adoptar nuevas tecnologías o lenguajes.
    *   **Acoplamiento total:** Cualquier cambio, por mínimo que sea, requiere un despliegue completo de la aplicación.
    *   **Punto único de fallo:** Si un módulo experimenta un error crítico (ej. fuga de memoria), toda la aplicación cae.
    *   Escalabilidad limitada y costes elevados al tener que escalar todo el monolito en lugar de la pieza que lo necesita.

<p align="center">
  <img src="https://github.com/user-attachments/assets/008c2301-7ba9-420b-9756-6c8f669677fe" alt="Esquema Monolito" width="400" />
</p>

#### ¿Qué es una Arquitectura de Microservicios?
En lugar de un único bloque de código, dividimos la aplicación en servicios pequeños, independientes y autónomos que se comunican entre sí. Para delimitar el tamaño y la responsabilidad de cada servicio, se optó por metodologías de diseño como **Domain-Driven Design (DDD)** y **Event Storming**.
*   **Ventajas ✅**
    *   **Poliglotismo:** Cada servicio puede utilizar el lenguaje, base de datos o framework que mejor se adapte a su necesidad.
    *   **Escalabilidad horizontal selectiva:** Se escalan únicamente los servicios con alta carga de tráfico.
    *   Alta agilidad, permitiendo despliegues independientes continuos sin impactar al resto del sistema.
*   **Desventajas ❌**
    *   Mayor complejidad en la gestión de infraestructura, red y contenedores.
    *   Al comunicarse a través de la red, aumenta la superficie de ataque y requiere un diseño de seguridad más estricto.
    *   Complejidad añadida en la monitorización y el rastreo de peticiones distributed (*Distributed Tracing*).

<p align="center">
  <img width="643" height="484" alt="Esquema Microservicios" src="https://github.com/user-attachments/assets/f81cb70e-5a06-4bfc-932a-035dc5e85189" />
</p>

---

### 2. Contenerización con Docker & Cloud Native Builds
En este entorno, cada microservicio debe ser agnóstico a la máquina donde se ejecuta. Para ello recurrimos a **Docker**, abstrayendo el código, entorno de ejecución y dependencias en imágenes ligeras.

Para optimizar la generación de imágenes y evitar mantener archivos `Dockerfile` redundantes, delegué la responsabilidad a <a href="https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better">**Google Jib**</a>. Herramienta que compila de forma nativa imágenes optimizadas para Java directamente hacia el daemon de Docker sin necesidad de configuraciones complejas.

Basta con incluir el plugin en el `pom.xml` y ejecutar:

```bash
mvn compile jib:dockerBuild
```

### 3. CONFIG SERVER (Centralización de la Configuración)
Gracias a **Config Server**, se resuelven grandes dolores de cabeza en arquitecturas distribuidas. Imagina un escenario con 15 microservicios conectados a una misma base de datos; si la contraseña cambia, tendrías que ir proyecto por proyecto modificando el archivo de configuración, hacer un commit, recompilar y volver a desplegar los 15 servicios.

[Spring Cloud Config](https://spring.io/projects/spring-cloud-config#overview) centraliza todas las configuraciones en un único lugar externo, ya sea en una base de datos o en un repositorio Git. Los microservicios descargan su configuración dinámicamente al arrancar. Además, para refrescar estas propiedades en tiempo de ejecución sin necesidad de reiniciar los servicios, nos apoyamos en [Spring Cloud Bus](https://spring.io/projects/spring-cloud-bus#learn).

<p align="center">
  <img width="634" alt="Esquema Config Server" src="https://github.com/user-attachments/assets/678d6b0b-921a-4280-9dde-bacae23b5bbb" />
</p>

---

### 4. EUREKA SERVER (Service Registration & Discovery)
Eureka Server nos incorpora los patrones de *Service Registration* y *Service Discovery* gracias a [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix#overview). Cada microservicio se registra de forma automática en Eureka para poder ser localizado dinámicamente. Sin esto, tendríamos que escribir las IPs de forma estática (*hardcoding*), lo cual es inviable hoy en día ya que en entornos de nube como AWS o Kubernetes, las instancias nacen, mueren y cambian de IP constantemente.

#### ¿Cómo funciona?
1. **Registro:** Al arrancar, el microservicio se conecta y se registra en Eureka Server.
2. **Monitoreo (Heartbeat):** Cada ciertos segundos, el microservicio envía un pulso de vida (*heartbeat*) a Eureka para notificar que sigue activo. Si deja de responder, Eureka lo elimina del registro.
3. **Consulta:** Si el servicio *Accounts* necesita comunicarse con *Cards*, le pregunta a Eureka Server la dirección IP de este último y la almacena temporalmente en su caché local.
4. **Conexión Directa:** *Accounts* realiza la llamada HTTP balanceada apuntando directamente a la IP proporcionada.

<p align="center">
  <img width="640" alt="Esquema Eureka Server" src="https://github.com/user-attachments/assets/3a36c354-ebf5-42a4-96b8-b114c7385d1b" />
</p>

---

### 5. GATEWAY SERVER (Edge Server / API Gateway)
Permitir que los clientes externos se comuniquen directamente con nuestros microservicios internos sería un riesgo crítico de seguridad y arquitectura. Para solucionar esto, disponemos de [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway). 

El **Gateway Server** actúa como el intermediario único entre los clientes y los microservicios, funcionando como un **Proxy Inverso**. Esto nos proporciona un enrutamiento inteligente y centraliza las **preocupaciones transversales (*Cross-Cutting Concerns*)**. Por ejemplo, si deseas incorporar seguridad mediante tokens, en lugar de pasar por cada microservicio configurándola individualmente, delegas toda esa responsabilidad a este único servidor de entrada.

#### ¿Cómo funciona?
1. El cliente realiza una petición HTTP dirigida al Gateway Server.
2. El Gateway intercepta la solicitud, valida los filtros correspondientes y le pregunta a Eureka Server la IP disponible de *Accounts*.
3. El Gateway enruta la petición de manera transparente hacia el microservicio de *Accounts*.

<p align="center">
  <img width="600" alt="Esquema Gateway Server" src="https://github.com/user-attachments/assets/e8faabef-58af-4ab5-a529-ce3f5b2167c7" />
</p>

> 💡 **Tolerancia a fallos:** Se ha incorporado [RESILIENCE4J](https://resilience4j.readme.io/docs/getting-started) en la capa del Gateway para evitar caídas en cascada. Dependiendo de las necesidades del negocio, se pueden activar módulos como *Circuit Breaker*, *Retry*, *Time Limiter* o *Rate Limiter* .

### 6. OBSERVABILITY & MONITORING
Gracias a la Observabilidad y Monitorizacion podemos tener un control de todo lo que esta ocurriendo y los estados de los microservicios.
 * Observabilidad: Es la capacidad de entender el estado interno del sistema como los logs, metricas y trazas.
 * Monitorizacion: Esta es la capa en la que controlaremos el estado de nuestro CPU, memoria, cantidad de errores, etc.

Para implementar la observabilidad en este proyecto, he utilizado el stack moderno de **Grafana**, delegando la recolección, almacenamiento y visualización en tres herramientas clave:

*   **Grafana Alloy:** Es el agente de recolección de telemetría de última generación (el sucesor espiritual de *Grafana Agent*). Se ejecuta junto a nuestros microservicios para recopilar métricas (vía Prometheus/Micrometer), logs y trazas de forma eficiente, y luego los distribuye hacia las herramientas de almacenamiento.
*   **Grafana Loki:** Es el motor de agregación de logs optimizado para microservicios. A diferencia de otras alternativas pesadas, Loki indexa únicamente los metadatos de los logs (como el nombre del microservicio o el entorno), lo que lo hace extremadamente rápido, ligero y fácil de escalar en entornos de contenedores.
*   **Grafana (Dashboard):** La plataforma web que unifica todo. Aquí se configuran los paneles visuales para monitorizar en tiempo real el uso de CPU, memoria, el estado de salud de los servicios, y realizar búsquedas de logs correlacionadas con los errores detectados.

#### ¿Como funciona el flujo de Observabilidad?
 1. Los microservicios generan métricas (mediante Spring Boot Actuator/Micrometer) y logs en la consola de Docker o Kubernetes.
 2. **Grafana Alloy** descubre activamente los contenedores, extrae esa información (logs y métricas) y la procesa localmente.
 3. **Alloy** envía los logs a **Loki** y las métricas a un sistema compatible (como Prometheus).
 4. Desde **Grafana**, consultamos visualmente ambos flujos de datos de forma unificada para detectar cuellos de botella o fallos en el sistema.

<p align="center">
 <img width="600" alt="image" src="https://github.com/user-attachments/assets/ce46be5b-547f-47b3-b3b6-7af95b082480" />
</p>

#### El flujo de la Monitorización y Métricas
A diferencia de los monolitos donde es difícil aislar el rendimiento de un módulo, en esta arquitectura distribuida controlamos la salud de cada servicio de manera independiente:

1. **Exposición:** Cada microservicio (Accounts, Cards, Gateway) expone sus métricas internas (uso de CPU, hilos activos, memoria JVM, latencia de las peticiones HTTP) a través de **Spring Boot Actuator** y **Micrometer**.
2. **Recolección Activa:** **Grafana Alloy** realiza un *scraping* (raspado) constante de esos endpoints, actuando como el motor de monitorización.
3. **Visualización en Tiempo Real:** **Grafana** consume estos datos para alimentar dashboards interactivos. Esto nos permite activar alertas automáticas si, por ejemplo, el Gateway supera el 80% de uso de CPU o si el microservicio de Cuentas empieza a responder con errores 500.
