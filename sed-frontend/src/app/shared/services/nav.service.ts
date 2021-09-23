import { Injectable } from '@angular/core';
import { Permission } from 'src/app/permission/models/permissions.enum';

// Menu
export interface Menu {
	path?: string;
	title?: string;
	icon?: string;
	type?: string;
	badgeType?: string;
	badgeValue?: string;
	active?: boolean;
	bookmark?: boolean;
	children?: Menu[];
}

@Injectable({
	providedIn: 'root'
})

export class NavService {
	permission = Permission;
	
	listOptionGestion = [
		{
			text: 'Consultar Documentos',
			icon: 'find_in_page',
			path: '/documentos/consulta',
			active: true,
			permissions: this.permission.VISUALIZAR_DETALLE_DOCUMENTOS,

		},
		{
			text: 'Carga de Documentos',
			icon: 'upload_file',
			path: '/documento/carga',
			active: true,
			permissions: this.permission.CARGAR_DOCUMENTO,
		},
	]
	listaOptionExpediente = [
		{
			text: 'Gestión de Lotes',
			icon: 'library_books',
			path: '/gestion/lote',
			active: true,
			permissions: this.permission.IR_EXPEDIENTE_DIGITAL,
		},

		{
			text: 'Monitoreo de Exp. Digitales Generados',
			icon: 'task',
			path: '/documentos/moni-expediente',
			active: true,
			permissions: this.permission.MONITOREO_EXPEDIENTE_GENERADOS
		}
	]

	listOption = [
		{
			text: 'Mantenimiento de roles',
			icon: 'supervised_user_circle',
			path: '/roles',
			active: true,
			permissions: this.permission.VISUALIZAR_ROL,
		},
		{
			text: 'Mantenimiento de usuarios',
			icon: 'account_box',
			path: '/user',
			active: true,
			permissions: this.permission.VISUALIZAR_USUARIO
		},
		{
			text: 'Gestión de documentos',
			icon: 'snippet_folder',
			path: '',
			clase: 'green',
			active: true,
			view: 'ges_doc',
			permissionsMenu: [this.permission.CARGAR_DOCUMENTO, this.permission.VISUALIZAR_DETALLE_DOCUMENTOS]
		},
		{
			text: 'Expediente Digital',
			icon: 'cloud_done',
			path: '',
			clase: 'green',
			active: true,
			view: 'exp_dig',
			permissionsMenu: [this.permission.IR_EXPEDIENTE_DIGITAL, this.permission.GENERAR_EXPEDIENTE_DIGITAL, this.permission.MONITOREO_EXPEDIENTE_GENERADOS]
		}
	];


}
