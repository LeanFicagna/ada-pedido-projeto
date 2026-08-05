# 🛒 Ada Pedido Compra - API REST

API de gerenciamento de pedidos com autenticação JWT, desenvolvida em **Quarkus 3.27.0** com **Hibernate Panache**.

## 🚀 Tecnologias

- **Quarkus 3.27.0** - Framework Java reativo
- **Hibernate ORM + Panache** - Persistência de dados
- **JWT (SmallRye JWT)** - Autenticação
- **H2 Database** - Banco de dados em arquivo
- **JUnit 5 + Mockito** - Testes unitários
- **JaCoCo** - Cobertura de testes (80%+)
- **OpenAPI/Swagger** - Documentação interativa

## 📋 Pré-requisitos

- **Java 17+**
- **Maven 3.8+**

## Configuração do Banco

Arquivo: `src/main/resources/application.properties`

- **Banco:** H2 em arquivo
- **URL:** `jdbc:h2:file:./pedidoscompras`
- **Usuário:** `sa`
- **Senha:** `123`

> Os dados persistem entre reinicializações da aplicação.

## Como rodar

### Desenvolvimento (hot reload)

```bash
mvn quarkus:dev
```

A aplicação estará disponível em `http://localhost:8080`

**Swagger/API Docs:**
- Interface interativa: `http://localhost:8080/q/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/q/openapi`

### Build e execução

```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## 🧪 Testes Unitários

### Executar testes

```bash
mvn test
```

### Gerar relatório de cobertura (JaCoCo)

```bash
mvn test verify
```

Acesse em: `target/site/jacoco/index.html`

**Cobertura esperada:** 80%+

### Testes Implementados

- ✅ `PedidoServiceTest` - Criação, listagem e validação de pedidos
- ✅ `PedidoValidarEstoqueTest` - Validação de estoque
- ✅ `PedidoBaixarEstoqueTest` - Atualização de estoque após pedido
- ✅ `PedidoResourceTest` - Endpoints de pedidos
- ✅ `ProdutoResourceTest` - Endpoints de produtos  
- ✅ `LoginResourceTest` - Autenticação e validação de credenciais

---

## 👥 Usuários de Teste (Auto-Seedados no Startup)

| Email | Senha | Role |
|-------|-------|------|
| `admin@ada.com` | `admin123` | ADMIN |
| `cliente@ada.com` | `cliente123` | CLIENTE |
| `joao@teste.com` | `123456` | CLIENTE |

## 📦 Produtos Seedados no Startup

| ID | Descrição | Preço | Estoque |
|----|-----------|-------|---------|
| 1 | Notebook Pro 14 | R$ 4.599,90 | 8 |
| 2 | Mouse Sem Fio | R$ 129,90 | 40 |
| 3 | Teclado Mecânico | R$ 349,90 | 25 |
| 4 | Monitor 27 Polegadas | R$ 1.399,00 | 12 |
| 5 | Headset Gamer | R$ 279,90 | 20 |

---

## Autenticação e Endpoints de Pedidos

### 🔐 Login

`POST /login`

```json
{
  "email": "cliente@ada.com",
  "senha": "cliente123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Use o token em todos os endpoints protegidos:
```
Authorization: Bearer <TOKEN>
```

### 🛒 Criar Pedido (CLIENTE/ADMIN)

`POST /pedidos`

```json
{
  "items": [
    {
      "produtoId": 1,
      "quantidade": 2
    },
    {
      "produtoId": 3,
      "quantidade": 1
    }
  ]
}
```

**Response esperada:**
```json
{
  "id": 1,
  "dataHora": "2026-08-05T18:15:00",
  "cliente": "Cliente Teste",
  "status": "PROCESSADO",
  "items": [
    {
      "descricaoProduto": "Notebook Pro 14",
      "precoProduto": 4599.90,
      "quantidade": 2,
      "totalItem": 9199.80
    }
  ],
  "totalPedido": 9199.80
}
```

### 📋 Listar Pedidos (CLIENTE/ADMIN)

`GET /pedidos`

Header: `Authorization: Bearer <TOKEN>`

---

## Endpoints de Produtos

Base path: `/produtos`

**Regras de acesso:**
- `GET /produtos/listar`: Público (`PermitAll`)
- `GET /produtos/buscar/{id}`: Público (`PermitAll`)
- `GET /produtos/buscar-por-descricao/{descricao}`: Público (`PermitAll`)
- `POST /produtos/criar`: ADMIN apenas
- `PUT /produtos/atualizar/{id}`: ADMIN apenas
- `PATCH /produtos/atualizar-parcialmente/{id}`: ADMIN apenas
- `DELETE /produtos/deletar/{id}`: ADMIN apenas

### Exemplos

#### Listar produtos (sem token)
```bash
GET /produtos/listar
```

#### Buscar por ID
```bash
GET /produtos/buscar/1
```

#### Criar produto (ADMIN)
```bash
POST /produtos/criar
Authorization: Bearer <TOKEN>

{
  "descricao": "Mouse Gamer RGB",
  "preco": 199.90,
  "estoque": 15
}
```

#### Atualizar produto (ADMIN)
```bash
PUT /produtos/atualizar/1
Authorization: Bearer <TOKEN>

{
  "descricao": "Mouse Gamer RGB v2",
  "preco": 249.90,
  "estoque": 20
}
```

#### Deletar produto (ADMIN)
```bash
DELETE /produtos/deletar/1
Authorization: Bearer <TOKEN>
```

---

## Testes de Validação

### 1️⃣ Estoque Insuficiente
```json
{
  "items": [
    {"produtoId": 1, "quantidade": 100}
  ]
}
```
**Esperado:** Status `NAO_PROCESSADO`

### 2️⃣ Produto Inexistente  
```json
{
  "items": [
    {"produtoId": 999, "quantidade": 1}
  ]
}
```
**Esperado:** Erro `404/400`

### 3️⃣ Pedido Sem Itens
```json
{
  "items": []
}
```
**Esperado:** Erro de validação `400`

---

## Exemplos com cURL (PowerShell)

### 1) Login

```powershell
$token = (curl -X POST "http://localhost:8080/login" `
  -H "Content-Type: application/json" `
  -d '{"email":"cliente@ada.com","senha":"cliente123"}' | ConvertFrom-Json).accessToken

Write-Host "Token: $token"
```

### 2) Criar pedido

```powershell
curl -X POST "http://localhost:8080/pedidos" `
  -H "Authorization: Bearer $token" `
  -H "Content-Type: application/json" `
  -d '{
    "items": [
      {"produtoId": 1, "quantidade": 2},
      {"produtoId": 2, "quantidade": 1}
    ]
  }'
