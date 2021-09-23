# Clínica Internacional

## Tabla de contenido
1. Información general
2. Tecnologías
3. Instalación
4. Configuración de variables de entorno
5. Apis
6. Despliegue


## Información general
***
El integrador es un microservicio que recibe una respuesta de servicios externos a través de un JSON request que toma información principalmente del encuentro enviado y a la vez solicita una respuesta de dichos servicios para traer la información correcta de los archivos a crear; si no tuviera un archivo a crear únicamente se estaría creando el documento con su lote correspondiente.
## Tecnologías
***
Lista de tecnologías utilizadas dentro del proyecto:
* [Spring Boot](https://spring.io/): Versión 2.4.7.
* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/): Versión 11.0.12
* [gradle](https://gradle.org/)
* [Blob storage](https://azure.microsoft.com/en-us/services/storage/blobs/)
* [Queue storage](https://azure.microsoft.com/en-us/services/storage/queues/#overview)
* [Servicio de Kubernetes](https://azure.microsoft.com/en-us/overview/kubernetes-getting-started/)
* [Az acr repository](https://docs.microsoft.com/en-us/cli/azure/acr/repository?view=azure-cli-latest)
* [Swagger](https://swagger.io/)

## Instalación
A continuación detallamos el proceso de instalación.
***
```
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/integrator-service
$ cd integrator-service
$ gradlew bootRun

```


## Configuración de variables de entorno
***
## Login azure
Para conectarnos a los servicios de azure desde el entorno local ejecutamos el comando en la terminal `az login`, a continuación seguir los pasos que se visualizarán en la pantalla.


## Configuración de CosmosDB, blob storage, queue storage, table storage, az acr repository.
La configuración y las variables de entorno del cosmosDB para los despliegues en `azure` se encuentran en el archivo `deployment.yml`, para desarrollos en el entorno local se encuentran en `application.yml`.

## Variables de entorno storage
```
name: AZURE_STORAGE_BLOB_ENDPOINT
value: `Link de configuración del servicio que brinda azure`

name: AZURE_STORAGE_QUEUE_ENDPOINT
value: `Link de configuración del servicio que brinda azure`
```
## Variables de entorno QUEUE STORAGE
se declaran los nombres de la cola y se asignan los valores a las colas declaradas.
```
name: QUEUE_CLINICAL
value: clinicalrecordqueuestorage

name: QUEUE_DOCUMENT
value: documentqueuestorage
```
## variable de entorno Containers

```
name: 'nombre del proyecto con el cual se va crear el contenedor'
image: 'ruta de la imagen docker en azure'
```

## Apis
Las apis desarrolladas se encuentran registradas en swagger con todos los parametros necesarios para ser invocadas, a las cuales tendremos acceso una vez iniciado el proyecto.
Después de haber configurado todas las variables y el proyecto esté iniciado de manera local, ingresar en su navegador de preferencia y colocar `http://localhost:8005/swagger-ui.html`. el <strong>puerto</strong> al cual se debe hacer referencia, se encuentra configurado en `application.yml`

### Integrator-controller

- /v1/integrator/unilab
    - Se realiza la creación de los documentos según sus parámetros que llegan de integración.
- /v1/integrator/iafas
    - Crean la factura dentro de clinicalrecord con el servicio de IAFAS.
- /v1/integrator/controldocumentario
    - Crean la factura dentro de clinicalrecord con el servicio de CONTROL DOCUMENTARIO.

## Despliegue
El despliegue se realiza en los servicios de `AZURE`, especificamente en `Servicio de Kubernetes`.
***

- Compilar el proyecto:
    ```
    gradlew clean build
    ```
- Construir la imagen Docker:
    - Para poder construir la imagen utilizamos el archivo `Dockerfile`, donde se especifica la versión del java, asi mismo la generación `.jar`, luego se realiza una copia del jar generado, el cual se sube a los servicios de AKS de azure.

        ```
        docker build -t docker build -t acrexpedientedigital.azurecr.io/expedientedigital/integratorasi-service:V1.0 .
        ```
- Desplegar la imagen con los parámetros del archivo `deployment.yml`
    ```
    kubectl apply -f deployment.yml
    ```