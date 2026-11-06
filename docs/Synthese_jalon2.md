Bonjour Dr Leye,

Voici la remise du Jalon 2 du projet POO pour le groupe 10, composé de BAMAZI Yesuwaba Esther, LOUE Rayan et KABORE Cedric.

**Dépôt GitHub :** https://github.com/EstherBamazi/poo-GestionTicketsAssistance-groupe10

**Tag de remise :** `jalon-2.1`

L'ancien tag `jalon-2`, créé lors d'un précédent TD, a été conservé conformément à votre demande. Nous utilisons donc le nouveau tag `jalon-2.1` pour cette remise.

## 1. Évolutions du modèle

Depuis le Jalon 1, le modèle a été enrichi par l'héritage, le polymorphisme, la composition et les interfaces.

### Héritage justifié

Deux relations d'héritage principales structurent le modèle :

- `Utilisateur` et `Technicien` héritent de la classe abstraite `Personne`, car un utilisateur et un technicien sont tous deux des personnes. Les attributs communs `numero`, `nom` et `email` sont ainsi définis une seule fois.
- `TicketIncident` et `TicketDemandeService` héritent de la classe abstraite `Ticket`, car ils représentent deux catégories de tickets partageant les mêmes informations et règles de traitement.

Ces relations respectent le principe « est un » et évitent la duplication du code.

### Composition et associations

La composition est représentée par l'historique contenu dans chaque `Ticket`.

Un ticket possède une `List<Evenement>`. Chaque événement mémorise une action et l'état obtenu après celle-ci. Les événements sont créés et conservés par le ticket, qui contrôle leur ajout et leur consultation.

Cette première implémentation constitue la base de notre contrainte complémentaire « historique obligatoire », qui sera approfondie au Jalon 4.

Les relations entre `Ticket`, `Utilisateur`, `Lieu` et `Technicien` sont en revanche des **associations**. Ces objets peuvent exister indépendamment d'un ticket et être référencés par plusieurs tickets.

**Remarque sur le diagramme de classes :** La composition entre `Ticket` et `Evenement` est représentée dans le diagramme actuel (`Ticket` 1 — 0..* `Evenement`). Son architecture reste cependant en cours de finalisation, notamment pour isoler la contrainte complémentaire du noyau métier. Le diagramme sera actualisé lors de cette évolution.

### Interface

La classe abstraite `Ticket` implémente l'interface `Assignable`, qui définit les opérations `assigner()`, `getTechnicien()` et `estAssigne()`.

Cette interface représente une capacité d'affectation, et non une relation de composition.

## 2. Polymorphisme

La classe abstraite `Ticket` déclare la méthode `delaiCibleHeures()`.

Ses deux sous-classes la redéfinissent :

- `TicketIncident` retourne 4 heures ;
- `TicketDemandeService` retourne 24 heures.

Dans `Main`, les deux objets sont manipulés à travers des références de type `Ticket`.

Java vérifie à la compilation que la méthode existe dans `Ticket`, puis appelle à l'exécution l'implémentation correspondant à la classe réelle de l'objet. C'est la liaison dynamique.

Le programme ne nécessite donc aucun test de type pour choisir le délai cible.

## 3. Invariants

Les invariants du Jalon 1 sont conservés :

- `Personne` et `Lieu` possèdent des attributs privés et `final`, sans setter. Ces objets sont immuables, ce qui permet le partage de références sans risque de modification par alias.
- Dans `Ticket`, le numéro est strictement positif et immuable. Le titre et la description sont non nuls et non vides. La priorité, l'auteur et le lieu sont non nuls.
- Un ticket est créé à l'état `OUVERT`. Les changements d'état, de priorité et d'affectation sont contrôlés par les méthodes métier.

De nouveaux invariants sont également appliqués :

- **`Evenement` :** l'action doit être non nulle et non vide, et l'état après l'action doit être non nul.
- **Historique du ticket :** les événements sont conservés dans leur ordre d'ajout. Une opération refusée ne doit pas ajouter d'événement et la liste interne n'est pas directement modifiable depuis l'extérieur.
- **`RegistreTicketsEnMemoire` :** la clé et le ticket doivent être non nuls. La clé doit correspondre au numéro du ticket. Un doublon est refusé sans remplacement de l'objet existant.

Les classes utilitaires de mesure et les exceptions ne possèdent pas d'état métier nécessitant des invariants supplémentaires.

## 4. Tests manuels cumulés

Les essais sont réalisés dans `Main`.

| N° | Essai | Résultat attendu |
|---|---|---|
| 1 | Numéro de ticket inférieur ou égal à zéro | Refus |
| 2 | Titre vide | Refus |
| 3 | Priorité nulle | Refus |
| 4 | Auteur nul | Refus |
| 5 | Email vide | Refus |
| 6 | Numéro de lieu inférieur ou égal à zéro | Refus |
| 7 | `TicketIncident` manipulé via une référence `Ticket` | Délai de 4 h |
| 8 | `TicketDemandeService` manipulé via une référence `Ticket` | Délai de 24 h |
| 9 | Cycle normal d'un ticket | `OUVERT → EN_COURS → RESOLU` |
| 10 | Résolution d'un ticket ouvert | Exception et état inchangé |
| 11 | Prise en charge d'un ticket déjà résolu | Exception et état inchangé |
| 12 | Modification de la priorité d'un ticket résolu | Exception, priorité et historique inchangés |

Ces essais vérifient les invariants, les transitions, le polymorphisme et l'absence de modification après certaines opérations refusées.

Des essais complémentaires contrôlent également l'affectation d'un technicien, l'historique des actions et les opérations du registre.

**Le projet est compilé et exécuté en Java 21.**

Cordialement,

**BAMAZI Yesuwaba Esther**  
Pour le groupe 10