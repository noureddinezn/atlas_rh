# Responsable RH: US1, US2, US3

Standalone Spring Boot 3.3.13 / Java 17 application. Open this directory as a Maven project. No API documentation library or annotations are included. `COMPLETE-CODE.md` contains the exact source of every Java class and configuration file for copying.

## Run (PowerShell)

Requires Java 17, Maven 3.6.3+, and PostgreSQL. Docker is optional.

```powershell
cd responsable-rh
$env:DB_PASSWORD = 'choose-a-local-password'
docker compose up -d --wait
mvn clean verify
mvn spring-boot:run
```

For an existing PostgreSQL server, create an empty `atlas_rh` database and set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; skip Docker. Defaults are `jdbc:postgresql://localhost:5432/atlas_rh` and user `atlas`. Flyway applies the schema and Hibernate validates it. Use a separate database from existing applications. The migration account must have schema creation privileges. In deployment, run migrations with a privileged migration account and use a restricted runtime account.

## Postman

Import `Atlas-RH.postman_collection.json`. Set collection variable `baseUrl` if needed. Run requests in order using Collection Runner: IDs are captured automatically and status assertions check success and rejection cases. Random values allow repeated runs, which retain test data. For manual requests, use actual numeric IDs. JSON dates use `yyyy-MM-dd`, contract types use `CDI`, `CDD`, `STAGE`. Set `Content-Type: application/json` for request bodies.

| Method | Path | Behavior |
|---|---|---|
| POST | /api/departments | Create, 201 + Location |
| GET | /api/departments | Paged list |
| GET / PUT / DELETE | /api/departments/{id} | Read, full update, delete (204) |
| POST | /api/employees | Create, 201 + Location |
| GET | /api/employees | Paged search: name, departmentId |
| GET / PUT / DELETE | /api/employees/{id} | Read, full update, delete (204) |
| GET | /api/employees/{id}/team | Paged direct reports |
| POST | /api/contracts | Create, 201 + Location |
| GET | /api/contracts/{id} | Read |
| GET | /api/contracts/employee/{employeeId} | Paged history |
| PATCH | /api/contracts/{id}/terminate | Body: {"terminationDate":"2026-10-30"} |

Pagination: `page=0&size=20&sort=id,asc`, size capped at 100. Sort fields are allowlisted in controllers. Responses contain `content`, `page`, `size`, `totalElements`, `totalPages`. Name search matches the combined first and last name, case-insensitively; department filtering uses its ID. Unknown referenced IDs return 404. PUT replaces all mutable fields; null managerId clears the manager.

Department creation: `{"name":"Engineering","description":"Engineering team"}`.

Employee creation: `{"matricule":"EMP001","email":"sara@example.com","firstName":"Sara","lastName":"Amrani","departmentId":1,"managerId":null}`.

Contract creation: `{"employeeId":1,"type":"CDD","startDate":"2026-01-01","endDate":"2026-12-31"}`. CDI endDate is optional.

Errors use `{timestamp,status,error,message,path,fields}`. Duplicate names, matricules and emails return 409, including concurrent inserts. Names and identifiers are trimmed; email is stored lowercase. Department names, matricules and emails are case-insensitively unique. Database foreign keys also protect deletion races. Employee deletion is blocked for managers with direct reports and employees with any contract history.

Active means administratively active until explicitly terminated; reaching endDate does not automatically terminate a contract. Creating a future contract makes it active immediately. Termination takes effect immediately while recording the supplied date; this is not a scheduled action. Termination must fall between startDate and endDate (if present). Original contractual endDate is retained separately. History is retained without a contract delete endpoint. Past contract periods may overlap after termination; temporal overlap restrictions and longer management cycles are outside the requested rules.

## Validation and deployment scope

`mvn verify` compiles the application, generates MapStruct implementations and runs business-rule unit tests. Unit tests do not prove PostgreSQL migration or concurrency behavior: run the Postman collection against PostgreSQL before deployment and exercise concurrent contract creation. A partial unique index and employee-row locking protect the single-active-contract rule; optimistic versions detect concurrent entity changes.

This module has no authentication/authorization because no identity model was specified. Do not expose employee data publicly: integrate the organization's authentication and RH authorization before production use. Supply deployment-specific TLS, secret management, backups, logging/monitoring and a supported dependency maintenance policy. This is a runnable module with persistence safeguards, not a certification that an unconfigured deployment is production-ready.

Version references: [Spring Boot 3.3 requirements](https://docs.spring.io/spring-boot/3.3/system-requirements.html) and [MapStruct annotation processing / Lombok integration](https://mapstruct.org/documentation/stable/reference/html/).
