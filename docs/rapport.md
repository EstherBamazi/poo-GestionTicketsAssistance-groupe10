# Rapport de Conception du Système de Tickets

**Gestion de tickets d’assistance** • *Sujet C Groupe 10*  
**Auteurs :** BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric

---

## Introduction
Les systemes de gestions de tickets sont omnipresents dans la societe actuelle. Lorsque l'on propose un service, il est tres important que celui ci arrive au client qui l'a demande sans problemes. Mais si probleme il y a, ce qui determine que le client reste ou pas c'est l'efficacicte du traitement de ce dernier. Aussi dans notre projet nos mettons en place un systeme inspire de GLPI, donc de la gestion de sinistre sur un campus.
Vous pouvez retrouve une analyse plus en detail de ce projet dans cahier_des_charges.md

## 1. Objectif du Projet

Notre projet permet de déclarer un incident ou une demande de service dans un établissement universitaire. Nous représentons les utilisateurs, les techniciens, les lieux et les tickets, puis nous contrôlons le cycle d'état : `OUVERT` -> `EN_COURS` -> `RESOLU`.

---

## 2. Organisation du Modèle et Conception

| Élément | Rôle dans la conception |
| :--- | :--- |
| **Personne (abstraite)** | Centraliser numéro, nom et email ; imposer la méthode `role()`. |
| **Utilisateur** et **Technicien** | Spécialiser `Personne` et définir le rôle retourné. |
| **Lieu** | Représenter une salle et son bâtiment. |
| **Ticket (abstrait)** | Centraliser les données, les associations et les transitions ; déclarer `delaiCibleHeures()`. |
| **TicketIncident** et **TicketDemandeService** | Ajouter une donnée spécifique et retourner respectivement 4 h et 24 h. |
| **Assignable** | Définir `assigner`, `getTechnicien` et `estAssigne`. |
| **EtatTicket** | Limiter les états à `OUVERT`, `EN_COURS` et `RESOLU`. |
| **Main** | Construire les objets et démontrer les comportements. |

> Les tickets référencent directement un `Utilisateur` et un `Lieu`, puis éventuellement un `Technicien`. Ces associations évitent de recopier leurs noms dans les tickets et permettent d’accéder aux objets concernés.

---

## 3. Encapsulation et Invariants

* **Visibilité & Immuabilité :** Les attributs sont déclarés `private` pour contrôler les modifications. Les données fixées à la construction sont `final`.
* **Évolution contrôlée :** L’état et le technicien évoluent uniquement via les méthodes `assigner`, `prendreEnCharge` et `resoudre`.
* **Validation à la construction :** Les constructeurs lèvent `IllegalArgumentException` si :
    * Les numéros ne sont pas positifs.
    * Les textes obligatoires sont absents ou vides.
    * L'auteur ou le lieu est absent.
    * L'adresse électronique n'est pas renseignée.
* **Gestion du cycle de vie :** Un ticket est `OUVERT` a sa creation et sans technicien. `prendreEnCharge` exige un ticket `OUVERT` et affecté ; `resoudre` exige l'état `EN_COURS`.
* **Exceptions métier :** Nous signalons les refus métier par `TransitionInvalideException` (Exception verifiee qui herite de `RuntimeException`). Les contrôles précèdent toute modification afin de garder l'attribut inchange si l'invariant de classe n'est pas respecte. `assigner` refuse un argument `null` avec `IllegalArgumentException`.

---

## 4. Choix Retenus et Choix Écartés

* **Héritage et Abstraction :** Une classe `Ticket` abstraite partage les données et le cycle de vie tout en obligeant chaque catégorie à définir son délai cible. `Personne` (abstrait) évite la répétition d'attributs.
* **Interface `Assignable` :** Exprime une capacité qui pourrait être implémentée par d'autres familles d'objets et qui est benefique pour notre classe Ticket.
* **Exceptions vs Booléens :** Utilisation d'une exception personnalisée (`TransitionInvalideException`) plutôt qu'un retour booléen pour les transitions : son type identifie le refus métier et son message explique la cause.
* **Polymorphisme :** Manipulé via `Ticket[]` dans `Main` où l'appel de `toString()` sollicite `delaiCibleHeures()` selon l'objet réel sans utiliser `instanceof`.

---

 Structures de Collections

| Structure | Intérêt | Limite |
| :--- | :--- | :--- |
| `ArrayList<Ticket>` | Parcours simple et ordre d’ajout conservé | Recherche par numéro linéaire ; doublons à gérer. |
| `HashSet<Ticket>` | Unicité selon `equals` et `hashCode` | Pas d’accès direct par numéro ; ordre non garanti. |
| `HashMap<Integer, Ticket>` | Accès par numéro en temps moyen constant | Ordre non garanti ; doublon susceptible d’écraser une valeur. |
| `LinkedHashSet<Ticket>` | Unicité et ordre d’insertion | Pas d’accès direct par numéro. |
| `TreeMap<Integer, Ticket>` | Clés triées par numéro | Recherche logarithmique ; tri non exigé pour le stockage. |

