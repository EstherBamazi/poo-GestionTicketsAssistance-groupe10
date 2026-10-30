# Sujet C : Gestion de tickets d'assistance

Application au cas d'un système de type GLPI.

## Membres du groupe 10

- BAMAZI Yesuwaba Esther
- LOUE Rayan
- KABORE Cedric

## Compiler et lancer

Dans IntelliJ IDEA : ouvrir le projet puis exécuter la classe `Main`.

En ligne de commande, depuis la racine du dépôt :

```bash
javac -d out src/*.java
java -cp out Main
```

## Structure du dépôt

```text
src/     classes Java et Main
docs/    diagramme-classe.png, cahier des charges, rapport et synthèse
```

## Le besoin

Le projet représente un système de gestion de tickets d'assistance de type GLPI permettant aux utilisateurs d'un établissement universitaire de déclarer un incident ou une demande de service concernant un lieu, puis de suivre son traitement.

Un ticket peut être affecté à un technicien, pris en charge puis résolu.

Le système conserve également un historique des principales actions réalisées sur chaque ticket.

## Actions principales

### Déclarer une demande

1. créer un utilisateur et un lieu ;
2. créer un ticket, automatiquement ouvert à sa création ;
3. affecter un technicien ;
4. prendre le ticket en charge.

### Traiter la demande

5. modifier éventuellement sa priorité ;
6. résoudre le ticket ;
7. consulter son état et son historique.

## Description du contexte

Nous suivons notamment Sidibé, étudiant rencontrant un problème d'affichage en salle B6.5.

Ce cas est représenté par :

- un `Utilisateur` : la personne qui déclare le ticket ;
- un `Lieu` : l'endroit où le problème est rencontré ;
- un `Ticket` : la demande contenant un numéro, un titre, une description, une priorité et un état ;
- un `Technicien` : la personne affectée au traitement du ticket.

## Les classes retenues et leur rôle

| Classe / type | Rôle |
|---|---|
| `Personne` | Classe abstraite regroupant les attributs communs `numero`, `nom` et `email` |
| `Utilisateur` | Personne qui déclare un ticket |
| `Technicien` | Personne pouvant être affectée à un ticket |
| `Lieu` | Localisation concernée par le ticket |
| `Assignable` | Interface définissant la capacité à recevoir un technicien |
| `EtatTicket` | Enum contenant `OUVERT`, `EN_COURS` et `RESOLU` |
| `Priorite` | Enum contenant `BASSE`, `MOYENNE` et `HAUTE` |
| `Ticket` | Classe abstraite regroupant les données communes et le cycle de vie d'un ticket |
| `TicketIncident` | Ticket concernant un équipement, délai cible de 4 h |
| `TicketDemandeService` | Ticket concernant une demande de service, délai cible de 24 h |
| `Evenement` | Représente une action conservée dans l'historique d'un ticket |
| `TransitionInvalideException` | Exception personnalisée signalant une transition métier interdite |
| `Catalogue<T,K>` | Interface générique définissant les opérations d'un catalogue |
| `RegistreTicketsEnMemoire` | Stockage et recherche des tickets en mémoire |
| `ComparaisonRecherche` | Comparaison de la recherche dans une liste et une Map |
| `Main` | Démonstration du fonctionnement et essais |

## Attributs et visibilité

Tous les attributs sont privés.

Les données fixées lors de la création et qui ne doivent plus changer sont également déclarées `final`.

Principaux attributs :

- `Personne` : `numero`, `nom`, `email`
- `Lieu` : `numero`, `nom`, `batiment`
- `Ticket` : `numero`, `titre`, `description`, `priorite`, `etat`, `auteur`, `lieu`, `technicien`
- `TicketIncident` : `equipementConcerne`
- `TicketDemandeService` : `serviceDemande`
- `Ticket` possède également un historique d'objets `Evenement`

La priorité utilise le type `Priorite` et non une chaîne de caractères.

`Utilisateur` et `Technicien` ne redéclarent pas les attributs communs puisqu'ils héritent de `Personne`.

## Construction des objets

Chaque constructeur reçoit les informations nécessaires et vérifie ses paramètres.

Un numéro doit être strictement positif.

Un texte obligatoire ne doit être ni `null` ni vide.

Pour un ticket :

- le titre et la description doivent être non nuls et non vides ;
- la priorité doit être non nulle ;
- l'auteur doit être non nul ;
- le lieu doit être non nul.

Une donnée de construction invalide provoque une `IllegalArgumentException`.

Deux informations sont fixées automatiquement par `Ticket` :

- l'état initial vaut `EtatTicket.OUVERT` ;
- aucun technicien n'est affecté à la création.

