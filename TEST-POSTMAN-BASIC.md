# AtlasRH — audit w tests Postman, endpoint b endpoint

## 1. Resultat dyal verification

| Fonctionnalite | Etat apres correction |
|---|---|
| Creer departement, nom unique | Kayna |
| Modifier departement sans reprendre nom existant | Kayna |
| Liste w details departements | Kayna |
| Refuser suppression departement fih employes | Kayna |
| Creer employe, email/matricule uniques, relations existantes | Kayna |
| Modifier employe, unicite w interdiction auto-management | Kayna |
| Liste paginee, tri, nom w departement | Kayna ; nom = lastName |
| Equipe directe dyal manager | Kayna |
| Creer contrat, un seul actif, CDD/STAGE avec date fin | Kayna |
| Terminer contrat actif | Kayna |
| Historique contrats | Kayna ; startDate decroissant |
| Demandes EN_ATTENTE dyal equipe | Kayna |
| Decision uniquement sur demande EN_ATTENTE | Kayna |
| Commentaire obligatoire pour refus | Kayna |
| Verifier equipe avant approbation/refus | Zednaha |
| Reverifier solde avant approbation | Zednaha |
| Creer demande, type existant, dates, motif | Kayna |
| Calcul lundi-vendredi, bornes incluses | Kayna |
| Refuser chevauchement avec EN_ATTENTE/APPROUVEE | Kayna |
| Refuser demande si solde insuffisant | Kayna |
| Historique employe avec filtre statut | Kayna |
| Annulation par employeeId proprietaire, EN_ATTENTE seulement | Kayna |
| Solde par type w anciennete | Kayna ; annee courante |

Changements limites : controller/interface/service manager pour recevoir managerId, controle de son equipe et verification de solde. Structure des tables, DTOs existants, configuration PostgreSQL et logique des autres modules restent les memes.

La decision utilise maintenant `?managerId=...`, obligatoire. Les IDs fournis servent aux controles metier de cette API de demonstration ; ils ne remplacent pas une authentification. Le serveur ne prouve pas l'identite de la personne qui saisit un ID.

Les demandes EN_ATTENTE reservaient deja le solde. Approuver ne consomme pas deux fois les jours. Le nouveau controle exclut la demande courante des autres reservations, puis verifie ses jours, par type et par annee.

## 2. Preparation simple

Demarre AtlasRhApplication avec Java 17. Base PostgreSQL configuree dans application.yml. Attends `Started AtlasRhApplication`.

Base URL : `http://localhost:8081`.

Dans Postman, cree ces variables de collection :

| Variable | Valeur |
|---|---|
| baseUrl | http://localhost:8081 |
| departmentId | ID retourne par creation departement |
| managerId | ID retourne par creation manager |
| employeeId | ID retourne par creation employe |
| contractId | ID retourne par creation contrat |
| leaveId | ID retourne par premiere demande |
| cancelLeaveId | ID retourne par seconde demande |
| refusalLeaveId | ID retourne par troisieme demande |
| leaveTypeId | 1 sur base neuve : ANNUEL, 18 jours |

Tu remplis les IDs apres chaque POST. Aucun ID d'exemple n'est garanti sur une base existante. Pour verifier leaveTypeId depuis pgAdmin : `SELECT id,name,days_per_year FROM leave_types;`.

Pour POST/PUT/PATCH avec JSON : Body → raw → JSON. Header `Content-Type: application/json`.

Pour GET, DELETE, terminaison contrat et annulation : Body → none.

Les requetes ci-dessous sont separees : tu peux cliquer Send pour chacune. Les creations doivent preceder les requetes qui utilisent leurs IDs. Si tu recommences manuellement, change noms, matricules et emails pour eviter les doublons.

## 3. POST departement

`POST {{baseUrl}}/api/departments`

```json
{"name":"Finance Test Basic","description":"Equipe finance"}
```

Attendu : 201. Copie id dans departmentId.

Refais exactement le meme POST : 409. Envoie name vide : 400.

## 4. GET liste departements

`GET {{baseUrl}}/api/departments`

Attendu : 200, liste contenant le departement. Cette liste n'est pas paginee.

## 5. GET details departement

`GET {{baseUrl}}/api/departments/{{departmentId}}`

Attendu : 200, details et employeeCount. ID inexistant : 404.

## 6. PUT departement

`PUT {{baseUrl}}/api/departments/{{departmentId}}`

