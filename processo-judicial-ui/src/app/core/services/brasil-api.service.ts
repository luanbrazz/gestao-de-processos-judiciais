import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface CnpjResponse {
  cnpj: string;
  razao_social: string;
  nome_fantasia?: string;
  natureza_juridica?: string;
  cnae_fiscal?: number;
  cnae_fiscal_descricao?: string;
  descricao_situacao_cadastral?: string;
  cep?: string;
  logradouro?: string;
  bairro?: string;
  municipio?: string;
  uf?: string;
}

@Injectable({ providedIn: 'root' })
export class BrasilApiService {
  private readonly BASE_URL = 'https://brasilapi.com.br/api';

  private http = inject(HttpClient);

  /**
   * Consulta os dados cadastrais de um CNPJ via BrasilAPI.
   * CNPJ deve conter apenas dígitos (14 caracteres).
   *
   * @param cnpj - CNPJ com ou sem formatação
   * @returns Observable com os dados da empresa
   */
  buscarCnpj(cnpj: string): Observable<CnpjResponse> {
    const cnpjSoDigitos = cnpj.replace(/\D/g, '');
    return this.http.get<CnpjResponse>(
      `${this.BASE_URL}/cnpj/v1/${cnpjSoDigitos}`
    ).pipe(
      catchError(err => throwError(() => err))
    );
  }
}
