## 🧠 Mifica — Backend em Spring Boot
Este é o backend oficial do Mifica, uma plataforma modular que integra reputação, gamificação e transações via blockchain. Desenvolvido com foco em escalabilidade, segurança e extensibilidade.

## 🚀 Funcionalidades principais
✅ Registro e listagem de transações blockchain

✅ Sistema de reputação por usuário

✅ Conquistas desbloqueáveis com lógica de progressão

✅ Cadastro de administradores com senha especial

✅ Autenticação via JWT e controle de acesso por roles

✅ API REST estruturada e documentada com Swagger

✅ Integração com frontend React e painel administrativo em Streamlit

✅ Persistência de dados com MySQL (novo)

## 🗄️ Banco de Dados
O projeto agora utiliza MySQL como banco de dados principal.

## Configuração
No arquivo application.properties ou application.yml, configure:
```bash
properties
spring.datasource.url=jdbc:mysql://localhost:3306/mifica
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```
⚠️ Certifique-se de ter o MySQL rodando localmente e um banco chamado mifica criado.

## 📚 Endpoints disponíveis

📄 A documentação completa está disponível via Swagger: 👉 http://localhost:8080/swagger-ui/index.html

## 🔐 Segurança e autenticação
Autenticação via JWT

Proteção de rotas com hasRole("ADMIN")

Cadastro de administradores exige senha especial definida em application.properties

## 🧪 Como rodar localmente
```bash
# Clone o repositório
git clone https://github.com/gabrielcaue/mifica-backend.git

# Acesse o diretório
cd mifica-backend

# Compile e rode o projeto
./mvnw spring-boot:run
```
## 📦 Próximos passos (em andamento)
# 🐳 Docker: containerização do backend e banco de dados
# ☁️ Google Cloud Platform (GCP): deploy em nuvem
# 🌐 GitHub Pages / GitHub Actions: CI/CD e documentação automatizada

## 🎯 Resumindo
Adicionei seção Banco de Dados com instruções de configuração.

Mantive as funcionalidades já listadas.

Criei seção Próximos passos para Docker, GCP e GitHub Pages.

## 🐳 Rodando com Docker

Você pode subir o backend e o banco MySQL juntos usando **docker-compose**.

Crie um arquivo `docker-compose.yml` na raiz do projeto com o seguinte conteúdo:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: mifica-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: mifica
      MYSQL_USER: mifica
      MYSQL_PASSWORD: mifica123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  backend:
    build: .
    container_name: mifica-backend
    restart: always
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mifica
      SPRING_DATASOURCE_USERNAME: mifica
      SPRING_DATASOURCE_PASSWORD: mifica123
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    ports:
      - "8080:8080"

volumes:
  mysql_data:
```
##🚀 Como rodar
```bash
# Subir os containers
docker-compose up --build

# Derrubar os containers
docker-compose down
```
O backend ficará disponível em http://localhost:8080 e o banco MySQL em localhost:3306.

## 📦 Próximos passos
# ☁️ Deploy em Google Cloud Platform (GCP)
# 🌐 CI/CD com GitHub Actions
# 📊 Documentação automatizada via GitHub Pages
