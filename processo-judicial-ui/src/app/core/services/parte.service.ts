import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Parte, ParteRequest } from '../models/processo.model';

@Injectable({ providedIn: 'root' })
export class ParteService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/processos';

  constructor(private http: HttpClient) {}

  adicionar(processoId: string, request: ParteRequest): Observable<Parte> {
    return this.http.post<Parte>(`${this.baseUrl}/${processoId}/partes`, request);
  }
}
