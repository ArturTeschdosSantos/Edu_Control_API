# EduControl

API Java 17 / Spring Boot para alunos e refeitorios.

## Executar

No PowerShell, na pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

A API atende em `http://localhost:8081`. Verifique `GET /health`.
O Maven baixa as dependencias na primeira execucao e precisa de internet.
O banco H2 fica em memoria: os dados sao apagados ao encerrar a aplicacao.

## Rotas

| Operacao | Alunos | Refeitorios |
| --- | --- | --- |
| Listar (GET) | /students | /cafeteria |
| Consultar (GET) | /students/{id} | /cafeteria/{id} |
| Cadastrar (POST) | /students | /cafeteria |
| Atualizar (PUT) | /students/{id} | /cafeteria/{id} |
| Excluir (DELETE) | /students/{id} | /cafeteria/{id} |

Envie `Content-Type: application/json`. O PUT substitui todos os campos editaveis.
O UUID e gerado pelo servidor; nao e necessario envia-lo no cadastro.

Aluno:

```json
{"name":"Ana","registration":"001","classroom":"1A","turn":"MORNING"}
```

Nome, matricula e turno sao obrigatorios. A matricula e unica.
Turnos aceitos: `MORNING` e `AFTERNOON`.
Nome e matricula sao salvos sem espacos nas extremidades. Nome, matricula e
turma aceitam ate 255 caracteres. A turma e opcional.

Refeitorio:

```json
{"name":"Central","location":"Bloco A","openingTime":"07:00","closingTime":"18:00","active":true}
```

Nome e obrigatorio. Horarios, quando informados, devem ser horarios validos, como `07:00`.
Tambem sao aceitos segundos (por exemplo, `07:00:30`). O fechamento pode ocorrer
no dia seguinte (por exemplo, abertura `22:00` e fechamento `06:00`). Nome e
localizacao aceitam ate 255 caracteres. Localizacao e horarios sao opcionais;
`active` assume `false` quando omitido.
Respostas: 201 no cadastro, 204 na exclusao, 400 para dados invalidos,
404 para UUID inexistente e 409 para conflito de integridade (como matricula duplicada).
Erros retornam JSON `application/problem+json`, com `status` e `detail`.
Um PUT com campos opcionais omitidos remove os valores anteriores desses campos.
IDs enviados no corpo nao substituem o ID da URL no PUT e sao ignorados no POST.

Exemplo de cadastro no PowerShell:

```powershell
$aluno = @{ name = 'Ana'; registration = '001'; classroom = '1A'; turn = 'MORNING' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/students -ContentType 'application/json' -Body $aluno
Invoke-RestMethod -Uri http://localhost:8081/students
```

## Testes

```powershell
.\mvnw.cmd clean verify
```

Os testes de integracao exercitam as rotas com Spring MVC e persistencia H2,
incluindo CRUD, campos obrigatorios, limites de tamanho, JSON/UUID invalidos,
matriculas duplicadas e preservacao dos dados quando uma atualizacao e rejeitada.
Cada contexto de teste usa um banco H2 em memoria independente.
O comando tambem gera `target/demo-0.0.1-SNAPSHOT.jar`, que pode ser executado com:

```powershell
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
