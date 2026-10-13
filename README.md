Sujet C : Gestion de tickets d'assistance
Appliqué au cas du système GLPI.

Membres du groupe 10

BAMAZI Yesuwaba Esther
LOUE Rayan
KABORE Cedric

Compiler et lancer


Dans IntelliJ IDEA : ouvrir le projet, puis exécuter la classe Main (bouton Run vert).
En ligne de commande, depuis la racine du dépôt :

javac -d out src/*.java
java -cp out Main
Structure du dépôt
src/     classes Java et Main
docs/    diagramme-classes.png (diagramme de classes)

Le besoin

Un système de gestion de tickets d'assistance de type GLPI qui permet aux etudiants(utilisateurs) de déclarer un incident ou une demande de service concernant leur salle de classe(lieu) , et de suivre l'avancement de leur ticket d'incident.

Actions principales
-Declarer un incident
1.	créer un utilisateur, un lieu et un technicien
2.	créer un ticket, ouvert à sa création

-Prendre en charge la reclamation
4.	assigner un technicien au ticket
5.	prendre le ticket en charge

-Resoudre le probleme
6.	résoudre le ticket
7.	afficher l'état courant d'un ticket


description du contexte

Nous suivons Sidibé, étudiant disposant d'un ordinateur prêté par l'université. Il constate un problème d'affichage en salle B6.5 pendant un projet Java. On représente ce cas par :

•	un Utilisateur : la personne a qui le dinistre est arrive ici Sidibe
•	un Lieu : l'endroit ou le sinistre a pris place
•	un Ticket : le numéro, le titre, la description, la priorité et l'état
•	un Technicien : celui qui prendra le ticket de sidibe en charge ici c'est Bob

Les classes retenues et leur rôle

Classe  :	Rôle

Personne :	classe abstraite, cette classe nous évite les répétitions d'attributs numero, nom, email
Utilisateur	: personne qui déclare un ticket
Technicien  : personne qui prend un ticket en charge
Lieu :	localisation u sinistre, en general une salle de classe ou une chambre sur le campus
Assignable: interface conseillee par le professeur, capacité à recevoir un technicien
EtatTicket :	énumération des états d'un ticket : OUVERT, EN_COURS, RESOLU, cela evite des erreurs du a des mauvaises valeurs prise par l'etat
Ticket : classe abstraite, contiens les données et le cycle de vie d'un ticket, implémente l'interface Assignable
TicketIncident	:ticket concernant un équipement en panne, délai cible 4 h
TicketDemandeService	:ticket demandant un service, délai cible 24 h
Main	: programme de démonstration et essais suggere par l'enonce

Attributs et visibilité

Tous les attributs sont privés. Ceux qui sont fixés à la création et ne changent plus sont en plus déclarés final. On respecte ainsi l'encapsulation.
•	Personne : numero (int), nom (String), email (String)
•	Lieu : numero (int), nom (String), batiment (String)
•	Ticket : numero (int), titre (String), description (String), priorite (String), etat (EtatTicket), auteur (Utilisateur), lieu (Lieu), technicien (Technicien)
•	TicketIncident ajoute equipementConcerne (String)
•	TicketDemandeService ajoute serviceDemande (String)
Utilisateur et Technicien ne déclarent aucun attribut : ils héritent de ceux de Personne, c'est l'interet de la classe abstraite.

Construction des objets

Chaque constructeur reçoit toutes les données obligatoires et valide ses paramètres : un numéro doit être strictement positif, un texte ne doit être ni nul ni vide. Une valeur invalide lève une IllegalArgumentException et l'objet n'est pas créé.
Deux valeurs ne sont pas reçues mais fixées par la classe Ticket elle même : l'état, qui vaut EtatTicket.OUVERT à la création, et le technicien, qui vaut null tant que personne n'a été assigné.

Les méthodes

•	assigner(Technicien) : affecte un technicien au ticket. Un argument null est refusé avec IllegalArgumentException. Une affectation sur un ticket RESOLU est refusée avec TransitionInvalideException.
•	prendreEnCharge() : fait passer l'état de OUVERT à EN_COURS. Refusée si le ticket n'est pas OUVERT ou si aucun technicien n'est assigné
•	resoudre() : fait passer l'état de EN_COURS à RESOLU. Refusée si le ticket n'est pas EN_COURS
•	delaiCibleHeures() : abstraite dans Ticket, redéfinie dans chaque sous classe
•	role() : abstraite dans Personne, redéfinie dans Utilisateur et Technicien
•	toString() : redéfinie, appelée automatiquement par System.out.println
•	les accesseurs utiles, sans aucun setter
Toute transition refusée lève une TransitionInvalideException et laisse l'état inchangé.

TransitionInvalideException hérite de RuntimeException. Cette exception personnalisée signale une opération interdite par les règles de traitement du ticket. Elle ne nécessite pas de déclaration throws ; Main la capture explicitement pour afficher la cause du refus. Les données de construction invalides et assigner(null) restent signalés par IllegalArgumentException. Le contrôle de null précède celui de l'état : assigner(null) est donc toujours une erreur d'argument, même sur un ticket résolu.

Diagramme de classes

Voir [le diagramme de classes](docs/diagramme-classe.png).

Choix retenus et choix écarté

Classe abstraite retenue : Ticket. Un ticket générique est trop vaste en terme de possibilite  :on la categorise en incident ou une demande de service. Ticket porte tout l'état commun et le cycle de vie, et déclare delaiCibleHeures() abstraite parce que chaque type impose son propre délai (4 h pour un incident, 24 h pour une demande). Personne est abstraite pour que numero, nom et email ne soient écrits qu'une seule fois.
Interface retenue: Assignable. Recevoir un technicien est une capacité, pas une famille d'objets. Aussi, elle est generalisable, par exemple un document different dun ticket peut etre assignable. L'interface contient seulement les trois opérations nécessaires : assigner, getTechnicien, estAssigne.

Choix écarté : des transitions qui renvoient un boolean. Nous avons préféré lever une TransitionInvalideException afin de donner une cause explicite au refus. Ce choix concerne les transitions du ticket ; le catalogue conserve les retours boolean imposés par le TD3 pour ajouter et retirer.

Autre choix : des associations par objets plutôt que par du texte. Un Ticket ne stocke pas le nom de son auteur sous forme de chaîne, il contient directement l'objet Utilisateur, l'objet Lieu et, après affectation, l'objet Technicien.

Essais réalisés dans Main

Essai	:Type	:Action	:Résultat attendu
Polymorphisme:		parcourir un tableau Ticket[] contenant un incident et une demande:	délai cible 4 h puis 24 h
1	:ordinaire	:assigner, prendre en charge puis résoudre un TicketIncident	:OUVERT, EN_COURS, RESOLU
2	:limite	:résoudre un ticket jamais pris en charge	refus: état reste OUVERT
3	:ordinaire	:cycle complet d'un TicketDemandeService, :affectation via une variable de type Assignable	EN_COURS puis RESOLU
4	:limite	:reprendre en charge un ticket déjà résolu	:refus, état reste RESOLU
5	:limite	:prendre en charge un ticket sans technicien	:refus, état reste OUVERT

6 : limite : assigner null à un ticket EN_COURS : IllegalArgumentException ; technicien et état conservés.
7 : limite : affecter un autre technicien après résolution : TransitionInvalideException ; technicien initial et état RESOLU conservés.
Ces deux nouveaux essais vérifient explicitement le refus et l'absence de modification ; ils arrêtent le programme avec AssertionError si le résultat est incorrect.



Difficultés rencontrées

1.	Faire communiquer les classes entre elles. Résolu par les associations entre objets plutôt que par recopie de données.
2.	Garantir que les états se succèdent dans le bon ordre. Résolu par une énumération EtatTicket et une condition sur l'état courant dans chaque méthode de transition.
3.	Travailler à trois sur le même dépôt : une commande git pull collée par erreur dans Utilisateur.java a empêché la compilation au jalon 1. Nous compilons désormais avant chaque push et vérifions le dépôt sur un clone neuf.
4. Un même humain jouant les deux rôles devrait aujourd'hui être représenté par deux objets distincts, que rien ne relie. Une version ultérieure utiliserait une classe Personne concrète portant un ou plusieurs rôles.

Questions pour la revue

Peut-on modifier directement les données depuis Main ? Non. Tous les attributs sont privés, et ceux qui ne changent jamais sont final. Toute lecture passe par un getter, toute modification par une méthode de la classe (assigner, prendreEnCharge, resoudre).

Les constructeurs créent-ils des objets complets ? Oui. Chaque constructeur reçoit toutes les données obligatoires, valide numéros et textes, et refuse une valeur invalide. Un ticket naît toujours à l'état OUVERT, sans technicien.

Peut-on modifier l'état autrement qu'en utilisant les méthodes prévues ?
Non. etat est privé et n'a pas de setter. Seules prendreEnCharge et resoudre le modifient.

Q. Les changements suivent-ils l'ordre demandé ? Oui. Dans l'essai 2, on appelle resoudre() sur un ticket jamais pris en charge : Main affiche le message de l'exception et l'état reste OUVERT.

S1. Oui. TicketIncident extends Ticket et Technicien extends Personne : dans les deux cas on peut dire « est un ».

S2. Les attributs communs sont-ils écrits une seule fois ? Oui. Ceux des tickets sont déclarés dans Ticket, ceux des personnes dans Personne. Les sous classes ne déclarent que ce qu'elles ajoutent.

S3. Le programme principal utilise-t-il le type de la classe mère sans rechercher la classe réelle de l'objet ? Oui. Main déclare des variables de type Ticket, un tableau Ticket[] et une variable de type Assignable, et n'utilise aucun instanceof. Le délai cible affiché varie selon l'objet réel, pas selon le type déclaré.

S4. Quand deux classes répètent les mêmes attributs, on les remonte dans une classe mère. Quand une capacité peut concerner des objets sans lien de parenté, on en fait une interface.

Contributions

Membre :	Partie
BAMAZI Yesuwaba Esther	: Modèle initial et encapsulation (jalon 1). Jalon 2 : compilation, constructeurs, nettoyage du dépôt, puis finalisation de Main, diagramme de classes et README

LOUE Rayan	: Interface Assignable, énumération EtatTicket, affectation et transitions d'état dans Ticket

KABORE Cedric	Jalon 2 : premiers scénarios de Main (polymorphisme, scénario normal, deux refus)

Le détail est visible dans l'historique des commits.

Recherches et usage de l'IA
Sources consultées :
.	supports de cours IIAA511 et fiches TD-TP 1 à 3, sections sur les invariants, l'héritage, le polymorphisme, les classes abstraites et les interfaces
.	draw.io pour l'écriture des diagrammes de classes
. stackoverflow pour comprendre la gestion des exceptions, les enums et les super

Esther a utilisé Claude (Anthropic) pour :

se faire expliquer les notions du cours (encapsulation, classes abstraites, interfaces) et en faire des fiches utilisees lors de l'edition du cours;
comprendre les erreurs de compilation et d'exécution, les corrections ayant été faites par le groupe ;
apprendre les commandes Git et le rôle du .gitignore ;
vérifier la conformité du dépôt à la liste de vérification du professeur ;
relire le README.

## Collections

### Catalogue et registre

`Catalogue<T, K>` définit les cinq opérations communes : `ajouter`, `chercher`,
`retirer`, `lister` et `taille`. `T` est le type des éléments et `K` le type des
clés. Chaque méthode est précédée d'un commentaire précisant son contrat.

`RegistreTicketsEnMemoire` implémente `Catalogue<Ticket, Integer>`. Son seul
attribut est `private final Map<Integer, Ticket> tickets = new HashMap<>()`.
`private` protège le stockage ; `final` empêche de remplacer la Map, mais permet
de modifier son contenu par les méthodes du registre.

- `ajouter` refuse une clé ou un ticket nul, ainsi qu'une clé différente du
  numéro du ticket, avec `IllegalArgumentException`. Une clé déjà présente
  renvoie `false` sans remplacer l'objet initial ; un ajout réussi renvoie `true`.
- `chercher` utilise `Optional.ofNullable` : le résultat contient le ticket ou
  est vide si le numéro est absent. Une clé nulle lève `IllegalArgumentException`.
- `retirer` renvoie `true` si un ticket est supprimé, sinon `false`. Une clé nulle
  lève `IllegalArgumentException`.
- `lister` utilise `List.copyOf` pour retourner une copie non modifiable.
- `taille` renvoie le nombre de tickets enregistrés.

### Comparaison et choix des structures

| Structure | Doublons | Ordre garanti | Accès principal | Usage et choix dans le projet |
| --- | --- | --- | --- | --- |
| `ArrayList` | Éléments répétés autorisés | Ordre de la liste | Par indice ; parcours pour rechercher un numéro | Retenue pour les listes de résultats ; écartée comme stockage principal car la recherche par numéro est linéaire |
| `LinkedHashSet` | Éléments égaux refusés | Ordre d'insertion | Test d'appartenance | Adapté à des étiquettes uniques ordonnées ; ce besoin ne fait pas partie du registre |
| `HashSet` | Éléments égaux refusés | Aucun ordre garanti | Test d'appartenance | Permet de vérifier l'unicité des tickets avec equals et hashCode ; ne fournit pas de recherche directe par numéro |
| `HashMap` | Clés uniques ; valeurs répétées possibles | Aucun ordre garanti | Par clé | Retenue pour retrouver les tickets par numéro, en temps moyen quasi constant avec un bon hachage |
| `TreeMap` | Clés uniques ; valeurs répétées possibles | Clés triées | Par clé | Écartée : le registre n'exige pas de parcours trié par numéro ; recherche logarithmique |

Une Map remplace normalement la valeur lors d'un `put` sur une clé existante.
Nous vérifions donc `containsKey` avant l'ajout pour appliquer notre règle de
refus des doublons. Le tri des recherches porte sur une nouvelle liste ; il ne
dépend pas de l'ordre de parcours de la HashMap.

### Identité des tickets

`Ticket.equals(Object o)` compare les numéros, même entre les deux catégories de
tickets. `hashCode()` utilise `Objects.hash(numero)`. Deux tickets égaux ont ainsi
le même code de hachage. `numero` est déclaré `private final int` : il ne change
pas après la construction, donc le code de hachage reste stable même si l'état
du ticket évolue.

Dans `HashMap<Integer, Ticket>`, ce sont les clés Integer qui servent à rechercher
les entrées ; equals et hashCode de Ticket définissent l'égalité métier et sont
notamment utilisés par un `HashSet<Ticket>`.

### Recherche par titre et regroupement par état

`rechercherParTitre` cherche une sous-chaîne sans distinction de casse et renvoie
une nouvelle liste triée par titre, puis par numéro en cas d'égalité des titres
sans distinction de casse. Un mot-clé nul lève `IllegalArgumentException` ; une
chaîne vide retrouve tous les tickets, et un mot absent donne une liste vide.

`regrouperParEtat` construit une nouvelle Map et de nouvelles listes avec
`putIfAbsent`, puis `get(...).add(...)`. Seuls les états représentés ont un groupe ;
un registre vide renvoie une Map vide. Ajouter ou supprimer des éléments dans
les collections retournées ne change pas le registre. Les objets Ticket restent
partagés : leurs méthodes métier peuvent modifier leur état, et il faut refaire
le regroupement pour actualiser les groupes après une transition.

## P6 — Essais du catalogue dans Main

Les dix essais P6 sont écrits directement dans `main`, après les essais de
transitions. Chacun affiche son nom, vérifie le résultat avec un `if` et lance
`AssertionError` en cas d’échec. Le message `OK` est affiché après la vérification.
Un échec arrête donc le programme, sans option `-ea`.

| Essai | Action | Résultat attendu |
| --- | --- | --- |
| P6.1 | Consulter un registre vide | Taille 0 et liste vide |
| P6.2 | Ajouter un ticket | `true` et taille 1 |
| P6.3 | Ajouter un autre objet avec le même numéro | `false`, taille inchangée et objet initial conservé |
| P6.4 | Chercher un numéro présent | `Optional` contenant l'objet initial |
| P6.5 | Chercher un numéro absent | `Optional` vide et taille inchangée |
| P6.6 | Retirer un numéro présent | `true` et ticket absent du registre |
| P6.7 | Retirer un numéro absent | `false` et taille inchangée |
| P6.8 | Ajouter un ticket dans la liste retournée par `lister` | `UnsupportedOperationException` et registre inchangé |
| P6.9 | Rechercher « PROBLEME » puis « probleme » | Le ticket « Probleme d’affichage » est retrouvé ; modifier la liste résultat ne modifie pas le registre |
| P6.10 | Rechercher un mot absent | Liste vide et contenu du registre conservé |

Des essais complémentaires vérifient le tri par titre puis numéro, les arguments nuls, la concordance entre
clé et numéro, le regroupement par état et l'indépendance des collections
retournées par ce regroupement. Les dix essais P6 et ces contrôles complémentaires
ont été exécutés avec succès.

## P7 — Comparaison des recherches

Main appelle `ComparaisonRecherche.executer`. La comparaison utilise une
`ArrayList<Ticket>` et une `HashMap<Integer, Ticket>` contenant les mêmes
10 000 objets. Les 1 000 numéros recherchés sont distincts, présents et répartis
dans l'ensemble des tickets : 1, 11, 21, jusqu'à 9991.

La création des objets est exclue du chronométrage. Après cinq passages de
préparation, cinq mesures sont effectuées avec `System.nanoTime`, en alternant
l'ordre de passage des structures. Aucun affichage n'est réalisé pendant les
recherches. La somme des numéros retrouvés est vérifiée pour les deux structures :
elle doit être égale à 4 996 000.

| Passage | Liste en ms | Map en ms |
| --- | ---: | ---: |
| 1 | 14,597 | 0,216 |
| 2 | 14,884 | 0,522 |
| 3 | 15,059 | 0,078 |
| 4 | 15,018 | 0,097 |
| 5 | 3,609 | 0,075 |
| Moyenne | 12,633 | 0,198 |

Lors de cette exécution, la Map est plus rapide : elle retrouve chaque ticket
par sa clé, tandis que la liste est parcourue jusqu'au numéro recherché. Une
recherche dans la liste est linéaire ; l'accès par clé dans une HashMap est en
moyenne quasi constant avec un bon hachage. Ces mesures restent approximatives :
elles dépendent de la machine, des optimisations de la JVM et de la charge du
système. La baisse du temps de la liste au dernier passage montre cette
variabilité. Cette expérience porte uniquement sur des recherches réussies ;
elle ne mesure ni la mémoire occupée, ni le coût de construction des structures.



Pour l’exécution du tableau ci-dessus, les moyennes sont de **12,6 ms pour la liste contre 0,2 ms pour la Map**, soit environ **64 fois plus rapide** pour la Map (rapport calculé sur 12,633 et 0,198 ms avant arrondi). Ce sont des mesures indicatives, elles varient selon la machine.