> **Catalogue :** Définition des opérations `ajouter`, `chercher`, `retirer`, `lister` et `taille`. Une clé dupliquée est refusée sans remplacement. Les recherches par titre retournent une nouvelle liste triée sans distinction de casse. Les copies non modifiables protègent la structure des collections.

## 5. Réalisation

Nous avons développé une application Java en console permettant de créer des tickets d’assistance, de les affecter à des techniciens et de suivre leur traitement. Un registre en mémoire permet de conserver les tickets, de les rechercher et de les regrouper par état.

### 5.1. Les classes du projet

La classe abstraite `Personne` rassemble les informations communes aux utilisateurs et aux techniciens. La classe abstraite `Ticket` contient les informations communes aux tickets : numéro, titre, description, priorité, auteur, lieu, état et technicien affecté.

Deux classes spécialisent `Ticket` : `TicketIncident` et `TicketDemandeService`. Elles définissent notamment des délais cibles différents : 4 heures pour un incident et 24 heures pour une demande de service.

```java
// Dans Ticket : chaque type de ticket doit définir son délai.
public abstract int delaiCibleHeures();

// Dans TicketIncident : le délai cible est de 4 heures.
@Override
public int delaiCibleHeures() {
    return 4;
}
```

Grâce au polymorphisme, un appel à `delaiCibleHeures()` sur une variable de type `Ticket` utilise la version correspondant à l’objet réel. La méthode `toString()` affiche les informations du ticket, dont ce délai.

### 5.2. La validation des données et les changements d’état

Les constructeurs vérifient les données obligatoires avant de créer les objets. Par exemple, le numéro d’un ticket doit être positif et son titre ne doit pas être vide. Une affectation à un technicien `null` est également refusée.

Les changements d’état passent par les méthodes `prendreEnCharge()` et `resoudre()`. Un ticket doit être ouvert et affecté à un technicien pour être pris en charge. Seul un ticket en cours peut être résolu.

```java
// Extrait de resoudre() : vérifier avant de changer l’état.
if (etat != EtatTicket.EN_COURS) {
    throw new TransitionInvalideException(
        "Impossible de resoudre un ticket qui n'est pas EN_COURS "
        + "(etat actuel : " + etat + ").");
}
this.etat = EtatTicket.RESOLU;
```

Si la condition n’est pas respectée, l’exception interrompt la méthode avant la modification : l’état du ticket reste inchangé.

Nous avons créé une exception personnalisée pour identifier ces refus :

```java
public class TransitionInvalideException extends RuntimeException {
    public TransitionInvalideException(String message) {
        super(message); // Transmet le message au constructeur parent.
    }
}
```

### 5.3. L’identification des tickets

Les méthodes `equals()` et `hashCode()` ont été redéfinies dans `Ticket`. Deux tickets sont considérés comme égaux lorsqu’ils ont le même numéro.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;            // Même objet.
    if (!(o instanceof Ticket)) return false;
    Ticket ticket = (Ticket) o;
    return numero == ticket.numero;       // Même numéro.
}

@Override
public int hashCode() {
    return Objects.hash(numero);
}
```

Le numéro est déclaré `private final` : il ne change pas après la construction. Le code de hachage fondé sur ce numéro reste donc stable.

### 5.4. Le catalogue et le stockage en mémoire

L’interface générique `Catalogue<T, K>` définit cinq opérations : ajouter, chercher, retirer, lister et obtenir la taille. La classe `RegistreTicketsEnMemoire` implémente ce contrat avec des tickets comme éléments et des entiers comme clés.

```java
// Chaque numéro est associé à un ticket.
private final Map<Integer, Ticket> tickets = new HashMap<>();
```

La méthode `ajouter()` vérifie que la clé et le ticket ne sont pas `null`, puis que la clé correspond au numéro du ticket. Elle refuse ensuite les doublons sans remplacer l’objet déjà enregistré :

```java
// Extrait situé après les contrôles des arguments.
if (tickets.containsKey(cle)) {
    return false; // Le ticket initial est conservé.
}
tickets.put(cle, element);
return true;
```

La recherche renvoie un `Optional<Ticket>` pour représenter la présence ou l’absence d’un résultat :

```java
// Extrait de chercher(), après le contrôle de la clé.
return Optional.ofNullable(tickets.get(cle));
```

La méthode `lister()` protège la structure du registre en renvoyant une copie non modifiable :

```java
@Override
public List<Ticket> lister() {
    return List.copyOf(tickets.values());
}
```

L’appelant ne peut pas ajouter ou retirer d’éléments dans cette liste. Les objets `Ticket` restent toutefois partagés : cette protection concerne la collection, pas l’état de chaque ticket.

### 5.5. La recherche par titre et le regroupement par état

La méthode `rechercherParTitre()` sélectionne les titres contenant le texte demandé, sans distinguer les majuscules des minuscules.

```java
String recherche = motCle.toLowerCase(Locale.ROOT);
List<Ticket> resultat = new ArrayList<>();

