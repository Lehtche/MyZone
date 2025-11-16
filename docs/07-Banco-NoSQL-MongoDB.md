# 7. Banco de Dados NoSQL (MongoDB)

[Anterior: Controle de Acesso](./06-Controle-de-Acesso.md) | [Próximo: Guia de Execução](./08-Guia-de-Execucao.md)

O sistema MyZone utiliza o MongoDB como SGBD NoSQL, complementando o MySQL em uma arquitetura híbrida.

### 7.1 Características Técnicas do MongoDB

O MongoDB é um banco de dados NoSQL orientado a documentos que armazena informações em formato BSON (Binary JSON). Sua arquitetura é *schema-flexible*, permitindo que documentos dentro de uma mesma coleção possuam estruturas diferentes. Suas características incluem flexibilidade, alta performance em operações de escrita e escalabilidade horizontal.

### 7.2 Justificativa da Escolha do MongoDB no MyZone

A escolha do MongoDB foi direcionada para armazenar as **avaliações de mídias** (nota e comentário). Este conjunto de dados apresenta três características que o tornam ideal para um banco NoSQL:

**1. Volume elevado de escritas**
Enquanto um usuário se cadastra *uma vez* e cadastra uma quantidade *limitada* de mídias, ele pode realizar *centenas ou milhares* de avaliações. A natureza intensiva em escrita torna o MongoDB mais eficiente que o MySQL para esse cenário.

**2. Flexibilidade para evolução futura**
Atualmente, uma avaliação possui `nota`, `comentario`, `midiaId` e `usuarioId`. No futuro, o modelo de avaliação pode evoluir para incluir:
* Marcação de spoilers
* Reações (like/dislike)
* Tags personalizadas
* Avaliações específicas (ex.: trilha sonora, fotografia)

No MySQL, essas mudanças exigiriam `ALTER TABLE`, gerando colunas `NULL`. No MongoDB, novos atributos podem ser adicionados a novos documentos sem afetar os antigos, justificando o modelo documental.

**3. Independência e Desacoplamento**
Os dados centrais (usuários, mídias) permanecem no MySQL, onde a rigidez e a integridade referencial são essenciais. As avaliações, por outro lado, são de baixa criticidade, não-relacionais e naturalmente representáveis como documentos. Essa separação resulta em uma arquitetura híbrida organizada e escalável.

### 7.3 Implementação da Coleção de Avaliações

A entidade `Avaliacao`, no MongoDB, é armazenada na coleção `avaliacoes`, com documentos no seguinte formato:

```json
{
  "_id": ObjectId("..."),
  "usuarioId": 1,
  "midiaId": 10,
  "nota": 5,
  "comentario": "Excelente filme!",
  "dataAvaliacao": ISODate("...")
}