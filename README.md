# Mifica — Backend em Spring Boot

Este é o backend do projeto **Mifica**, uma plataforma modular voltada para reputação, gamificação e transações via blockchain.

## 🚀 Funcionalidades principais

- Registro e listagem de transações blockchain
- Sistema de reputação por usuário
- Conquistas desbloqueáveis
- API REST estruturada e documentada com Swagger
- Arquitetura modular com foco em escalabilidade

## 📚 Endpoints disponíveis

- `GET /api/blockchain/transacoes` — Lista todas as transações registradas
- `POST /api/blockchain/transacoes` — Registra uma nova transação
- `GET /api/usuarios` — Lista os usuários e suas reputações
- `GET /api/conquistas` — Lista as conquistas disponíveis

> A documentação completa está disponível via Swagger em:  
> `http://localhost:8080/swagger-ui/index.html`

## 🛠️ Em desenvolvimento

Este backend está em fase final de implementação. As próximas melhorias incluem:

✅ Criar perfil de usuário com login via JWT  
✅ Proteger rotas específicas com roles (`hasRole("ADMIN")`)  
✅ Adicionar exemplos e tags na documentação Swagger

## 🧪 Como rodar localmente

1. Clone o repositório:
   ```bash
   git clone https://github.com/gabrielcaue/mifica-backend.git
