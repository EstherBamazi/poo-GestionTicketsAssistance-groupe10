# Sujet C : Gestion de tickets d'assistance

Appliqué au cas du système GLPI.
POO en Java, ECUE IIAA511, Dr Babacar LEYE, 2iE, Semestre 5 IIAA.

## Membres du groupe 10

- BAMAZI Yesuwaba Esther
- LOUE Rayan
- KABORE Cedric

## Lancer le programme

Ouvrir le projet dans IntelliJ IDEA, puis exécuter la classe `Main`
(bouton Run vert). L'évolution des tickets s'affiche dans la console.

## Structure du dépôt

```
src/     classes Java et Main
docs/    diagramme-classes.png
```

## Le besoin

Un système de gestion de tickets d'assistance de type GLPI qui permet
aux utilisateurs de déclarer un incident ou une demande de service
concernant leur lieu de travail, et de suivre l'avancement de leur
prise en charge.

Actions principales :

1. créer un utilisateur, un lieu et un technicien
2. créer un ticket, ouvert à sa création
3. prendre le ticket en charge en lui affectant un technicien
4. résoudre le ticket
5. afficher l'état courant d'un ticket

## Travail de conception

### Les objets du système

Prenons le cas de Sidibé, étudiant disposant d'un ordinateur prêté par
l'université. Il constate un problème d'affichage en salle B6.5 pendant
un projet Java. On représente ce cas par :

- un `Utilisateur` : l'auteur de la déclaration
- un `Lieu` : la salle et son bâtiment
- un `Ticket` : le numéro, le titre, la description, la priorité et l'état
- un `Technicien` : celui qui prendra le ticket en charge

### Les classes retenues et leur rôle

| Classe | Rôle |
|---|---|
| `Personne` | classe abstraite, porte les données communes à toute personne du système |
| `Utilisateur` | personne qui déclare un ticket |
| `Technicien` | personne qui prend un ticket en charge |
| `Lieu` | localisation d'un incident |
| `Ticket` | classe abstraite, porte les données et le cycle de vie d'un ticket |
| `TicketIncident` | ticket concernant un équipement en panne, délai cible 4 h |
| `TicketDemandeService` | ticket demandant un service, délai cible 24 h |
| `Main` | programme de démonstration |

### Attributs et visibilité

Tous les attributs sont **privés**. Ceux qui sont fixés à la création et
ne changent plus sont en plus déclarés `final`.

- `Personne` : `numero` (int), `nom` (String), `email` (String)
- `Lieu` : `numero` (int), `nom` (String), `batiment` (String)
- `Ticket` : `numero` (int), `titre` (String), `description` (String),
  `priorite` (String), `etat` (String), `auteur` (Utilisateur),
  `lieu` (Lieu), `technicien` (Technicien)
- `TicketIncident` ajoute `equipementConcerne` (String)
- `TicketDemandeService` ajoute `serviceDemande` (String)

### Construction des objets

Chaque constructeur reçoit toutes les données obligatoires et valide
ses paramètres : un numéro doit être strictement positif, un texte ne
doit être ni nul ni vide. Une valeur invalide lève une
`IllegalArgumentException` et l'objet n'est pas créé.

Deux valeurs ne sont pas reçues mais fixées par la classe `Ticket`
elle-même : l'état, qui vaut `"OUVERT"` à la création, et le technicien,
qui vaut `null` tant que personne n'a pris le ticket en charge.

### Les méthodes

- `prendreEnCharge(Technicien)` : affecte un technicien et fait passer
  l'état de `"OUVERT"` à `"EN COURS"`
- `resoudre()` : fait passer l'état de `"EN COURS"` à `"RESOLU"`
- `delaiCibleHeures()` : abstraite dans `Ticket`, redéfinie dans chaque
  sous-classe
- `role()` : abstraite dans `Personne`, redéfinie dans `Utilisateur` et
  `Technicien`
- `toString()` : redéfinie dans toutes les classes, appelée
  automatiquement par `System.out.println`
- les accesseurs utiles, sans aucun setter

## Diagramme de classes

Voir `docs/diagramme-classes.png`.

## Deux choix expliqués

