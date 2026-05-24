# Gestão de Processos Judiciais

Sistema fullstack para gestão de processos judiciais, desenvolvido como desafio técnico para a vaga de Desenvolvedor Full Stack Java & Angular/React na Attus Procuradoria Digital.

---

## Tecnologias Utilizadas

### Backend
- Java 17 + Spring Boot 3.5
- PostgreSQL 15 + Flyway
- Spring Data JPA + Hibernate
- OpenFeign + Resilience4j (Circuit Breaker + Retry + Cache)
- Apache Kafka (eventos de domínio)
- Elasticsearch + Kibana + Filebeat (observabilidade)
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito (testes unitários)

### Frontend
- Angular 17
- Bootstrap 5
- ngx-toastr + ngx-spinner

### Infraestrutura
- Docker + Docker Compose

---

## Pré-requisitos

- Java 17+
- Node.js 20+
- Docker Desktop
- Maven 3.8+

---

## Como Executar

### 1. Clonar o repositório

    git clone https://github.com/luanbrazz/gestao-de-processos-judiciais.git
    cd gestao-de-processos-judiciais

### 2. Subir a infraestrutura com Docker

    cd processo-judicial-api
    docker-compose up -d

Aguarde todos os containers subirem:

| Container | Porta | Descrição |
|---|---|---|
| processo-judicial-db | 5432 | PostgreSQL |
| processo-judicial-es | 9200 | Elasticsearch |
| processo-judicial-kibana | 5601 | Kibana |
| processo-judicial-kafka | 9092 | Apache Kafka |
| processo-judicial-zookeeper | 2181 | Zookeeper |
| processo-judicial-filebeat | - | Coleta de logs |

### 3. Executar o Backend

    cd processo-judicial-api
    ./mvnw spring-boot:run

O backend sobe em: http://localhost:8080

O Flyway executa automaticamente as migrations e insere dados de exemplo.

### 4. Executar o Frontend

    cd processo-judicial-ui
    npm install
    ng serve

O frontend sobe em: http://localhost:4200

---

## Endpoints da API

A documentação completa está disponível via Swagger:

    http://localhost:8080/swagger-ui.html

### Resumo dos endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | /api/v1/processos | Listar processos com paginação e filtro |
| POST | /api/v1/processos | Criar novo processo |
| GET | /api/v1/processos/{id} | Buscar processo por ID |
| PUT | /api/v1/processos/{id} | Atualizar processo |
| PATCH | /api/v1/processos/{id}/status | Atualizar status do processo |
| POST | /api/v1/processos/{id}/partes | Adicionar parte ao processo |
| GET | /api/v1/processos/{id}/partes | Listar partes do processo |
| POST | /api/v1/processos/{id}/movimentacoes | Adicionar movimentação |
| GET | /api/v1/processos/{id}/movimentacoes | Listar movimentações |

---

## Executar os Testes

    cd processo-judicial-api
    ./mvnw test

Os testes unitários utilizam Mockito e não dependem de banco de dados ou Docker.

---

## Observabilidade

Com os containers rodando, acesse o Kibana em http://localhost:5601

Os logs da aplicação são enviados automaticamente pelo Filebeat para o Elasticsearch e ficam disponíveis no índice `processo-judicial-logs`.

Para visualizar:
1. Acesse http://localhost:5601
2. Vá em Analytics > Discover
3. Selecione o data view `processo-judicial-logs`

---

## Funcionalidades

### Processos
- Cadastro, visualização e edição de processos judiciais
- Filtro por status (ATIVO, SUSPENSO, ENCERRADO) com paginação
- Alteração de status via PATCH
- Eventos Kafka publicados ao criar um processo (`processo-criado`)

### Partes — Pessoa Física e Jurídica
- Suporte a **Pessoa Física (CPF)** e **Pessoa Jurídica (CNPJ)** via herança JPA (tabelas separadas)
- **Máscara automática** de CPF (`000.000.000-00`) e CNPJ (`00.000.000/0000-00`) no formulário
- **Validação de CPF e CNPJ** pelo algoritmo oficial da Receita Federal (módulo 11), tanto no frontend quanto no backend
- **Regra de maioridade**: Pessoa Física deve ter 18 anos ou mais — validação no frontend (campo bloqueado) e no backend (HTTP 422)
- **Busca automática de endereço por CEP** (ViaCEP) para Pessoa Física
- **Busca automática de dados empresariais** por CNPJ (BrasilAPI) para Pessoa Jurídica: razão social, CNAE, natureza jurídica, situação cadastral, endereço
- Circuit Breaker e Retry para ambas as integrações externas; fallback gracioso em caso de indisponibilidade
- Cache Caffeine para CEPs e CNPJs já consultados

### Frontend
- Modo visualização separado do modo edição
- Registro de movimentações com timeline cronológica
- Toasts com **mensagens de erro reais da API** (não mais mensagens genéricas)
- Serviços Angular dedicados: `ViaCepService`, `BrasilApiService`, `ProcessoService`, `ParteService`, `MovimentacaoService`

### Observabilidade
- Logs estruturados em JSON enviados ao Elasticsearch via Filebeat
- Cache de CEPs e CNPJs consultados (Caffeine)

---

## Estrutura do Projeto

    gestao-de-processos-judiciais/
    ├── processo-judicial-api/          # Backend Spring Boot
    │   ├── src/main/java/
    │   │   └── com/attus/processojudicial/
    │   │       ├── api/                # Controllers e GlobalExceptionHandler
    │   │       ├── application/        # Services, DTOs e Interfaces
    │   │       │   └── dto/            # ParteRequestDTO (polimórfico), CnpjDTO, EnderecoDTO...
    │   │       ├── domain/             # Entidades JPA, Enums, Repositories, Validators
    │   │       │   ├── entity/         # Processo, Parte (abstract), PessoaFisica, PessoaJuridica
    │   │       │   └── validator/      # DocumentoValidator (CPF/CNPJ algoritmo Receita Federal)
    │   │       └── infrastructure/     # Feign clients (ViaCepClient, BrasilApiClient), Kafka, Config
    │   ├── src/main/resources/
    │   │   └── db/migration/           # V1–V7 Flyway migrations
    │   ├── docker-compose.yml
    │   └── filebeat.yml
    └── processo-judicial-ui/           # Frontend Angular 17
        └── src/app/
            ├── core/
            │   ├── models/             # processo.model.ts, api-error.model.ts
            │   └── services/           # ProcessoService, ParteService, MovimentacaoService,
            │                           # ViaCepService, BrasilApiService
            ├── features/               # Módulos por domínio (processos, partes, movimentacoes)
            └── shared/                 # StatusBadgeComponent e outros reutilizáveis

---

## Autor

Luan Braz
Desenvolvedor Full Stack Java & Angular