/**
 * Utilitários relacionados a documentos brasileiros (CPF/CNPJ) e campos de identidade.
 * Funções puras, sem dependências de framework — reutilizáveis em qualquer componente ou service.
 */
export class DocumentoUtils {

  /**
   * Remove todos os caracteres não numéricos de uma string.
   * Útil para limpar CPF, CNPJ, CEP, telefone etc. antes de enviar à API.
   *
   * @example somentNumeros("123.456.789-09") → "12345678909"
   */
  static somentNumeros(valor: string): string {
    return valor.replace(/\D/g, '');
  }

  /**
   * Aplica máscara de CPF (000.000.000-00) ou CNPJ (00.000.000/0000-00)
   * de forma progressiva, detectando o tipo pelo número de dígitos.
   * Limita a entrada a no máximo 14 dígitos.
   *
   * @example aplicarMascaraDocumento("12345678909") → "123.456.789-09"
   * @example aplicarMascaraDocumento("12345678000190") → "12.345.678/0001-90"
   */
  static aplicarMascaraDocumento(valor: string): string {
    const digitos = valor.replace(/\D/g, '').substring(0, 14);

    if (digitos.length <= 11) {
      return digitos
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }

    let v = digitos;
    v = v.replace(/^(\d{2})(\d)/, '$1.$2');
    v = v.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
    v = v.replace(/\.(\d{3})(\d)/, '.$1/$2');
    v = v.replace(/(\d{4})(\d{1,2})$/, '$1-$2');
    return v;
  }

  /**
   * Aplica máscara de CEP (00000-000).
   *
   * @example aplicarMascaraCep("12030145") → "12030-145"
   */
  static aplicarMascaraCep(valor: string): string {
    const digitos = valor.replace(/\D/g, '').substring(0, 8);
    return digitos.replace(/^(\d{5})(\d{1,3})$/, '$1-$2');
  }

  /**
   * Calcula a idade em anos completos a partir de uma data de nascimento.
   * Considera se o aniversário já ocorreu no ano corrente.
   *
   * @param dataNascimento - Data de nascimento como objeto Date
   * @returns Idade em anos completos
   */
  static calcularIdade(dataNascimento: Date): number {
    const hoje = new Date();
    let idade = hoje.getFullYear() - dataNascimento.getFullYear();
    const aniversarioPassou =
      hoje.getMonth() > dataNascimento.getMonth() ||
      (hoje.getMonth() === dataNascimento.getMonth() &&
        hoje.getDate() >= dataNascimento.getDate());
    if (!aniversarioPassou) idade--;
    return idade;
  }

  /**
   * Retorna true se o documento (CPF ou CNPJ) possui exatamente 11 dígitos (CPF).
   */
  static isCpf(documento: string): boolean {
    return this.somentNumeros(documento).length === 11;
  }

  /**
   * Retorna true se o documento (CPF ou CNPJ) possui exatamente 14 dígitos (CNPJ).
   */
  static isCnpj(documento: string): boolean {
    return this.somentNumeros(documento).length === 14;
  }
}
