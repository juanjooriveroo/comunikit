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
  mostrarFormularioPictograma: boolean = false;
  reloadKey: number = 0;
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

  crearPictograma(): void {
    this.pictogramaEditando = null;
    this.mostrarFormularioPictograma = true;
  }

  cerrarFormularioPictograma(): void {
    this.mostrarFormularioPictograma = false;
    this.pictogramaEditando = null;
    this.reloadKey++;
  }

  editarPictograma(pictograma: any): void {
    this.pictogramaEditando = pictograma;
    this.mostrarFormularioPictograma = true;
  }
}
