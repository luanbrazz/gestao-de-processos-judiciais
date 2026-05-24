# Nota Técnica — Gestão de Processos Judiciais

## Decisões de Arquitetura

### Backend

- **Arquitetura em camadas** com separação clara entre `api`, `application`, `domain` e `infrastructure`, seguindo princípios de Clean Architecture e SOLID.
- **Spring Boot 3.5 com Java 17**, escolhido por ser o stack declarado na vaga e por oferecer maturidade, ecossistema robusto e suporte LTS.
- **UUID como identificador** nas entidades para evitar enumeração de recursos e facilitar integração distribuída.
- **Flyway** para versionamento do banco de dados, garantindo rastreabilidade e reprodutibilidade das migrations em qualquer ambiente.
- **Herança JPA com estratégia JOINED** para as entidades `PessoaFisica` e `PessoaJuridica`, derivadas de `Parte`. Cada subclasse tem sua própria tabela com os campos específicos, enquanto a tabela `parte` armazena os campos comuns. O discriminador `tipo_pessoa` (`PESSOA_FISICA` | `PESSOA_JURIDICA`) permite que o Spring Data materialize o tipo correto automaticamente.
- **DTOs polimórficos com `@JsonTypeInfo`** para que o controller receba `ParteRequestDTO` e Jackson deserialize para `PessoaFisicaRequestDTO` ou `PessoaJuridicaRequestDTO` com base no campo `tipoPessoa`. Elimina a necessidade de lógica de roteamento no controller.
- **`DocumentoValidator`** como utilitário estático isolado no `domain/validator`, implementando o algoritmo oficial da Receita Federal (módulo 11) para validar CPF e CNPJ. Lança `RegraDeNegocioException` (HTTP 422) para documentos inválidos, centralizando a regra independente de framework.
- **Regra de maioridade** implementada no `ParteService` via `Period.between(dataNascimento, LocalDate.now()).getYears()`. Apenas Pessoa Física precisa dessa validação; a verificação é dupla: no frontend (botão bloqueado) e no backend (HTTP 422).
- **OpenFeign + Resilience4j** para integração com ViaCEP e BrasilAPI, com Circuit Breaker e Retry configurados para tolerância a falhas. Em caso de indisponibilidade de qualquer das APIs externas, o sistema continua funcionando sem os dados complementares (fallback gracioso).
- **Cache com Caffeine** para endereços (CEP) e dados de CNPJ já consultados, reduzindo chamadas externas e latência em consultas repetidas.
- **Kafka** para publicação de eventos de domínio (`processo-criado`), desacoplando o núcleo da aplicação de integrações futuras como notificações, auditoria e BI.
- **Elasticsearch + Kibana + Filebeat** para observabilidade, com logs estruturados em JSON permitindo busca e análise centralizada.
- **GlobalExceptionHandler** com exceções semânticas (`RecursoNaoEncontradoException` → 404, `RegraDeNegocioException` → 422, `ErroDeIntegracaoException` → 502) para respostas padronizadas com corpo `{ status, erro, mensagem, timestamp, detalhes }`.

### Frontend

- **Angular 17** com componentes standalone e lazy loading por rota.
- **Componentização por domínio**: `ParteCardComponent`, `ParteFormComponent`, `MovimentacaoTimelineComponent`, `MovimentacaoFormComponent`, `StatusBadgeComponent`.
- **Services por domínio e por integração externa**: `ProcessoService`, `ParteService`, `MovimentacaoService` para o backend; `ViaCepService` e `BrasilApiService` para APIs externas. Nenhum componente faz chamadas HTTP diretas — toda comunicação passa pelos services.
- **`ApiErrorResponse` model** (`core/models/api-error.model.ts`) mapeando o corpo de erro padronizado do backend. A função utilitária `extrairMensagemErro()` extrai `detalhes[0]` ou `mensagem` do erro, exibindo ao usuário a mensagem real da API em vez de strings genéricas.
- **Máscara de CPF/CNPJ** via evento nativo `(input)`, aplicando formatação progressiva com regex sequenciais. A detecção do tipo de pessoa (PF ou PJ) ocorre no `blur` com base no número de dígitos (11 = CPF, 14 = CNPJ), disparando automaticamente a consulta à BrasilAPI quando for CNPJ.
- **Modo visualização vs edição** controlado via data da rota, impedindo edições acidentais na tela de detalhes.

---

## Trade-offs

