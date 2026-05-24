import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ProcessoService } from '../../../../core/services/processo.service';
import { ParteService } from '../../../../core/services/parte.service';
import { MovimentacaoService } from '../../../../core/services/movimentacao.service';
import { Processo, StatusProcesso, ParteRequest, MovimentacaoRequest } from '../../../../core/models/processo.model';
import { extrairMensagemErro } from '../../../../core/models/api-error.model';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ParteCardComponent } from '../../../partes/components/parte-card/parte-card.component';
import { ParteFormComponent } from '../../../partes/components/parte-form/parte-form.component';
import { MovimentacaoTimelineComponent } from '../../../movimentacoes/components/movimentacao-timeline/movimentacao-timeline.component';
import { MovimentacaoFormComponent } from '../../../movimentacoes/components/movimentacao-form/movimentacao-form.component';

@Component({
  selector: 'app-processo-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    StatusBadgeComponent,
    ParteCardComponent,
    ParteFormComponent,
    MovimentacaoTimelineComponent,
    MovimentacaoFormComponent
  ],
  templateUrl: './processo-detail.component.html'
})
export class ProcessoDetailComponent implements OnInit {

  processo?: Processo;
  readonly statusOptions: StatusProcesso[] = ['ATIVO', 'SUSPENSO', 'ENCERRADO'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private processoService: ProcessoService,
    private parteService: ParteService,
    private movimentacaoService: MovimentacaoService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.spinner.show();
    this.processoService.buscarPorId(id).subscribe({
      next: p => { this.processo = p; this.spinner.hide(); },
      error: (err: HttpErrorResponse) => {
        const mensagem = extrairMensagemErro(err, 'Processo não encontrado');
        this.toastr.error(mensagem, 'Erro');
        this.spinner.hide();
        this.router.navigate(['/processos']);
      }
    });
  }

  atualizarStatus(status: StatusProcesso): void {
    if (!this.processo) return;
    this.spinner.show();
    this.processoService.atualizarStatus(this.processo.id, status).subscribe({
      next: p => {
        this.processo = p;
        this.toastr.success(`Status atualizado para ${status}`);
        this.spinner.hide();
      },
      error: (err: HttpErrorResponse) => {
        const mensagem = extrairMensagemErro(err, 'Erro ao atualizar status');
        this.toastr.error(mensagem, 'Erro');
        this.spinner.hide();
      }
    });
  }

  onParteAdicionada(request: ParteRequest): void {
    this.spinner.show();
    this.parteService.adicionar(this.processo!.id, request).subscribe({
      next: () => {
        this.toastr.success('Parte adicionada com sucesso!');
        this.carregar();
      },
      error: (err: HttpErrorResponse) => {
        const mensagem = extrairMensagemErro(err, 'Erro ao adicionar parte');
        this.toastr.error(mensagem, 'Erro');
        this.spinner.hide();
      }
    });
  }

  onMovimentacaoAdicionada(request: MovimentacaoRequest): void {
    this.spinner.show();
    this.movimentacaoService.adicionar(this.processo!.id, request).subscribe({
      next: () => {
        this.toastr.success('Movimentação registrada com sucesso!');
        this.carregar();
      },
      error: (err: HttpErrorResponse) => {
        const mensagem = extrairMensagemErro(err, 'Erro ao registrar movimentação');
        this.toastr.error(mensagem, 'Erro');
        this.spinner.hide();
      }
    });
  }
}
