/**
 * Representa a estrutura padronizada de erro retornada pelo backend.
 * Corresponde ao ErroResponseDTO do GlobalExceptionHandler.
 */
export interface ApiErrorResponse {
  status: number;
  erro: string;
  mensagem: string;
  timestamp: string;
  detalhes: string[] | null;
}

/**
 * Extrai uma mensagem legível de um HttpErrorResponse cujo corpo
 * segue o formato ApiErrorResponse do backend.
 *
 * @param err - O erro recebido do Angular HttpClient
 * @param fallback - Mensagem genérica usada quando não há corpo estruturado
 * @returns Mensagem de erro para exibição ao usuário
 */
export function extrairMensagemErro(err: unknown, fallback: string): string {
  if (err && typeof err === 'object' && 'error' in err) {
    const httpErr = err as { error?: ApiErrorResponse };
    const body = httpErr.error;

    if (body?.detalhes && body.detalhes.length > 0) {
      return body.detalhes.join(' | ');
    }

    if (body?.mensagem) {
      return body.mensagem;
    }
  }

  return fallback;
}
