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
Web-service es un micro servicio en el cual se realizan el mantenimiento de roles, usuarios y la carga incial del catálogo. Así mismo, tenemos integración con los servicios de document-service, clinicalrecord-service, expediente-service, report-service. Por otro lado para la persistencia de los datos nos conectamos a  storage de azure.
## Tecnologías
***
Lista de tecnologías utilizadas dentro del proyecto:
* [Spring Boot](https://spring.io/): Versión 2.4.7.
* [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/): Versión 11.0.12
* [gradle](https://gradle.org/)
* [Table storage](https://azure.microsoft.com/en-us/services/storage/tables/#features)
* [Servicio de Kubernetes](https://azure.microsoft.com/en-us/overview/kubernetes-getting-started/)
* [Az acr repository](https://docs.microsoft.com/en-us/cli/azure/acr/repository?view=azure-cli-latest)
* [Swagger](https://swagger.io/)
* [gradle](https://gradle.org/)

## Instalación
A continuación detallamos el proceso de instalación.
***
```
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/sed-backend
$ cd sed-backend
$ gradlew bootRun

```


## Configuración de variables de entorno
***
## Login azure
Para conectarnos a los servicios de azure desde el entorno local ejecutamos el comando en la terminal `az login`, a continuación seguir los pasos que se visualizarán en la pantalla.


## Configuración del table storage y az acr repository.
La configuración y las variables de entorno del cosmosDB para los despliegues en `azure` se encuentran en el archivo `deployment.yml`.

## Variables de entorno storage
```
name: AZURE_STORAGE_TABLE_ENDPOINT
value: `Link de configuración del servicio que brinda azure`,

```

## variable de entorno Containers

```
name: 'nombre del proyecto con el cual se va crear el contenedor'
image: 'ruta de la imagen docker en azure'
```

## Apis 
Las apis desarrolladas se encuentran registradas en swagger con todos los parámetros necesarios para ser invocadas, a las cuales tendremos acceso una vez iniciado el proyecto.
Después de haber configurado todas las variables y el proyecto esté iniciado de manera local, ingresar en su navegador de preferencia y colocar `http://localhost:8001/swagger-ui.html`. el <strong>puerto</strong> al cual se debe hacer referencia, se encuentra configurado en `application.yml`
### CatalogoController
***
- /v1/catalogo/{catalogo}
    - Lista los catálogos dependiendo del `query params` que se envía, estos datos son para la carga inicial de los módulos.
- /v1/catalogo/validar
    - Realiza la validación de los documentos requeridos en la carga manual.

### MClinicaRecordController
- Los microservicios detallados hacen referencia a los microservicios de clinicalrecord-service.
***
- /v1/clinicalrecord/lista
- /v1/reportes/lista
- /v1/clinicalrecord/registrarSiniestro



### MDocumentController
- Los microservicios detallados hacen referencia a los microservicios de document-service.
***
- /v1/documento/registro
- /v1/documento/lista
- /v1/documento/documentoXfactura
- /v1/documento/{nrLote}/{facturaNro}
- /v1/documento/modificar
- /v1/documento/detalle

### MExpedienteController
- Los microservicios detallados hacen referencia a los microservicios de document-service.
***
- /v1/expediente/buscar
- /v1/expediente/enviar
- /v1/expediente/reporte/mecanismoFacturacion
- /v1/expediente/reporte/expError
- /v1/expediente/reporte/expedienteEstadoOrigen
- /v1/expediente/reporte/listaReporteTotalParcial

### MGestionLotesController
- Los microservicios detallados hacen referencia a los microservicios de clinicalrecord-service.
***
- /v1/gestionlotes/buscarLotes
- /v1/gestionlotes/eliminarLote
- /v1/gestionlotes/buscarHistorial
- /v1/gestionlotes/registrarHistorial
- /v1/gestionlotes/listarEnviadoGarante
- /v1/gestionlotes/EnviadoGaranteReporte
- /v1/gestionlotes/eliminarExpXlote

### MReporteController
- Los microservicios detallados hacen referencia a los microservicios de reporte-service.
***
- /v1/reporte/listar
- /v1/reporte/enviar
- /v1/reporte/enviarTP

### PrivilegioService
- /v1/privilegio/lista
    - Lista los privilegios cargados, para poder crear un rol.
- /v1/privilegio/{codigo}
    - Obtiene el privilegio dependiendo del código enviado.

### RolController
- /v1/rol/crear
    - Crea el rol con los parámetros enviados.
- /v1/rol/lista
    - Obtiene el listado de los roles.
- /v1/rol/{codigo}
    - Obtiene el rol dependiendo del codigo enviado.
- /v1/rol/modificar
    - Modifica el rol dependiendo del codigo enviado.

### UsuarioController
- /v1/usuario/registro
    - Registra al usuario con los parámetros enviados en el cuerpo.
- /v1/usuario/actualizar
    - Actualiza el usuario con los campos necesarios.
- /v1/usuario/{username}
    - Elimina el usuarios por el nombre de usuario.
- /v1/usuario/lista
    - Lista todos los usuarios.
- /v1/usuario/{username}
    - Obtiene el usuario con el parámetro `username`.
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
