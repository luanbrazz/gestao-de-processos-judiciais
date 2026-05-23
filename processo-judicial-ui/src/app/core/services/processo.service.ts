import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Processo, ProcessoRequest, Page, StatusProcesso } from '../models/processo.model';

@Injectable({ providedIn: 'root' })
export class ProcessoService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/processos';

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 10, status?: StatusProcesso): Observable<Page<Processo>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Processo>>(this.apiUrl, { params });
  }

  buscarPorId(id: string): Observable<Processo> {
    return this.http.get<Processo>(`${this.apiUrl}/${id}`);
  }

  criar(request: ProcessoRequest): Observable<Processo> {
    return this.http.post<Processo>(this.apiUrl, request);
  }

  atualizar(id: string, request: ProcessoRequest): Observable<Processo> {
    return this.http.put<Processo>(`${this.apiUrl}/${id}`, request);
  }

  atualizarStatus(id: string, status: StatusProcesso): Observable<Processo> {
    return this.http.patch<Processo>(
      `${this.apiUrl}/${id}/status`, null,
      { params: new HttpParams().set('status', status) }
    );
  }
}