```json
{"name":"Finance Test Basic Modifie","description":"Description modifiee"}
```

Attendu : 200. Essaie ensuite de lui donner le nom exact d'un autre departement existant : 409.

## 7. POST employes : manager puis employe

Meme endpoint pour les deux creations : `POST {{baseUrl}}/api/employees`.

Manager :

```json
{
  "matricule":"MGR-BASIC-100",
  "firstName":"Sara",
  "lastName":"Amrani",
  "email":"sara.basic100@example.com",
  "position":"Manager",
  "hireDate":"2020-01-01",
  "departmentId":"{{departmentId}}",
  "managerId":null
}
```

Attendu : 201. Copie id dans managerId.

Employe :

```json
{
  "matricule":"EMP-BASIC-100",
  "firstName":"Amine",
  "lastName":"Bennani",
  "email":"amine.basic100@example.com",
  "position":"Comptable",
  "hireDate":"2024-01-01",
  "departmentId":"{{departmentId}}",
  "managerId":"{{managerId}}"
}
```

Attendu : 201. Copie id dans employeeId. Les variables sont entre guillemets dans JSON pour pouvoir les substituer simplement avec Postman ; les champs Java sont des Long.

Renvoie meme matricule/email : 409. Utilise departmentId inexistant ou managerId inexistant : 404. hireDate future : 400.

## 8. PUT employe

`PUT {{baseUrl}}/api/employees/{{employeeId}}`

Reprends body de l'employe et change position en `Comptable senior`. Attendu : 200.

Mets managerId a employeeId : 400. Reprends matricule/email d'un autre employe : 409. `managerId: null` retire le manager : remets ensuite le bon manager pour poursuivre les tests.

## 9. GET details employe

`GET {{baseUrl}}/api/employees/{{employeeId}}`

Attendu : 200, informations de l'employe et de ses relations dans le DTO.

## 10. GET recherche, pagination et tri

`GET {{baseUrl}}/api/employees?page=0&size=10&sort=lastName,asc`

`GET {{baseUrl}}/api/employees?name=Bennani&departmentId={{departmentId}}&page=0&size=10`

Attendu : 200. Resultats dans content ; totalElements indique le nombre total. page=0 est la premiere page. name filtre lastName sans tenir compte de la casse, et les deux filtres se combinent. Un sort non autorise renvoie 400.

## 11. GET equipe manager

`GET {{baseUrl}}/api/employees/manager/{{managerId}}/team`

Attendu : 200, liste contenant Amine. Les equipes des sous-managers ne sont pas parcourues recursivement.

## 12. POST contrat

`POST {{baseUrl}}/api/contracts`

```json
{
  "employeeId":"{{employeeId}}",
  "type":"CDD",
  "startDate":"2026-01-01",
  "endDate":"2026-12-31",
  "grossSalary":12000
}
```

Attendu : 201. Copie id dans contractId. Refaire un contrat pour le meme employe : 409.

Pour tester les dates sans rencontrer d'abord le conflit de contrat actif, termine ce contrat ou utilise un autre employe sans contrat actif. CDD/STAGE sans endDate : 400 ; endDate avant startDate : 400 ; employeeId inexistant : 404.

## 13. GET historique contrats

`GET {{baseUrl}}/api/contracts/employee/{{employeeId}}`

Attendu : 200. Contrats actifs et termines, tries par startDate decroissant.

## 14. PATCH terminer contrat

`PATCH {{baseUrl}}/api/contracts/{{contractId}}/terminate`

Sans body. Attendu : 200, active=false, endDate=date du jour. Refaire : 409. Terminer un contrat avant son debut : 400. Adapte startDate si tu executes ces exemples avant 2026.

## 15. GET solde et anciennete

`GET {{baseUrl}}/api/leaves/employee/{{employeeId}}/balance`

Attendu : 200. Pour un nouvel employe sans demandes : ANNUEL=18 et EXCEPTIONNEL=10. seniority est une duree ISO telle que P2Y9M29D, calculee a la date d'execution.

Le champ year indique l'annee courante. Les exemples de demandes ci-dessous sont en 2026 ; pour observer leur impact dans ce solde, execute les tests en 2026 ou remplace les dates par des jours de semaine de l'annee courante.

## 16. POST demande de conge

`POST {{baseUrl}}/api/leaves`

```json
{
  "employeeId":"{{employeeId}}",
  "leaveTypeId":"{{leaveTypeId}}",
  "startDate":"2026-11-02",
  "endDate":"2026-11-04",
  "reason":"Repos annuel"
}
```

