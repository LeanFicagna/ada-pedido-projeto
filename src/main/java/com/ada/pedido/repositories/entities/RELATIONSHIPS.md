# Explicação dos Relacionamentos entre Entidades

Aqui está a explicação detalhada dos relacionamentos entre `Cliente`, `Produto`, `ItemPedido` e `Pedido`.

## 1. Cliente e Pedido (Relacionamento 1:N)
Existe uma relação de **Um para Muitos** entre Cliente e Pedido.

*   **Lógica:** Um único `Cliente` pode fazer vários pedidos ao longo do tempo, mas cada `Pedido` pertence obrigatoriamente a apenas um cliente.
*   **No Código:** Isso é definido na classe `Pedido` com a anotação:
    ```java
    @ManyToOne
    private Cliente cliente;
    ```
    No banco de dados, a tabela de pedidos terá uma chave estrangeira apontando para o cliente.

## 2. Pedido e ItemPedido (Relacionamento 1:N Bidirecional)
Esta é uma relação de composição ("Pai e Filho"). Um `Pedido` é composto por vários itens.

*   **Lógica:** Um pedido contém uma lista de itens (ex: 2 unidades de um produto, 1 unidade de outro).
*   **No Código:**
    *   Na classe `Pedido`, você tem:
        ```java
        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
        private List<ItemPedido> itens;
        ```
        O `CascadeType.ALL` é muito importante aqui: significa que se você salvar o Pedido, o banco salva automaticamente os itens da lista. Se deletar o Pedido, os itens também são deletados.
    *   Na classe `ItemPedido`, você tem o caminho de volta:
        ```java
        @ManyToOne
        private Pedido pedido;
        ```
        É quem "segura" a chave estrangeira.

## 3. ItemPedido e Produto (Relacionamento N:1)
Existe uma relação de **Muitos para Um** entre ItemPedido e Produto.

*   **Lógica:** O `ItemPedido` funciona como uma linha do carrinho de compras. Ele diz "nesta compra, estou levando X unidades deste Produto". O `Produto` é apenas o cadastro (ex: "Notebook Dell"). Vários pedidos diferentes podem ter itens que apontam para o mesmo produto.
*   **No Código:** A classe `ItemPedido` possui:
    ```java
    @ManyToOne
    private Produto produto;
    ```
    Note que a classe `Produto` não sabe quais itens a referenciam (não tem uma lista de itens lá), o que é correto para manter o sistema leve.

## Resumo Visual
O fluxo de dados segue esta hierarquia:

1.  **Cliente** inicia a compra.
2.  Cria-se um **Pedido** vinculado a esse cliente.
3.  O Pedido é preenchido com vários **ItemPedido**.
4.  Cada ItemPedido faz referência a um **Produto** do catálogo e define a quantidade comprada.