import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ComponentsRoutingModule } from './components-routing.module';
import { MatIconModule } from '@angular/material/icon';
import { SharedModule } from '../shared/shared.module';
import { HomePageComponent } from './home-page/home-page.component';
import { DemoMaterialModule } from '../material-modules';
import { BotoneMenuModule } from '../shared/components/boton-menu/botone-menu.module';
import { RolPageComponent } from './roles-flow/rol-page/rol-page.component';
import { TableComponent } from './roles-flow/table/table.component';
import { CrearRolComponent } from './roles-flow/crear-rol/crear-rol.component';
import { ViewRolComponent } from './roles-flow/view-rol/view-rol.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserPageComponent } from './user-flow/user-page/user-page.component';
import { ModalCreateComponent } from './user-flow/modal-create/modal-create.component';
import { ModalViewComponent } from './user-flow/modal-view/modal-view.component';
import { TablaUserComponent } from './user-flow/tabla/tabla-user.component';
import { ConsultardocPageComponent } from './consultardoc-flow/consultardoc-page/consultardoc-page.component';
import { ModalEditComponent } from './consultardoc-flow/modal-edit/modal-edit.component';
import { ModalVisualizarComponent } from './consultardoc-flow/modal-visualizar/modal-visualizar.component';
import { TableDocsComponent } from './consultardoc-flow/table-docs/table-docs.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CargadocPageComponent } from './cargadoc-flow/cargadoc-page/cargadoc-page.component';
import { AlifeFileToBase64Module } from 'alife-file-to-base64';
import { MatButtonModule } from '@angular/material/button';
import { ExpedientePageComponent } from './expediente-flow/expediente-page/expediente-page.component';
import { TableExpedienteComponent } from './expediente-flow/table-expediente/table-expediente.component';
import { DocumentosModalComponent } from './expediente-flow/documentos-modal/documentos-modal.component';
import { ModalErrorComponent } from './moni-expediente-flow/modal-error/modal-error.component';
import { MoniExpedientePageComponent } from './moni-expediente-flow/moni-expediente-page/moni-expediente-page.component';
import { TableMoniComponent } from './moni-expediente-flow/table-moni/table-moni.component';
import { GestionLotePageComponent } from './gestion-lote-flow/gestion-lote-page/gestion-lote-page.component';
import { ModalGestionLoteDeleteComponent } from './gestion-lote-flow/modal-gestion-lote-delete/modal-gestion-lote-delete.component';
import { GestionLoteTableComponent } from './gestion-lote-flow/gestion-lote-table/gestion-lote-table.component';
import { ModalValidateComponent } from './expediente-flow/modal-validate/modal-validate.component';
import { ReporteTotalPageComponent } from './reporte-total-flow/reporte-total-page/reporte-total-page.component';
import { ReporteParcialPageComponent } from './reporte-parcial-flow/reporte-parcial-page/reporte-parcial-page.component';
import { OtrosReportesPageComponent } from './otros-reportes-flow/otros-reportes-page/otros-reportes-page.component';
import { ReporteTotalTableComponent } from './reporte-total-flow/reporte-total-table/reporte-total-table.component';
import { ReporteParcialTableComponent } from './reporte-parcial-flow/reporte-parcial-table/reporte-parcial-table.component';
import { OtrosReportesTableComponent } from './otros-reportes-flow/otros-reportes-table/otros-reportes-table.component';
import { DashExpGeneradosPageComponent } from './dash-exp-generados-flow/dash-exp-generados-page/dash-exp-generados-page.component';
import { DashExpMecFactPageComponent } from './dash-exp-mec-fact-flow/dash-exp-mec-fact-page/dash-exp-mec-fact-page.component';
import { ChartComponent } from './dash-exp-generados-flow/chart/chart.component';
import { ChartMecanismoComponent } from './dash-exp-mec-fact-flow/chart-mecanismo/chart-mecanismo.component';
import { ChartDonutComponent } from './dash-exp-generados-flow/chart-donut/chart-donut.component';
import { PermissionModule } from '../permission/permission.module';
import { ModalReportesinloteComponent } from './consultardoc-flow/modal-reportesinlote/modal-reportesinlote.component';
import { ModalReporteComponent } from './moni-expediente-flow/modal-reporte/modal-reporte.component';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { LotesEnviadosGarantePageComponent } from './lotes-enviados-garante-flow/lotes-enviados-garante-page/lotes-enviados-garante-page.component';
import { LotesEnviadosTableComponent } from './lotes-enviados-garante-flow/lotes-enviados-table/lotes-enviados-table.component';
import { LotesEnviadosModalComponent } from './lotes-enviados-garante-flow/lotes-enviados-modal/lotes-enviados-modal.component';
import { LotesEnviadosLogComponent } from './lotes-enviados-garante-flow/lotes-enviados-log/lotes-enviados-log.component';
import { DashLoteEnviadoGarantePageComponent } from './dash-lote-enviado-garante/dash-lote-enviado-garante-page/dash-lote-enviado-garante-page.component';
import { ChartLoteEnviadoGaranteComponent } from './dash-lote-enviado-garante/chart-lote-enviado-garante/chart-lote-enviado-garante.component';
import { ModalMensajeErrorComponent } from './otros-reportes-flow/modal-mensaje-error/modal-mensaje-error.component';
import { ModalReprocesoComponent } from './gestion-lote-flow/modal-reproceso/modal-reproceso.component';
import { ErrorDocumentosModalComponent } from './expediente-flow/error-documentos-modal/error-documentos-modal.component';
import { ErrorModalParcialComponent } from './reporte-parcial-flow/error-modal-parcial/error-modal-parcial.component';

