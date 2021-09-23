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
Clinicalrecord es un micro servicio en el cual se crean facturas. Así mismo, tenemos conexión con los servicios de expediente-service, document-service. Por otro lado para la persistencia de los datos nos conectamos a cosmosDB y el queue storage para poder procesar la información de forma asíncrona.
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
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/clinicarecord-service
$ cd clinicarecord-service
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
Después de haber configurado todas las variables y el proyecto esté iniciado de manera local, ingresar en su navegador de preferencia y colocar `http://localhost:8002/swagger-ui.html`. el <strong>puerto</strong> al cual se debe hacer referencia, se encuentra configurado en `application.yml`
### Gestion-lotes-controller
- ​/v1​/gestionlotes​/registrarHistorial
    - Registra y actualiza el lote con su respectivo garante en la tabla "historialgarante"
- ​/v1​/gestionlotes​/listarEnviadoGarante
    -   Envía el listado de lotes a un garante para su aprobación o rechazo.
- ​/v1​/gestionlotes​/eliminarLote
  -   Elimina los expedientes digitales, registro, facturas y documentos de un lote en específico.
- ​/v1​/gestionlotes​/eliminarExpXlote
  -   Elimina los expedientes asociados al lote del Table Storage, actualiza lote existente, elimina los archivos asociados al lote del Blob Storage y actualiza el listado de facturas generadas en el CosmosDB.
- ​/v1​/gestionlotes​/buscarLotes
  -   Realiza la búsqueda de lotes y lista la información correspondiente al lote.
- ​/v1​/gestionlotes​/buscarHistorial
  -   Realiza la búsqueda y listado del historial de un garante en específico.
- ​/v1​/gestionlotes​/EnviadoGaranteReporte
  -   Realiza el reporte de los lotes enviados al garante
### Clinical-record-controller

- ​/v1​/clinicalrecord​/obtenerFactura
  -   Se obtienen los archivos de anexo detallado y facturas a través de la búsqueda del nroLote y facturaNro
- ​/v1​/clinicalrecord​/lista
  -   Lista los datos de clinicalrecord del lote de un garante
- ​/v1​/clinicalrecord​/actualizarFacturaGenerada
  -   Actualiza el número de lote y las facturas asociadas a el.
- ​/v1​/clinicalrecord​/actualizarFacturaEstado
  -   Actualiza el estado de la factura inafecta de un determinado lote

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
        docker build -t docker build -t acrexpedientedigital.azurecr.io/expedientedigital/clinicalrecord-service:V1.0 .
        ```
- Desplegar la imagen con los parámetros del archivo `deployment.yml`
    ```
    kubectl apply -f deployment.yml
    ```

