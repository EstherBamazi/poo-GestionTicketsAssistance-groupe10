Sujet C : Gestion de tickets d'assistance
Appliqué au cas du système GLPI. POO en Java, ECUE IIAA511, Dr Babacar LEYE, 2iE, Semestre 5 IIAA.

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

Un système de gestion de tickets d'assistance de type GLPI qui permet aux utilisateurs de déclarer un incident ou une demande de service concernant leur lieu de travail, et de suivre l'avancement de leur prise en charge.

Actions principales 

1.	créer un utilisateur, un lieu et un technicien
2.	créer un ticket, ouvert à sa création
3.	assigner un technicien au ticket
4.	prendre le ticket en charge
5.	résoudre le ticket
6.	afficher l'état courant d'un ticket

   
Les objets du système

Prenons le cas de Sidibé, étudiant disposant d'un ordinateur prêté par l'université. Il constate un problème d'affichage en salle B6.5 pendant un projet Java. On représente ce cas par :

•	un Utilisateur : l'auteur de la déclaration
•	un Lieu : la salle et son bâtiment
•	un Ticket : le numéro, le titre, la description, la priorité et l'état
•	un Technicien : celui qui prendra le ticket en charge

Les classes retenues et leur rôle

Classe	Rôle

Personne	classe abstraite, porte les données communes à toute personne du système
Utilisateur	personne qui déclare un ticket
Technicien	personne qui prend un ticket en charge
Lieu	localisation d'un incident
Assignable	interface, capacité à recevoir un technicien
EtatTicket	énumération des états d'un ticket : OUVERT, EN_COURS, RESOLU
Ticket	classe abstraite, porte les données et le cycle de vie d'un ticket, implémente Assignable
TicketIncident	ticket concernant un équipement en panne, délai cible 4 h
TicketDemandeService	ticket demandant un service, délai cible 24 h
Main	programme de démonstration et essais

Attributs et visibilité

Tous les attributs sont privés. Ceux qui sont fixés à la création et ne changent plus sont en plus déclarés final.
•	Personne : numero (int), nom (String), email (String)
•	Lieu : numero (int), nom (String), batiment (String)
•	Ticket : numero (int), titre (String), description (String), priorite (String), etat (EtatTicket), auteur (Utilisateur), lieu (Lieu), technicien (Technicien)
•	TicketIncident ajoute equipementConcerne (String)
•	TicketDemandeService ajoute serviceDemande (String)
Utilisateur et Technicien ne déclarent aucun attribut : ils héritent de ceux de Personne.

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

Classe abstraite retenue : Ticket. Un ticket générique n'a pas de sens métier : c'est toujours un incident ou une demande de service. Ticket porte tout l'état commun et le cycle de vie, et déclare delaiCibleHeures() abstraite parce que chaque type impose son propre délai (4 h pour un incident, 24 h pour une demande). Personne est abstraite pour la même raison, et pour que numero, nom et email ne soient écrits qu'une seule fois.
Interface retenue : Assignable. Recevoir un technicien est une capacité, pas une famille d'objets. Aujourd'hui seuls les tickets sont assignables, mais demain une intervention planifiée ou une tâche de maintenance pourrait l'être sans être un ticket. L'interface contient seulement les trois opérations nécessaires : assigner, getTechnicien, estAssigne.

Choix écarté : des transitions qui renvoient un boolean. En TD3, les transitions renvoient true si elles sont acceptées et false sinon. Nous avons préféré lever une IllegalStateException : un refus ne peut pas passer inaperçu, alors qu'un false oublié dans Main laisse croire que la transition a eu lieu. Le message de l'exception indique aussi l'état courant.

Autre choix : des associations par objets plutôt que par du texte. Un Ticket ne stocke pas le nom de son auteur sous forme de chaîne, il contient directement l'objet Utilisateur, l'objet Lieu et, après affectation, l'objet Technicien.

Essais réalisés dans Main

Essai	Type	Action	Résultat attendu
Polymorphisme		parcourir un tableau Ticket[] contenant un incident et une demande	délai cible 4 h puis 24 h
1	ordinaire	assigner, prendre en charge puis résoudre un TicketIncident	OUVERT, EN_COURS, RESOLU
2	limite	résoudre un ticket jamais pris en charge	refus, état reste OUVERT
3	ordinaire	cycle complet d'un TicketDemandeService, affectation via une variable de type Assignable	EN_COURS puis RESOLU
4	limite	reprendre en charge un ticket déjà résolu	refus, état reste RESOLU
5	limite	prendre en charge un ticket sans technicien	refus, état reste OUVERT


