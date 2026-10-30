# Rapport de conception du système de tickets d’assistance

**Sujet C : Groupe 10**  
**Auteurs : BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric**

## Introduction

Ce projet met en pratique les principales notions de programmation orientée objet étudiées en cours à travers la réalisation d’un système de gestion de tickets d’assistance.

Les besoins fonctionnels et les règles métier sont décrits dans le fichier `cahier_des_charges.md`. Le présent rapport se concentre sur la conception retenue, les choix d’implémentation, les essais réalisés et les difficultés rencontrées.

## 1. Organisation du modèle

La classe abstraite `Personne` regroupe les données communes aux utilisateurs et aux techniciens. Elle est spécialisée par `Utilisateur` et `Technicien`.

`Lieu` représente l’endroit concerné par une demande.

La classe abstraite `Ticket` centralise les données et comportements communs aux tickets. Deux classes en héritent :

- `TicketIncident`, associé à un équipement et à un délai cible de 4 heures ;
- `TicketDemandeService`, associé à un service demandé et à un délai cible de 24 heures.

Les états sont représentés par `EtatTicket` et les priorités par `Priorite`.

L’interface `Assignable` définit les opérations liées à l’affectation d’un technicien.

## 2. Encapsulation et invariants

Les attributs sont déclarés `private`.

Les données qui définissent durablement un objet sont également déclarées `final`.

C’est notamment le cas des attributs de `Personne` et de `Lieu`. Ces classes ne fournissent aucun setter et leurs objets sont donc immuables dans notre modèle.

Les constructeurs vérifient les valeurs reçues avant de créer l’objet.

Par exemple, un ticket refuse :

- un numéro inférieur ou égal à zéro ;
- un titre vide ;
- une priorité nulle ;
- un auteur nul ;
- un lieu nul.

Les données modifiables, comme la priorité ou l’état, ne sont pas exposées directement. Leur évolution passe par des méthodes contrôlées.

## 3. Héritage et polymorphisme

`Ticket` déclare :

```java id="s92j9q"
public abstract int delaiCibleHeures();
```

Chaque type concret fournit sa propre valeur.

Dans `Main`, les différents tickets peuvent être manipulés à travers des références de type `Ticket` :

```java id="mxx7cg"
Ticket[] tickets = {ticketSidibe, ticketBernadette};

for (Ticket ticket : tickets) {
    System.out.println(ticket);
}
```

Aucun test du type réel n’est nécessaire pour obtenir le délai correspondant au ticket.

## 4. Gestion du cycle de vie

Les évolutions d’état sont réalisées par les méthodes métier de `Ticket`.

`prendreEnCharge()` vérifie que le ticket est ouvert et possède un technicien avant de modifier son état.

`resoudre()` vérifie que le ticket est en cours.

Les transitions interdites produisent une `TransitionInvalideException`.

Les contrôles sont effectués avant la modification afin qu’une opération refusée laisse l’objet inchangé.

## 5. Gestion de la priorité

La priorité est représentée par :

```java id="43w5sq"
public enum Priorite {
    BASSE,
    MOYENNE,
    HAUTE
}
```

La méthode `changerPriorite()` vérifie notamment que la nouvelle priorité n’est pas `null`.

Elle refuse également toute modification sur un ticket déjà résolu.

## 6. Historique

La contrainte complémentaire retenue est l’**historique obligatoire**.

Chaque ticket possède une liste d’événements.

Un `Evenement` représente une action réalisée sur le ticket ainsi que son état après cette action.

L’historique permet actuellement de tracer notamment :

- la création ;
- l’affectation ;
- le changement de priorité ;
- la prise en charge ;
- la résolution.

La consultation de l’historique ne permet pas de modifier directement sa liste interne.

Les événements sont ajoutés après validation de l’opération. Une action refusée ne modifie donc pas l’historique.

## 7. Identité métier

Deux tickets sont considérés comme identiques lorsqu’ils possèdent le même numéro.

Les méthodes `equals()` et `hashCode()` utilisent donc cet identifiant.

Cette cohérence est importante lors de l’utilisation des collections Java.

## 8. Catalogue et registre

Les opérations communes de stockage sont définies par l’interface générique :

```java id="5w1r99"
public interface Catalogue<T, K> {
    boolean ajouter(K cle, T element);
    Optional<T> chercher(K cle);
    boolean retirer(K cle);
    List<T> lister();
    int taille();
}
```

`RegistreTicketsEnMemoire` implémente ce contrat.

Le stockage principal utilise :

```java id="pmfy6r"
Map<Integer, Ticket>
```

Le numéro du ticket sert de clé.

Un doublon est refusé avant l’ajout afin de conserver le ticket déjà présent.

## 9. Justification du choix de collection

Plusieurs structures ont été comparées.

| Structure | Avantage | Limite |
|---|---|---|
| `ArrayList` | parcours simple | recherche par numéro nécessitant un parcours |
| `HashSet` | gestion de l’unicité | pas d’accès direct par identifiant |
| `HashMap` | accès direct par clé | ordre non garanti |
| `LinkedHashSet` | unicité et ordre | pas d’accès direct par numéro |
| `TreeMap` | clés triées | tri inutile pour le besoin principal |

