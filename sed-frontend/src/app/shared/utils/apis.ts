export enum ENDPOINT_USERS {
    LIST = '/v1/usuario/lista',
    CREATE = '/v1/usuario/registro',
    ROLLIST = '/v1/rol/lista',
    CREATE_ROLE = '/v1/rol/crear',
    LIST_PRIVILEGIOS = '/v1/privilegio/lista',
    UPDATE_USER = '/v1/usuario/actualizar',
    UPDATE_ROLE = '/v1/rol/modificar',
    TYPE_DOCUMENT_LIST = '/v1/catalogo/TIPODOCUMENTOUSUARIO',
    GET_ROL_USER = '/v1/usuario/:user',
    GET_ROL = '/v1/rol/:idRol'
}

export enum ENDPOINT_DOCUMENTS {
    DOCUMENTLIST = '/v1/documento/lista',
    CREATE_DOCUMENT = '/v1/documento/registro',
    UPDATE_DOCUMENTO = '/v1/documento/modificar',
    TIPO_DOCUMENTO = '/v1/catalogo/TIPODOCUMENTO',
    SERVICIOS = '/v1/catalogo/SERVICIOS',
    BENEFICIOS = '/v1/catalogo/BENEFICIOS',
    CONSULTA_DOCUMENTO = '/v1/documento/detalle',
}

export enum ENDPOINT_EXPED {
    FIND_EXPED = '/v1/expediente/buscar',
    LIST_RECORD = '/v1/clinicalrecord/lista',
    GEN_EXPE = '/v1/expediente/enviar',
    LIST_DOCUMENT = '/v1/documento/documentoXfactura',

    DOCS_FACTU = '/v1/documento/lista'
}

export enum LOGIN_APIS {
    SIGNIN = '/oauth/token',
}

export enum ENPOINT_LOTE {
    LIST_LOTE = '/v1/gestionlotes/buscarLotes',
    DELETE_LOTE = '/v1/gestionlotes/eliminarLote',
    // LISTA DEL COMBO
    GARANTE_LIST = '/v1/catalogo/GARANTE',
    SEDE_LIST = '/v1/catalogo/SEDE', //sprint-3
    //eliminar lotes para el reproceso
    REPROCESAR_LOTE = '/v1/gestionlotes/eliminarExpXlote'
}

export enum ENPOINT_OTROSREPORTES {
    OTROS_REPORTES = '/v1/reporte/listar',
}

export enum ENPOINT_REPORTE {
    GENERAR_REPORTE = '/v1/reporte/enviar',
}

export enum ENPOINT_MECANISMOFACTURACION {
    MECANISMOFACTURACION = '/v1/catalogo/MECANISMOFACTURACION',
    DASHBOARD_MECANISMO = '/v1/expediente/reporte/mecanismoFacturacion',
}
export enum ENPOINT_EXPEDIENTES_GENERADOS {
    EXPEDIENTES_GENERADOS = '/v1/expediente/reporte/expedienteEstadoOrigen'
}

export enum ENPOINT_REPORTE_TOTAL {
    LIST_REPOTE_TOTAL = '/v1/reporte/listar',
    SEARCH_REPOTE_TOTAL = '/v1/reporte/listar',
}

export enum ENPOINT_REPORTE_PARCIAL {
    GENERATE_REPORTE_PARCIAL = '/v1/reporte/enviar',
    LIST_REPORTE_PARCIAL = '/v1/reporte/listar',
}

export enum ENPOINT_REPORTE_SINLOTE {
    REPORTE_ENCUENTRO_SINLOTE = '/v1/reporte/enviar',
}

export enum ENPOINT_ENVIADOS_GARANTE {
    LISTA_ENVIADOS_ALGARANTE = '/v1/gestionlotes/buscarLotes',
    CHANGE_ESTADO_ENVIADOS_GARANTE = '/v1/gestionlotes/registrarHistorial',
    LOG_ENVIADOS_GARANTE = '/v1/gestionlotes/buscarHistorial',
    DASHBOARD_ENVIADOS_GARANTE = '/v1/gestionlotes/listarEnviadoGarante',
    REPORTE_ENVIADOS_GARANTE = '/v1/reporte/enviar'
}
