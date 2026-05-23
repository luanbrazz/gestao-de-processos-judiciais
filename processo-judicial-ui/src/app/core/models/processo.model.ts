export type StatusProcesso = 'ATIVO' | 'SUSPENSO' | 'ENCERRADO';
export type TipoParte = 'AUTOR' | 'REU';
export type TipoPessoa = 'PESSOA_FISICA' | 'PESSOA_JURIDICA';

export interface Parte {
  id: string;
  tipo: TipoParte;
  tipoPessoa: TipoPessoa;
  nome: string;
  documento: string;
  cep?: string;
  logradouro?: string;
  bairro?: string;
  cidade?: string;
  uf?: string;
  // Pessoa Física
  dataNascimento?: string;
  // Pessoa Jurídica
  razaoSocial?: string;
  cnae?: string;
  naturezaJuridica?: string;
  situacao?: string;
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

export interface ParteRequestBase {
  tipoPessoa: TipoPessoa;
  tipo: TipoParte;
  nome: string;
}

export interface PessoaFisicaRequest extends ParteRequestBase {
  tipoPessoa: 'PESSOA_FISICA';
  documento: string;
  cep?: string;
  dataNascimento?: string;
}

export interface PessoaJuridicaRequest extends ParteRequestBase {
  tipoPessoa: 'PESSOA_JURIDICA';
  documento: string;
}

export type ParteRequest = PessoaFisicaRequest | PessoaJuridicaRequest;

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
