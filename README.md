# EvnGest - Sistema de Gestão de Eventos

![Java](https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-gray?logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-blue?logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/Security-JWT-purple?logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-red?logo=apachemaven&logoColor=white)

**EvnGest** é uma API RESTful robusta e completa para a gestão de eventos, construída com tecnologias modernas e seguindo as melhores práticas de desenvolvimento de software. A plataforma permite a criação e gestão de eventos, autenticação de utilizadores, sistema de inscrições, avaliações, comentários e notificações assíncronas.

Este projeto foi desenhado para ser a base de uma aplicação full-stack, demonstrando uma arquitetura de backend segura, escalável e desacoplada, pronta para ser consumida por qualquer cliente front-end.

---

## ✨ Funcionalidades Principais

* **Autenticação e Autorização Segura:**
    * Sistema de registo e login com senhas encriptadas (BCrypt).
    * Segurança de endpoints baseada em JWT (JSON Web Tokens), garantindo que apenas utilizadores autenticados possam aceder a recursos protegidos.

* **Gestão Completa de Eventos (CRUD):**
    * Criação, leitura, atualização e exclusão de eventos.
    * Regras de negócio que garantem que apenas o criador de um evento o pode alterar ou apagar.

* **Sistema de Interação do Utilizador:**
    * **Inscrições:** Utilizadores podem inscrever-se em eventos.
    * **Comentários:** Sistema de comentários por evento.
    * **Avaliações:** Funcionalidade para dar uma nota a um evento, com a regra de que cada utilizador só pode avaliar um evento uma única vez.

* **Notificações Assíncronas com RabbitMQ:**
    * Quando um novo evento é criado, uma mensagem é enviada para uma fila no RabbitMQ.
    * Um serviço "ouvinte" (`listener`) consome a mensagem de forma assíncrona и dispara o envio de uma notificação (atualmente configurado para e-mail via Mailtrap).
    * Esta arquitetura garante que a API responde rapidamente ao utilizador, enquanto as tarefas mais lentas (como enviar e-mails) são executadas em segundo plano.

* **Documentação Interativa da API:**
    * API totalmente documentada com **Springdoc OpenAPI (Swagger UI)**, permitindo que qualquer pessoa explore e teste os endpoints diretamente no navegador.

* **Gestão de Banco de Dados com Flyway:**
    * As migrações do esquema do banco de dados são versionadas e geridas automaticamente pelo Flyway, garantindo consistência entre ambientes de desenvolvimento e produção.

* **Qualidade de Código e Segurança:**
    * Utilização do padrão **DTO (Data Transfer Object)** para separar as entidades internas da API dos dados expostos, evitando a fuga de informações sensíveis (como a hash da senha do utilizador).
    * Tratamento de exceções centralizado com `@ControllerAdvice` para respostas de erro consistentes.
    * Cobertura de testes robusta com **testes unitários e de integração (JUnit 5)** para garantir a fiabilidade do código.

---

## 🏛️ Arquitetura e Decisões Técnicas

Este projeto não é apenas sobre funcionalidades, mas também sobre as decisões de arquitetura que o tornam robusto e escalável.

* **Spring Boot:** Escolhido pela sua rapidez no desenvolvimento, ecossistema maduro e facilidade de configuração, permitindo focar na lógica de negócio.
* **PostgreSQL:** Um sistema de banco de dados relacional poderoso e fiável, ideal para aplicações que exigem consistência de dados.
* **RabbitMQ:** Implementado para desacoplar o sistema de notificações da API principal. Isto melhora a performance (respostas rápidas da API) e a resiliência (se o serviço de e-mail falhar, a criação de eventos não é afetada).
* **Docker e Docker Compose:** A aplicação e os seus serviços (PostgreSQL, RabbitMQ) são totalmente contentorizados, garantindo um ambiente de desenvolvimento consistente e simplificando o processo de deploy.
* **JWT para Segurança:** A utilização de tokens JWT torna a API *stateless*, o que é essencial para a escalabilidade horizontal em arquiteturas modernas.

