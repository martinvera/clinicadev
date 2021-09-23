import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/shared/services/security/auth.service';
import { Permission } from '../models/permissions.enum';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  constructor(private userService: AuthService) {

  }

  checkPermission(permission: Permission): boolean {
    switch (permission) {
      case Permission.CREAR_ROL:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.CREAR_ROL);
      case Permission.EDITAR_ROL:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.EDITAR_ROL);
      case Permission.VISUALIZAR_ROL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.VISUALIZAR_ROL);
      case Permission.CREAR_USUARIO:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.CREAR_USUARIO);
      case Permission.EDITAR_USUARIO:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.EDITAR_USUARIO);
      case Permission.VISUALIZAR_USUARIO:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.VISUALIZAR_USUARIO);
      case Permission.CARGAR_DOCUMENTO:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.CARGAR_DOCUMENTO);
      case Permission.ELIMINAR_DOCUMENTO:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.ELIMINAR_DOCUMENTO);
      case Permission.EDITAR_CARGA_DOCUMENTOS:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.EDITAR_CARGA_DOCUMENTOS);
      case Permission.VISUALIZAR_DETALLE_DOCUMENTOS:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.VISUALIZAR_DETALLE_DOCUMENTOS);
      case Permission.GENERAR_REPORTE_DOCUMENTOS_SIN_LOTE:

        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.GENERAR_REPORTE_DOCUMENTOS_SIN_LOTE);
      case Permission.IR_EXPEDIENTE_DIGITAL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.IR_EXPEDIENTE_DIGITAL);
      case Permission.ELIMINAR_LOTE:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.ELIMINAR_LOTE);
      case Permission.GENERAR_EXPEDIENTE_DIGITAL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.GENERAR_EXPEDIENTE_DIGITAL);
      case Permission.MONITOREO_EXPEDIENTE_GENERADOS:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.MONITOREO_EXPEDIENTE_GENERADOS);
      case Permission.EXONERAR_DOCUMENTOS_FALTANTES:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.EXONERAR_DOCUMENTOS_FALTANTES);
      case Permission.VER_FACTURA:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.VER_FACTURA);
      case Permission.VER_ANEXO:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.VER_ANEXO);
      case Permission.VER_DOCUMENTO:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.VER_DOCUMENTO);
      case Permission.REPORTE_EXPEDIENTES_ERROR:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.REPORTE_EXPEDIENTES_ERROR);
      case Permission.DESCARGAR_ZIP:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.DESCARGAR_ZIP);
      case Permission.VER_DETALLE_ERROR:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.VER_DETALLE_ERROR);
      case Permission.DESCARGAR_REPORTE_TOTAL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.DESCARGAR_REPORTE_TOTAL);
      case Permission.GENERAR_REPORTE_PARCIAL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.GENERAR_REPORTE_PARCIAL);
      case Permission.DESCARGAR_REPORTE_PARCIAL:
        return this.userService.getRolUserStorage().find(val => val?.codigo == Permission?.DESCARGAR_REPORTE_PARCIAL);
      case Permission.DESCARGAR_REPORTE:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.DESCARGAR_REPORTE);
      case Permission.DASHBOARD_EXPEDIENTES_GENERADOS:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.DASHBOARD_EXPEDIENTES_GENERADOS);
      case Permission.GENERAR_REPORTE_MECANISMO:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.GENERAR_REPORTE_MECANISMO);
      case Permission.VER_HISTORIAL:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.VER_HISTORIAL);
      case Permission.EDITAR_FECHA_ENVIO:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.EDITAR_FECHA_ENVIO);
      case Permission.EDITAR_FECHA_DE_ACEPTACION:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.EDITAR_FECHA_DE_ACEPTACION);
      case Permission.EDITAR_FECHA_NUMERO_SOLICITUD:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.EDITAR_FECHA_NUMERO_SOLICITUD);
      case Permission.DASHBOARD_ENVIADOS_GARANTE:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.DASHBOARD_ENVIADOS_GARANTE);
      case Permission.GENERAR_REPORTE_LOTES_GARANTE:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.GENERAR_REPORTE_LOTES_GARANTE);
      case Permission.BOTON_REPROCESAR_GESTION_LOTE:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.BOTON_REPROCESAR_GESTION_LOTE);
      case Permission.REPROCESAR_COLA:
        return this.userService?.getRolUserStorage()?.find(val => val?.codigo == Permission?.REPROCESAR_COLA);
    }
  }

  checkPermissionHeader(permission: Permission[]): boolean {
    let retur: boolean = false;
    permission.find((val:any) => {
      let tem = this.userService?.getRolUserStorage()?.find(data => data?.codigo ==parseInt(val))
      tem ? retur = true : retur = false;
    })
    return retur;
  }
}
