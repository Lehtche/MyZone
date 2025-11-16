# 3. Metodologia

[Anterior: Objetivos](./02-Objetivos.md) | [Próximo: Descrição do Sistema](./04-Descricao-do-Sistema.md)

A metodologia adotada para o desenvolvimento do sistema MyZone fundamentou-se em princípios de engenharia de software, boas práticas de modelagem de dados e integração entre múltiplas tecnologias.

O processo iniciou-se pela definição dos requisitos funcionais e pela identificação das entidades essenciais do sistema, permitindo a elaboração de um modelo conceitual consistente. A partir dessa estrutura inicial, avançou-se para os modelos lógico e físico, garantindo que todas as relações, chaves e dependências estivessem adequadamente representadas no banco de dados relacional.

Para a implementação do backend, utilizou-se a linguagem Java em conjunto com o framework Spring Boot, escolhido pela sua robustez, modularidade e capacidade de integração simultânea com bancos de dados SQL e NoSQL. O backend foi responsável por gerenciar autenticação, regras de negócio, validações, controle de acesso e comunicação com as camadas de persistência.

A interação com o banco de dados relacional MySQL foi realizada por meio do Spring Data JPA, enquanto o acesso ao banco MongoDB ocorreu via Spring Data MongoDB, permitindo que cada tipo de dado fosse manipulado de acordo com suas características naturais.

O frontend foi desenvolvido utilizando HTML, CSS e JavaScript, oferecendo uma interface simples, funcional e alinhada aos requisitos de usabilidade do projeto. Por meio dessa interface, os usuários podem realizar operações de cadastro, login, gerenciamento de mídias e visualização de avaliações, comunicando-se com o backend por meio de requisições HTTP.

Durante o desenvolvimento, aplicaram-se boas práticas de versionamento utilizando GitHub, bem como organização modular do código, divisão de responsabilidades e definição de camadas. O projeto também contemplou a criação de artefatos essenciais, como triggers, views, índices e procedures no MySQL, assegurando integridade dos dados, automação de rotinas e otimização das consultas.