# ada-pedido-compra

API de pedidos com cadastro de **clientes** e **produtos**, autenticação por JWT e autorização por perfis (`ADMIN` e `CLIENTE`).

## Stack

- Java + Quarkus
- REST com JAX-RS
- Panache/Hibernate ORM
- Banco H2 (em memória)
- Segurança com JWT

## Pré-requisitos

- JDK 17+
- Maven 3.8+

## Configuração atual do banco

Arquivo: `src/main/resources/application.properties`

- Banco: H2 em memória
- URL: `jdbc:h2:mem:///pedidoscompras`
- Usuário: `sa`
- Senha: `123`

> Como o banco é em memória, os dados são recriados ao reiniciar a aplicação.

## Como rodar

### Desenvolvimento (hot reload)

```powershell
mvn quarkus:dev
```

API disponível em `http://localhost:8080`.

### Build e execução

```powershell
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

## Usuário admin padrão

Ao subir a aplicação, o `Startup` cria automaticamente um usuário admin se ele não existir:

- Email: `admin@ada.com`
- Senha: `admin123`
- Perfil: `ADMIN`

## Fluxo recomendado para popular o banco

1. Fazer login com o admin padrão para obter o token.
2. Criar produtos (somente ADMIN).
3. Criar clientes (endpoint público).
4. Testar listagem/busca de produtos (público).

---

## Autenticação

### Login

`POST /login`

Exemplo de request:

```json
{
  "email": "admin@ada.com",
  "senha": "admin123"
}
```

Exemplo de response:

```json
{
  "token": "<JWT>"
}
```

Use o token no header dos endpoints protegidos:

```text
Authorization: Bearer <JWT>
```

---

## Endpoints de produtos

Base path: `/produtos`

### Regras de acesso

- `GET /produtos/listar`: público (`PermitAll`)
- `GET /produtos/buscar/{id}`: público (`PermitAll`)
- `GET /produtos/buscar-por-descricao/{descricao}`: público (`PermitAll`)
- `POST /produtos/criar`: somente `ADMIN`
- `PUT /produtos/atualizar/{id}`: somente `ADMIN`
- `PATCH /produtos/atualizar-parcialmente/{id}`: somente `ADMIN`
- `DELETE /produtos/deletar/{id}`: somente `ADMIN`

### Exemplos para popular produtos

#### Criar produto

`POST /produtos/criar`

```json
{
  "descricao": "Mouse sem fio",
  "preco": 129.90,
  "estoque": 50
}
```

#### Criar outro produto

`POST /produtos/criar`

```json
{
  "descricao": "Teclado mecânico",
  "preco": 299.90,
  "estoque": 20
}
```

#### Listar produtos

`GET /produtos/listar`

#### Buscar produto por ID

`GET /produtos/buscar/1`

#### Buscar produto por descrição

`GET /produtos/buscar-por-descricao/mouse`

#### Atualizar produto completo

`PUT /produtos/atualizar/1`

```json
{
  "id": 1,
  "descricao": "Mouse sem fio gamer",
  "preco": 149.90,
  "estoque": 40
}
```

#### Atualizar produto parcial

`PATCH /produtos/atualizar-parcialmente/1`

```json
{
  "preco": 139.90
}
```

#### Deletar produto

`DELETE /produtos/deletar/1`

---

## Endpoints de clientes

Base path: `/clientes`

### Regras de acesso

- `POST /clientes/criar`: público (`PermitAll`)
- `GET /clientes/listar`: somente `ADMIN`
- `GET /clientes/buscar/{id}`: somente `ADMIN`
- `GET /clientes/buscar-por-email/{email}`: somente `ADMIN`
- `GET /clientes/buscar-por-nome/{nome}`: somente `ADMIN`
- `PUT /clientes/atualizar/{id}`: `ADMIN` ou `CLIENTE`
- `PATCH /clientes/atualizar-parcialmente/{id}`: `ADMIN` ou `CLIENTE`
- `DELETE /clientes/deletar/{id}`: somente `ADMIN`

> Observação: para atualização de cliente, existe validação para impedir que um `CLIENTE` altere outro cliente.

### Exemplo para popular clientes

#### Criar cliente

`POST /clientes/criar`

```json
{
  "nome": "Maria da Silva",
  "email": "maria@teste.com",
  "senha": "123456"
}
```

#### Criar outro cliente

`POST /clientes/criar`

```json
{
  "nome": "Joao Pereira",
  "email": "joao@teste.com",
  "senha": "123456"
}
```

---

## Endpoint de teste

- `GET /hello` -> retorna `Hello, World!`

---

## Exemplos com cURL

### 1) Login admin

```bash
curl -X POST "http://localhost:8080/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ada.com","senha":"admin123"}'
```

### 2) Criar produto (ADMIN)

```bash
curl -X POST "http://localhost:8080/produtos/criar" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"descricao":"Monitor 24","preco":899.90,"estoque":10}'
```

### 3) Listar produtos (público)

```bash
curl -X GET "http://localhost:8080/produtos/listar"
```

---

## Códigos HTTP comuns

- `200 OK`: sucesso em consultas/atualizações
- `201 Created`: criação com sucesso
- `204 No Content`: remoção sem conteúdo no retorno
- `401 Unauthorized`: sem token/token inválido
- `403 Forbidden`: usuário sem permissão
- `404 Not Found`: recurso não encontrado

