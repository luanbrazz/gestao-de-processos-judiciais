import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParteRequest, TipoPessoa } from '../../../../core/models/processo.model';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-parte-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxSpinnerModule],
  templateUrl: './parte-form.component.html'
})
export class ParteFormComponent {
  @Output() parteAdicionada = new EventEmitter<ParteRequest>();

  readonly tipoOptions = ['AUTOR', 'REU'];
  form: FormGroup;
  tipoPessoa: TipoPessoa | null = null;
  buscandoDados = false;

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private toastr = inject(ToastrService);
  private spinner = inject(NgxSpinnerService);

  constructor() {
    this.form = this.fb.group({
      tipo: ['AUTOR', Validators.required],
      nome: ['', Validators.required],
      documento: ['', Validators.required],
      // Pessoa Física
      dataNascimento: ['', Validators.required],
      cep: [''],
      // Campos somente leitura (preenchidos automaticamente)
      logradouro: [{ value: '', disabled: true }],
      bairro: [{ value: '', disabled: true }],
      cidade: [{ value: '', disabled: true }],
      uf: [{ value: '', disabled: true }],
      // Pessoa Jurídica
      razaoSocial: [{ value: '', disabled: true }],
      cnae: [{ value: '', disabled: true }],
      naturezaJuridica: [{ value: '', disabled: true }],
      situacao: [{ value: '', disabled: true }],
    });
  }

  get isPessoaFisica(): boolean {
    return this.tipoPessoa === 'PESSOA_FISICA';
  }

  get isPessoaJuridica(): boolean {
    return this.tipoPessoa === 'PESSOA_JURIDICA';
  }

  get menorDeIdade(): boolean {
    const nascimento = this.form.get('dataNascimento')?.value;
    if (!nascimento || !this.isPessoaFisica) return false;
    return this.calcularIdade(new Date(nascimento)) < 18;
  }

  get idadeCalculada(): number {
    const nascimento = this.form.get('dataNascimento')?.value;
    if (!nascimento) return 0;
    return this.calcularIdade(new Date(nascimento));
  }

  // ======================== Máscara CPF/CNPJ ========================

  onDocumentoInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const mascarado = this.aplicarMascaraDocumento(input.value);
    this.form.get('documento')?.setValue(mascarado, { emitEvent: false });
    input.value = mascarado;
  }

  private aplicarMascaraDocumento(valor: string): string {
    const digitos = valor.replace(/\D/g, '').substring(0, 14);

    if (digitos.length <= 11) {
      // Máscara CPF: 000.000.000-00
      return digitos
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    } else {
      // Máscara CNPJ: 00.000.000/0000-00
      let v = digitos;
      v = v.replace(/^(\d{2})(\d)/, '$1.$2');
      v = v.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
      v = v.replace(/\.(\d{3})(\d)/, '.$1/$2');
      v = v.replace(/(\d{4})(\d{1,2})$/, '$1-$2');
      return v;
    }
  }

  // ======================== Blur: detectar tipo e buscar dados ========================

  onDocumentoBlur(): void {
    const doc = this.somentNumeros(this.form.get('documento')?.value || '');
    if (!doc) return;

    if (doc.length === 11) {
      this.tipoPessoa = 'PESSOA_FISICA';
      this.limparDadosAutomaticos();
      this.form.get('dataNascimento')?.setValidators([Validators.required]);
    } else if (doc.length === 14) {
      this.tipoPessoa = 'PESSOA_JURIDICA';
      this.limparDadosAutomaticos();
      this.form.get('dataNascimento')?.clearValidators();
      this.buscarDadosCnpj(doc);
    } else {
      this.tipoPessoa = null;
    }
    this.form.get('dataNascimento')?.updateValueAndValidity();
  }

  onCepBlur(): void {
    const cep = this.somentNumeros(this.form.get('cep')?.value || '');
    if (cep.length !== 8) return;
    this.buscarEnderecoCep(cep);
  }

  // ======================== Consultas externas ========================

  private buscarDadosCnpj(cnpj: string): void {
    this.buscandoDados = true;
    this.spinner.show('parte-spinner');
    this.http.get<any>(`https://brasilapi.com.br/api/cnpj/v1/${cnpj}`)
      .pipe(finalize(() => {
        this.buscandoDados = false;
        this.spinner.hide('parte-spinner');
      }))
      .subscribe({
        next: (dados) => {
          this.form.patchValue({
            nome: dados.razao_social || '',
            razaoSocial: dados.razao_social || '',
            cnae: dados.cnae_fiscal ? `${dados.cnae_fiscal} - ${dados.cnae_fiscal_descricao}` : '',
            naturezaJuridica: dados.natureza_juridica || '',
            situacao: dados.descricao_situacao_cadastral || '',
            logradouro: dados.logradouro || '',
            bairro: dados.bairro || '',
            cidade: dados.municipio || '',
            uf: dados.uf || '',
            cep: dados.cep || ''
          });
          this.toastr.success(`Empresa encontrada: ${dados.razao_social}`, 'CNPJ consultado');
        },
        error: () => {
          this.toastr.warning(
            'Não foi possível buscar dados do CNPJ. O backend irá tentar novamente ao salvar.',
            'BrasilAPI indisponível'
          );
        }
      });
  }

  private buscarEnderecoCep(cep: string): void {
    this.buscandoDados = true;
    this.spinner.show('parte-spinner');
    this.http.get<any>(`https://viacep.com.br/ws/${cep}/json/`)
      .pipe(finalize(() => {
        this.buscandoDados = false;
        this.spinner.hide('parte-spinner');
      }))
      .subscribe({
        next: (dados) => {
          if (dados.erro) {
            this.toastr.warning('CEP não encontrado.', 'ViaCEP');
            return;
          }
          this.form.patchValue({
            logradouro: dados.logradouro || '',
            bairro: dados.bairro || '',
            cidade: dados.localidade || '',
            uf: dados.uf || ''
          });
          this.toastr.success('Endereço preenchido automaticamente!', 'CEP encontrado');
        },
        error: () => {
          this.toastr.warning('Não foi possível consultar o CEP.', 'ViaCEP indisponível');
        }
      });
  }


  submeter(): void {
    if (!this.tipoPessoa) {
      this.toastr.error('Informe um CPF (11 dígitos) ou CNPJ (14 dígitos) válido.', 'Documento inválido');
      return;
    }

    if (this.menorDeIdade) {
      this.toastr.error(
        `A parte tem ${this.idadeCalculada} ano(s). Apenas maiores de 18 anos podem ser parte em processos judiciais.`,
        'Menor de idade'
      );
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();

    const request: ParteRequest = this.tipoPessoa === 'PESSOA_FISICA'
      ? {
          tipoPessoa: 'PESSOA_FISICA',
          tipo: raw.tipo,
          nome: raw.nome,
          documento: raw.documento,
          cep: raw.cep || undefined,
          dataNascimento: raw.dataNascimento || undefined
        }
      : {
          tipoPessoa: 'PESSOA_JURIDICA',
          tipo: raw.tipo,
          nome: raw.nome,
          documento: raw.documento
        };

    this.parteAdicionada.emit(request);
    this.form.reset({ tipo: 'AUTOR' });
    this.tipoPessoa = null;
  }

  invalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }

  private somentNumeros(valor: string): string {
    return valor.replace(/\D/g, '');
  }

  private limparDadosAutomaticos(): void {
    this.form.patchValue({
      logradouro: '', bairro: '', cidade: '', uf: '',
      razaoSocial: '', cnae: '', naturezaJuridica: '', situacao: ''
    });
  }

  private calcularIdade(dataNascimento: Date): number {
    const hoje = new Date();
    let idade = hoje.getFullYear() - dataNascimento.getFullYear();
    const aniversarioPassou =
      hoje.getMonth() > dataNascimento.getMonth() ||
      (hoje.getMonth() === dataNascimento.getMonth() &&
        hoje.getDate() >= dataNascimento.getDate());
    if (!aniversarioPassou) idade--;
    return idade;
  }
}
