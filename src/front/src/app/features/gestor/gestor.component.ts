import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-gestor',
  templateUrl: './gestor.component.html',
  styleUrls: ['./gestor.component.css'],
  standalone: false
})
export class GestorComponent implements OnInit {
  activeTab: 'tablero' | 'secciones' | 'pictogramas' = 'tablero';
  dependienteId: string = '';
  mostrarFormularioSeccion: boolean = false;
  mostrarFormularioPictograma: boolean = false;
  reloadKeySeccion: number = 0;
  reloadKeyPictograma: number = 0;
  seccionEditando: any = null;
  pictogramaEditando: any = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.dependienteId = params['id'];
      console.log('Dependent ID:', this.dependienteId);
    });
  }

  switchTab(tab: 'tablero' | 'secciones' | 'pictogramas'): void {
    this.activeTab = tab;
  }

  // Métodos para secciones
  crearSeccion(): void {
    this.seccionEditando = null;
    this.mostrarFormularioSeccion = true;
  }

  cerrarFormularioSeccion(): void {
    this.mostrarFormularioSeccion = false;
    this.seccionEditando = null;
    this.reloadKeySeccion++;
  }

  editarSeccion(seccion: any): void {
    this.seccionEditando = seccion;
    this.mostrarFormularioSeccion = true;
  }

  seccionGuardada(seccion: any): void {
    // Si se acaba de crear, abrirlo automáticamente en edición
    if (seccion && seccion.shouldReopen === true) {
      // Pequeño delay para que el componente se re-cargue
      setTimeout(() => {
        this.seccionEditando = seccion;
        this.mostrarFormularioSeccion = true;
      }, 100);
    } else if (seccion) {
      // Si es actualización, recargar lista
      this.cerrarFormularioSeccion();
    }
  }

  // Métodos para pictogramas
  crearPictograma(): void {
    this.pictogramaEditando = null;
    this.mostrarFormularioPictograma = true;
  }

  cerrarFormularioPictograma(): void {
    this.mostrarFormularioPictograma = false;
    this.pictogramaEditando = null;
    this.reloadKeyPictograma++;
  }

  editarPictograma(pictograma: any): void {
    this.pictogramaEditando = pictograma;
    this.mostrarFormularioPictograma = true;
  }
}
