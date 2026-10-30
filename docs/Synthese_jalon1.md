## Contexte du projet

Dans le cadre du projet de Programmation Orientée Objet pour la gestion de tickets d’assistance, notre groupe développe une application inspirée du fonctionnement d’un système de type GLPI.

Le dépôt contient déjà certaines fonctionnalités correspondant à des jalons ultérieurs, car nous avons progressivement intégré les notions étudiées dans les différents TD. Cette synthèse se concentre toutefois sur les éléments demandés pour le Jalon 1 : les classes de base, leurs responsabilités, l’encapsulation, les constructeurs, les invariants et les premiers essais.

Le système permet à un utilisateur de déclarer un incident ou une demande de service en décrivant le lieu de celui-ci, ces acteurs...

## Classes et responsabilités

| Classe / type | Responsabilité | Invariants principaux |
|---|---|---|
| `Personne` | Regrouper les données communes aux personnes | numéro > 0, nom non vide, email non vide |
| `Utilisateur` | Représenter l’auteur d’un ticket | hérite des invariants de `Personne` |
| `Technicien` | Représenter la personne affectée au traitement | hérite des invariants de `Personne` |
| `Lieu` | Représenter le lieu concerné par la demande | numéro > 0, nom et bâtiment non vides |
| `Ticket` | Regrouper les informations communes et contrôler les transitions | numéro > 0, titre/description/auteur/lieu/priorité non nuls, état initial `OUVERT` |
| `TicketIncident` | Représenter un incident sur un équipement | équipement renseigné |
| `TicketDemandeService` | Représenter une demande de service | service demandé renseigné |
| `EtatTicket` | Limiter les états possibles | `OUVERT`, `EN_COURS`, `RESOLU` |
| `Priorite` | Limiter les priorités possibles | `BASSE`, `MOYENNE`, `HAUTE` |
| `TransitionInvalideException` | Signaler une transition métier interdite | pas vraiment d’invariant métier propre |
| `Assignable` | Définir le contrat d’affectation d’un technicien | contrat, pas d’état propre |

## Encapsulation, mutabilité et alias

Les attributs des différentes classes sont déclarés `private` afin d'empêcher leur modification directe depuis l'extérieur de la classe.

Dans `Ticket`, les attributs définissant son identité et ses informations principales, notamment le numéro, le titre, la description, l’auteur et le lieu, sont déclarés `final` lorsqu’ils ne doivent plus être réaffectés après la construction. Aucun setter n’est fourni pour les données qui doivent rester immuables.

Il faut cependant distinguer l’immutabilité d’une référence de celle de l’objet référencé. Par exemple, un attribut `final Utilisateur auteur` garantit que le ticket conservera la référence vers le même utilisateur, mais ne rend pas nécessairement l’objet `Utilisateur` lui-même immuable. Les opérations publiques des différentes classes doivent donc continuer à préserver leurs propres invariants.

Les attributs modifiables, comme l’état ou la priorité, ne sont pas exposés à une modification directe. Leur évolution passe par des méthodes métier qui contrôlent la validité de la modification avant de l’effectuer.

## Invariants

Un invariant est une propriété qui doit être vraie après la construction d’un objet puis après chaque opération publique.

Pour les objets de type `Personne`, le numéro doit être strictement positif et les informations obligatoires, comme le nom et l’adresse électronique, doivent être renseignées.

Pour les objets de type `Lieu`, le numéro doit être strictement positif et les informations nécessaires à l’identification du lieu doivent être renseignées.

Pour les objets de type `Ticket`, le numéro doit être strictement positif et les informations obligatoires telles que le titre, la description, la priorité, l’auteur et le lieu doivent être renseignées. Le numéro du ticket ne doit pas pouvoir être modifié après sa création.

L’utilisation des enums `Priorite` et `EtatTicket` limite les valeurs possibles aux constantes définies dans le programme. Le constructeur vérifie néanmoins qu’une valeur obligatoire de type enum n’est pas `null`.

Enfin, l’évolution de l’état d’un ticket est contrôlée par des méthodes de transition. Un ticket est créé dans l’état `OUVERT`, peut passer à `EN_COURS`, puis à `RESOLU`. Une transition interdite est refusée et doit laisser le ticket dans son état antérieur.

La priorité peut également être modifiée par une méthode dédiée. Une priorité `null` est refusée et la priorité d’un ticket résolu ne peut plus être modifiée.

## Essais

Les essais sont réalisés dans la classe `Main`.

Ils couvrent à la fois des cas nominaux et des cas d’échec. Les constructeurs sont testés avec des données valides puis avec des données ne respectant pas les invariants, par exemple un identifiant non positif ou une information obligatoire absente.

Les méthodes modifiant l’état d’un ticket sont également testées afin de vérifier le cycle normal ainsi que le refus des transitions interdites.

Enfin, `changerPriorite` est vérifiée dans trois situations : modification valide, refus d’une priorité `null` et refus d’une modification sur un ticket déjà résolu. Après chaque opération refusée, nous vérifions que la priorité du ticket est restée inchangée.

Ces essais permettent donc de vérifier non seulement que les erreurs attendues sont détectées, mais également qu’une opération refusée ne laisse pas l’objet dans un état partiellement modifié.
Liste des tests effectués:
1. Création d’un utilisateur valide : réussit.
2. Création d’un utilisateur avec numéro négatif : IllegalArgumentException.
3. Création d’un lieu avec nom vide : IllegalArgumentException.
4. Création d’un ticket avec priorité nulle : IllegalArgumentException.
5. Modification valide de la priorité : la nouvelle priorité est conservée.
6. Tentative de modification de la priorité d’un ticket résolu :
   TransitionInvalideException et priorité inchangée.