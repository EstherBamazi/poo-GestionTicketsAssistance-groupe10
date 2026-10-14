# Cahier des Charges: Système de Tickets d’Assistance GLPI

**Gestion de tickets d’assistance** • *Sujet C  Groupe 10*  
**Auteurs :** BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric

---

## 1. Contexte et Objectifs

Notre projet vise à faciliter le suivi des demandes d’assistance dans un établissement universitaire comme 2IE. Le système doit centraliser les incidents et les demandes de service, identifier leur auteur et leur lieu, puis suivre leur traitement jusqu’à la résolution.

Un étudiant qui rencontre un problème doit pouvoir le signaler depuis son ordinateur en précisant sa nature et son lieu. Le service d’assistance doit disposer des informations nécessaires pour affecter un technicien, suivre le traitement et connaître les demandes résolues. Le système s’inspire du fonctionnement d’un outil de gestion d’assistance comme **GLPI**.

> **Exemple :** Sidibé rencontre un problème d’affichage en salle B6.5. Un ticket décrit la panne et l’écran concerné. Bob est affecté au ticket, le prend en charge et le marque comme résolu lorsque son intervention est terminée.

---

## 2. Acteurs

| Acteur | Rôle |
| :--- | :--- |
| **Utilisateur** | Signaler un incident ou demander un service ; consulter les informations et l’avancement du ticket. |
| **Technicien** | Être affecté à une demande, la prendre en charge et en signaler la résolution. |

---

## 3. Périmètre

Le système doit gérer les utilisateurs, les techniciens, les lieux et les tickets. Il doit permettre de conserver plusieurs tickets pendant une exécution, de les consulter, de les rechercher et de les regrouper par état. Deux catégories sont distinguées :
* **Incidents :** Concernent un équipement.
* **Demandes de service :** Concernent une prestation/demande.

> **Remarque :** La démonstration des fonctionnalités se fait en console. Le périmètre **exclut** l’interface graphique, l’authentification, l’affectation automatique, les pièces jointes, les notifications, les dates, l’historique et la conservation des données après l’arrêt du programme.

---

## 4. Informations Nécessaires

| Élément | Informations à conserver |
| :--- | :--- |
| **Utilisateur / Technicien** | Numéro strictement positif, nom et adresse électronique non vides. |
| **Lieu** | Numéro strictement positif, nom et bâtiment non vides. |
| **Ticket** | Numéro unique et strictement positif, titre, description, priorité (`HAUTE`, `MOYENNE`, `BASSE`), auteur, lieu, état et technicien éventuellement affecté. |
| **Incident** | Équipement concerné et délai cible de **4 heures**. |
| **Demande de service** | Service demandé et délai cible de **24 heures**. |

* Les informations obligatoires ne doivent pas être absentes ou vides.
* Un ticket doit toujours avoir un auteur et un lieu. Son numéro doit rester inchangé.
* Les délais cibles sont affichés à titre indicatif, sans calcul d’échéance ni déclenchement d’alerte.

---

## 5. Besoins fonctionnels

Chaque besoin décrit une action attendue. Le service d’assistance désigne ici l’activité assurée par les techniciens ; il ne constitue pas un rôle supplémentaire dans le système.

| Référence | Besoin |
| :--- | :--- |
| **BF1** | Un utilisateur déclare un incident. |
| **BF2** | Un utilisateur déclare une demande de service. |
| **BF3** | Le service d’assistance affecte un technicien à un ticket. |
| **BF4** | Le technicien affecté prend en charge un ticket. |
| **BF5** | Le technicien affecté résout un ticket. |
| **BF6** | Un utilisateur ou un technicien consulte les informations d’un ticket, notamment son état et son délai cible. |
| **BF7** | Le service d’assistance enregistre un ticket dans le registre. |
| **BF8** | Le service d’assistance retrouve un ticket par son numéro. |
| **BF9** | Le service d’assistance retire un ticket du registre. |
| **BF10** | Le service d’assistance consulte la liste des tickets enregistrés. |
| **BF11** | Le service d’assistance consulte le nombre de tickets enregistrés. |
| **BF12** | Le service d’assistance recherche des tickets par un texte contenu dans leur titre. |
| **BF13** | Le service d’assistance consulte les tickets regroupés par état. |

Ces acteurs expriment les responsabilités métier. L’authentification et le contrôle de l’identité de la personne qui réalise une action sont hors périmètre.

---

## 6. Règles métier des tickets

| Référence | Règle |
| :--- | :--- |
| **RG1** | La résolution d’un ticket qui n’est pas `EN_COURS` est interdite. |
| **RG2** | La prise en charge d’un ticket qui n’est pas `OUVERT` est interdite. |
| **RG3** | La prise en charge d’un ticket sans technicien affecté est interdite. |
| **RG4** | Une affectation sans technicien est refusée ; elle ne doit pas supprimer une affectation existante. |
| **RG5** | Un ticket `RESOLU` ne peut plus être réaffecté, repris ou résolu à nouveau. Aucune réouverture n’est prévue. |
| **RG6** | Une action refusée doit produire un message explicite et laisser les informations du ticket inchangées. |
| **RG7** | À sa déclaration, un ticket est `OUVERT` et sans technicien. Le cycle autorisé est `OUVERT` → `EN_COURS` → `RESOLU`. |
| **RG8** | L’affectation ou le remplacement d’un technicien ne change pas l’état du ticket. Le remplacement est autorisé tant que le ticket n’est pas résolu. |
| **RG9** | Le numéro d’un ticket est strictement positif et ne change pas. Il identifie le ticket indépendamment de sa catégorie. |
| **RG10** | Une déclaration dont une information obligatoire définie en section 4 est absente ou vide est refusée. Les numéros des personnes et des lieux sont également strictement positifs. |
| **RG11** | La priorité d’un ticket appartient aux valeurs `HAUTE`, `MOYENNE` ou `BASSE`. |