Attendu : 201, EN_ATTENTE, workingDays=3. Copie id dans leaveId. En 2026, solde ANNUEL devient 15, meme avant approbation.

Refaire memes dates : 409 (chevauchement). Dates inversees : 400. Type inexistant : 404. Samedi 2026-11-07 au dimanche 2026-11-08 : 400, aucun jour ouvre.

Pour tester solde insuffisant, utilise une autre periode sans chevauchement, par exemple du 2026-12-01 au 2026-12-31 avec ANNUEL : 409, car cette periode depasse le quota.

## 17. GET demandes employe

`GET {{baseUrl}}/api/leaves/employee/{{employeeId}}`

`GET {{baseUrl}}/api/leaves/employee/{{employeeId}}?status=EN_ATTENTE`

Attendu : 200, demandes triees par date de debut decroissante. Les statuts possibles sont EN_ATTENTE, APPROUVEE, REFUSEE et ANNULEE.

## 18. GET demandes en attente de l'equipe

`GET {{baseUrl}}/api/managers/{{managerId}}/leave-requests/pending`

Attendu : 200, contenant la demande leaveId. Demandes d'une autre equipe ou deja decidees : absentes.

## 19. PATCH decision manager

`PATCH {{baseUrl}}/api/managers/leave-requests/{{leaveId}}/decision?managerId={{managerId}}`

```json
{"status":"APPROUVEE","comment":"Conge valide"}
```

Attendu : 200, APPROUVEE. En 2026, solde reste 15, il ne descend pas une deuxieme fois. Refaire decision sur meme demande : 409.

Avant cette approbation, tu peux essayer :

- meme URL sans managerId : 400 ;
- managerId d'un employe existant qui n'est pas le manager du demandeur : 400 ;
- managerId inexistant : 404 ;
- body `{"status":"REFUSEE","comment":""}` : 400 ;
- body `{"status":"ANNULEE"}` : 400.

Pour tester un refus reussi : cree une troisieme demande sur 2026-11-16 → 2026-11-17, copie id dans refusalLeaveId, puis :

`PATCH {{baseUrl}}/api/managers/leave-requests/{{refusalLeaveId}}/decision?managerId={{managerId}}`

```json
{"status":"REFUSEE","comment":"Presence necessaire"}
```

Attendu : 200, REFUSEE. Les deux jours reserves sont liberes.

Le controle de solde avant approbation est couvert par des tests unitaires, notamment si d'autres reservations ont epuise le quota. Par les endpoints normaux, les reservations EN_ATTENTE empechent deja de surconsommer le quota a la creation ; inutile de modifier la base manuellement pour ce test.

## 20. PATCH annuler demande

Cree une deuxieme demande via POST /api/leaves : memes champs mais dates 2026-11-09 → 2026-11-10. Copie id dans cancelLeaveId.

`PATCH {{baseUrl}}/api/leaves/{{cancelLeaveId}}/cancel?employeeId={{employeeId}}`

Sans body. Attendu : 200, ANNULEE ; les deux jours reserves sont liberes.

Essaie employeeId d'un autre employe existant sur une demande en attente : 400. Essaie d'annuler leaveId deja APPROUVEE : 409. Refaire annulation de cancelLeaveId : 409.

## 21. DELETE departement

`DELETE {{baseUrl}}/api/departments/{{departmentId}}`

Sans body. Attendu : 409, car Sara et Amine appartiennent au departement.

Pour tester 204 : cree un departement vide `{"name":"Temp Basic Delete"}`, recupere son id, puis DELETE /api/departments/ID_RETOURNE. Refaire suppression : 404.

## 22. Ordre conseille

Departement → manager → employe → contrat → historique → terminaison → solde → demande → liste employe → pending manager → decision → nouvelle demande → annulation → refus sur autre demande → solde final → suppression departement vide.

Pour automatiser sans saisir les IDs, importe AtlasRH.postman_collection.json et utilise Collection Runner. Pour comprendre, commence par les requetes manuelles de ce document.

## 23. Verification realisee

- Compilation et packaging du JAR : reussis.
- 19 tests unitaires : reussis, dont 9 tests de decision manager.
- 36 scenarios HTTP de la collection actualisee : reussis sur PostgreSQL de test.
- La base de test est separee de atlasrh_db ; les tests automatiques n'ont pas modifie les donnees de cette base principale.
