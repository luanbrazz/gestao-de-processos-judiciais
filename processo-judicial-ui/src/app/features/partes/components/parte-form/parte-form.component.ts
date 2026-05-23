import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParteRequest } from '../../../../core/models/processo.model';

@Component({
  selector: 'app-parte-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './parte-form.component.html'
})
export class ParteFormComponent {
  @Output() parteAdicionada = new EventEmitter<ParteRequest>();

  readonly tipoOptions = ['AUTOR', 'REU'];
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      tipo: ['AUTOR', Validators.required],
      nome: ['', Validators.required],
      documento: ['', Validators.required],
      cep: ['']
    });
  }

  submeter(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.parteAdicionada.emit(this.form.value);
    this.form.reset({ tipo: 'AUTOR' });
  }

  invalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }
}
