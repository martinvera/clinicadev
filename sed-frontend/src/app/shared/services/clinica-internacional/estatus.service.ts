import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class EstatusService {
   
    listaEstatus() {
        return [
            { id: "TERMINADO", value: 'Terminado' },
            { id: "EN_PROCESO", value: 'En proceso' },
            { id: "PENDIENTE", value: 'Pendiente' }
        ]
    }
    listaEstatusMonitoreo() {
        return [
            { id: "PENDIENTE", value: 'Pendiente' },
            { id: "EN_PROCESO", value: 'En proceso' },
            { id: "TERMINADO", value: 'Terminado' },
            { id: "ERROR", value: 'Error' },
        ]
    }
    listaEstatusGenerarExpediente() {
        return [
            { id: "COMPLETO", value: 'Completo' },
            { id: "INCOMPLETO", value: 'Incompleto' }
        ]
    }
    listaEstatusEnviadoGarante() {
        return [
            { id: "PENDIENTE", value: 'PENDIENTE' },
            { id: "ENVIADO", value: 'ENVIADO' },
            { id: "ACEPTADO", value: 'ACEPTADO' },
            { id: "RECHAZADO", value: 'RECHAZADO' }
        ]
    }
}