for (Ticket ticket : tickets.values()) {
    if (ticket.getTitre().toLowerCase(Locale.ROOT).contains(recherche)) {
        resultat.add(ticket);
    }
}
```

Les résultats sont ensuite triés par titre sans tenir compte de la casse, puis par numéro lorsque les titres sont équivalents.

La méthode `regrouperParEtat()` construit une nouvelle Map contenant une liste pour chacun des trois états :

```java
Map<EtatTicket, List<Ticket>> groupes = new HashMap<>();

for (EtatTicket etat : EtatTicket.values()) {
    groupes.put(etat, new ArrayList<>()); // Prépare les trois groupes.
}
for (Ticket ticket : tickets.values()) {
    groupes.get(ticket.getEtat()).add(ticket);
}
```

Même lorsque le registre est vide, les trois clés sont présentes avec des listes vides. Modifier ces collections retournées ne supprime aucun ticket du registre.

### 5.6. Le programme de démonstration

La classe `Main` présente le polymorphisme, les transitions autorisées et les opérations refusées. Elle contient également les dix essais du registre : registre vide, ajout, doublon, recherches présente et absente, retraits présent et absent, protection de la liste et deux recherches par titre.

Des vérifications supplémentaires portent sur le tri et le regroupement par état. Les essais lèvent une `AssertionError` lorsqu’un résultat ne correspond pas au comportement attendu.

Enfin, la classe `ComparaisonRecherche` compare la recherche de 1 000 identifiants dans une liste et une Map contenant 10 000 tickets. Plusieurs mesures sont réalisées afin d’observer l’effet de la structure choisie sur le temps de recherche.
---

## 6. Essais sur les Tickets

| Essai présent | Résultat attendu |
| :--- | :--- |
| **Polymorphisme dans `Ticket[]`** | Affichage de délais de 4 h pour l’incident et 24 h pour la demande. |
| **Cycle de `TicketIncident`** | `OUVERT` $\rightarrow$ `EN_COURS` $\rightarrow$ `RESOLU`. |
| **Résolution d’une demande ouverte** | `TransitionInvalideException` ; l'état reste `OUVERT`. |
| **Prise en charge d’un incident résolu** | `TransitionInvalideException` ; l'état reste `RESOLU`. |
| **Cycle de `TicketDemandeService` via `Assignable`** | Technicien affecté $\rightarrow$ `EN_COURS` $\rightarrow$ `RESOLU`. |
| **Nouvelle reprise du même incident résolu** | Refus identique au précédent ; état affiché `RESOLU`. |
| **Prise en charge sans technicien** | Refus ; état affiché `OUVERT`. |

---

## 7. Critères des Essais du Registre

| N° | Vérification attendue |
| :---: | :--- |
| **1** | Catalogue vide : taille zéro et liste vide. |
| **2** | Ajout ordinaire : `true` et taille augmentée. |
| **3** | Doublon : `false` et objet initial conservé. |
| **4** | Recherche présente : `Optional` contient le ticket. |
| **5** | Recherche absente : `Optional.empty()`. |
| **6** | Retrait présent : `true` et taille diminuée. |
| **7** | Retrait absent : `false` et registre inchangé. |
| **8** | Liste protégée : tentative de modification sans effet sur le registre. |
| **9** | Recherche textuelle avec casse différente : mêmes résultats triés. |
| **10** | Recherche textuelle absente : nouvelle liste vide et registre inchangé. |

---

## 8. Difficultés et Limites

1. **Gestion des transitions & états :** Correction d'une affectation nulle qui pouvait retirer le technicien d’un ticket `EN_COURS`. Le contrôle de `null` précède désormais toute modification.
2. **Collaboration Git :** Au jalon 1, un conflit Git collé par erreur dans `Utilisateur.java` a bloqué la compilation. Adoption d'une règle systématique : vérification de la compilation avant chaque `push`.
3. **Gestion des Rôles :** Une même personne ne peut pas être à la fois utilisateur et technicien sans créer deux objets distincts. Une évolution future permettrait d'associer plusieurs rôles à un seul individu.

---

## 9. Contributions du Groupe

| Membre | Contribution |
| :--- | :--- |
| BAMAZI Yesuwaba Esther | Modèle initial et encapsulation (Jalon 1) ; compilation, constructeurs et nettoyage (Jalon 2) ; exception personnalisée et sécurisation des transitions ; equals et hashCode ; catalogue générique et registre en mémoire ; recherche par titre, tri et regroupement par état ; essais dans Main et comparaison liste/Map ; correction de toString ; realisation du diagramme et du README du cahier des charges et du rapport. |
| **LOUE Rayan** | Interface `Assignable`, énumération `EtatTicket`, affectation et transitions dans `Ticket`. |
| **KABORE Cedric** | Premiers scénarios du `Main` (Jalon 2) : polymorphisme, scénario normal et gestion des refus. |

---

## 10. Sources et Usage de l’IA

* **Supports de cours :** IIAA511 et TP/TD 1 à 3.
* **Outils :** draw.io (diagrammes), Stack Overflow (questions Java).8. https://docs.oracle.com/en/java/
* **Intelligence Artificielle :Explication de notions et aide a la redaction**
    * *Claude :* Explication des erreurs de compilation, commandes Git et relecture du `README`.
    * *ChatGPT :* Restructuration de documents.