La `HashMap` a été retenue car le besoin principal est de retrouver rapidement un ticket à partir de son numéro.

`ComparaisonRecherche` permet d’observer expérimentalement la différence entre une recherche dans une liste et une recherche dans une Map.

## 10. Protection des collections

Le registre ne renvoie pas directement sa structure interne.

La méthode `lister()` retourne une vue protégée des tickets afin que le code appelant ne puisse pas ajouter ou retirer directement des éléments du registre.

Les recherches et regroupements construisent également des collections distinctes du stockage principal.

## 11. Recherche et regroupement

Une recherche par titre permet de retrouver les tickets dont le titre contient un texte donné sans distinction de casse.

Les résultats sont triés par titre puis par numéro.

Le registre permet également de regrouper les tickets selon leur état.

Les trois états sont présents dans le résultat même lorsqu’aucun ticket ne correspond à l’un d’eux.

## 12. Essais

### 12.1 Invariants

Six essais vérifient les constructeurs :

1. numéro de ticket inférieur ou égal à zéro ;
2. titre vide ;
3. priorité nulle ;
4. auteur nul ;
5. email vide ;
6. numéro de lieu inférieur ou égal à zéro.

Les six essais sont refusés comme prévu.

### 12.2 Priorité

Les essais vérifient :

- le refus de `null` ;
- une modification valide ;
- le refus d’une modification après résolution.

Les cas refusés conservent la priorité précédente.

### 12.3 États

Le scénario nominal effectue :

`OUVERT → EN_COURS → RESOLU`

D’autres scénarios testent les transitions interdites ainsi que la prise en charge sans technicien.

### 12.4 Historique

Le scénario principal produit successivement des événements de création, d’affectation, de prise en charge et de résolution.

Les essais de refus vérifient également que l’historique ne change pas lorsque l’opération échoue.

### 12.5 Registre

Dix essais couvrent :

- registre vide ;
- ajout ;
- doublon ;
- recherche présente ;
- recherche absente ;
- retrait présent ;
- retrait absent ;
- protection de la liste ;
- recherche textuelle ;
- recherche sans résultat.

L’exécution affiche :

```text id="2ku4xc"
P6 : 10 essais sur 10 reussis.
```

Des essais supplémentaires vérifient le tri, le regroupement par état et les arguments invalides.

## 13. Vérification de la compilation

Après les dernières modifications, l’ensemble des fichiers Java a été recompilé avec :

```text id="rwbvvg"
javac -d out src\*.java
```

puis exécuté avec :

```text id="p0jtxm"
java -cp out Main
```

L’exécution complète s’est terminée normalement.

## 14. Difficultés rencontrées

### Évolution du modèle

La priorité a évolué au cours du projet. Certains anciens essais utilisaient encore une représentation devenue incompatible avec l’enum `Priorite`. Ils ont dû être corrigés.

### Tests manuels

Certains essais avaient été écrits avec des mécanismes plus complexes que ceux vus en cours. Ils ont été simplifiés afin de conserver des tests reposant principalement sur des `try/catch`, des conditions et des affichages explicites.

### Atomicité

Les vérifications ont été placées avant les modifications afin d’éviter de laisser un ticket partiellement modifié lorsqu’une opération échoue.

### Git et IDE

Le travail collaboratif a nécessité plusieurs vérifications de compilation après synchronisation.

Les fichiers compilés générés par l’IDE ont également été retirés du dépôt afin de ne conserver que les fichiers nécessaires au projet.

## 15. Travail déjà réalisé pour les jalons suivants

Le dépôt contient déjà plusieurs éléments dépassant les exigences du Jalon 1 :

- abstraction de `Ticket` ;
- polymorphisme ;
- `Catalogue<T,K>` ;
- `RegistreTicketsEnMemoire` ;
- `equals()` et `hashCode()` ;
- recherche par titre ;
- regroupement par état ;
- historique.

Ces éléments seront complétés et adaptés aux exigences précises des prochains jalons.

## 16. Contributions

| Membre | Contribution principale |
|---|---|
| **BAMAZI Yesuwaba Esther** | Modèle initial, encapsulation, constructeurs, priorités, compilation et nettoyage, registre et collections, validation du `Main`, documentation et diagramme. |
| **LOUE Rayan** | Interface `Assignable`, gestion des états, affectation et transitions. |
| **KABORE Cedric** | Scénarios de démonstration et essais des invariants dans `Main`. |

## 17. Sources et assistance

Le projet s’appuie sur les supports du cours IIAA511 et les différents TD/TP réalisés.

La documentation Java a également été consultée.

Des outils d’intelligence artificielle ont été utilisés pour aider à comprendre certaines erreurs, relire le code et restructurer la documentation. Les éléments remis ont ensuite été vérifiés par le groupe.