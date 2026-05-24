import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface EnderecoViaCep {
  cep: string;
  logradouro: string;
  bairro: string;
  localidade: string;
  uf: string;
}

@Injectable({ providedIn: 'root' })
export class ViaCepService {
  private readonly BASE_URL = 'https://viacep.com.br/ws';

  private http = inject(HttpClient);

  /**
   * Consulta o endereço correspondente ao CEP informado.
   * CEP deve conter apenas dígitos (8 caracteres).
   *
   * @param cep - CEP com ou sem formatação
   * @returns Observable com os dados de endereço
   */
  buscarEndereco(cep: string): Observable<EnderecoViaCep> {
    const cepSoDigitos = cep.replace(/\D/g, '');
    return this.http.get<EnderecoViaCep & { erro?: boolean }>(
      `${this.BASE_URL}/${cepSoDigitos}/json/`
    ).pipe(
      map(dados => {
        if (dados.erro) {
          throw new Error('CEP não encontrado');
        }
        return dados as EnderecoViaCep;
      }),
      catchError(err => throwError(() => err))
    );
  }
}
