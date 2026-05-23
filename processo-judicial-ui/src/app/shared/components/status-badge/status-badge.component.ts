import { StatusProcesso } from './../../../core/models/processo.model';
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html'
})
export class StatusBadgeComponent {
  @Input() status!: StatusProcesso;

  badgeClass(): string {
    const map = { ATIVO: 'bg-success', SUSPENSO: 'bg-warning text-dark', ENCERRADO: 'bg-danger' };
    return map[this.status];
  }
}
