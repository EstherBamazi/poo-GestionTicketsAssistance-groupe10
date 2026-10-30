Bonjour Dr Leye, CI-Apres la synthese pour le groupe 10 constitue de BAMAZI Y. Esther, LOUE Rayan, KABORE Cedric

Contexte
Le groupe 10 développe un système de gestion de tickets d’assistance inspiré d’un outil de type GLPI.

Le dépôt contient déjà certains éléments des jalons suivants et de TD, notamment Catalogue, RegistreTicketsEnMemoire et l’historique des tickets. Cette synthèse se limite aux éléments évalués au Jalon 1.

Classes et responsabilités
Classe / type	Responsabilité
Personne	Regrouper les informations communes aux personnes
Utilisateur	Représenter l’auteur d’un ticket
Technicien	Représenter la personne affectée au traitement
Lieu	Représenter le lieu concerné
Ticket	Regrouper les informations communes et contrôler le cycle du ticket
TicketIncident	Représenter un incident sur un équipement
TicketDemandeService	Représenter une demande de service
EtatTicket	Définir les états possibles
Priorite	Définir les priorités possibles
Assignable	Définir le contrat d’affectation
TransitionInvalideException	Signaler une transition interdite
Encapsulation, mutabilité et alias
Les attributs sont déclarés private afin d’empêcher leur modification directe.

Les informations de Personne et de Lieu sont final et aucune méthode de modification n’est fournie. Ces objets sont donc immuables : plusieurs tickets peuvent partager une même référence vers un Utilisateur ou un Lieu sans risque de modification par alias.

Dans Ticket, le numéro est strictement positif et immuable. Le titre et la description doivent être non nuls et non vides. La priorité, l’auteur et le lieu doivent être non nuls.

Un ticket est créé dans l’état OUVERT. Son état, sa priorité et son affectation évoluent uniquement par les méthodes métier prévues, qui vérifient les invariants avant toute modification.

Tests manuels
Six essais vérifient les invariants :

numéro de ticket inférieur ou égal à zéro ;

titre vide ;

priorité null ;

auteur null ;

email vide ;

numéro de lieu inférieur ou égal à zéro.

Les six essais affichent REUSSI.

Des essais complémentaires vérifient le changement de priorité, le cycle OUVERT → EN_COURS → RESOLU et plusieurs transitions interdites.

La compilation complète du projet et l’exécution de Main ont été vérifiées avec succès avant la remise.

