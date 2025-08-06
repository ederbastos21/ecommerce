# Ecommerce Spring Boot Mysql App

## Requisitos
- Java 21 (microsoft openJDK 21.0.8)
- Maven
- Docker

## Como rodar

### 1. Clone o repositório:
```bash
git clone <git@github.com:ederbastos21/ecommerce.git>
```

### 2. Execute a instalação e build do projeto:

No Linux/macOS:

    ./mvnw clean install

No Windows:

    mvnw.cmd clean install


### 3. Inicialize o banco de dados Mysql com o Docker
```
cd <pasta-root-do-projeto-ecommerce>
docker compose up -d
```
Verifique se as imagens e containers foram corretamente criadas com:
```
docker images (vê imagens)
docker ps (vê containers ativos)
docker ps -a (vê todos os containers)
```
Caso as imagens e containers estejam corretos, verifique se o mysql está funcionando:
```
docker exec -it <container_id_or_name> mysql -uroot -p (permite executar comandos sql diretamente pelo terminal)
*inserir senha 1234*
*exit no terminal para sair*
```
### 4. Rode a aplicação:

No Linux/macOS:

    ./mvnw spring-boot:run

No Windows:

    mvnw.cmd spring-boot:run


### 5. Acesse a aplicação no navegador:

http://localhost:8080