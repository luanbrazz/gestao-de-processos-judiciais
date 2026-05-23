import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MovimentacaoRequest } from '../../../../core/models/processo.model';

@Component({
  selector: 'app-movimentacao-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimentacao-form.component.html'
})
export class MovimentacaoFormComponent {
  @Output() movimentacaoAdicionada = new EventEmitter<MovimentacaoRequest>();

  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({ descricao: ['', Validators.required] });
  }

  submeter(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.movimentacaoAdicionada.emit(this.form.value);
    this.form.reset();
  }

  invalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }
}
