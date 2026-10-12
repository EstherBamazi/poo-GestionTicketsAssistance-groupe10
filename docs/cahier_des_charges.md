# Cahier des Charges — Système de Tickets d’Assistance

**Gestion de tickets d’assistance** • *Sujet C — Groupe 10*  
**Auteurs :** BAMAZI Yesuwaba Esther, LOUE Rayan, KABORE Cedric

---

## 1. Contexte et Objectifs

Notre projet vise à faciliter le suivi des demandes d’assistance dans un établissement universitaire. Le système doit centraliser les incidents et les demandes de service, identifier leur auteur et leur lieu, puis suivre leur traitement jusqu’à la résolution.

Un étudiant qui rencontre un problème doit pouvoir le signaler en précisant sa nature et son lieu. Le service d’assistance doit disposer des informations nécessaires pour affecter un technicien, suivre le traitement et connaître les demandes résolues. Le système s’inspire du fonctionnement d’un outil de gestion d’assistance comme **GLPI**.

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

## 5. Fonctions Attendues

| Fonction | Résultat attendu |
| :--- | :--- |
| **Déclarer une demande** | Créer un ticket incident ou une demande de service avec toutes les informations obligatoires. |
| **Affecter un technicien** | Associer un technicien à un ticket non résolu, sans changer son état. |
| **Prendre en charge** | Faire passer un ticket ouvert et affecté à l’état `EN_COURS`. |
| **Résoudre** | Faire passer un ticket `EN_COURS` à l’état `RESOLU`. |
| **Consulter** | Afficher les informations du ticket, son état et son délai cible. |
| **Gérer le registre** | Ajouter, retrouver par numéro, retirer et lister les tickets ; connaître leur nombre. |
| **Rechercher par titre** | Retrouver les titres contenant un mot-clé (insensible à la casse) et présenter un résultat trié. |
| **Regrouper par état** | Présenter ensemble les tickets ouverts, en cours et résolus. |

---

## 6. Règles de Gestion des États

| Situation | Action | Résultat |
| :--- | :--- | :--- |
| **Création** | Déclarer un ticket | `OUVERT`, sans technicien |
| **OUVERT** | Affecter un technicien | État inchangé |
| **OUVERT avec technicien** | Prendre en charge | `EN_COURS` |
| **OUVERT sans technicien** | Prendre en charge | **Refus** |
| **OUVERT** | Résoudre | **Refus** |
| **EN_COURS** | Changer de technicien | État inchangé |
| **EN_COURS** | Prendre en charge à nouveau | **Refus** |
| **EN_COURS** | Résoudre | `RESOLU` |
| **RESOLU** | Affecter, reprendre ou résoudre à nouveau | **Refus** |

* Une affectation nulle est refusée.
* Un ticket en cours de traitement doit conserver un technicien affecté. Le remplacement de ce technicien est autorisé tant que le ticket n’est pas résolu.
* Le cycle de traitement suit strictemen l'ordre `OUVERT` $\rightarrow$ `EN_COURS` $\rightarrow$ `RESOLU`. Aucune réouverture n’est prévue.
* Toute action refusée doit produire un message explicite et laisser le ticket inchangé.

---

## 7. Règles de Gestion du Registre

* **Identifiant :** Le numéro identifie un ticket indépendamment de sa catégorie. L’ajout d’un numéro déjà enregistré est refusé et ne remplace pas le ticket conservé.
* **Intégrité :** Une recherche ou un retrait sur un numéro absent doit signaler cette absence sans modifier les autres tickets.
* **Protection :** La consultation d’une liste, une recherche et un regroupement doivent préserver le contenu du registre (les collections retournées sont protégées contre toute modification directe).
* **Absence de résultat :** Une recherche sans correspondance retourne une liste vide.

---

## 8. Critères de Validation et Livrables

### Essais du Registre (10 essais obligatoires)
1. Registre vide
2. Ajout ordinaire
3. Doublon refusé
4. Recherche d’un numéro présent
5. Recherche d’un numéro absent
6. Retrait présent
7. Retrait absent
8. Protection de la liste retournée
9. Recherche textuelle (insensibilité à la casse)
10. Recherche textuelle sans correspondance (registre inchangé)

### Livrables du Projet
* Code Java
* Scénarios d’essai
* Diagramme de classes
* README présentant les choix de structures
* Cahier des charges et Rapport
* Commit Git : `Ajoute catalogue et collections` avec le tag `jalon-collections`