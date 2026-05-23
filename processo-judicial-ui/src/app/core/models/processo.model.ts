export type StatusProcesso = 'ATIVO' | 'SUSPENSO' | 'ENCERRADO';
export type TipoParte = 'AUTOR' | 'REU';

export interface Parte {
  id: string;
  tipo: TipoParte;
  nome: string;
  documento: string;
  cep?: string;
  logradouro?: string;
  bairro?: string;
  cidade?: string;
  uf?: string;
}

export interface Movimentacao {
  id: string;
  descricao: string;
  dataMovimentacao: string;
}

export interface Processo {
  id: string;
  numero: string;
  assunto: string;
  vara: string;
  status: StatusProcesso;
  dataAbertura: string;
  criadoEm: string;
  atualizadoEm: string;
  partes: Parte[];
  movimentacoes: Movimentacao[];
}

export interface ProcessoRequest {
  numero: string;
  assunto: string;
  vara: string;
  dataAbertura: string;
}

export interface ParteRequest {
  tipo: TipoParte;
  nome: string;
  documento: string;
  cep?: string;
}

export interface MovimentacaoRequest {
  descricao: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
