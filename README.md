🧠 Mifica — Backend em Spring Boot
Este é o backend oficial do Mifica, uma plataforma modular que integra reputação, gamificação e transações via blockchain. Desenvolvido com foco em escalabilidade, segurança e extensibilidade.

🚀 Funcionalidades principais
✅ Registro e listagem de transações blockchain

✅ Sistema de reputação por usuário

✅ Conquistas desbloqueáveis com lógica de progressão

✅ Cadastro de administradores com senha especial

✅ Autenticação via JWT e controle de acesso por roles

✅ API REST estruturada e documentada com Swagger

✅ Integração com frontend React e painel administrativo em Streamlit

📚 Endpoints disponíveis

<img width="738" height="297" alt="Image" src="https://github.com/user-attachments/assets/0c08ce3c-a9a6-40e5-b691-3bdfeb94875b" />

📄 A documentação completa está disponível via Swagger: http://localhost:8080/swagger-ui/index.html

🔐 Segurança e autenticação
Autenticação via JWT

Proteção de rotas com hasRole("ADMIN")

Cadastro de administradores exige senha especial definida em application.properties

🧪 Como rodar localmente
```bash
# Clone o repositório
git clone https://github.com/gabrielcaue/mifica-backend.git

# Acesse o diretório
cd mifica-backend

# Compile e rode o projeto
./mvnw spring-boot:run
```