export const MY_FORMATS = {
  parse: {
    dateInput: 'LL'
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'YYYY'
  }
};

@NgModule({
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [
    HomePageComponent,
    RolPageComponent,
    TablaUserComponent,
    TableComponent,
    CrearRolComponent,
    ViewRolComponent,
    UserPageComponent,
    ModalCreateComponent,
    ModalViewComponent,
    TablaUserComponent,
    ConsultardocPageComponent,
    ModalEditComponent,
    ModalVisualizarComponent,
    TableDocsComponent,
    CargadocPageComponent,
    ExpedientePageComponent,
    TableExpedienteComponent,
    DocumentosModalComponent,
    ModalErrorComponent,
    MoniExpedientePageComponent,
    TableMoniComponent,
    GestionLotePageComponent,
    ModalGestionLoteDeleteComponent,
    GestionLoteTableComponent,
    ModalValidateComponent,
    ReporteTotalPageComponent,
    ReporteParcialPageComponent,
    OtrosReportesPageComponent,
    ReporteTotalTableComponent,
    ReporteParcialTableComponent,
    OtrosReportesTableComponent,
    DashExpGeneradosPageComponent,
    DashExpMecFactPageComponent,
    ChartComponent,
    ChartMecanismoComponent,
    ChartDonutComponent,
    ModalReportesinloteComponent,
    ModalReporteComponent,
    LotesEnviadosGarantePageComponent,
    LotesEnviadosTableComponent,
    LotesEnviadosModalComponent,
    LotesEnviadosLogComponent,
    DashLoteEnviadoGarantePageComponent,
    ChartLoteEnviadoGaranteComponent,
    ModalMensajeErrorComponent,
    ModalReprocesoComponent,
    ErrorDocumentosModalComponent,
    ErrorModalParcialComponent,

  ],
  imports: [
    SharedModule,
    CommonModule,
    ComponentsRoutingModule,
    DemoMaterialModule,
    MatIconModule,
    MatButtonModule,
    BotoneMenuModule,
    FormsModule,
    ReactiveFormsModule,
    AlifeFileToBase64Module,
    PermissionModule
  ],
  providers: [
    DatePipe,
    { provide: MAT_DIALOG_DATA, useValue: {} },
    { provide: MatDialogRef, useValue: {} },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE]
    },

    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }
  ]
})
export class componentsModule { }
