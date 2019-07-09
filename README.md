# indra-park-exam
Desafio técnico – Desenvolvedor Web

### Database

* [Postgres] 

### Clone
Run: `git clone https://github.com/jhonatanjal/indra-park-exam.git`

### Build

Run: `mvn clean install`

### Config Database

Rode no postgres:
```sql
CREATE USER indrapark SUPERUSER PASSWORD 'indrapark';
CREATE DATABASE indra_park_exam;
GRANT ALL PRIVILEGES ON DATABASE indra_park_exam TO indrapark;
```
ou configure o banco de dados em **application.properties.**

### Start

#### API

Para iniciar a API use o comando: `mvn spring-boot:run`

#### UI

Para iniciar a UI use o comando: `npm start`
