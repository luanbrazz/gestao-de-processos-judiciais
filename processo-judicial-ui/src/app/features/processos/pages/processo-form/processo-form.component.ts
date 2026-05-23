import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ProcessoService } from '../../../../core/services/processo.service';

@Component({
  selector: 'app-processo-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './processo-form.component.html'
})
export class ProcessoFormComponent implements OnInit {

  form!: FormGroup;
  isEdicao = false;
  processoId?: string;

  constructor(
    private fb: FormBuilder,
    private processoService: ProcessoService,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      numero: ['', [Validators.required, Validators.maxLength(50)]],
      assunto: ['', [Validators.required, Validators.maxLength(255)]],
      vara: ['', [Validators.required, Validators.maxLength(255)]],
      dataAbertura: ['', Validators.required]
    });

    this.processoId = this.route.snapshot.paramMap.get('id') ?? undefined;
    this.isEdicao = !!this.processoId;

    if (this.isEdicao && this.processoId) {
      this.spinner.show();
      this.processoService.buscarPorId(this.processoId).subscribe({
        next: p => {
          this.form.patchValue(p);
          this.spinner.hide();
        },
        error: () => {
          this.toastr.error('Erro ao carregar processo');
          this.spinner.hide();
        }
      });
    }
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.spinner.show();
    const request = this.form.value;

    const operacao = this.isEdicao && this.processoId
      ? this.processoService.atualizar(this.processoId, request)
      : this.processoService.criar(request);

    operacao.subscribe({
      next: p => {
        this.toastr.success(this.isEdicao ? 'Processo atualizado!' : 'Processo criado!');
        this.spinner.hide();
        this.router.navigate(['/processos', p.id]);
      },
      error: err => {
        this.toastr.error(err.error?.mensagem || 'Erro ao salvar processo');
        this.spinner.hide();
      }
    });
  }

  campoInvalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }
}
