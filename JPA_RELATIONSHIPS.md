# Guia de Anotações de Relacionamento JPA

Este guia explica as quatro principais anotações de relacionamento na Jakarta Persistence API (JPA) com exemplos.

## 1. @OneToOne
**Definição:** Representa um relacionamento onde uma única instância de entidade está associada a exatamente uma instância de outra entidade.

**Caso de Uso:** Um `Usuario` tem exatamente um `Passaporte`.

**Exemplo:**
```java
@Entity
public class User {
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id") // Chave estrangeira na tabela User
    private Passport passport;
}
```

## 2. @ManyToOne
**Definição:** Representa um relacionamento onde múltiplas instâncias de uma entidade estão associadas a uma única instância de outra entidade. Este é frequentemente o lado "dono" (owning side) do relacionamento (contém a Chave Estrangeira).

**Caso de Uso:** Muitos `ItemPedido` pertencem a um `Pedido`.

**Exemplo:**
```java
@Entity
public class ItemPedido {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
```

## 3. @OneToMany
**Definição:** Representa uma relação em que uma única instância de entidade está associada a várias instâncias de outra entidade. Este é tipicamente o lado "inverso" de uma relação `@ManyToOne`.

**Caso de Uso:** Um `Pedido` (Pedido) contém muitos `ItemPedido` (Itens de Pedido).

**Exemplo:**
```java
@Entity
public class Pedido {
    @Id
    private Long id;

    // 'mappedBy' refere-se ao campo em ItemPedido que é dono do relacionamento
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;
}
```

## 4. @ManyToMany
**Definição:** Representa uma relação em que múltiplas instâncias de uma entidade estão associadas a múltiplas instâncias de outra entidade. Isso requer uma Tabela de Junção intermediária.

**Caso de Uso:** Um aluno frequenta muitos cursos, e um curso tem muitos alunos.

**Exemplo:**
```java
@Entity
public class Estudante {
    @Id
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "estudante_curso",
        joinColumns = @JoinColumn(name = "estudante_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;
}
```
