# Cahier des charges : système de tickets d’assistance

**Sujet C : Groupe 10**  
**Auteurs : BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric**

## 1. Contexte

Le projet consiste à réaliser un système simplifié de gestion de tickets d’assistance dans un établissement universitaire.

Lorsqu’un utilisateur rencontre un incident ou souhaite demander un service, il doit pouvoir créer un ticket contenant les informations nécessaires à son traitement.

Le système doit permettre de suivre ce ticket depuis sa création jusqu’à sa résolution.

La solution s’inspire du fonctionnement général d’un outil de type GLPI.

## 2. Acteurs

| Acteur | Rôle |
|---|---|
| **Utilisateur** | Déclarer un incident ou une demande de service. |
| **Technicien** | Être affecté à un ticket et participer à son traitement. |

## 3. Périmètre fonctionnel

Le système doit gérer :

- les utilisateurs ;
- les techniciens ;
- les lieux ;
- les tickets ;
- les priorités ;
- les états des tickets ;
- l’historique des principales actions réalisées sur un ticket.

Deux catégories de tickets sont prévues :

- **incident**, concernant un équipement ;
- **demande de service**, concernant une prestation ou un besoin.

## 4. Besoins fonctionnels

| Référence | Besoin |
|---|---|
| **BF1** | Créer des utilisateurs, techniciens et lieux. |
| **BF2** | Créer un ticket de type incident ou demande de service. |
| **BF3** | Affecter un technicien à un ticket. |
| **BF4** | Modifier la priorité d’un ticket lorsque cette modification est autorisée. |
| **BF5** | Prendre en charge un ticket. |
| **BF6** | Résoudre un ticket. |
| **BF7** | Consulter les informations et l’état d’un ticket. |
| **BF8** | Conserver l’historique des principales actions effectuées sur un ticket. |
| **BF9** | Ajouter et conserver plusieurs tickets dans un registre. |
| **BF10** | Rechercher un ticket par son numéro. |
| **BF11** | Retirer, lister et compter les tickets enregistrés. |
| **BF12** | Rechercher des tickets par leur titre. |
| **BF13** | Regrouper les tickets selon leur état. |

## 5. Informations obligatoires

### Personne

Une personne doit posséder :

- un numéro strictement positif ;
- un nom renseigné ;
- une adresse électronique renseignée.

### Lieu

Un lieu doit posséder :

- un numéro strictement positif ;
- un nom renseigné ;
- un bâtiment renseigné.

### Ticket

Un ticket doit posséder :

- un numéro strictement positif ;
- un titre ;
- une description ;
- une priorité ;
- un auteur ;
- un lieu.

Les priorités autorisées sont :

- `BASSE` ;
- `MOYENNE` ;
- `HAUTE`.

Les états autorisés sont :

- `OUVERT` ;
- `EN_COURS` ;
- `RESOLU`.

## 6. Règles métier

### RG1 : création d’un ticket

Un ticket est créé dans l’état `OUVERT` et sans technicien affecté.

### RG2 : affectation

Un technicien peut être affecté à un ticket tant que celui-ci n’est pas résolu.

Un technicien absent ou `null` est refusé.

L’affectation d’un technicien ne change pas l’état du ticket.

### RG3 : prise en charge

Un ticket peut passer de `OUVERT` à `EN_COURS` uniquement s’il possède un technicien affecté.

### RG4 : résolution

Un ticket peut passer de `EN_COURS` à `RESOLU`.

Un ticket ne peut pas être résolu directement depuis l’état `OUVERT`.

### RG5 : ticket résolu

Un ticket résolu ne peut plus être repris en charge ni réaffecté.

Aucune réouverture n’est prévue dans cette version.

### RG6 : priorité

La priorité d’un ticket doit être renseignée.

La priorité d’un ticket résolu ne peut plus être modifiée.

### RG7 : opérations refusées

Une opération refusée doit laisser le ticket dans l’état où il se trouvait avant la tentative.

### RG8 : identité

Le numéro constitue l’identité du ticket et ne doit pas être modifié après sa création.

Deux tickets ayant le même numéro représentent la même identité métier.

### RG9 : registre

Deux tickets portant le même numéro ne peuvent pas être enregistrés simultanément.

Une opération de consultation ne doit pas permettre de modifier directement le contenu du registre.

### RG10 : historique obligatoire

Chaque ticket doit conserver une trace des principales actions réalisées au cours de son traitement.

L’historique doit notamment pouvoir représenter :

- la création ;
- l’affectation ;
- le changement de priorité ;
- la prise en charge ;
- la résolution.

Une opération refusée ne doit pas ajouter d’événement à l’historique.

## 7. Délais cibles

Un incident possède un délai cible de **4 heures**.

Une demande de service possède un délai cible de **24 heures**.

Ces délais sont indicatifs. Aucun calcul d’échéance réelle n’est demandé dans cette version.

## 8. Hors périmètre

La version actuelle ne prévoit pas :

- d’interface graphique ;
- d’authentification ;
- de notifications ;
- de pièces jointes ;
- de stockage permanent ;
- d’affectation automatique ;
- de dates ou d’échéances réelles ;
- de réouverture des tickets résolus.

## 9. Critères de validation

Le projet est considéré conforme lorsque :

- les objets invalides sont refusés ;
- les transitions autorisées fonctionnent ;
- les transitions interdites sont refusées sans modifier le ticket ;
- les deux catégories de tickets peuvent être manipulées ;
- les tickets peuvent être enregistrés, recherchés et regroupés ;
- l’historique est conservé ;
- le programme compile et les scénarios de démonstration s’exécutent correctement.