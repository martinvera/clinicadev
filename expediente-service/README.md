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
Expediente-service es un micro servicio en el cual se realizar la busqueda de expedientes y archivos para poder realizar la validación de los documentos requeridos. Además, se realiza la generación de expedientes digitales. Así mismo, tenemos integración con los servicios de document-service, clinicalrecord-service. Por otro lado para la persistencia de los datos nos conectamos a cosmosDB, storage de azure y el queue storage para poder procesar la información de forma asíncrona.
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
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/expediente-service
$ cd expediente-service
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

## Apis 
Las apis desarrolladas se encuentran registradas en swagger con todos los parametros necesarios para ser invocadas, a las cuales tendremos acceso una vez iniciado el proyecto.
Después de haber configurado todas las variables y el proyecto esté iniciado de manera local, ingresar en su navegador de preferencia y colocar `http://localhost:8003/swagger-ui.html`. el <strong>puerto</strong> al cual se debe hacer referencia, se encuentra configurado en `application.yml`

### Expediente-controller
-​ /v1​/expediente​/validar
    - Realiza la validación de documentos.
- ​/v1​/expediente​/eliminarExpdienteXNroLote
    - Elimina registros generados en ZIP.
- ​/v1​/expediente​/eliminarArchivoXNroLote
    - Elimina los documentos asociados al número de lote.
- ​/v1​/expediente​/buscar
    - Busca los expedientes según los parámetros del filtro enviados desde el frontend.

### Generar-reporte-controller
- ​/v1​/expediente​/reporte​/validarNroLote
    - Valida si el número de lote existe.
- ​/v1​/expediente​/reporte​/registrarReporteTotalParcial
    - Lee la cola y genera el reporte parcial.
- ​/v1​/expediente​/reporte​/mecanismoFacturacion
    - Lee la cola y genera el reporte de mecanismo de facturación.
- ​/v1​/expediente​/reporte​/listaReporteTotalParcial
    - Lista el reporte total y parcial para las descargas de los archivos, dependiendo del parámetro que se envía desde el frontend.
- ​/v1​/expediente​/reporte​/expedienteEstadoOrigen
    - Lee la cola y genera el reporte de los expedientes generados  por estado y origen.
- ​/v1​/expediente​/reporte​/expedienteError
    - Lee la cola y genera el reporte de expedientes generados con error.
- ​/v1​/expediente​/reporte​/Totalparcial
    - Lee la cola y genera el reporte total y parcial, dependiendo del parámetro que se envía desde el frontend.
### Generar-expediente-controller
- ​/v1​/expediente​/enviar
    - Genera los expedientes según los parámetros enviados desde el frontend.

## Despliegue
El despliegue se realiza en los servicios de `AZURE`, especificamente en `Servicio de Kubernetes`.
***


- Compilar el proyecto:
    ```
    gradlew clean build
    ```
- Construir la imagen Docker:
    - Para poder construir la imagen utilizamos el archivo `Dockerfile`, donde se especifica la versión del java, así mismo la generación `.jar`, luego se realiza una copia del jar generado, el cual se sube a los servicios de AKS de azure.

        ```
        docker build -t acrexpedientedigital.azurecr.io/expedientedigital/expediente-service:v1.0 .
        ```
- Desplegar la imagen con los parámetros del archivo `deployment.yml`
    ```
    kubectl apply -f deployment.yml
    ```