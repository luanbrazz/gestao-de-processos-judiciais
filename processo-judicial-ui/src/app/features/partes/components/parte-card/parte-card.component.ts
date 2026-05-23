import { Parte } from './../../../../core/models/processo.model';
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-parte-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './parte-card.component.html'
})
export class ParteCardComponent {
  @Input() parte!: Parte;

  tipoBadgeClass(): string {
    return this.parte.tipo === 'AUTOR' ? 'bg-primary' : 'bg-danger';
  }
}
