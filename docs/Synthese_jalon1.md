## Contexte

Le groupe 10 développe un système Java de gestion de tickets d’assistance inspiré d’un outil de type GLPI.

Le dépôt contient déjà certains éléments des jalons suivants, notamment `Catalogue`, `RegistreTicketsEnMemoire` et l’historique des tickets. Cette synthèse se limite aux éléments évalués au Jalon 1.

## Classes et responsabilités

| Classe / type | Responsabilité |
|---|---|
| `Personne` | Regrouper les informations communes aux personnes |
| `Utilisateur` | Représenter l’auteur d’un ticket |
| `Technicien` | Représenter la personne affectée au traitement |
| `Lieu` | Représenter le lieu concerné |
| `Ticket` | Regrouper les informations communes et contrôler le cycle du ticket |
| `TicketIncident` | Représenter un incident sur un équipement |
| `TicketDemandeService` | Représenter une demande de service |
| `EtatTicket` | Définir les états possibles |
| `Priorite` | Définir les priorités possibles |
| `Assignable` | Définir le contrat d’affectation |
| `TransitionInvalideException` | Signaler une transition interdite |

## Invariants

`Personne` et `Lieu` possèdent des attributs privés et `final` sans setter.

Les constructeurs refusent les données obligatoires invalides.

Un ticket possède un numéro positif et immuable, une priorité, un auteur et un lieu valides. Il est créé dans l’état `OUVERT` et son évolution est contrôlée par les méthodes métier.

## Tests manuels

Six essais vérifient les invariants :

1. numéro de ticket inférieur ou égal à zéro ;
2. titre vide ;
3. priorité `null` ;
4. auteur `null` ;
5. email vide ;
6. numéro de lieu inférieur ou égal à zéro.

Les six essais affichent `REUSSI`.

Des essais complémentaires vérifient le changement de priorité, le cycle `OUVERT → EN_COURS → RESOLU` et plusieurs transitions interdites.

La compilation complète du projet et l’exécution de `Main` ont été vérifiées avec succès avant la remise.