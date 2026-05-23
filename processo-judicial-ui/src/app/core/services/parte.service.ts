import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Parte, ParteRequest } from '../models/processo.model';

@Injectable({ providedIn: 'root' })
export class ParteService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/processos';

  constructor(private http: HttpClient) {}

  adicionar(processoId: string, request: ParteRequest): Observable<Parte> {
    return this.http.post<Parte>(`${this.baseUrl}/${processoId}/partes`, request).pipe(
      catchError((error: HttpErrorResponse) => {
        const mensagem = this.extrairMensagem(error);
        return throwError(() => new Error(mensagem));
      })
    );
  }

  private extrairMensagem(error: HttpErrorResponse): string {
    const body = error.error;
    if (!body) return 'Erro ao adicionar parte';
    if (body.detalhes?.length) return body.detalhes[0];
    if (body.mensagem) return body.mensagem;
    return 'Erro ao adicionar parte';
  }
}
