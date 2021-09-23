import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CargadocPageComponent } from './cargadoc-flow/cargadoc-page/cargadoc-page.component';
import { ConsultardocPageComponent } from './consultardoc-flow/consultardoc-page/consultardoc-page.component';
import { DashExpGeneradosPageComponent } from './dash-exp-generados-flow/dash-exp-generados-page/dash-exp-generados-page.component';
import { DashExpMecFactPageComponent } from './dash-exp-mec-fact-flow/dash-exp-mec-fact-page/dash-exp-mec-fact-page.component';
import { DashLoteEnviadoGarantePageComponent } from './dash-lote-enviado-garante/dash-lote-enviado-garante-page/dash-lote-enviado-garante-page.component';
import { ExpedientePageComponent } from './expediente-flow/expediente-page/expediente-page.component';
import { GestionLotePageComponent } from './gestion-lote-flow/gestion-lote-page/gestion-lote-page.component';
import { HomePageComponent } from './home-page/home-page.component';
import { LotesEnviadosGarantePageComponent } from './lotes-enviados-garante-flow/lotes-enviados-garante-page/lotes-enviados-garante-page.component';
import { MoniExpedientePageComponent } from './moni-expediente-flow/moni-expediente-page/moni-expediente-page.component';
import { OtrosReportesPageComponent } from './otros-reportes-flow/otros-reportes-page/otros-reportes-page.component';
import { ReporteParcialPageComponent } from './reporte-parcial-flow/reporte-parcial-page/reporte-parcial-page.component';
import { ReporteTotalPageComponent } from './reporte-total-flow/reporte-total-page/reporte-total-page.component';
import { RolPageComponent } from './roles-flow/rol-page/rol-page.component';
import { UserPageComponent } from './user-flow/user-page/user-page.component';


const routes: Routes = [
  {
    path: 'home',
    children: [
      {
        path: '',
        component: HomePageComponent,
        data: {
          title: "home",
          breadcrumb: "home"
        }
      },
    ]
  },
  {
    path: 'roles',
    children: [
      {
        path: '',
        component: RolPageComponent,
        data: {
          title: "roles",
          breadcrumb: "roles"
        }
      },
    ]
  },
  {
    path: 'user',
    children: [
      {
        path: '',
        component: UserPageComponent,
        data: {
          title: "roles",
          breadcrumb: "roles"
        }
      },
    ]
  },

  {
    path: 'documentos',
    children: [
      {
        path: 'consulta',
        component: ConsultardocPageComponent,
        data: {
          title: "Consultar Documentos",
          breadcrumb: "Consultar documentos"
        }
      },

    ]
  },
  {
    path: 'documento',
    children: [
      {
        path: 'carga',
        component: CargadocPageComponent,
        data: {
          title: "carga documento",
          breadcrumb: "carga documento"
        }
      },
    ]
  },
  {
    path: 'genererar',
    children: [
      {
        path: 'expediente',
        component: ExpedientePageComponent,
        data: {
          title: "generar expediente",
          breadcrumb: "generar expediente"
        }
      },
    ]
  },
  {
    path: 'documentos',
    children: [
      {
        path: 'moni-expediente',
        component: MoniExpedientePageComponent,
        data: {
          title: "monitoreo de expediente",
          breadcrumb: "monitoreo de expediente"
        }
      },
    ]
  },
  {
    path: 'gestion',
    children: [
      {
        path: 'lote',
        component: GestionLotePageComponent,
        data: {
          title: "gestion de lote",
          breadcrumb: "gestion de lote"
        }
      },
    ]
  },

  {
    path: 'reporte',
    children: [
      {
        path: 'total',
        component:ReporteTotalPageComponent,
        data: {
          title: "reporte total",
          breadcrumb: "reporte total"
        }
      },
    ]
  },

  {
    path: 'reporte',
    children: [
      {
        path: 'parcial',
        component: ReporteParcialPageComponent,
        data: {
          title: "reporte parcial",
          breadcrumb: "reporte parcial"
        }
      },
    ]
  },

  {
    path: 'otros',
    children: [
      {
        path: 'reportes',
        component: OtrosReportesPageComponent,
        data: {
          title: "otros reportes",
          breadcrumb: "otros reportes"
        }
      },
    ]
  },

  {
    path: 'dashboard',
    children: [
      {
        path: 'dash-exp-generados',
        component: DashExpGeneradosPageComponent,
        data: {
          title: "Dashboard de Exp. Generados",
          breadcrumb: "Dashboard de Exp. Generados"
        }
      },
      {
        path: 'dash-exp-mec-fact',
        component: DashExpMecFactPageComponent,
        data: {
          title: "Dashboard de Exp. Generados por Mecanismo y Modo de Facturación",
          breadcrumb: "Dashboard de Exp. Generados por Mecanismo y Modo de Facturación"
        }
      },
    ]
  },
  {
    path: 'lote',
    children: [
      {
        path: 'enviados-garante',
        component: LotesEnviadosGarantePageComponent,
        data: {
          title: "Lotes enviados al Garante",
          breadcrumb: "Lotes enviados al Garante"
        }
      },
    ]
  },

  {
    path: 'dash',
    children: [
      {
        path: 'lote-enviado-garante',
        component: DashLoteEnviadoGarantePageComponent,
        data: {
          title: "Dashboard lotes enviados al garante",
          breadcrumb: "Dashboard lotes enviados al garante"
        }
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComponentsRoutingModule { }
