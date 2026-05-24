import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParteRequest, TipoPessoa } from '../../../../core/models/processo.model';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { ViaCepService } from '../../../../core/services/via-cep.service';
import { BrasilApiService } from '../../../../core/services/brasil-api.service';
import { DocumentoUtils } from '../../../../core/utils/documento.utils';

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
  private toastr = inject(ToastrService);
  private spinner = inject(NgxSpinnerService);
  private viaCepService = inject(ViaCepService);
  private brasilApiService = inject(BrasilApiService);

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
    return DocumentoUtils.calcularIdade(new Date(nascimento)) < 18;
  }

  get idadeCalculada(): number {
    const nascimento = this.form.get('dataNascimento')?.value;
    if (!nascimento) return 0;
    return DocumentoUtils.calcularIdade(new Date(nascimento));
  }

  // ======================== Máscara CPF/CNPJ ========================

  onDocumentoInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const mascarado = DocumentoUtils.aplicarMascaraDocumento(input.value);
    this.form.get('documento')?.setValue(mascarado, { emitEvent: false });
    input.value = mascarado;
  }

  // ======================== Blur: detectar tipo e buscar dados ========================

  onDocumentoBlur(): void {
    const doc = DocumentoUtils.somentNumeros(this.form.get('documento')?.value || '');
    if (!doc) return;

    if (DocumentoUtils.isCpf(doc)) {
      this.tipoPessoa = 'PESSOA_FISICA';
      this.limparDadosAutomaticos();
      this.form.get('dataNascimento')?.setValidators([Validators.required]);
    } else if (DocumentoUtils.isCnpj(doc)) {
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
    const cep = DocumentoUtils.somentNumeros(this.form.get('cep')?.value || '');
    if (cep.length !== 8) return;
    this.buscarEnderecoCep(cep);
  }

  // ======================== Consultas externas via serviços ========================

  private buscarDadosCnpj(cnpj: string): void {
    this.buscandoDados = true;
    this.spinner.show('parte-spinner');
    this.brasilApiService.buscarCnpj(cnpj)
      .pipe(finalize(() => {
        this.buscandoDados = false;
        this.spinner.hide('parte-spinner');
      }))
      .subscribe({
        next: (dados) => {
          this.form.patchValue({
            nome: dados.razao_social || '',
            razaoSocial: dados.razao_social || '',
            cnae: dados.cnae_fiscal
              ? `${dados.cnae_fiscal} - ${dados.cnae_fiscal_descricao}`
              : '',
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
    this.viaCepService.buscarEndereco(cep)
      .pipe(finalize(() => {
        this.buscandoDados = false;
        this.spinner.hide('parte-spinner');
      }))
      .subscribe({
        next: (dados) => {
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

  private limparDadosAutomaticos(): void {
    this.form.patchValue({
      logradouro: '', bairro: '', cidade: '', uf: '',
      razaoSocial: '', cnae: '', naturezaJuridica: '', situacao: ''
    });
  }

}