## Encapsulation, mutabilité et alias

`Personne` et `Lieu` possèdent uniquement des attributs privés et `final` et ne fournissent aucun setter.

Ils sont donc immuables dans notre modèle.

Plusieurs tickets peuvent ainsi partager une même référence vers un `Utilisateur` ou un `Lieu` sans risque qu'un ticket modifie indirectement les données de cet objet par alias.

Le numéro, le titre, la description, l'auteur et le lieu d'un ticket sont également fixés lors de sa construction.

Les données qui peuvent évoluer, notamment l'état, la priorité et le technicien, sont modifiées uniquement par les méthodes métier prévues.

## Les méthodes principales

### Affectation

```java
assigner(Technicien technicien)
```

Affecte un technicien au ticket.

- `null` est refusé avec `IllegalArgumentException` ;
- une affectation sur un ticket résolu est refusée avec `TransitionInvalideException` ;
- l'affectation ne change pas l'état du ticket.

### Prise en charge

```java
prendreEnCharge()
```

Fait passer un ticket de `OUVERT` à `EN_COURS`.

L'opération est refusée si :

- le ticket n'est pas `OUVERT` ;
- aucun technicien n'est affecté.

### Résolution

```java
resoudre()
```

Fait passer un ticket de `EN_COURS` à `RESOLU`.

L'opération est refusée si le ticket n'est pas dans l'état `EN_COURS`.

### Modification de priorité

```java
changerPriorite(Priorite priorite)
```

Permet de modifier la priorité.

Une priorité `null` est refusée.

La priorité d'un ticket résolu ne peut plus être modifiée.

### Autres méthodes

- `delaiCibleHeures()` : abstraite dans `Ticket`, redéfinie dans chaque sous-classe ;
- `role()` : abstraite dans `Personne`, redéfinie dans `Utilisateur` et `Technicien` ;
- `toString()` : fournit l'affichage d'un objet ;
- getters nécessaires pour consulter les informations.

L'état n'a pas de setter.

Il ne peut évoluer qu'à travers `prendreEnCharge()` et `resoudre()`.

## Exceptions

`TransitionInvalideException` hérite de `RuntimeException`.

Elle représente un refus lié aux règles métier, par exemple :

- résoudre un ticket ouvert ;
- reprendre en charge un ticket déjà résolu ;
- affecter un technicien à un ticket résolu.

Les données invalides fournies aux constructeurs ou à `assigner(null)` sont signalées avec `IllegalArgumentException`.

Les contrôles sont réalisés avant toute modification afin qu'une opération refusée laisse le ticket inchangé.

## Historique obligatoire

Chaque ticket possède un historique constitué d'objets `Evenement`.

Un événement contient l'action réalisée et l'état du ticket après cette action.

Les actions tracées comprennent notamment :

- la création du ticket ;
- l'affectation d'un technicien ;
- le changement de priorité ;
- la prise en charge ;
- la résolution.

Une opération refusée ne doit pas ajouter d'événement.

La consultation de l'historique ne permet pas de modifier directement la liste interne du ticket.

## Diagramme de classes

Voir [le diagramme de classes](docs/diagramme-classe.png).

## Choix retenus et choix écartés

### Classe abstraite `Ticket`

Un ticket générique pouvant représenter plusieurs catégories, nous avons créé une classe abstraite `Ticket`.

Elle contient les informations et comportements communs.

`TicketIncident` et `TicketDemandeService` ajoutent leurs caractéristiques propres.

### Classe abstraite `Personne`

`Personne` évite de répéter les attributs `numero`, `nom` et `email` dans `Utilisateur` et `Technicien`.

### Interface `Assignable`

Recevoir un technicien est considéré comme une capacité.

L'interface contient :

```java
assigner
getTechnicien
estAssigne
```

### Exceptions plutôt que booléens pour les transitions

Les transitions interdites lèvent une `TransitionInvalideException`.

Cela permet de distinguer clairement un refus métier et d'en préciser la cause.

Les opérations du catalogue utilisent toutefois les booléens demandés dans le TD pour `ajouter` et `retirer`.

### Associations entre objets

Un ticket ne stocke pas le nom de son auteur ou de son lieu sous forme de texte.

Il contient directement des références vers :

- un `Utilisateur` ;
- un `Lieu` ;
- éventuellement un `Technicien`.

## Polymorphisme

`Main` manipule les deux catégories de tickets à travers le type abstrait `Ticket`.

Exemple :

```java
Ticket[] tickets = {ticketSidibe, ticketBernadette};

for (Ticket ticket : tickets) {
    System.out.println(ticket);
}
```

Le délai cible affiché dépend automatiquement du type réel de l'objet.

