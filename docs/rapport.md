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
* **Exceptions métier :** Nous signalons les refus métier par `TransitionInvalideException` (exception non vérifiée qui hérite de `RuntimeException`). Les contrôles précèdent toute modification afin de garder l'attribut inchange si l'invariant de classe n'est pas respecte. `assigner` refuse un argument `null` avec `IllegalArgumentException`.

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

> **Catalogue :** Définition des opérations `ajouter`, `chercher`, `retirer`, `lister` et `taille`. Une clé dupliquée est refusée sans remplacement. Les recherches par titre retournent une nouvelle liste triée sans distinction de casse. La liste fournie par `lister()` est une copie non modifiable. La recherche et le regroupement fournissent des collections indépendantes du stockage : leur modification ne change pas les entrées du registre. Les objets tickets restent partagés.

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

## 6. Essais sur les tickets et traçabilité

Les références **BF** et **RG** renvoient aux besoins et règles du [cahier des charges](cahier_des_charges.md). Les repères `a`, `b`, `c1` à `c5` et `P6.1` à `P6.10` permettent de retrouver les scénarios dans `Main` et leur sortie console.

La compilation et l’exécution complète de `Main` ont abouti sans erreur lors de la vérification du 14 octobre 2026. La [trace console](resultats_validation.txt) conserve les résultats observés. Les premiers scénarios sont contrôlés par lecture de l’affichage ; les essais d’affectation refusée et du registre comportent des vérifications qui lèvent une `AssertionError` en cas d’écart.

| Essai | Repère dans `Main` | Besoins et règles vérifiés | Résultat observé et portée du contrôle |
| :--- | :--- | :--- | :--- |
| **T1** | Création puis scénario `a` | BF1, BF2, BF6, RG7 | Les deux catégories sont créées et affichées à l’état `OUVERT`, sans technicien, avec les délais de 4 h et 24 h attendus en section 4 du cahier. Contrôle visuel. |
| **T2** | Scénario `b` | BF3, BF4, BF5, RG7 | Le ticket incident suit le cycle `OUVERT` → `EN_COURS` → `RESOLU`. Contrôle visuel ; l’état juste après l’affectation n’est pas affiché séparément. |
| **T3** | Scénario `c1` | BF5, RG1 ; RG6 pour le message | La résolution d’une demande ouverte provoque `TransitionInvalideException` avec un message explicite. Ce scénario ne contrôle pas séparément l’état après le refus. |
| **T4** | Scénario `c2` | BF4, RG2, RG5 ; RG6 pour le message et l’état | La prise en charge d’un ticket résolu est refusée ; l’état affiché reste `RESOLU`. |
| **T5** | Scénario `c3` via `Assignable` | BF3, BF4, BF5, RG7 | Bob est affecté à la demande de service, puis les états `EN_COURS` et `RESOLU` sont affichés. |
| **T6** | Scénario `c4` | BF4, RG2, RG5 ; RG6 pour le message et l’état | Une nouvelle tentative de reprise est refusée ; l’état affiché reste `RESOLU`. |
| **T7** | Scénario `c5` | BF4, RG3 ; RG6 pour le message et l’état | La prise en charge sans technicien est refusée ; l’état affiché reste `OUVERT`. |
| **T8** | Affectation nulle après `c5` | BF3, RG4, RG6 | `IllegalArgumentException` est interceptée ; une vérification confirme la conservation de Bob et de l’état `EN_COURS`. |
| **T9** | Affectation après résolution | BF3, RG5, RG6 | `TransitionInvalideException` est interceptée ; une vérification confirme la conservation de Bob et de l’état `RESOLU`. |

---

## 7. Essais du registre et résultats

### 7.1. Les dix essais P6

Chaque ligne correspond à un essai effectivement présent dans `Main`. Les dix essais se terminent avec le résultat attendu.