---

## 🚀 Como Executar o Projeto Localmente

Para executar este projeto no seu ambiente local, siga os passos abaixo.

### Pré-requisitos
* **Java 17** ou superior
* **Apache Maven** 3.8+
* **Docker** e **Docker Compose**
* Uma IDE de sua preferência (IntelliJ, VS Code, etc.)
* Um cliente de API como o [Postman](https://www.postman.com/) para testar os endpoints.

### Passos para a Configuração

1.  **Clone o Repositório:**
    ```bash
    git clone https://github.com/vitorbnr/evngest.git
    cd evngest
    ```

2.  **Configure as Variáveis de Ambiente:**
    Na raiz do projeto, crie um ficheiro chamado `.env`. Este ficheiro guardará as suas credenciais locais e não será enviado para o GitHub (está no `.gitignore`). Copie e cole o conteúdo abaixo, substituindo pelos seus valores:

    ```dotenv
    # Credenciais do Banco de Dados PostgreSQL (usado pelo Docker Compose)
    POSTGRES_USER=admin
    POSTGRES_PASSWORD=admin
    POSTGRES_DB=evngest

    # Chave Secreta para os Tokens JWT (deve ter pelo menos 32 caracteres)
    JWT_SECRET_KEY=a7d9f8b3c2e1a0d9f8b7c6d5e4f3a2b1c0d9f8e7d6c5b4a3f2e1d0c9b8a7f6e5

    # Credenciais do Mailtrap (para teste de e-mail)
    MAIL_HOST=smtp.mailtrap.io
    MAIL_PORT=2525
    MAIL_USERNAME=seu_username_do_mailtrap
    MAIL_PASSWORD=sua_password_do_mailtrap
    ```

3.  **Inicie os Serviços com Docker Compose:**
    Este comando irá iniciar os contentores do PostgreSQL e do RabbitMQ em segundo plano.
    ```bash
    docker-compose up -d
    ```

4.  **Execute a Aplicação Spring Boot:**
    Você pode executar a aplicação através da sua IDE ou usando o Maven no terminal:
    ```bash
    ./mvnw spring-boot:run
    ```

5.  **A Aplicação está Pronta!**
    * A sua API estará a correr em `http://localhost:8080`.
    * A interface do RabbitMQ estará disponível em `http://localhost:15672` (login: `guest`/`guest`).

---

## 📖 Documentação da API (Swagger UI)

Após iniciar a aplicação, pode aceder à documentação interativa da API no seu navegador:

**➡️ [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

A partir desta interface, pode:
* Visualizar todos os endpoints disponíveis.
* Ver os DTOs esperados para cada pedido e resposta.
* **Testar a API diretamente**, incluindo a autenticação com o token JWT.

---

## 🧪 Estratégia de Testes

A qualidade e a fiabilidade do código são garantidas através de uma cobertura de testes abrangente:
* **Testes Unitários:** Focados em validar a lógica de negócio de classes específicas de forma isolada (ex: `JwtService`).
* **Testes de Integração:** Testam os fluxos completos da API, desde a requisição HTTP até à interação com o banco de dados (em memória H2), garantindo que todos os componentes funcionam em conjunto.

Para executar todos os testes, use o comando:
```bash
./mvnw test
```

---

## 🔮 Próximos Passos

Este projeto é uma base sólida com potencial para crescer. Os próximos passos planeados incluem:
* **Módulo de Relatórios:** Implementar a exportação da lista de inscritos para PDF.
* **Desenvolvimento do Front-End:** Construir uma interface completa em React para consumir esta API.
* **Testes de Performance:** Usar ferramentas como o JMeter para testar a performance da API sob carga.

---

Obrigado por analisar o meu projeto! Fique à vontade para entrar em contacto caso tenha alguma pergunta.