| Decisão | Alternativa considerada | Escolha e motivo |
|---|---|---|
| **Herança JOINED** para Parte | SINGLE_TABLE (uma tabela com NULLs) | JOINED: normalização correta, sem colunas nullable desnecessárias; custo de JOIN em queries é aceitável no contexto |
| **`@JsonTypeInfo` no DTO** | Endpoint separado por tipo (`/partes/fisica`, `/partes/juridica`) | Tipo único: contrato de API mais limpo, Jackson faz o roteamento automaticamente |
| **`DocumentoValidator` estático** | Bean Spring (`@Component`) | Utilitário puro, sem dependências — testável diretamente e reutilizável sem injeção |
| **Validação dupla CPF/CNPJ** (frontend + backend) | Somente backend | Frontend melhora UX; backend garante integridade mesmo em chamadas diretas à API |
| **`extrairMensagemErro` função pura** | Pipe Angular customizado | Função TypeScript simples: sem overhead de registro de módulo, testável com Jest |
| **UUID ao invés de Long** | Long autoincrement | Maior tamanho no banco e nas URLs, mas necessário para sistemas distribuídos |
| **Kafka no mesmo projeto** | Sem mensageria | Adiciona complexidade de infra, mas demonstra conhecimento de mensageria assíncrona |
| **Cache em memória (Caffeine)** | Redis | Não compartilhado entre instâncias, mas suficiente para o contexto do desafio |
| **Angular 17 ao invés de 19** | Angular 19 | Versão anterior por compatibilidade com Node 20.12, sem impacto funcional |
| **Sem autenticação JWT** | Spring Security + JWT | Priorizado o escopo funcional do teste dentro do prazo disponível |

---

## Melhorias Futuras

- **Autenticação e autorização** com Spring Security + JWT, com controle de perfis (Administrador, Procurador, Visualizador).
- **Cache distribuído** com Redis para compartilhar estado de CEPs e CNPJs entre múltiplas instâncias.
- **Dead Letter Queue** no Kafka para tratamento de mensagens que falharam no processamento.
- **Paginação no frontend** com filtros avançados por data, vara e assunto.
- **Testes de integração** com Testcontainers para PostgreSQL e Kafka.
- **CI/CD** com GitHub Actions para build, testes e deploy automatizados.
- **Soft delete** nos processos ao invés de deleção física.
- **Histórico de alterações de status** com timestamp e usuário responsável.
- **Validação de CPF/CNPJ em tempo real** no frontend (enquanto digita, não apenas no blur).
- **Cache de CNPJ no frontend** para evitar consultas repetidas à BrasilAPI na mesma sessão.

---

## Parte 2 — Análise de Incidente

### Cenário de Logs

    2026-05-22 03:14:52 ERROR [http-nio-8080-exec-7] GlobalExceptionHandler
    java.lang.NullPointerException: Cannot invoke "String.replaceAll(String, String)"
    because "dto.getCep()" is null
        at ParteService.preencherEnderecoViaCep(ParteService.java:67)
        at ParteService.adicionar(ParteService.java:45)
        at ParteController.adicionar(ParteController.java:38)

    2026-05-22 03:15:01 ERROR [http-nio-8080-exec-3] GlobalExceptionHandler
    java.lang.NullPointerException (recorrente a cada ~10 segundos)

    2026-05-22 03:18:44 WARN  o.s.k.l.KafkaMessageListenerContainer
    org.apache.kafka.common.errors.RecordDeserializationException:
    Error deserializing VALUE for partition processo-criado-0

### Diagnóstico

**Problema 1 — NullPointerException no CEP**

A causa raiz é que o método `preencherEnderecoViaCep` chamava `dto.getCep().replaceAll(...)` sem verificar se o CEP era nulo, mesmo sendo um campo opcional no formulário. O erro ocorreu de forma recorrente indicando que múltiplos clientes enviavam requisições sem o campo CEP.

**Problema 2 — Falha na deserialização Kafka**

O consumer tentava deserializar mensagens antigas gravadas antes da configuração correta do `trusted packages`, causando `RecordDeserializationException` em loop infinito.

### Correções Aplicadas

**Problema 1 — Validação defensiva antes de usar o CEP:**

    // Antes (quebrava com NullPointerException)
    viaCepClient.buscarEnderecoPorCep(dto.getCep().replaceAll("-", ""));

    // Depois (validação defensiva)
    if (cep == null || cep.isBlank()) return;
    String cepLimpo = cep.replaceAll("-", "");

**Problema 2 — Configuração correta do deserializador Kafka:**

    JsonDeserializer<ProcessoCriadoEvent> deserializer = new JsonDeserializer<>(ProcessoCriadoEvent.class);
    deserializer.addTrustedPackages("*");

    return new DefaultKafkaConsumerFactory<>(
        props,
        new ErrorHandlingDeserializer<>(new StringDeserializer()),
        new ErrorHandlingDeserializer<>(deserializer)
    );

### Medidas de Prevenção

1. **Validação na camada de entrada** com `@Valid` no DTO para garantir que campos opcionais sejam tratados corretamente antes de chegar ao service.
2. **Testes unitários** cobrindo o cenário de CEP nulo/vazio, como demonstrado em `ParteServiceTest.deveContinuarSemEnderecoQuandoViaCepFalhar()`.
3. **Circuit Breaker** configurado no Feign client para isolar falhas de integração externa.
4. **Dead Letter Topic** no Kafka para redirecionar mensagens que falham na deserialização, evitando loop infinito no consumer.
5. **Monitoramento no Kibana** com alertas para `level: ERROR` recorrente no mesmo endpoint, permitindo detecção proativa antes do impacto ao usuário.