**1. Deux hiérarchies avec classes abstraites.**
`Ticket` est abstraite et déclare `delaiCibleHeures()` abstraite : un
ticket générique n'a pas de sens métier, et chaque type impose son
propre délai. `Personne` est abstraite pour la même raison, et pour que
les attributs communs à `Utilisateur` et `Technicien` ne soient écrits
qu'une seule fois. Le programme principal manipule des `Ticket` et des
`Personne` sans jamais tester leur classe réelle.

**2. Des associations par objets plutôt que par du texte.**
Un `Ticket` ne stocke pas le nom de son auteur sous forme de chaîne, il
contient directement l'objet `Utilisateur`, l'objet `Lieu` et, après
prise en charge, l'objet `Technicien`. Le ticket interroge lui-même ses
objets liés, et `prendreEnCharge` reçoit l'objet `Technicien` concerné.

## Difficultés rencontrées

- Faire communiquer les classes entre elles. Résolu par les associations
  entre objets plutôt que par recopie de données.
- Garantir que les états se succèdent dans le bon ordre. Résolu en
  plaçant une condition sur l'état courant dans chaque méthode de
  transition, plutôt que d'affecter l'état directement.
- Un même humain jouant les deux rôles devrait aujourd'hui être
  représenté par deux objets distincts, que rien ne relie. Une version
  ultérieure utiliserait une classe `Personne` concrète portant un ou
  plusieurs rôles, plutôt que deux sous-classes.

## Questions pour la revue

**Peut-on modifier directement les données depuis `Main` ?**
Non. Tous les attributs sont privés, et ceux qui ne changent jamais sont
`final`. Toute lecture passe par un accesseur, toute modification par
une méthode de la classe.

**Les constructeurs créent-ils des objets complets ?**
Oui. Chaque constructeur reçoit toutes les données obligatoires, valide
numéros et textes, et refuse une valeur invalide. Un ticket naît
toujours à l'état `"OUVERT"`, sans technicien affecté.

**Les noms des méthodes correspondent-ils aux actions du système ?**
Oui : `prendreEnCharge(Technicien)`, `resoudre()`, `delaiCibleHeures()`.
L'affichage passe par `toString()` redéfinie.

**Peut-on modifier l'état autrement qu'en utilisant les méthodes prévues ?**
Non. `etat` est privé et n'a pas de setter. Seules `prendreEnCharge` et
`resoudre` le modifient.

**Les changements suivent-ils l'ordre demandé ?**
Oui. `prendreEnCharge` n'agit que sur un ticket `"OUVERT"`, `resoudre`
que sur un ticket `"EN COURS"`. Un appel hors séquence laisse l'état
inchangé, ce que `Main` démontre explicitement.

**S1. La relation classe fille / classe mère signifie-t-elle bien « est un » ?**
Oui : un incident est un ticket, une demande de service est un ticket,
un utilisateur est une personne, un technicien est une personne.

**S2. Les attributs communs sont-ils écrits une seule fois ?**
Oui. `numero`, `titre`, `description`, `priorite`, `etat`, `auteur`,
`lieu` et `technicien` sont déclarés dans `Ticket`. `numero`, `nom` et
`email` sont déclarés dans `Personne`. Les sous-classes ne déclarent que
ce qu'elles ajoutent.

**S3. Le programme principal utilise-t-il le type de la classe mère sans rechercher la classe réelle de l'objet ?**
Oui. `Main` déclare `Ticket ticketSidibe`, `Ticket[] tickets` et
`Personne[] personnes`, et n'utilise aucun `instanceof`. Le délai cible
affiché varie selon l'objet réel, pas selon le type déclaré.

**S4. Quelle règle appliquer maintenant au projet ?**
Placer dans la classe mère tout ce qui est commun, et ne laisser dans
les classes filles que ce qui varie réellement d'un type à l'autre.

## Sources consultéesgit add .
git status

- draw.io pour la construction du diagramme de classes
- PlantUML pour les conventions d'écriture des diagrammes de classes
- Support de cours IIAA511, sections 2.5 (invariants de représentation)
  et 4 (héritage et polymorphisme)