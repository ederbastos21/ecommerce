# Ecommerce Spring Boot - MySQL Edition

## Pré-requisitos
- Java 21 (Microsoft Open JDK 21.0.8)
- Maven 3.9+
- Docker

# Como executar

## 1. Clone o repositório
```bash
git clone git@github.com:ederbastos21/ecommerce.git
cd ecommerce
```

## 2. Build do projeto:

#### Linux/macOS (ou Windows via PS):
    ./mvnw clean install

#### Windows (via CMD):
    mvnw.cmd clean install

## 3. Inicialize o banco de dados MySQL com o Docker
```
cd pasta-root-do-projeto-ecommerce
docker compose up -d
```
### Verificando o banco de dados

- Listar containers ativos:
```
docker ps
```

- Acesse o MySQL via terminal:
```
docker exec -it mysql-ecommerce mysql -uroot -p1234
```

(Senha padrão: 1234)

## 4. Executar a aplicação

- Linux/macOS (ou Windows via PS)
```
./mvnw spring-boot:run
```

- Windows (via CMD)
```
mvnw.cmd spring-boot:run
```

## 5. Acesse o sistema

http://localhost:8080

## 6. Comandos úteis SQL

- Primeiro comando (mostrar banco de dados):
```
show databases;
```

- Segundo comando (entrar/usar o banco de dados):
```
use <nome_do_banco>
```

- Terceiro comando (mostrar as tabelas do banco de dados em uso):
```
show tables;
```

- Quarto comando (mostrar as colunas da tabela):
```
show columns from <nome_tabela>;
```

- Quinto comando (inserir dados):
```
insert into <nome_tabela> (<coluna1>,<coluna2>) values(<valor1>,<valor2>);
```

- Sexto comando (mostrar dados da tabela):
```
select * from <nome_tabela>;
```

## Git Pull Test2