```

### 3) Listar pedidos

```powershell
curl -X GET "http://localhost:8080/pedidos" `
  -H "Authorization: Bearer $token"
```

### 4) Listar produtos (sem token)

```powershell
curl -X GET "http://localhost:8080/produtos/listar"
```

---

## 🔑 Roles e Permissões

| Endpoint | Método | Role | Descrição |
|----------|--------|------|-----------|
| `/login` | POST | ✅ Público | Login e obter token |
| `/produtos/listar` | GET | ✅ Público | Listar produtos |
| `/produtos/criar` | POST | 🔐 ADMIN | Criar produto |
| `/pedidos` (criar) | POST | 🔐 CLIENTE/ADMIN | Criar pedido |
| `/pedidos` (listar) | GET | 🔐 CLIENTE/ADMIN | Listar pedidos |

---

## 🛠️ Troubleshooting

**Erro: "405 Method Not Allowed"**
- Causa: Token sem role CLIENTE
- Solução: Restart a app com `mvn quarkus:dev`

**Erro: "Unsatisfied dependency"**
- Causa: `PedidoService` sem `@ApplicationScoped`
- Solução: Verifique a anotação na classe

**Banco vazio**
- Causa: Startup não executou
- Solução: Verifique `StartupEvent` observer

**Swagger não carrega**
- Causa: Dependência `quarkus-smallrye-openapi` não instalada
- Solução: Rode `mvn clean install`

---

## Códigos HTTP comuns

- `200 OK`: sucesso
- `201 Created`: criação bem-sucedida
- `400 Bad Request`: validação falhou
- `401 Unauthorized`: autenticação falhou
- `403 Forbidden`: sem permissão
- `404 Not Found`: recurso não encontrado
- `500 Internal Server Error`: erro no servidor

---

**[Documentação Swagger disponível em `http://localhost:8080/q/swagger-ui`]**

