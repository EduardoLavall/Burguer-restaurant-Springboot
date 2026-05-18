# Burguer Restaurant

Sistema de restaurante para tablets de atendimento com backend em Spring Boot e frontend planejado em React.

## Objetivo

Construir uma aplicacao simples para operacao de restaurante, com foco inicial no backend e na organizacao do dominio de cardapio e pedidos.

## Stack inicial

- Java 21
- Spring Boot
- Maven Wrapper
- Docker
- MySQL
- React (planejado para a proxima etapa)

## Estrutura

- `src/main/java`: codigo-fonte do backend
- `src/main/resources`: configuracoes da aplicacao
- `src/test/java`: testes automatizados
- `docs`: documentacao funcional e tecnica
- `TODO.md`: backlog do projeto
- `AGENTS.md`: combinados de colaboracao com IA
- `docs/PROJETO.md`: visao funcional e tecnica do sistema
- `docs/HISTORICO_IA.md`: historico das implementacoes e decisoes

## Pre-requisitos

- Java instalado
- Docker Desktop instalado e em execucao
- Docker Compose habilitado

Observacao importante:

- o `pom.xml` esta configurado para `Java 21`
- nesta maquina o ambiente estava com `Java 22`
- para desenvolvimento isso ainda funcionou, mas a recomendacao do projeto continua sendo usar `Java 21`

## Como preparar o ambiente

### 0. Arquivo de ambiente

O projeto agora possui:

- `.env.example`: modelo versionado com as variaveis esperadas
- `.env`: arquivo local para desenvolvimento

Se voce quiser recriar manualmente o arquivo local, copie os valores de `.env.example` para `.env`.

### 1. Subir o MySQL com Docker

Na raiz do projeto, rode:

```powershell
docker compose up -d
```

Esse comando sobe o container `MySQL 8` definido no `docker-compose.yml`.
As credenciais e a porta lidas pelo Docker vem do arquivo `.env`.

### 2. Conferir o Maven Wrapper

O repositório ja possui:

- `mvnw`
- `mvnw.cmd`
- pasta `.mvn`

Isso significa que voce nao precisa instalar Maven manualmente na maquina.

Se esses arquivos sumirem no futuro, voce pode regenerar com Docker:

```powershell
docker run --rm -v "${PWD}:/app" -w /app maven:3.9.9-eclipse-temurin-21 mvn -N wrapper:wrapper
```

### 3. Baixar as dependencias do backend

Rode:

```powershell
.\mvnw.cmd dependency:resolve
```

Esse comando baixa todas as dependencias declaradas no `pom.xml`.

### 4. Rodar os testes

Rode:

```powershell
.\mvnw.cmd test
```

Esse passo confirma se o contexto do Spring Boot sobe corretamente e se o projeto compila.

### 5. Subir a aplicacao

Rode:

```powershell
.\mvnw.cmd spring-boot:run
```

Se tudo estiver correto, a API deve subir na porta `8080`.

### 6. Validar a API

Teste o endpoint de saude:

```powershell
Invoke-RestMethod http://localhost:8080/api/saude
```

Exemplo esperado:

```json
{
  "status": "UP",
  "servico": "burguer-restaurant"
}
```

## Fluxo rapido

Se voce quiser o caminho mais direto, use:

```powershell
docker compose up -d
.\mvnw.cmd dependency:resolve
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Quick Start — subir e rodar (um comando)

Se você quer iniciar o ambiente de desenvolvimento rapidamente, rode o seguinte comando na raiz do projeto (Windows PowerShell):

```powershell
# Sobe o MySQL via Docker e inicia a aplicação (usa .env)
.\run-with-env.ps1 -StartDb
```

Em Unix/WSL/macOS, use:

```bash
chmod +x run-with-env.sh
./run-with-env.sh --start-db
```

Observação: os scripts carregam automaticamente o arquivo `.env`. Para forçar uma porta diferente, passe `-Port 9090` (PowerShell) ou `--port 9090` (bash).

## One-liners úteis

```powershell
# Iniciar DB + rodar app (PowerShell)
.\run-with-env.ps1 -StartDb

