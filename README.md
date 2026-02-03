# Biere - Catálogo de Cervejas

Este projeto é uma API REST robusta e escalável para gerenciamento de um catálogo de cervejas, construída com Kotlin e Spring Boot. O objetivo principal é demonstrar a aplicação de boas práticas de design de APIs e a implementação do **Modelo de Maturidade de Richardson** até o seu nível mais alto.

## 🚀 Boas Práticas Adotadas

A API foi desenvolvida seguindo padrões rigorosos para garantir consistência, usabilidade e facilidade de integração:

### 1. Status Codes Semânticos
Utilizamos os códigos de status HTTP de forma precisa para comunicar o resultado de cada operação:
- `200 OK`: Requisição bem-sucedida com retorno de dados.
- `201 Created`: Recurso criado com sucesso.
- `204 No Content`: Operação (como deleção) realizada com sucesso, sem corpo de resposta.
- `400 Bad Request`: Erro de validação ou entrada de dados inválida.
- `404 Not Found`: Recurso não encontrado.
- `500 Internal Server Error`: Erros inesperados no servidor.

### 2. Content Negotiation
A API suporta múltiplos formatos de representação para o mesmo recurso, permitindo que o cliente escolha o formato desejado através do header `Accept`:
- `application/json`: Formato padrão para troca de dados estruturados.
- `text/csv`: Exportação de listagens em formato CSV disponível para determinados endpoints.

### 3. HATEOAS (Hypermedia as the Engine of Application State)
As respostas da API não contêm apenas dados, mas também links que guiam o cliente sobre as próximas ações possíveis (ex: `self`, `update_beer`, `delete_beer`, `create_new_beer`). Isso reduz o acoplamento entre o cliente e o servidor.

### 4. Padronização de URIs
Seguimos convenções de nomes claros e previsíveis:
- Uso de substantivos no plural para coleções (ex: `/v1/beers`, `/v1/styles`).
- Hierarquia lógica para recursos dependentes.
- Versionamento via URI (`/v1/...`) para garantir retrocompatibilidade.

### 5. Documentação com OpenAPI (Swagger)
A API é autodescritiva e possui documentação interativa gerada automaticamente.
- **Acesse em:** `http://localhost:8080/swagger-ui.html`
- Fornece detalhes sobre endpoints, modelos de dados, parâmetros e exemplos de uso.

---

## 🏗️ Modelo de Maturidade de Richardson

Este projeto incorpora todos os níveis do modelo de Richardson, atingindo a "Glória do REST":

- **Nível 0 (The Swamp of POX):** Transcedido pela superação de chamadas RPC via HTTP, utilizando URIs e métodos HTTP corretamente.
- **Nível 1 (Recursos):** Implementação de URIs individuais para cada recurso (ex: `/v1/beers/{id}`), permitindo a identificação única e manipulação granular.
- **Nível 2 (Verbos HTTP):** Uso adequado dos verbos `GET` (leitura), `POST` (criação), `PATCH`/`PUT` (atualização) e `DELETE` (remoção), aproveitando a semântica nativa do protocolo HTTP.
- **Nível 3 (Hypermedia Controls):** Implementação total de **HATEOAS**, onde o servidor fornece links de navegação dinâmica, tornando a API autodocumentável e navegável.

---

## 🔮 O que vem por aí? (Futuras Implementações)

Para elevar ainda mais a qualidade da API, as seguintes boas práticas serão incorporadas:

- **Problem Details (RFC 7807):** Padronização de respostas de erro para facilitar o consumo por clientes.
- **Caching & ETags:** Implementação de estratégias de cache para melhorar a performance e reduzir tráfego de rede.
- **Idempotência em POST:** Header `Idempotency-Key` para prevenir criações duplicadas em caso de falhas de rede.
- **Rate Limiting:** Proteção contra abusos e garantia de disponibilidade do serviço.
- **Versioning by Header:** Implementação de versionamento via media type ou headers customizados como alternativa à URI.

---

## ✅ TODO inicial do projeto
- [x] Endpoint de tipos de cerveja (Pilsner, Lager, etc.);
- [x] Testes de integração com TestContainers;
- [ ] Implementação de logs estruturados e observabilidade avançada.