Le tableau suivant précise les actions autorisées et les refus associés.

| Situation | Action | Résultat | Règles |
| :--- | :--- | :--- | :--- |
| Déclaration | Déclarer un ticket | `OUVERT`, sans technicien | RG7 |
| `OUVERT` | Affecter un technicien | État inchangé | RG4, RG8 |
| `OUVERT` avec technicien | Prendre en charge | `EN_COURS` | RG2, RG3, RG7 |
| `OUVERT` sans technicien | Prendre en charge | Refus | RG3, RG6 |
| `OUVERT` | Résoudre | Refus | RG1, RG6 |
| `EN_COURS` | Changer de technicien | État inchangé | RG4, RG8 |
| `EN_COURS` | Prendre en charge à nouveau | Refus | RG2, RG6 |
| `EN_COURS` | Résoudre | `RESOLU` | RG1, RG7 |
| `RESOLU` | Affecter, reprendre ou résoudre à nouveau | Refus | RG1, RG2, RG5, RG6 |

---

## 7. Règles métier du registre

| Référence | Règle |
| :--- | :--- |
| **RG12** | L’enregistrement d’un numéro déjà présent est refusé, sans remplacement du ticket initial ni modification du nombre de tickets. |
| **RG13** | Une recherche ou un retrait sur un numéro absent signale cette absence et ne modifie pas les autres tickets. |
| **RG14** | Consulter les tickets, effectuer une recherche ou consulter un regroupement ne modifie pas le registre. Une modification de la présentation des résultats ne doit ni ajouter ni supprimer de tickets dans le registre. |
| **RG15** | La recherche par titre ignore les majuscules et les minuscules. Les résultats suivent l’ordre alphabétique des titres, sans distinction de casse, puis l’ordre croissant des numéros lorsque les titres sont équivalents. Sans correspondance, le résultat est vide. Un texte de recherche vide correspond à tous les titres. |
| **RG16** | Le regroupement présente les trois états, même lorsqu’un groupe ne contient aucun ticket. Chaque ticket figure dans le groupe correspondant à son état au moment de la consultation. |
| **RG17** | Un enregistrement sans ticket ou sans numéro, une recherche ou un retrait sans numéro, ainsi qu’une recherche par titre sans texte renseigné sont refusés. Le numéro d’enregistrement doit correspondre au numéro du ticket. Un texte vide est distinct d’un texte absent. |

---

## 8. Critères de Validation et Livrables

### Critères de validation

Les scénarios de validation couvrent la déclaration des deux catégories, l’affectation, le cycle de traitement et les refus définis par les règles métier. Le rapport identifie chaque essai et précise les besoins et règles qu’il vérifie.

Les dix essais du registre portent sur les critères suivants :

| Essai | Critère | Références |
| :--- | :--- | :--- |
| **P6.1** | Registre vide : aucun ticket et nombre égal à zéro. | BF10, BF11 |
| **P6.2** | Enregistrement d’un ticket et augmentation du nombre de tickets. | BF7, BF11 |
| **P6.3** | Doublon refusé et ticket initial conservé. | BF7, RG12 |
| **P6.4** | Recherche d’un numéro présent. | BF8 |
| **P6.5** | Recherche d’un numéro absent, sans modification du registre. | BF8, RG13 |
| **P6.6** | Retrait d’un ticket présent et diminution du nombre de tickets. | BF9, BF11 |
| **P6.7** | Retrait d’un numéro absent, sans modification du registre. | BF9, RG13 |
| **P6.8** | Protection du registre lors de la consultation de la liste. | BF10, RG14 |
| **P6.9** | Recherche par titre avec une casse différente, sans modification du registre. | BF12, RG14, RG15 |
| **P6.10** | Recherche par titre sans correspondance, sans modification du registre. | BF12, RG14, RG15 |

Le tri de plusieurs résultats et les regroupements sont vérifiés par des scénarios complémentaires associés à BF12, BF13, RG14, RG15 et RG16.

### Livrables du Projet
* Code Java
* Scénarios d’essai
* Diagramme de classes
* README présentant les choix de structures
* Cahier des charges et Rapport
* Commit Git : `Ajoute catalogue et collections` avec le tag `jalon-collections`


---

## 9. Contraintes imposées

| Référence | Contrainte |
| :--- | :--- |
| **CT1** | Le programme est réalisé en Java, dans le cadre des notions de programmation orientée objet étudiées en cours. |
| **CT2** | La démonstration et les résultats sont présentés en console, sans interface graphique. Aucun menu interactif n’est exigé. |
| **CT3** | Les données sont conservées uniquement pendant l’exécution, sans base de données ni sauvegarde persistante. |
| **CT4** | Le code et les documents du projet sont versionnés dans un dépôt GitHub commun au groupe. |

Ces contraintes fixent le cadre du projet. Le choix des classes et des structures de stockage est présenté dans le rapport de conception.
