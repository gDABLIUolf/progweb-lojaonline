# 👕 VesteBem — E-Commerce de Moda de Luxo

Plataforma completa de e-commerce de moda de luxo, composta por um backend robusto em **Java + Spring Boot** e um frontend interativo e moderno em **Vue 3 + Vite**.

A aplicação fornece um sistema completo e fluído para gerenciamento de catálogo, carrinho de compras, fluxo de pedidos com controle de estoque, avaliações detalhadas de produtos e perfis de usuários.

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA** (Persistência)
- **Spring Security** (Autenticação baseada em JWT)
- **Spring Validation** (Validação de entrada de dados)
- **PostgreSQL 16** (Banco de dados relacional)
- **Swagger / OpenAPI** (Documentação de endpoints)

### Frontend
- **Vue 3** (Composition API)
- **Vite** (Build tool rápido)
- **Vanilla CSS** + **TailwindCSS** (Estilização responsiva e moderna)
- **Composables** customizados para gerenciamento de estado e APIs
- Estrutura modular de componentes reutilizáveis

---

## 📁 Estrutura do Projeto

O projeto é dividido em duas partes principais:

### ☕ Backend (`/backend`)
```txt
backend/src/main/java/com/vesteBem
├── config              # Configurações globais e inicializador de banco de dados
├── controller          # Controllers REST (Exposição de endpoints)
├── dto                 # Objetos de Transferência de Dados (Requests/Responses)
├── model               # Entidades JPA (Banco de Dados)
├── repository          # Interfaces Spring Data JPA
├── security            # Configurações de filtro e autenticação JWT
├── service             # Regras de negócio do sistema
└── specs               # Especificações para filtragem dinâmica de produtos
```

### ⚡ Frontend (`/frontend`)
```txt
frontend/src
├── assets              # Arquivos estáticos (fontes, imagens globais)
├── components          # Componentes modulares reutilizáveis
│   ├── carrinho        # Itens do carrinho e resumo
│   ├── layout          # Navbar, Footer e Sidebar
│   ├── modal           # Modais de autenticação e mensagens
│   ├── perfil          # Formulários de dados e listagem de pedidos
│   └── produto         # Galeria de imagens e avaliações de produtos
├── composables         # Lógica compartilhada (useAuth, useCarrinho)
├── router              # Configuração do Vue Router
└── views               # Páginas principais da loja virtual
```

---

## 🛠️ Arquitetura e Boas Práticas

Recentemente, a base de código passou por uma série de refatorações focadas em legibilidade e manutenabilidade:

- **Clean Code (Sem Comentários Redundantes)**: Código limpo e autodocumentado em ambos os lados (frontend e backend). Comentários de checklist acadêmicos, marcadores de getters/setters e códigos comentados/mortos foram completamente eliminados.
- **Separação de Responsabilidades**: Toda a lógica pesada de views no frontend foi extraída para **Composables** (`useAuth` e `useCarrinho`) e componentes menores, reduzindo o tamanho de páginas centrais em até **90%**.
- **Segurança e Validação**: Regras rígidas de validação de DTO no Spring e autenticação JWT stateless no frontend.

---

## 🌐 Endpoints da API (Prefixados com `/api`)

Abaixo estão listadas as rotas do backend expostas pela aplicação:

### Autenticação & Usuários
- `POST /api/auth/login` — Autenticação de usuário e obtenção do token JWT
- `POST /api/usuarios` — Cadastro de novo cliente
- `POST /api/auth/esqueci-senha` — Solicitação de link de redefinição de senha
- `POST /api/auth/redefinir-senha` — Confirmação de nova senha

### Produtos & Categorias
- `GET /api/produtos` — Vitrine de produtos (com filtros de busca, categoria, desconto e carrossel)
- `GET /api/produtos/{id}` — Detalhes de um produto específico
- `GET /api/categorias` — Listagem de categorias da loja
- `POST /api/produtos` — Criação de produto (Administrador)
- `PUT /api/produtos/{id}` — Edição de produto (Administrador)
- `DELETE /api/produtos/{id}` — Exclusão de produto (Administrador)

### Carrinho & Pedidos
- `GET /api/carrinho` — Obter itens do carrinho do usuário autenticado
- `POST /api/carrinho` — Adicionar/atualizar itens do carrinho
- `POST /api/pedidos` — Processamento de checkout e criação de pedido
- `GET /api/pedidos` — Histórico de pedidos do usuário
- `PUT /api/pedidos/{id}/status` — Atualização de status de entrega (e.g. CONCLUIDO) com controle de estoque automático

### Avaliações
- `GET /api/avaliacoes/produto/{id}` — Obter lista de comentários e notas do produto
- `POST /api/avaliacoes` — Enviar uma nota (0.5 a 5.0 estrelas) e comentário sobre um produto adquirido

---

## 🐳 Banco de Dados com Docker

Para subir a instância do PostgreSQL configurada para a aplicação:

```bash
cd backend
docker compose up -d
```

### Configurações de Acesso (`backend/.env`)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lojaonline
SPRING_DATASOURCE_USERNAME=lojaonline
SPRING_DATASOURCE_PASSWORD=lojaonline
```

---

## ▶️ Como Executar a Aplicação

### 1. Backend (Spring Boot)
Dentro da pasta `/backend`, execute:

**Windows (cmd/powershell):**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

O backend inicializará na porta `8080`. Um inicializador de banco de dados (`DatabaseInitializer`) povoará automaticamente a base de dados com 10 categorias de luxo, 50 produtos premium, usuários de teste e avaliações simuladas.

---

### 2. Frontend (Vue 3 + Vite)
Dentro da pasta `/frontend`, execute:

```bash
npm install
npm run dev
```

O frontend abrirá localmente em: `http://localhost:5173`.

---

## 🛠️ Status do Projeto (Roadmap)

### Backend
- [x] Estrutura inicial do projeto
- [x] CRUD de categorias e carregamento de imagens de capa
- [x] CRUD de produtos e galeria de fotos
- [x] Sistema de autenticação JWT e controle de permissões
- [x] Carrinho de compras integrado ao banco
- [x] Fluxo de pedidos com controle transacional de estoque
- [x] Sistema de avaliações de produtos (cálculo de média de estrelas)

### Frontend
- [x] Página inicial com vitrine e destaque carrossel
- [x] Filtros dinâmicos no catálogo de produtos (por categoria e nome)
- [x] Telas de Login, Cadastro e Recuperação de Senha
- [x] Gerenciamento dinâmico de Carrinho de Compras (Sidebar e Página de Checkout)
- [x] Perfil do Usuário com Edição de Dados, Histórico de Pedidos e Formulário de Avaliação

---

## 👨‍💻 Autores
- Gabriel Wolf
- Karlos Eduardo
- André Augusto
