# Clínica Internacional
## Tabla de Contenido 
1. Información general
2. Tecnologias
3. Instalación
4. Configuración de variables de entorno
5. Despliegue

## Información General
En la aplicación web desarrollada en `Angular` para Clínica Internacional, donde muestran todas las vista de interfaz de usuario, divididas en tres módulos como `Seguridad`, `Gestión de documentos gitales` y `Generación de expediente digital`. Estos módulos tiene comunicación con los microservicios desarrollados en java (clinicalrecord-service, document-service, expediente-service, expedientedigital-frontend,reporte-service).
***

<<<<<<< HEAD
<<<<<<< HEAD


=======
>>>>>>> dev
=======

>>>>>>> dev
## Tecnologías
* [Angular CLI](https://github.com/angular/angular-cli): Version 12.0.4.
* [Node.js](https://nodejs.org/en/): Version: 12.18.4
* [Angular Material](https://material.angular.io/): Versión 12.2.4.
* [Bootstrap](https://getbootstrap.com/): Versión 5.1.1.
* [Chart.js](https://www.chartjs.org/)
* [Hammer.js](https://hammerjs.github.io/)
* [nginx](https://www.nginx.com/)

## Instalación
***
Para el proceso de instalación, se deberán ejecutar los siguientes comandos:
```
$ git clone https://cinternacional@dev.azure.com/cinternacional/BRM-ExpedienteDigital/_git/sed-frontend
$ cd sed-frontend
$ npm install
$ ng serve -o
```

 El proyecto iniciara en `http://localhost:4200/`. La aplicación se recargará automáticamente si cambia alguno de los archivos de origen.

 ## Configuración de Variables de Entorno 
 ***
 ## Variable de Entorno API_BASE
 Para cambiar la variable de entorno API_BASE, se deberá entrar a las rutas `src\environments\environment.ts`, `src\environments\environment.prod.ts`, esta variable es la responsable de la comunicación con el los microservicios de backend.

## Despliegue
El despliegue se realiza en los servicios de `AZURE`, especificamente en `Servicio de Kubernetes`.
***
Estos son los pasos a seguir para realizar un despliegue en el entorno de azure.

- Compilar Angular:
```
    ng build --prod
```
- Construir la imagen Docker:
    - Para poder construir la imagen utilizamos el archivo `Dockerfile`, donde se especifica la versión del servidor web (nginx), así mismo se realiza la compilación del proyecto, luego se realiza una copia del proyecto compilado, el cual se sube a los servicios de AKS de azure.
    ```
    docker build -t acrexpedientedigital.azurecr.io/expedientedigital/expedientedigital-frontend:v1.0 .
    ```

- Aplicar cambios los cambios del archivo `deployment`:
```
    kubectl apply -f deployment.yml
```

