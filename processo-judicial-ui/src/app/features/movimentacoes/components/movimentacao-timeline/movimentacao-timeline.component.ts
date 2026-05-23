import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Movimentacao } from '../../../../core/models/processo.model';

@Component({
  selector: 'app-movimentacao-timeline',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './movimentacao-timeline.component.html'
})
export class MovimentacaoTimelineComponent {
  @Input() movimentacoes: Movimentacao[] = [];
}