Aucun `instanceof` n'est nécessaire pour déterminer le type du ticket.

## Collections

### Catalogue et registre

`Catalogue<T,K>` définit les opérations :

```java
ajouter
chercher
retirer
lister
taille
```

`RegistreTicketsEnMemoire` implémente :

```java
Catalogue<Ticket, Integer>
```

Son stockage principal est :

```java
private final Map<Integer, Ticket> tickets = new HashMap<>();
```

Le numéro du ticket sert de clé.

### Ajouter

`ajouter` vérifie que :

- la clé n'est pas `null` ;
- le ticket n'est pas `null` ;
- la clé correspond au numéro du ticket.

Une clé déjà présente renvoie `false` sans remplacer le ticket existant.

### Chercher

`chercher` utilise `Optional<Ticket>`.

Le résultat contient le ticket s'il existe ou est vide si le numéro est absent.

### Retirer

`retirer` renvoie :

- `true` si un ticket a été retiré ;
- `false` si le numéro n'était pas présent.

### Lister

`lister` utilise `List.copyOf`.

La liste retournée ne permet donc pas d'ajouter ou retirer directement des tickets du registre.

## Comparaison des structures

| Structure | Doublons | Ordre | Accès principal | Utilisation |
|---|---|---|---|---|
| `ArrayList` | autorisés | ordre de la liste | parcours | utilisée pour les résultats, pas comme stockage principal |
| `LinkedHashSet` | refusés selon l'égalité | insertion | appartenance | non retenue |
| `HashSet` | refusés selon l'égalité | aucun ordre garanti | appartenance | utile pour l'unicité mais pas pour la recherche par numéro |
| `HashMap` | clés uniques | aucun ordre garanti | accès par clé | retenue comme stockage principal |
| `TreeMap` | clés uniques | clés triées | accès par clé | tri par numéro non nécessaire |

Une recherche par numéro dans une liste nécessite en général un parcours de complexité `O(n)`.

L'accès par clé dans une `HashMap` est en moyenne de complexité `O(1)`.

## Identité des tickets

`Ticket.equals(Object o)` compare les numéros.

`hashCode()` utilise également le numéro.

Deux tickets portant le même numéro sont donc considérés comme égaux au niveau métier, même s'ils appartiennent à deux catégories différentes.

Le numéro étant `final`, le code de hachage reste stable pendant toute la durée de vie du ticket.

## Recherche par titre

`rechercherParTitre` effectue une recherche de sous-chaîne sans distinction entre majuscules et minuscules.

Elle retourne une nouvelle liste.

Les résultats sont triés :

1. par titre ;
2. puis par numéro lorsque les titres sont équivalents.

Un mot-clé `null` est refusé.

Une recherche sans correspondance retourne une liste vide.

## Regroupement par état

`regrouperParEtat()` retourne une nouvelle Map contenant les trois états :

- `OUVERT`
- `EN_COURS`
- `RESOLU`

Chaque état est associé à une liste de tickets.

Même lorsque le registre est vide, les trois clés sont présentes et associées à des listes vides.

Modifier les listes ou la Map retournées ne modifie pas le registre.

Les objets `Ticket` eux-mêmes restent cependant partagés.

## Essais réalisés dans Main

### Tests du Jalon 1

Six essais vérifient les invariants :

1. numéro du ticket inférieur ou égal à zéro ;
2. titre vide ;
3. priorité `null` ;
4. auteur `null` ;
5. email vide ;
6. numéro du lieu inférieur ou égal à zéro.

Les six essais affichent `REUSSI`.

### Tests de priorité

Les essais vérifient :

- le refus d'une priorité `null` et la conservation de l'ancienne priorité ;
- un changement valide vers `BASSE` ;
- le refus d'un changement sur un ticket résolu ;
- l'absence de modification de l'historique après le refus.

### Tests des transitions

Les scénarios vérifient notamment :

- le cycle `OUVERT -> EN_COURS -> RESOLU` ;
- la résolution d'un ticket jamais pris en charge ;
- la reprise d'un ticket résolu ;
- la prise en charge sans technicien ;
- l'affectation de `null` ;
- l'affectation après résolution.

Les vérifications utilisent des conditions et affichent `REUSSI` ou `ECHEC`.

## P6 : essais du catalogue

Les dix essais P6 sont exécutés dans `Main`.

