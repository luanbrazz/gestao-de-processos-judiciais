import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Movimentacao, MovimentacaoRequest } from '../models/processo.model';

@Injectable({ providedIn: 'root' })
export class MovimentacaoService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/processos';

  constructor(private http: HttpClient) {}

  adicionar(processoId: string, request: MovimentacaoRequest): Observable<Movimentacao> {
    return this.http.post<Movimentacao>(`${this.baseUrl}/${processoId}/movimentacoes`, request);
  }
}
