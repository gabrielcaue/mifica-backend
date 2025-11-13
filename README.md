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
Método	Rota	Descrição
GET	/api/blockchain/transacoes	Lista todas as transações registradas
POST	/api/blockchain/transacoes	Registra uma nova transação
GET	/api/usuarios	Lista os usuários e suas reputações
POST	/api/usuarios/cadastro-admin	Cadastra um administrador com senha de acesso
POST	/api/auth/login	Realiza login e retorna token JWT
GET	/api/conquistas	Lista as conquistas disponíveis
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