| Essai | Besoins et règles vérifiés | Résultat observé |
| :--- | :--- | :--- |
| **P6.1** | BF10, BF11 | Registre vide : taille zéro et liste vide. |
| **P6.2** | BF7, BF11 | Ajout du ticket 101 : `true` et taille égale à 1. |
| **P6.3** | BF7, RG12 | Doublon : `false`, taille inchangée et objet initial conservé, vérifié par identité. |
| **P6.4** | BF8, RG14 | Recherche de 101 : `Optional` présent contenant l’objet initial ; taille inchangée. |
| **P6.5** | BF8, RG13 | Recherche de 999 : résultat absent ; taille et ticket témoin conservés. |
| **P6.6** | BF9, BF11 | Retrait de 101 : `true`, taille zéro et ticket absent du registre. |
| **P6.7** | BF9, RG13 | Retrait de 999 : `false`, taille et ticket témoin conservés. |
| **P6.8** | BF10, RG14 | Ajout dans la liste de consultation refusé par `UnsupportedOperationException` ; registre inchangé. |
| **P6.9** | BF12, RG14, RG15 | `PROBLEME` et `probleme` retrouvent le même ticket. Vider la liste de résultats ne modifie pas le registre. Le tri de plusieurs tickets est vérifié séparément par C1. |
| **P6.10** | BF12, RG14, RG15 | Le texte `introuvable` donne un résultat vide ; taille et ticket témoin conservés. |

### 7.2. Essais complémentaires

Les identifiants C1 à C5 désignent les blocs de vérification situés après P6.10 dans `Main`.

| Essai | Besoins et règles vérifiés | Résultat observé |
| :--- | :--- | :--- |
| **C1 — Tri** | BF12, RG15 | Les résultats sont ordonnés par titre sans distinction de casse, puis par numéro : 104, 106, 101. |
| **C2 — Groupes** | BF13, RG16 | Les tickets 105 et 106 sont ouverts, 104 est en cours et 101 est résolu ; chacun figure dans le groupe attendu. |
| **C3 — Protection des groupes** | BF13, RG14 | Vider un groupe puis le résultat du regroupement conserve les quatre tickets du registre ; une nouvelle consultation retrouve les deux tickets ouverts. |
| **C4 — Registre vide** | BF13, RG16 | Les trois groupes existent et sont tous vides. |
| **C5 — Arguments invalides** | BF7, BF12, RG17 | Quatre appels sont refusés : numéro absent, ticket absent, numéro incohérent et texte de recherche absent. Le registre conserve ses quatre tickets. |

### 7.3. Comparaison des temps de recherche P7

Cette expérience éclaire le choix technique associé à **BF8**, la recherche par numéro. Elle n’impose aucun seuil de performance au cahier des charges et ne remplace pas les essais fonctionnels.

`ComparaisonRecherche` effectue 1 000 recherches d’identifiants présents parmi 10 000 tickets, dans une liste et dans une Map contenant les mêmes objets. La création des objets est exclue du chronométrage. Cinq passages de préparation précèdent cinq mesures dont l’ordre est alterné. La somme des numéros retrouvés est contrôlée pour les deux structures : **4 996 000**.

Lors de l’exécution conservée dans la trace, les moyennes sont de **12,871 ms pour la liste** et **0,183 ms pour la Map**. Ces mesures sont indicatives : elles varient selon la machine, la JVM et la charge. Elles portent uniquement sur des recherches réussies.

### 7.4. Portée de la validation

Un essai associé à une règle ne couvre que les cas décrits dans son scénario. `Main` ne vérifie pas systématiquement tous les champs après chaque refus. Il ne comporte notamment pas d’essai dédié au remplacement autorisé d’un technicien et à la conservation de l’état après affectation (**RG8**), aux données de construction invalides (**RG9**, **RG10**), ni à une priorité hors des valeurs prévues (**RG11**).

Les variantes suivantes ne sont pas directement testées : prise en charge répétée à l’état `EN_COURS` (**RG2**), seconde résolution d’un ticket résolu (**RG1**, **RG5**), recherche et retrait sans numéro (**RG17**), recherche par titre avec un texte vide (**RG15**). La réussite des scénarios exécutés ne constitue donc pas une validation exhaustive de ces règles.

---

## 8. Difficultés et Limites

1. **Gestion des transitions & états :** Correction d'une affectation nulle qui pouvait retirer le technicien d’un ticket `EN_COURS`. Le contrôle de `null` précède désormais toute modification.
2. **Collaboration Git :** Au jalon 1, un conflit Git collé par erreur dans `Utilisateur.java` a bloqué la compilation. Adoption d'une règle systématique : vérification de la compilation avant chaque `push`.
3. **Gestion des Rôles :** Une même personne ne peut pas être à la fois utilisateur et technicien sans créer deux objets distincts. Une évolution future permettrait d'associer plusieurs rôles à un seul individu.
4. **Priorités :** Le cahier des charges limite la priorité à `HAUTE`, `MOYENNE` et `BASSE` (RG11). Le constructeur contrôle uniquement que le texte est renseigné ; il accepte aussi une autre valeur non vide. Cette restriction métier n’est donc pas entièrement appliquée.

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
