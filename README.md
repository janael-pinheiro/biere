# Biere - Catálogo de Cervejas

Este projeto é uma API REST robusta e escalável para gerenciamento de um catálogo de cervejas, construída com Kotlin e Spring Boot. O projeto utiliza a **Arquitetura Hexagonal (Ports and Adapters)** para garantir um core de negócio desacoplado de tecnologias externas e segue o **Modelo de Maturidade de Richardson** até o seu nível mais alto.

## 🏗️ Arquitetura

O projeto foi refatorado para seguir os princípios da **Arquitetura Hexagonal**, focando na inversão de dependência e na proteção das regras de negócio.

### Estrutura de Camadas:
- **`domain`**: Contém o "Core" da aplicação (Modelos, Exceções de Negócio e Interfaces de Ports). Não possui dependências de frameworks externos.
- **`application`**: Contém os **Use Cases (Services)** que implementam as regras de negócio e interagem com os Ports.
- **`infrastructure`**: Contém os **Adapters** de entrada (REST Controllers) e saída (Persistence Adapters), além de configurações do framework (Spring Boot).

---

## 🚀 Boas Práticas Adotadas

A API foi desenvolvida seguindo padrões rigorosos para garantir consistência, usabilidade e facilidade de integração:

### 1. HATEOAS (Hypermedia as the Engine of Application State)
Atingimos o nível 3 do Modelo de Richardson. As respostas da API não contêm apenas dados, mas também links dinâmicos que guiam o cliente (ex: `self`, `update_beer`, `delete_beer`).

### 2. Problem Details (RFC 9457)
Padronização de respostas de erro utilizando o formato `application/problem+json`. Erros incluem `title`, `status`, `detail`, `instance`, `timestamp` e `trace-id`, além de links HATEOAS para auxiliar na recuperação do erro.

### 3. Content Negotiation
Suporte a múltiplos formatos via header `Accept`:
- `application/json`: Formato padrão.
- `application/hal+json`: Hypermedia Application Language.
- `text/csv`: Exportação de dados estruturados.

### 4. Status Codes Semânticos
Uso preciso dos códigos HTTP:
- `200 OK`, `201 Created`, `204 No Content` para sucesso.
- `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `409 Conflict`, `422 Unprocessable Entity` para erros de cliente.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin 1.9+
- **Framework:** Spring Boot 3.4+
- **Banco de Dados:** PostgreSQL
- **Persistência:** Spring Data JPA / Hibernate
- **Segurança:** Spring Security + JWT (JSON Web Token)
- **Documentação:** OpenAPI 3 / SpringDoc (Swagger)
- **Testes:** JUnit 5, MockK, Testcontainers (PostgreSQL)
- **HATEOAS:** Spring HATEOAS

---

## 🏗️ Modelo de Maturidade de Richardson

Este projeto incorpora todos os níveis do modelo de Richardson, atingindo a "**Glória do REST**":

- **Nível 0:** Superação de chamadas RPC simples.
- **Nível 1 (Recursos):** URIs individuais para cada recurso (ex: `/v1/beers/{id}`).
- **Nível 2 (Verbos HTTP):** Uso adequado de `GET`, `POST`, `PUT`, `PATCH` e `DELETE`.
- **Nível 3 (Hypermedia Controls):** Implementação total de **HATEOAS**.

---

## 🛠️ Como Executar

1. **Requisitos:** Docker e JDK 17+ instalados.
2. **Setup:** O banco de dados é configurado via Testcontainers nos testes, mas para execução manual, utilize um banco PostgreSQL local ou containerizado.
3. **Execução:**
   ```bash
   ./gradlew bootRun
   ```
4. **Documentação:** Acesse `http://localhost:8080/swagger-ui.html` após iniciar a aplicação.

---

## ✅ Status do Projeto
- [x] Arquitetura Hexagonal (Ports & Adapters)
- [x] Autenticação e Autorização com JWT
- [x] CRUD completo de Cervejas, Cervejarias, Estilos e Países
- [x] Integração total com HATEOAS
- [x] Padronização de Erros (RFC 9457)
- [x] Testes de Integração com Testcontainers
- [ ] Implementação de Caching (ETags/Redis)
- [ ] Logs estruturados e Observabilidade (Prometheus/Grafana)