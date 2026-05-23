import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Processo, StatusProcesso } from '../../../../core/models/processo.model';
import { ProcessoService } from '../../../../core/services/processo.service';

@Component({
  selector: 'app-processo-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './processo-detail.component.html'
})
export class ProcessoDetailComponent implements OnInit {

  processo?: Processo;
  parteForm!: FormGroup;
  movimentacaoForm!: FormGroup;
  readonly statusOptions: StatusProcesso[] = ['ATIVO', 'SUSPENSO', 'ENCERRADO'];
  readonly tipoOptions = ['AUTOR', 'REU'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private processoService: ProcessoService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.parteForm = this.fb.group({
      tipo: ['AUTOR', Validators.required],
      nome: ['', Validators.required],
      documento: ['', Validators.required],
      cep: ['']
    });

    this.movimentacaoForm = this.fb.group({
      descricao: ['', Validators.required]
    });

    this.carregar();
  }

  carregar(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.spinner.show();
    this.processoService.buscarPorId(id).subscribe({
      next: p => {
        this.processo = p;
        this.spinner.hide();
      },
      error: () => {
        this.toastr.error('Processo não encontrado');
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
      error: () => {
        this.toastr.error('Erro ao atualizar status');
        this.spinner.hide();
      }
    });
  }

  adicionarParte(): void {
    if (this.parteForm.invalid) {
      this.parteForm.markAllAsTouched();
      return;
    }
    this.spinner.show();
    this.processoService.adicionarParte(this.processo!.id, this.parteForm.value).subscribe({
      next: () => {
        this.toastr.success('Parte adicionada com sucesso!');
        this.parteForm.reset({ tipo: 'AUTOR' });
        this.carregar();
      },
      error: () => {
        this.toastr.error('Erro ao adicionar parte');
        this.spinner.hide();
      }
    });
  }

  adicionarMovimentacao(): void {
    if (this.movimentacaoForm.invalid) {
      this.movimentacaoForm.markAllAsTouched();
      return;
    }
    this.spinner.show();
    this.processoService.adicionarMovimentacao(this.processo!.id, this.movimentacaoForm.value).subscribe({
      next: () => {
        this.toastr.success('Movimentação registrada!');
        this.movimentacaoForm.reset();
        this.carregar();
      },
      error: () => {
        this.toastr.error('Erro ao registrar movimentação');
        this.spinner.hide();
      }
    });
  }

  badgeClass(status: StatusProcesso): string {
    const map = { ATIVO: 'bg-success', SUSPENSO: 'bg-warning text-dark', ENCERRADO: 'bg-danger' };
    return map[status];
  }

  campoInvalido(form: FormGroup, campo: string): boolean {
    const c = form.get(campo);
    return !!(c?.invalid && c?.touched);
  }
}
