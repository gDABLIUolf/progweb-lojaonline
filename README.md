# 👕 VesteBem API

Backend da loja virtual **VesteBem**, desenvolvido com Java + Spring Boot.

A aplicação tem como objetivo fornecer uma API REST para gerenciamento de produtos, categorias, usuários, carrinho de compras e pedidos de uma plataforma e-commerce.

---

# 🚀 Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation

## Banco de Dados

- PostgreSQL 16

## DevOps

- Docker
- Docker Compose

## Ferramentas

- Maven
- Postman
- Git/GitHub

---

# 📁 Estrutura do Projeto

```txt
src/main/java/com/VesteBem

├── controller
├── service
├── repository
├── entity
├── dto
├── security
└── config
```

---

# 📌 Funcionalidades

## ✅ Usuários

- Cadastro de usuários
- Login/autenticação
- Controle de acesso

## ✅ Produtos

- Cadastro de produtos
- Atualização de produtos
- Listagem de produtos
- Remoção de produtos

## ✅ Categorias

- Cadastro de categorias
- Associação de produtos por categoria

## ✅ Carrinho

- Adicionar produtos
- Remover produtos
- Alterar quantidade

## ✅ Pedidos

- Finalizar compra
- Histórico de pedidos
- Controle de status

## ✅ Avaliações

- Avaliação de produtos
- Comentários e notas

---

# 🧠 Modelo do Sistema

O sistema é composto pelas seguintes entidades principais:

- Usuário
- Produto
- Categoria
- Carrinho
- ItemCarrinho
- Pedido
- Avaliação

---

# ⚙️ Como Executar o Projeto

## Pré-requisitos

- Java 21+
- Docker Desktop
- Maven
- Git

---

# 🐳 Banco de Dados com Docker

## Subindo PostgreSQL

```bash
docker compose up -d
```

---

## docker-compose.yml

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: postgres_db
    restart: always

    environment:
      POSTGRES_DB: vesteBem
      POSTGRES_USER: vesteBem
      POSTGRES_PASSWORD: vesteBem

    ports:
      - "5432:5432"

    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

---

# 🔧 Configuração da Aplicação

## application.yml

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/vesteBem}
    username: ${SPRING_DATASOURCE_USERNAME:vesteBem}
    password: ${SPRING_DATASOURCE_PASSWORD:vesteBem}
    driver-class-name: org.postgresql.Driver

  jpa:
    show-sql: true

    hibernate:
      ddl-auto: update

    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  devtools:
    restart:
      additional-paths: src/main/java
      exclude: static/**,templates/**,**/*.txt
```

---

# ▶️ Executando a Aplicação

## Executar com Maven Wrapper

### Linux/macOS

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

---

# 🌐 Endpoints da API

## Produtos

```http
GET    /api/v1/produtos
GET    /api/v1/produtos/{id}
POST   /api/v1/produtos
PUT    /api/v1/produtos/{id}
DELETE /api/v1/produtos/{id}
```

---

## Categorias

```http
GET    /api/v1/categorias
GET    /api/v1/categorias/{id}
POST   /api/v1/categorias
PUT    /api/v1/categorias/{id}
DELETE /api/v1/categorias/{id}
```

---

## Autenticação

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
```

---

# 📦 Exemplo de Requisição

## Criar Produto

### Request

```json
{
  "nome": "Camiseta Oversized",
  "descricao": "Camiseta oversized preta",
  "preco": 89.9
}
```

---

## Response

```json
{
  "id": 1,
  "nome": "Camiseta Oversized",
  "descricao": "Camiseta oversized preta",
  "preco": 89.9
}
```

---

# 🔐 Segurança

A API utilizará autenticação baseada em JWT para proteção das rotas privadas.

Exemplo de header:

```http
Authorization: Bearer TOKEN
```

---

# 🛠️ Roadmap do Projeto

## Backend

- [x] Estrutura inicial do projeto
- [ ] CRUD de categorias
- [ ] CRUD de produtos
- [ ] Sistema de autenticação JWT
- [ ] Carrinho de compras
- [ ] Sistema de pedidos
- [ ] Avaliações de produtos

## Frontend

- [ ] Página inicial
- [ ] Catálogo de produtos
- [ ] Login e cadastro
- [ ] Carrinho de compras

---

# 📚 Objetivo do Projeto

Projeto desenvolvido para fins acadêmicos com foco em aprendizado de:

- APIs REST
- Spring Boot
- JPA/Hibernate
- PostgreSQL
- Arquitetura backend
- Segurança com JWT

---

# 👨‍💻 Autores

Gabriel Wolf

Karlos Eduardo

André Augusto

---
