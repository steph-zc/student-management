# Student Management

Aplicacao Java de console que gerencia alunos e os cursos em que estao
matriculados, com os dados armazenados em um banco PostgreSQL.

Cada aluno pode estar matriculado em varios cursos e cada curso tem varios
alunos - relacionamento N:N, materializado pela tabela associativa
`enrollments`, cuja chave primaria composta impede matricular a mesma pessoa
duas vezes no mesmo curso.

## Tecnologias

| Componente  | Versao |
|-------------|--------|
| Java        | 17+    |
| Maven       | 3.8+   |
| PostgreSQL  | 14+    |
| Driver JDBC | 42.7.4 |

## Como rodar

1. `createdb -U postgres college`
2. `psql -U postgres -d college -f sql/schema.sql`
3. Copie o arquivo de exemplo e informe a sua senha do PostgreSQL nele:
   - Linux / macOS: `cp src/main/resources/db.properties.example src/main/resources/db.properties`
   - Windows: `copy src\main\resources\db.properties.example src\main\resources\db.properties`
4. `mvn clean compile exec:java`

Pre-requisitos: JDK 17+, Maven 3.8+ e PostgreSQL 14+ instalados, com o
servidor do banco no ar. O driver JDBC e baixado pelo Maven, nao precisa
instalar nada a mais.

A opcao 9 do menu gera a base de dados completa: o catalogo de cursos, 5.000
alunos e o relatorio de quantos alunos cada curso tem. Cada aluno entra em um
curso; cerca de 5% recebem um segundo, o suficiente para demonstrar que o
modelo N:N funciona. Por isso a soma do relatorio passa um pouco de 5.000.

## Jar executavel

    mvn clean package
    java -jar target/student-management.jar

## Solucao de problemas

| Sintoma | O que fazer |
|---------|-------------|
| `Copie db.properties.example para db.properties` | Ainda nao existe o arquivo local de credenciais. |
| `database "college" does not exist` | Rode `createdb -U postgres college` antes do schema. |
| Acentos saem como `?` no console do Windows | Rode `chcp 65001` antes de `mvn compile exec:java`. |
| `autenticacao ... falhou para o usuario "<seu login>"` | Faltou o `-U postgres` nos comandos do psql. |
