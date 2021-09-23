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
Document-service es un micro servicio en el cual se realiza el procesamiento de los documentos, crear y modificarlos. Así mismo, tenemos integración con los servicios de expediente-service, clinicalrecord-service, SALESFORCE y XHIS. La integración con XHIS se realiza para poder generar un documento con el numero de encuentro y descargar los comprobantes. Por otro lado para la persistencia de los datos nos conectamos a cosmosDB y el queue storage para poder procesar la información de forma asíncrona.
## Tecnologías
***
Lista de tecnologías utilizadas dentro del proyecto:
* [Spring Boot](https://spring.io/): Versión 2.4.7.
* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/): Versión 11.0.12
* [gradle](https://gradle.org/)
* [CosmosDB](https://azure.microsoft.com/en-us/services/cosmos-db/#features)
* [Blob storage](https://azure.microsoft.com/en-us/services/storage/blobs/)
* [Queue storage](https://azure.microsoft.com/en-us/services/storage/queues/#overview)
* [Table storage](https://azure.microsoft.com/en-us/services/storage/tables/#features)
* [Servicio de Kubernetes](https://azure.microsoft.com/en-us/overview/kubernetes-getting-started/)
* [Az acr repository](https://docs.microsoft.com/en-us/cli/azure/acr/repository?view=azure-cli-latest)
* [Swagger](https://swagger.io/)

## Instalación
A continuación detallamos el proceso de instalación.
***
```
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/document-service
$ cd document-service
$ gradlew bootRun

```


## Configuración de variables de entorno
***
## Login azure
Para conectarnos a los servicios de azure desde el entorno local ejecutamos el comando en la terminal `az login`, a continuación seguir los pasos que se visualizarán en la pantalla.


## Configuración de CosmosDB, blob storage, queue storage, table storage, az acr repository.
La configuración y las variables de entorno del cosmosDB para los despliegues en `azure` se encuentran en el archivo `deployment.yml`, para desarrollos en el entorno local se encuentran en `application.yml`.

## Variables de entorno CosmosDB
```
name: AZURE_COSMOS_URI
value: `link del servicio de azure configurado`

name: AZURE_COSMOS_KEY
value: `llave que se configura en el azure para el acceso`
```
## Variables de entorno storage
```
name: AZURE_STORAGE_BLOB_ENDPOINT
value: `Link de configuración del servicio que brinda azure`
name: AZURE_STORAGE_TABLE_ENDPOINT
value: `Link de configuración del servicio que brinda azure`
name: AZURE_STORAGE_QUEUE_ENDPOINT
value: `Link de configuración del servicio que brinda azure`
```

## variable de entorno Containers

```
name: 'nombre del proyecto con el cual se va crear el contenedor'
image: 'ruta de la imagen docker en azure'
```

## Variable de entorno XHIS
las variables se configuran en `application.yml` y `deployment.yml`, dichas variables las provee <strong>Clínica internacional</strong>
```
name: SSL_CERT_XHIS_URI
value: certificado/integradordev.clinicainternacional.com.pe.pfx
```
## Variable de entorno SALESFORCE
las variables se configuran en `application.yml` y `deployment.yml`, dichas variables las provee <strong>Clínica internacional</strong>
```
name: SSL_CERT_SALESFORCE_URI
value: certificado/integradordev.clinicainternacional.com.pe.pfx

name: SSL_CERT_SALESFORCE_PWD
value: Rh4ps0dyC1
```

## Apis 
Las apis desarrolladas se encuentran registradas en swagger con todos los parametros necesarios para ser invocadas, a las cuales tendremos acceso una vez iniciado el proyecto.
Después de haber configurado todas las variables y el proyecto esté iniciado de manera local, ingresar en su navegador de preferencia y colocar `http://localhost:8004/swagger-ui.html`. el <strong>puerto</strong> al cual se debe hacer referencia, se encuentra configurado en `application.yml`

### Documento-controller
- /v1/documento/registro
     - Realiza el registro de los documentos cargados de forma manual, con los parámetros que se encuentran detallados en el `swagger`.
- /v1/documento/modificar
    - Se realiza la modificación de los documentos cargados de forma manual.
- /v1/documento/lista
    - Lista todos los documentos según el número de lote desde, hasta (nroLoteDesde,nroLoteHasta).
- /v1/documento/detalle
    - Lista el detalle del documento según la facturaNro, nroEncuentro, nroLote.
- /v1/documento/documentoXfactura
    - Lista el detalle del documento por facturaNro.
- /v1/documento/integracionsalesforce
    - Se realiza la creación de los documentos según sus parámetros.
- /v1/documento/integracionxhis
    - Se realiza la creación de los documentos según sus parámetros.
### Reporte-controller
- ​/v1​/documento​/reporte​/sinLote
    - Lee la cola para poder generar el reporte(excel) sin lote.
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
        docker build -t acrexpedientedigital.azurecr.io/expedientedigital/document-service:$v1.0 .
        ```
- Desplegar la imagen con los parámetros del archivo `deployment.yml`
    ```
    kubectl apply -f deployment.yml
    ```