# Rodar na porta 9090 sem subir DB
.\run-with-env.ps1 -Port 9090 -NoBuild
```

Em WSL / macOS / Linux:

```bash
chmod +x run-with-env.sh
./run-with-env.sh --port 9090
```

## Scripts de ajuda

Existem dois scripts úteis na raiz do projeto:

- `run-with-env.ps1` (PowerShell - Windows)
  - Carrega `.env`, opcionalmente sobe o DB (`-StartDb`), executa build (a menos que `-NoBuild`) e roda a aplicação.
  - Exemplos:
    - `.\run-with-env.ps1 -StartDb` — sobe DB e roda a app.
    - `.\run-with-env.ps1 -Port 9090 -NoBuild` — roda na porta 9090 sem rebuild.

- `run-with-env.sh` (bash/WSL/macOS/Linux)
  - Equivalente ao script PowerShell.
  - Exemplos:
    - `./run-with-env.sh --start-db`
    - `./run-with-env.sh --port 9090 --no-build`

Os scripts garantem que `SERVER_PORT` e outras variáveis do `.env` estejam disponíveis para o processo do Maven/Spring Boot.

## Fallback para erro de permissao no Maven

Se o Maven tentar escrever em `C:\Users\...\ .m2` e der erro de permissao, use os comandos abaixo:

```powershell
.\mvnw.cmd -Dmaven.repo.local=.mvn/repository dependency:resolve
.\mvnw.cmd -Dmaven.repo.local=.mvn/repository test
.\mvnw.cmd -Dmaven.repo.local=.mvn/repository spring-boot:run
```

Esse fallback força o Maven a guardar dependencias dentro da pasta do proprio projeto.

## Banco local

O banco local esta definido em `docker-compose.yml` com estes dados:

- host: `localhost`
- porta: `3306`
- database: `burguer_restaurant`
- usuario: `burguer_user`
- senha: `burguer_password`
- usuario root: `root`
- senha root: `root`

As configuracoes acima podem ser alteradas no `.env`.

Variaveis principais:

- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

## Migrations do banco

O projeto agora usa `Flyway` para versionar o schema do banco.

As migrations ficam em:

- `src/main/resources/db/migration`

Arquivos criados nesta etapa:

- `V1__criar_tabela_produto.sql`
- `V2__criar_tabela_cliente.sql`
- `V3__criar_tabela_pedido.sql`
- `V4__criar_tabela_item_pedido.sql`

Ao subir a aplicacao com o banco disponivel, o Spring Boot executa automaticamente essas migrations.

Se voce quiser validar apenas a aplicacao das migrations, basta subir o MySQL e depois rodar:

```powershell
.\mvnw.cmd spring-boot:run
```

Se houver erro de permissao no Maven local:

```powershell
.\mvnw.cmd -Dmaven.repo.local=.mvn/repository spring-boot:run
```

## Solucao de problemas

### `mvn` nao encontrado

Isso e esperado se o Maven nao estiver instalado globalmente. Use o `Maven Wrapper` com `.\mvnw.cmd`.

### Erro de permissao no repositorio Maven

Use o fallback:

```powershell
.\mvnw.cmd -Dmaven.repo.local=.mvn/repository test
```

### Porta `3306` ocupada

Se o MySQL nao subir, veja se ja existe outro banco usando essa porta.

### Porta `8080` ocupada
### Porta `8080` ocupada (passos rápidos)

1. Descobrir quem está usando a porta:

```powershell
netstat -ano | findstr :8080
```

2. Identificar o processo (substitua `<PID>`):

```powershell
Get-Process -Id <PID>
```

3. Finalizar o processo (se for seguro):

```powershell
Stop-Process -Id <PID> -Force
```

4. Alternativa: rodar a aplicação em outra porta sem encerrar processos:

```powershell
.\run-with-env.ps1 -Port 9090
```

### Java diferente da versao do projeto

Se houver erro de compatibilidade, instale `Java 21` e ajuste o `JAVA_HOME`.

## O que ja foi validado neste ambiente

- o `Maven Wrapper` foi gerado com sucesso
- as dependencias do `pom.xml` foram baixadas com sucesso
- os testes com `.\mvnw.cmd test` passaram
- o container do `MySQL` definido no `docker-compose.yml` esta subindo
- a tentativa final de subir a API falhou porque a porta `8080` ja estava em uso no ambiente

## Proximo passo

Evoluir a base criada aqui com os primeiros endpoints de produtos, clientes e pedidos.
