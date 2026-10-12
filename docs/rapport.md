# Rapport de Conception du Système de Tickets

**Gestion de tickets d’assistance** • *Sujet C — Groupe 10*  
**Auteurs :** BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric

---

## 1. Objectif du Projet

Notre projet permet de déclarer un incident ou une demande de service dans un établissement universitaire. Nous représentons les utilisateurs, les techniciens, les lieux et les tickets, puis nous contrôlons le cycle d'état : `OUVERT` $\rightarrow$ `EN_COURS` $\rightarrow$ `RESOLU`.

---

## 2. Organisation du Modèle

| Élément | Rôle dans la conception |
| :--- | :--- |
| **Personne (abstrait)** | Centraliser numéro, nom et email ; imposer la méthode `role()`. |
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
* **Gestion du cycle de vie :** Un ticket naît `OUVERT` et sans technicien. `prendreEnCharge` exige un ticket `OUVERT` et affecté ; `resoudre` exige l'état `EN_COURS`.
* **Exceptions métier :** Nous signalons les refus métier par `TransitionInvalideException` (dérivée de `RuntimeException`). Les contrôles précèdent toute modification. `assigner` refuse un argument `null` avec `IllegalArgumentException`.

---

## 4. Choix Retenus et Choix Écartés

* **Héritage et Abstraction :** Une classe `Ticket` abstraite partage les données et le cycle de vie tout en obligeant chaque catégorie à définir son délai cible. `Personne` (abstrait) évite la répétition d'attributs.
* **Interface `Assignable` :** Exprime une capacité qui pourrait être implémentée par d'autres familles d'objets.
* **Exceptions vs Booléens :** Utilisation d'une exception personnalisée (`TransitionInvalideException`) plutôt qu'un retour booléen pour les transitions : son type identifie le refus métier et son message explique la cause. L’héritage de `RuntimeException` simplifie la signature des méthodes.
* **Polymorphisme :** Manipulé via `Ticket[]` dans `Main` où l'appel de `toString()` sollicite `delaiCibleHeures()` selon l'objet réel sans utiliser `instanceof`.

---

## 5. Structures de Collections

| Structure | Intérêt | Limite |
| :--- | :--- | :--- |
| `ArrayList<Ticket>` | Parcours simple et ordre d’ajout conservé | Recherche par numéro linéaire ; doublons à gérer. |
| `HashSet<Ticket>` | Unicité selon `equals` et `hashCode` | Pas d’accès direct par numéro ; ordre non garanti. |
| `HashMap<Integer, Ticket>` | Accès par numéro en temps moyen constant | Ordre non garanti ; doublon susceptible d’écraser une valeur. |
| `LinkedHashSet<Ticket>` | Unicité et ordre d’insertion | Pas d’accès direct par numéro. |
| `TreeMap<Integer, Ticket>` | Clés triées par numéro | Recherche logarithmique ; tri non exigé pour le stockage. |

> **Catalogue :** Définition des opérations `ajouter`, `chercher`, `retirer`, `lister` et `taille`. Une clé dupliquée est refusée sans remplacement. Les recherches par titre retournent une nouvelle liste triée sans distinction de casse. Les copies non modifiables protègent la structure des collections.

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
| **BAMAZI Yesuwaba Esther** | Modèle initial et encapsulation (Jalon 1) ; compilation, constructeurs et nettoyage (Jalon 2) ; finalisation du `Main`, du diagramme et du `README`. |
| **LOUE Rayan** | Interface `Assignable`, énumération `EtatTicket`, affectation et transitions dans `Ticket`. |
| **KABORE Cedric** | Premiers scénarios du `Main` (Jalon 2) : polymorphisme, scénario normal et gestion des refus. |

---

## 10. Sources et Usage de l’IA

* **Supports de cours :** IIAA511 et TP/TD 1 à 3.
* **Outils :** draw.io (diagrammes), Stack Overflow (questions Java).
* **Intelligence Artificielle :**
    * *Claude :* Explication des erreurs de compilation, commandes Git et relecture du `README`.
    * *ChatGPT :* Restructuration de documents.