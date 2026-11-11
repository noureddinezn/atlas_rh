# AtlasRH — installation et tests

## STEP 1 — Base PostgreSQL et configuration

Le projet Maven à ouvrir est celui du dossier courant. Java 17 et Maven 3.6.3+ sont nécessaires. PostgreSQL doit écouter sur localhost:5432.

Créer une base vide depuis pgAdmin ou psql :

```sql
CREATE DATABASE atlasrh_db;
```

Dans le terminal PowerShell du projet, définir le mot de passe réel du compte PostgreSQL :

```powershell
$env:DB_PASSWORD = Read-Host 'Mot de passe PostgreSQL'
$env:DB_USERNAME = 'postgres'
```

Les mêmes variables doivent être ajoutées à la configuration Run d'IntelliJ si vous démarrez depuis IntelliJ. `DB_URL` permet de remplacer `jdbc:postgresql://localhost:5432/atlasrh_db`. Le port HTTP par défaut est 8081, modifiable avec `PORT`.

Liquibase crée les cinq tables, les clés étrangères, les contraintes et les données initiales. Les change sets existants de création sont conservés ; `003-integrity.yaml` ajoute les index et contraintes. Une ancienne base avec des doublons ou plusieurs contrats actifs doit être corrigée avant cette migration. Aucune base n'est effacée automatiquement.

## STEP 2 — Entités

Les cinq entités sont dans `com.atlasrh.api.entity`, les deux enums dans `entity.enums`. Les relations sont converties en DTO pour les réponses HTTP. La sérialisation ne traverse pas les collections JPA.

## STEP 3 — Exceptions

Les erreurs JSON contiennent `timestamp`, `status`, `error`, `message`, `path`, `validationErrors`. Les erreurs métier renvoient 400, 404 ou 409 ; la validation des champs renvoie 400. Les conflits de contraintes PostgreSQL renvoient 409.

## STEP 4 — Compiler et démarrer

```powershell
mvn clean verify
mvn spring-boot:run
```

Depuis IntelliJ, recharger le projet Maven, activer annotation processing puis lancer `com.atlasrh.api.AtlasRhApplication`. Attendre `Tomcat started on port 8081` et `Started AtlasRhApplication` avant Postman. L'ancienne classe `AtlasRhApiApplication` est remplacée.

Les services RH couvrent les départements, les employés et les contrats. La recherche `name` filtre le nom de famille, avec combinaison possible du filtre département. Pagination à partir de zéro, taille plafonnée à 100. PUT remplace les champs modifiables ; `managerId: null` retire le manager.

## STEP 5 — Tester US1 à US6 dans Postman

Importer `AtlasRH.postman_collection.json`, puis exécuter les requêtes dans l'ordre avec Collection Runner. Les IDs sont récupérés automatiquement. La collection crée des données avec des identifiants aléatoires et les conserve pour inspection. `baseUrl` vaut `http://localhost:8081`.

Sur une base neuve, `leaveTypeId=1` correspond à ANNUEL (18 jours). EXCEPTIONNEL dispose de 10 jours. Si la base existait déjà, vérifier les IDs via `SELECT id,name,days_per_year FROM leave_types;` et adapter la variable. Les données initiales comprennent un département Informatique, Sara Amrani (MGR001) et Youssef Alami (EMP001) rattaché à Sara. La collection utilise ses propres employés.

| Méthode | URL | Fonction |
|---|---|---|
| POST / GET | /api/departments | Créer / lister |
| GET / PUT / DELETE | /api/departments/{id} | Lire / modifier / supprimer |
| POST / GET | /api/employees | Créer / recherche paginée |
| GET / PUT | /api/employees/{id} | Lire / modifier |
| GET | /api/employees/manager/{managerId}/team | Équipe directe |
| POST | /api/contracts | Créer un contrat |
| PATCH | /api/contracts/{id}/terminate | Terminer, sans body |
| GET | /api/contracts/employee/{employeeId} | Historique |
| GET | /api/managers/{managerId}/leave-requests/pending | Demandes en attente de l'équipe |
| PATCH | /api/managers/leave-requests/{requestId}/decision?managerId={managerId} | Décider |
| POST | /api/leaves | Soumettre |
| GET | /api/leaves/employee/{employeeId}?status=EN_ATTENTE | Historique filtré |
| PATCH | /api/leaves/{requestId}/cancel?employeeId={employeeId} | Annuler |
| GET | /api/leaves/employee/{employeeId}/balance | Solde et ancienneté |

### Exemples de bodies JSON

Département :
```json
{"name":"Finance","description":"Equipe finance"}
```

Employé (adapter departmentId et managerId) :
```json
{"matricule":"EMP002","firstName":"Amine","lastName":"Bennani","email":"amine@example.com","position":"Comptable","hireDate":"2024-01-01","departmentId":1,"managerId":null}
```

Contrat :
```json
{"employeeId":2,"type":"CDD","startDate":"2026-01-01","endDate":"2026-12-31","grossSalary":12000}
```

Demande :
```json
{"employeeId":2,"leaveTypeId":1,"startDate":"2026-11-02","endDate":"2026-11-04","reason":"Repos annuel"}
```

Décision :
```json
{"status":"REFUSEE","comment":"Presence necessaire pour la cloture"}
```

## Règles de calcul

Les dates de début et de fin sont incluses. Samedi et dimanche sont exclus ; les jours fériés sont comptés selon le périmètre demandé. Une demande sans jour ouvré ou antérieure à l'embauche est refusée. Les demandes EN_ATTENTE et APPROUVEE réservent le solde, les demandes REFUSEE et ANNULEE le libèrent. Le contrôle de chevauchement inclut les bornes et tous les types de congé.

Le quota s'applique par année civile et par type, sans proratisation ni report. Les demandes sur plusieurs années sont décomptées séparément par année. Le solde affiché concerne l'année courante. L'ancienneté est un Period ISO (par exemple P6Y9M). Les opérations de création, décision et annulation utilisent le même verrou d'employé pour sérialiser les modifications concurrentes.

Un contrat reste actif jusqu'à sa terminaison explicite. Terminer fixe endDate à la date du jour. Une terminaison avant startDate est refusée pour conserver des dates cohérentes.

## Portée de sécurité et validation

Les endpoints demandés ne comportent pas d'authentification. Les IDs fournis dans les URLs ne prouvent pas l'identité de l'appelant. Avant une exposition réelle, intégrer l'authentification et vérifier les rôles, l'appartenance du manager à l'équipe et l'identité de l'employé connecté. La décision demandée dans le cahier des charges ne fournit pas de managerId.

`mvn verify` exécute les tests unitaires métier. Tester la collection contre PostgreSQL pour valider le démarrage, les migrations et les échanges HTTP ; les tests unitaires ne les remplacent pas.

Validation réalisée : `mvn clean verify` réussi, 10 tests unitaires réussis. Démarrage du JAR vérifié avec PostgreSQL 18 sur une instance temporaire : migrations Liquibase et validation Hibernate réussies, puis 32 scénarios HTTP de la collection exécutés avec succès via `scripts/verify_http.py`. Les services temporaires ont ensuite été arrêtés. La base PostgreSQL existante n'a pas été utilisée.

Guide de v?rification fonction par fonction et tests manuels : [TEST-POSTMAN-BASIC.md](TEST-POSTMAN-BASIC.md). Le contr?le d??quipe et le contr?le du solde ? l?approbation ont ?t? ajout?s sans modifier la r?servation des jours EN_ATTENTE.