Difficultés rencontrées

•	Faire communiquer les classes entre elles. Résolu par les associations entre objets plutôt que par recopie de données.
•	Garantir que les états se succèdent dans le bon ordre. Résolu par une énumération EtatTicket et une condition sur l'état courant dans chaque méthode de transition.
•	Travailler à trois sur le même dépôt : une commande git pull collée par erreur dans Utilisateur.java a empêché la compilation au jalon 1. Nous compilons désormais avant chaque push et vérifions le dépôt sur un clone neuf.
•	Un même humain jouant les deux rôles devrait aujourd'hui être représenté par deux objets distincts, que rien ne relie. Une version ultérieure utiliserait une classe Personne concrète portant un ou plusieurs rôles.

Questions pour la revue


Peut-on modifier directement les données depuis Main ? Non. Tous les attributs sont privés, et ceux qui ne changent jamais sont final. Toute lecture passe par un accesseur, toute modification par une méthode de la classe.

Les constructeurs créent-ils des objets complets ? Oui. Chaque constructeur reçoit toutes les données obligatoires, valide numéros et textes, et refuse une valeur invalide. Un ticket naît toujours à l'état OUVERT, sans technicien.

Peut-on modifier l'état autrement qu'en utilisant les méthodes prévues ?
Non. etat est privé et n'a pas de setter. Seules prendreEnCharge et resoudre le modifient.

Les changements suivent-ils l'ordre demandé ?
Oui. prendreEnCharge n'agit que sur un ticket OUVERT avec un technicien, resoudre que sur un ticket EN_COURS. Un appel hors séquence lève une IllegalStateException et laisse l'état inchangé, ce que les essais 2, 4 et 5 démontrent.

S1. La relation classe fille / classe mère signifie-t-elle bien « est un » ? Oui : un incident est un ticket, une demande de service est un ticket, un utilisateur est une personne, un technicien est une personne.

S2. Les attributs communs sont-ils écrits une seule fois ? Oui. Ceux des tickets sont déclarés dans Ticket, ceux des personnes dans Personne. Les sous classes ne déclarent que ce qu'elles ajoutent.

S3. Le programme principal utilise-t-il le type de la classe mère sans rechercher la classe réelle de l'objet ? Oui. Main déclare des variables de type Ticket, un tableau Ticket[] et une variable de type Assignable, et n'utilise aucun instanceof. Le délai cible affiché varie selon l'objet réel, pas selon le type déclaré.

S4. Quelle règle appliquer maintenant au projet ? Placer dans la classe mère tout ce qui est commun, ne laisser dans les classes filles que ce qui varie réellement, et exprimer par une interface ce qu'un objet sait faire.

Contributions

Membre	Partie
BAMAZI Yesuwaba Esther	Modèle initial et encapsulation (jalon 1). Jalon 2 : compilation, constructeurs, nettoyage du dépôt, puis finalisation de Main, diagramme de classes et README

LOUE Rayan	Interface Assignable, énumération EtatTicket, affectation et transitions d'état dans Ticket

KABORE Cedric	Jalon 2 : premiers scénarios de Main (polymorphisme, scénario normal, deux refus)

Le détail est visible dans l'historique des commits.

Recherches et usage de l'IA
Sources consultées :
•	supports de cours IIAA511 et fiches TD-TP 1 à 3, sections sur les invariants, l'héritage, le polymorphisme, les classes abstraites et les interfaces
•	draw.io pour l'écriture des diagrammes de classes

Esther a utilisé Claude (Anthropic) pour :

1.	comprendre le cours de POO, en particulier la séance manquée du a une erreur de l'administration sur son email: encapsulation, classes abstraites, interfaces ;
2.	auditer et debugger son code : Claude a signalé la commande git pull restée dans Utilisateur.java, les champs dupliqués dans Utilisateur et Technicien, le getter récursif de TicketIncident et les fichiers IDE versionnés ;
3.	apprendre à utiliser Git dans le terminal : status, pull, commit, push, retrait des fichiers IDE avec .gitignore et rediger les noms des commits;
4.	vérifier la conformité du dépôt à la liste du professeur et aux fiches TD, en clonant et en compilant le projet ;
5.	rédiger les messages d'organisation envoyés au groupe ;
6.	la relecture de ce README.
Cette déclaration concerne l'usage d'Esther.

