# PROJECTO MICROSERVICIOS

Este es un projecto que he estado realizando a lo largo del curso a medida que iba aprendiende e implementando diferentes tecnologias. Para poder utilizar el contenido agregado al final del README hay un tutorial de como importarlo y probarlo

## Contenidos aprendidos a lo largo del proyecto 💻

- 15 Factor methodology
- Spring boot
- Spring cloud
- Spring security
- Spring Cloud Functions
- Spring Cloud Streams
- RESILIENCE4J
- Docker
- Kubernetes
- OAuth2
- OpenID Connect
- KeyCloak
- Grafana Stack
- Helm

### DIAGRAMA
<img width="906" height="617" alt="image" src="https://github.com/user-attachments/assets/d35595cf-076f-4d63-92bd-5bbe90ea7dcb" />

### Contenido del proyecto

Primero vamos a explicar cada apartado del proyecto para poder entender todo su funcionamiento y como soluciona ciertos problemas que puede tener una Arquitectura Monolitica

### 1. ARQUITECTURA MONOLITICA VS ARQUITECTURA DE MICROSERVICIOS

#### ¿Que es una Arquitectura Monolitica?
Es un unico servidor que contiene toda la funcionalidad de la aplicacion(Cuentas, Tarjetas, Prestamos), es comunmente utilizado en proyectos pequeños
 - **VENTAJAS** ✅
   - Facil desarrollo
   - Menor complejidad
   - Alto rendimiento debido a que no hay latencia de red
 - **DESVENTAJAS** ❌
   - Dificultad para adoptar nuevas tecnologias
   - Cualquier cambio requiere de un despliege total
   - Si un modulo falla, puede caer toda la app
   - Escalabilidad limitada y dificultad de mantener a largo plazo

<p align="center">
  <img src="https://github.com/user-attachments/assets/008c2301-7ba9-420b-9756-6c8f669677fe" alt="Descripción de la imagen" width="400" />
</p>

#### ¿Que es una Arquitectura de Microservios?
En vez de tener un solo servidor como los Monolitos, vamos a dividir la aplicacion en pequeños servicios y autonomos. Optando entre **Domain-Driven-Sizing** o **Event Storming Sizing** 
 - **VENTAJAS** ✅
   - Cada servicio puede usar un lenguaje o framework distinto
   - Escalabilidad horizontal
   - Alta agilidad y despliegues rapidos sin afectar a otros modulos
     
 - **DESVENTAJAS** ❌
   - Alta complejidad para gestionar los servicios y contenedores
   - Al comunicarse por red es mas facil a vulnerabilidades
   - Sobrecarga de infraestructura y monitoreo
  
<p align="center">
<img width="643" height="484" alt="image" src="https://github.com/user-attachments/assets/f81cb70e-5a06-4bfc-932a-035dc5e85189" />
</p>


### 2. CONTENERIZACION CON DOCKER
En una arquitectura de Microservicios se conteneriza cada servicio independiente, para ello recurrimos a Docker. Gracias a esta plataforma de codigo abierto podemos convertir nuestro codigo en imagenes y automatizar el despliegue, escalado y gestion de aplicaciones

Para la generacion de imagenes en vez de crearnos un DockerFile delegaremos esa responsabilidad a una herramienta de Java para la creacion de imagenes con <a href="https://cloud.google.com/blog/products/application-development/introducing-jib-build-java-docker-images-better">Google Jib</a>, basta con que pongamos este plugin en nuestro **pom.xml** y el siguiente comando para generar la imagen:
<p align="center">
<img width="586" height="320" alt="image" src="https://github.com/user-attachments/assets/0cf9deec-d2dd-4188-a259-2c89a8c1d9aa" />
</p>

```bash
mvn compile jib:dockerBuild
```

### 3. CONFIGSERVER
Gracias a configserver se nos resuelve muchos dolores de cabeza, imagina que tienes 15 microservicios conectados a una misma database y cambias la contraseña, tendrias que ir proyecto por proyecto modificando el archivo de configuración, hacer un commit, recompilar y volver a desplegar los 15 servicios.
<p>Pues gracias a <a href="https://spring.io/projects/spring-cloud-config#overview">Spring Cloud Config</a> centraliza todas las configuraciones en un unico lugar externo, puede ser tanto en database como en un repositorio en git. Los microservicios descargan su configuracion al arrancar y para refrescar la configuracion en tiempo de ejecucion nos podemos apoyar en <a href="https://spring.io/projects/spring-cloud-bus#learn">Spring Cloud Bus</a></p>

<p align="center">
  <img width="634"  alt="image" src="https://github.com/user-attachments/assets/678d6b0b-921a-4280-9dde-bacae23b5bbb" />
</p>


### 4. EUREKASERVER
Eurekaserver nos incorpora Service Registration y Service Discovery gracias a <a href="https://spring.io/projects/spring-cloud-netflix#overview">Spring Cloud Netflix</a>. Cada microservicio se registra a eurekaserver para poder encontrarse de forma dinamica, sin esto tendriamos que hardcodear la IP y seria un problema ya que hoy en dia los micros nacen, mueren y cambian de IP constantemente en entornos de nube como AWS o Kubernetes

#### ¿Como funciona?
  1. Se crea un microservicio y se registra en eurekaserver
  2. Cada x segundos el microservicio debe enviar a eurekaserver que sigue vivo, sino se borra y se crea otro microservicio automaticamente
  3. Si Accounts quiere comunicarse con Cards, le pregunta a Eurekaserver su IP y la guarda en cache
  4. Accounts hace una llamada HTTP directamente a la IP proporcionada
  <p align="center">
    <img width="640"  alt="image" src="https://github.com/user-attachments/assets/3a36c354-ebf5-42a4-96b8-b114c7385d1b" />
  </p>

### 5. GATEWAYSERVER (EDGE SERVER)