| Essai | Action | Résultat attendu |
|---|---|---|
| P6.1 | Consulter un registre vide | taille 0 et liste vide |
| P6.2 | Ajouter un ticket | `true` et taille 1 |
| P6.3 | Ajouter un doublon | `false`, ticket initial conservé |
| P6.4 | Chercher un numéro présent | `Optional` contenant le ticket |
| P6.5 | Chercher un numéro absent | `Optional` vide |
| P6.6 | Retirer un numéro présent | `true` |
| P6.7 | Retirer un numéro absent | `false`, registre inchangé |
| P6.8 | Modifier la liste retournée | modification refusée, registre inchangé |
| P6.9 | Rechercher `PROBLEME` puis `probleme` | même ticket retrouvé |
| P6.10 | Rechercher un mot absent | liste vide |

L'exécution actuelle affiche :

```text
P6 : 10 essais sur 10 reussis.
```

Des essais complémentaires portent sur :

- le tri ;
- le regroupement par état ;
- la protection des collections retournées ;
- les arguments invalides.

## P7 : comparaison des recherches

`ComparaisonRecherche.executer` construit :

- une `ArrayList<Ticket>` ;
- une `HashMap<Integer, Ticket>`.

Les deux structures contiennent les mêmes 10 000 tickets.

1 000 numéros présents sont ensuite recherchés dans chaque structure.

Les deux méthodes doivent retrouver les 1 000 tickets.

La comparaison utilise `System.nanoTime()`.

Les temps obtenus varient selon les exécutions, la JVM et la machine. Ils sont donc affichés à titre indicatif et ne sont pas enregistrés comme valeurs fixes dans cette documentation.

Lors des essais, la `HashMap` est nettement plus rapide pour la recherche directe par numéro.

## Difficultés rencontrées

1. Faire communiquer les classes entre elles.  
   Les associations entre objets ont été utilisées plutôt que la duplication des données.

2. Garantir l'ordre des changements d'état.  
   L'enum `EtatTicket` et les vérifications des méthodes métier contrôlent les transitions.

3. Garantir l'atomicité.  
   Les conditions sont vérifiées avant toute modification afin qu'une opération refusée laisse l'objet inchangé.

4. Travailler à plusieurs sur le même dépôt.  
   La compilation est vérifiée après les synchronisations et avant les principaux `push`.

5. Gérer les fichiers générés par l'IDE.  
   Les fichiers compilés ont été retirés du dépôt et le dossier correspondant est ignoré par Git.

## Questions pour la revue

### Peut-on modifier directement les données depuis Main ?

Non.

Les attributs sont privés.

Les informations immuables ne possèdent pas de setter.

Les autres modifications passent par les méthodes métier.

### Les constructeurs créent-ils des objets complets ?

Oui.

Les constructeurs vérifient les informations obligatoires avant de créer l'objet.

### Peut-on modifier directement l'état ?

Non.

`etat` est privé et ne possède pas de setter.

Seules les méthodes prévues peuvent le faire évoluer.

### Les changements suivent-ils l'ordre demandé ?

Oui.

Le cycle prévu est :

```text
OUVERT -> EN_COURS -> RESOLU
```

Une transition interdite provoque une `TransitionInvalideException`.

### Où utilise-t-on l'héritage ?

`Utilisateur` et `Technicien` héritent de `Personne`.

`TicketIncident` et `TicketDemandeService` héritent de `Ticket`.

### Où utilise-t-on le polymorphisme ?

`Main` manipule les deux catégories de tickets avec des références de type `Ticket`.

Le comportement dépend ensuite de l'objet réel.

### Quand choisir une classe abstraite ou une interface ?

Une classe abstraite est utilisée lorsque plusieurs classes partagent des données et comportements communs.

Une interface est utilisée pour représenter une capacité pouvant être implémentée par différentes classes.

## Contributions

| Membre | Contribution |
|---|---|
| **BAMAZI Yesuwaba Esther** | Modèle initial et encapsulation ; compilation, constructeurs et nettoyage ; gestion de la priorité ; finalisation et validation de `Main` ; registre et collections ; diagramme et documentation |
| **LOUE Rayan** | Interface `Assignable`, enum `EtatTicket`, affectation et transitions d'état |
| **KABORE Cedric** | Premiers scénarios de `Main` et essais des invariants |

Le détail des contributions est également visible dans l'historique Git.

## Recherches et usage de l'IA

Sources utilisées :

- supports de cours IIAA511 ;
- fiches TD/TP ;
- documentation Java ;
- Stack Overflow pour certaines questions techniques ;
- draw.io pour le diagramme de classes.

L'intelligence artificielle a été utilisée comme outil d'assistance pour :

- expliquer certaines notions du cours ;
- comprendre des erreurs de compilation ou d'exécution ;
- apprendre certaines commandes Git ;
- relire et restructurer la documentation ;
  

Les corrections et choix retenus ont ensuite été vérifiés dans le projet.
