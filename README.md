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

Personne :	classe abstraite,cette classe nous evite les repetitions d'attributs nom, prenom, email
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

•	assigner(Technicien) : affecte un technicien au ticket. Refusée si le ticket est déjà RESOLU
•	prendreEnCharge() : fait passer l'état de OUVERT à EN_COURS. Refusée si le ticket n'est pas OUVERT ou si aucun technicien n'est assigné
•	resoudre() : fait passer l'état de EN_COURS à RESOLU. Refusée si le ticket n'est pas EN_COURS
•	delaiCibleHeures() : abstraite dans Ticket, redéfinie dans chaque sous classe
•	role() : abstraite dans Personne, redéfinie dans Utilisateur et Technicien
•	toString() : redéfinie, appelée automatiquement par System.out.println
•	les accesseurs utiles, sans aucun setter
Toute transition refusée lève une IllegalStateException et laisse l'état inchangé.

Diagramme de classes

Voir docs/diagramme-classes.png. GitHub l'affiche directement sous forme de diagramme.

Choix retenus et choix écarté

Classe abstraite retenue : Ticket. Un ticket générique est trop vaste en terme de possibilite  :on la categorise en incident ou une demande de service. Ticket porte tout l'état commun et le cycle de vie, et déclare delaiCibleHeures() abstraite parce que chaque type impose son propre délai (4 h pour un incident, 24 h pour une demande). Personne est abstraite pour que numero, nom et email ne soient écrits qu'une seule fois.
Interface retenue: Assignable. Recevoir un technicien est une capacité, pas une famille d'objets. Aussi, elle est generalisable, par exemple un document different dun ticket peut etre assignable. L'interface contient seulement les trois opérations nécessaires : assigner, getTechnicien, estAssigne.

Choix écarté : des transitions qui renvoient un boolean. En TD3, les transitions renvoient true si elles sont acceptées et false sinon. Nous avons préféré lever une IllegalStateException. Le message de l'exception indique aussi l'état courant.

Autre choix : des associations par objets plutôt que par du texte. Un Ticket ne stocke pas le nom de son auteur sous forme de chaîne, il contient directement l'objet Utilisateur, l'objet Lieu et, après affectation, l'objet Technicien.

Essais réalisés dans Main

Essai	:Type	:Action	:Résultat attendu
Polymorphisme:		parcourir un tableau Ticket[] contenant un incident et une demande:	délai cible 4 h puis 24 h
1	:ordinaire	:assigner, prendre en charge puis résoudre un TicketIncident	:OUVERT, EN_COURS, RESOLU
2	:limite	:résoudre un ticket jamais pris en charge	refus: état reste OUVERT
3	:ordinaire	:cycle complet d'un TicketDemandeService, :affectation via une variable de type Assignable	EN_COURS puis RESOLU
4	:limite	:reprendre en charge un ticket déjà résolu	:refus, état reste RESOLU
5	:limite	:prendre en charge un ticket sans technicien	:refus, état reste OUVERT


Difficultés rencontrées

1.	Faire communiquer les classes entre elles. Résolu par les associations entre objets plutôt que par recopie de données.
2.	Garantir que les états se succèdent dans le bon ordre. Résolu par une énumération EtatTicket et une condition sur l'état courant dans chaque méthode de transition.
3.	Travailler à trois sur le même dépôt : une commande git pull collée par erreur dans Utilisateur.java a empêché la compilation au jalon 1. Nous compilons désormais avant chaque push et vérifions le dépôt sur un clone neuf.
4. Un même humain jouant les deux rôles devrait aujourd'hui être représenté par deux objets distincts, que rien ne relie. Une version ultérieure utiliserait une classe Personne concrète portant un ou plusieurs rôles.

Questions pour la revue

Peut-on modifier directement les données depuis Main ? Non. Tous les attributs sont privés, et ceux qui ne changent jamais sont final. Toute lecture passe par un getter, toute modification par une méthode de la classe (setter).

Les constructeurs créent-ils des objets complets ? Oui. Chaque constructeur reçoit toutes les données obligatoires, valide numéros et textes, et refuse une valeur invalide. Un ticket naît toujours à l'état OUVERT, sans technicien.

Peut-on modifier l'état autrement qu'en utilisant les méthodes prévues ?
Non. etat est privé et n'a pas de setter. Seules prendreEnCharge et resoudre le modifient.

Q. Les changements suivent-ils l'ordre demandé ? Oui. Dans l'essai 2, on appelle resoudre() sur un ticket jamais pris en charge : Main affiche le message de l'exception et l'état reste OUVERT.

S1. Oui. TicketIncident extends Ticket et Technicien extends Personne : dans les deux cas on peut dire « est un ».nne.

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

