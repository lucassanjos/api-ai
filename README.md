# API AI - Análise Inteligente de Documentos com Spring AI

Uma API robusta desenvolvida em **Kotlin** e **Spring Boot** que utiliza inteligência artificial para análise automática de documentos PDF, especialmente voltada para análise financeira e de relatórios de postos de combustíveis.

---

## 🎯 Sobre o Projeto

Este projeto demonstra a integração do **Spring AI** com **OpenAI GPT** para processar e analisar documentos de forma inteligente. A aplicação extrai dados de PDFs, processa página por página usando IA, mantém histórico de conversas em banco de dados e envia resultados por email.

### Caso de Uso Principal

A API foi desenvolvida especialmente para analisar **relatórios financeiros e operacionais de postos de combustíveis**, gerando análises executivas detalhadas que incluem:
- Identificação de filiais e períodos
- Comparação mês a mês com variação percentual
- Análise de tendências e sazonalidade
- Identificação de anomalias e pontos de atenção
- Comparativo de desempenho entre filiais

---

## 🏗️ Arquitetura e Estrutura do Projeto

```
api-ai/
├── src/
│   ├── main/
│   │   ├── kotlin/com/qualityposto/api_ai/
│   │   │   ├── ApiAiApplication.kt          # Inicializador da aplicação
│   │   │   ├── config/
│   │   │   │   └── AsyncConfig.kt           # Configuração de processamento assíncrono
│   │   │   ├── controller/
│   │   │   │   └── ChatController.kt        # Endpoints REST
│   │   │   └── service/
│   │   │       ├── ChatService.kt           # Orquestração da análise
│   │   │       ├── OpenAiService.kt         # Integração com OpenAI
│   │   │       ├── PdfService.kt            # Extração de texto de PDFs
│   │   │       ├── PromptService.kt         # Templates de prompts
│   │   │       └── EmailService.kt          # Envio de resultados
│   │   └── resources/
│   │       ├── application.properties        # Configurações da aplicação
│   │       └── schema-postgresql.sql        # Schema do banco de dados
│   └── test/
│       └── kotlin/...                        # Testes unitários
├── build.gradle.kts                          # Configuração Gradle
├── compose.yaml                              # Docker Compose para PostgreSQL
└── README.md
```

### 📦 Componentes Principais

#### **1. ChatController** (`controller/ChatController.kt`)
- **Endpoint:** `POST /ai/pdf/analise`
- **Função:** Recebe arquivo PDF e ID da conversa
- **Características:**
  - Suporta upload de arquivos multipart
  - Retorna resposta 202 (Accepted) para operações assíncronas

#### **2. ChatService** (`service/ChatService.kt`)
- **Responsabilidade:** Orquestrador principal da análise
- **Fluxo:**
  1. Extrai texto do PDF página por página
  2. Processa cada página com prompts específicos
  3. Mantém contexto em chat memory (histórico)
  4. Gera análise final consolidada
  5. Envia resultado por email
- **Características:**
  - Integração com Spring AI ChatClient
  - Suporte a histórico de conversas com `MessageChatMemoryAdvisor`
  - Processamento assíncrono com `@Async`

#### **3. PdfService** (`service/PdfService.kt`)
- Utiliza **Apache PDFBox** para extração de texto
- Extrai conteúdo página por página
- Suporta PDFs complexos com múltiplas páginas

#### **4. PromptService** (`service/PromptService.kt`)
- Gerencia templates de prompts
- **Dois tipos de prompts:**
  - `promptResumoPagina()`: Extrai informações objetivas de cada página
  - `promptFinal()`: Gera análise executiva consolidada
- Prompts otimizados para domínio financeiro e ERP

#### **5. EmailService** (`service/EmailService.kt`)
- Envia resultados via integrações HTTP
- Inclui PDF original e análise em HTML
- Utiliza WebClient reativo do Spring

#### **6. OpenAiService** (`service/OpenAiService.kt`)
- Integração direta com API OpenAI (complementar ao Spring AI)
- Upload de arquivos para assistentes
- Preparação para funcionalidades avançadas

#### **7. AsyncConfig** (`config/AsyncConfig.kt`)
- Thread pool configurado para processamento paralelo
- **Configurações:**
  - Core pool: 5 threads
  - Max pool: 10 threads
  - Queue capacity: 50 tarefas

---

## 🤖 Por que Spring AI?

### Problemas que Spring AI Resolve

1. **Abstração de Provider:** 
   - Suporta múltiplos provedores (OpenAI, Claude, Ollama, etc.)
   - Troca de provedor sem mudanças em código de negócio

2. **Chat Memory Management:**
   - Integração automática com JDBC para persistência
   - Mantém histórico de conversas estruturado
   - Schema SQL já mapeado

3. **Advisors Pattern:**
   - `MessageChatMemoryAdvisor` injeta contexto automaticamente
   - Reutilização de conversas anteriores
   - Melhor contexto em análises multipassagem

4. **ChatClient Fluente:**
   - API reativa e síncrona
   - Integração fácil com Spring Ecosystem
   - Type-safe prompting

### Exemplo de Uso no Projeto

```kotlin
val chatClient = chatClientBuilder
    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
    .build()

val response = chatClient.prompt()
    .user(promptService.promptResumoPagina(pageText))
    .advisors { it.param(ChatMemory.CONVERSATION_ID, idChat) }
    .call()
    .content()
```

**Benefícios neste contexto:**
- Cada página é processada com contexto das páginas anteriores
- Memória persistida em PostgreSQL
- Reutilizável em futuras análises

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| **Kotlin** | 1.9.25 | Linguagem principal |
| **Spring Boot** | 3.5.10 | Framework web |
| **Spring AI** | 1.1.2 | Integração com IA |
| **OpenAI API** | Latest | Modelo GPT para análise |
| **PostgreSQL** | Latest | Persistência de históricos |
| **PDFBox** | 3.0.6 | Extração de texto de PDFs |
| **Apache POI** | 5.2.5 | Processamento de arquivos |
| **Java** | 21 | Runtime |

---

## 🚀 Como Executar

### Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Chave de API da OpenAI

### 1. Preparar o Ambiente

```bash
# Clonar repositório
git clone <seu-repo>
cd api-ai

# Iniciar PostgreSQL com Docker Compose
docker-compose up -d
```

### 2. Configurar Variáveis de Ambiente

Edite `src/main/resources/application.properties`:

```properties
# OpenAI API Key (CRÍTICO!)
spring.ai.openai.api-key=sk-proj-SEU_TOKEN_AQUI

# PostgreSQL (já configurado para docker-compose)
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myuser
spring.datasource.password=secret

# Endpoints internos (ajuste conforme necessário)
# webClient baseUrl em ChatService e EmailService
```

⚠️ **SEGURANÇA:** Nunca committe a chave da API! Use variáveis de ambiente:

```bash
export SPRING_AI_OPENAI_API_KEY="seu-token-aqui"
```

### 3. Construir e Executar

```bash
# Com Gradle
./gradlew bootRun

# Ou compilar JAR
./gradlew build
java -jar build/libs/api-ai-0.0.1-SNAPSHOT.jar
```

A aplicação iniciará em `http://localhost:8080`

---

## 📡 Endpoints da API

### Analisar PDF

```http
POST /ai/pdf/analise
Content-Type: multipart/form-data

arquivo: <arquivo.pdf>
idChat: "conversa-123"
```

**Respostas:**
- `202 Accepted` - Análise iniciada assincronamente
- `400 Bad Request` - Arquivo inválido
- `500 Internal Server Error` - Erro na IA ou banco de dados

**Fluxo:**
1. Arquivo é recebido
2. Análise é enfileirada no `analiseExecutor`
3. Resultado é enviado por email

---

## 💾 Banco de Dados

### Schema PostgreSQL

```sql
CREATE TABLE SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(36) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX ON SPRING_AI_CHAT_MEMORY(conversation_id, timestamp);
```

**Função:** Armazenar histórico de mensagens por conversa (conversation_id)

---

## 🔄 Fluxo de Processamento

```
1. Upload PDF (ChatController)
        ↓
2. Enfileirar análise (ChatService.analisarAsync)
        ↓
3. Extrair texto do PDF (PdfService)
        ↓
4. Processar página por página (ChatService + ChatClient)
        ↓
5. Manter histórico em ChatMemory (PostgreSQL)
        ↓
6. Gerar análise final (PromptService)
        ↓
7. Enviar por email (EmailService)
        ↓
8. Retornar 202 ao usuário
```

---

## ✨ Recursos Implementados

- ✅ Upload e processamento de PDFs
- ✅ Análise com IA (OpenAI GPT)
- ✅ Extração página por página
- ✅ Chat memory persistido (PostgreSQL + JDBC)
- ✅ Processamento assíncrono com thread pool
- ✅ Geração de análise executiva em HTML
- ✅ Integração com email/webhooks
- ✅ Suporte a múltiplas conversas (conversation_id)

---

## 📈 Possíveis Melhorias

- [ ] Autenticação e autorização (JWT/OAuth2)
- [ ] Rate limiting e quotas
- [ ] Suporte a outros formatos (DOCX, XLSX, imagens)
- [ ] Webhooks para notificação de conclusão
- [ ] Dashboard para visualizar históricos
- [ ] Testes unitários e integração
- [ ] Cache de análises
- [ ] Suporte a múltiplos modelos de IA
- [ ] Logging estruturado (SLF4J + Logback)
- [ ] Métricas (Micrometer/Prometheus)

---

## 🧪 Testes

```bash
./gradlew test
```

Testes unitários incluem:
- Integração com mock de OpenAI
- Processamento de PDFs
- Persistência em banco de dados

---

## 📝 Licença

MIT License - Veja LICENSE.md para detalhes

---

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas:
- Abra uma [Issue](https://github.com/seu-usuario/api-ai/issues)
- Consulte a [Documentação do Spring AI](https://docs.spring.io/spring-ai/reference/)
- Verifique a [API OpenAI](https://platform.openai.com/docs)

---

**Desenvolvido com ❤️ usando Spring AI**
