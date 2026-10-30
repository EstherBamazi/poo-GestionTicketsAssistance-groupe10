import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        testerInvariants();

        // --- Creation des objets de base ---
        Lieu salleB65 = new Lieu(65, "salleB6.5(Projet Java)", "Batiment Principal");
        Lieu salleB67 = new Lieu(67, "salleB6.7(Projet Python)", "Batiment Principal");
        Lieu chambre14 = new Lieu(14, "chambre14(dortoir)", "Residance Campus Filles");

        Utilisateur sidibe = new Utilisateur(56, "Sidibe", "sidibe@2IE.bf");
        Utilisateur bernadette = new Utilisateur(75, "Bernadette", "bernadette@2IE.bf");
        Utilisateur test = new Utilisateur(1, "test", "test@2IE.bf");

        Technicien superBob = new Technicien(743, "Bob le depanneur", "bob@tech2IE.bf");
        Technicien marianne = new Technicien(747, "Mari-Anne", "marie.anne@tech2IE.bf");

        Ticket ticketSidibe = new TicketIncident(101, "Probleme d'affichage",
                "L'affichage de l'ecran est pixelise", Priorite.HAUTE,
                sidibe, salleB65, "Ecran salle B6.5");

        Ticket ticketBernadette = new TicketDemandeService(102, "Plus d'internet",
                "Le wifi ne fonctionne plus dans le batiment B", Priorite.MOYENNE,
                bernadette, salleB67, "Reparation reseau Wifi");

        // ==========================================
        // Tests pour changerPriorite
        // ==========================================
        System.out.println("=== Tests pour changerPriorite ===");

        Ticket ticketTest = new TicketIncident(107, "Probleme de plomberie",
                "Les toilettes sont bouchees", Priorite.HAUTE,
                test, chambre14, "WC chambre 14");
        ticketTest.assigner(marianne);
        ticketTest.prendreEnCharge();

        Priorite prioriteAvant = ticketTest.getPriorite();
        boolean prioriteNulleRefusee = false;

        try {
            ticketTest.changerPriorite(null);
        } catch (IllegalArgumentException e) {
            prioriteNulleRefusee = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "priorite null refusee et priorite inchangee",
                prioriteNulleRefusee && ticketTest.getPriorite() == prioriteAvant
        );

        ticketTest.changerPriorite(Priorite.BASSE);
        verifierCondition(
                "changement valide de priorite vers BASSE",
                ticketTest.getPriorite() == Priorite.BASSE
        );

        ticketTest.resoudre();
        Priorite prioriteAvantRefus = ticketTest.getPriorite();
        int tailleHistoriqueAvantRefus = ticketTest.getHistorique().size();
        boolean changementRefuse = false;

        try {
            ticketTest.changerPriorite(Priorite.HAUTE);
        } catch (TransitionInvalideException e) {
            changementRefuse = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "priorite d'un ticket resolu inchangee",
                changementRefuse
                        && ticketTest.getPriorite() == prioriteAvantRefus
                        && ticketTest.getHistorique().size() == tailleHistoriqueAvantRefus
        );
        System.out.println();

        // ==========================================
        // a) Polymorphisme
        // ==========================================
        System.out.println("=== a. Demonstration du polymorphisme ===");
        Ticket[] tickets = {ticketSidibe, ticketBernadette};

        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
        System.out.println();

        // ==========================================
        // b) Scenario normal
        // ==========================================
        System.out.println("=== b. Scenario normal ===");
        System.out.println("Etat initial : " + ticketSidibe.getEtat());

        ticketSidibe.assigner(superBob);
        ticketSidibe.prendreEnCharge();
        System.out.println("Etat apres prise en charge : " + ticketSidibe.getEtat());

        ticketSidibe.resoudre();
        System.out.println("Etat apres resolution : " + ticketSidibe.getEtat());

        verifierCondition(
                "cycle OUVERT -> EN_COURS -> RESOLU",
                ticketSidibe.getEtat() == EtatTicket.RESOLU
        );

        System.out.println("Historique du ticket 101 :");
        for (Evenement evenement : ticketSidibe.getHistorique()) {
            System.out.println("- " + evenement);
        }
        verifierCondition(
                "historique de creation, affectation, prise en charge et resolution",
                ticketSidibe.getHistorique().size() == 4
        );
        System.out.println();

        // ==========================================
        // c) Cas de refus et limites
        // ==========================================
        System.out.println("=== c1. Resolution sur un ticket non pris en charge ===");
        EtatTicket etatAvantRefus = ticketBernadette.getEtat();
        boolean resolutionRefusee = false;

        try {
            ticketBernadette.resoudre();
        } catch (TransitionInvalideException e) {
            resolutionRefusee = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "resolution refusee et etat inchange",
                resolutionRefusee && ticketBernadette.getEtat() == etatAvantRefus
        );
        System.out.println();

        System.out.println("=== c2. Prise en charge sur un ticket deja resolu ===");
        int historiqueAvantReprise = ticketSidibe.getHistorique().size();
        boolean repriseRefusee = false;

        try {
            ticketSidibe.prendreEnCharge();
        } catch (TransitionInvalideException e) {
            repriseRefusee = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "reprise refusee sans modification de l'etat ni de l'historique",
                repriseRefusee
                        && ticketSidibe.getEtat() == EtatTicket.RESOLU
                        && ticketSidibe.getHistorique().size() == historiqueAvantReprise
        );
        System.out.println();

        System.out.println("=== c3. Cas d'un TicketDemandeService ===");
        Assignable aAffecter = ticketBernadette;
        aAffecter.assigner(superBob);
        System.out.println("Technicien assigne : " + aAffecter.estAssigne()
                + " (" + aAffecter.getTechnicien().getNom() + ")");

        ticketBernadette.prendreEnCharge();
        ticketBernadette.resoudre();
        verifierCondition(
                "TicketDemandeService resolu",
                ticketBernadette.getEtat() == EtatTicket.RESOLU
        );
        System.out.println();

        System.out.println("=== c4. Prise en charge sans technicien ===");
        Ticket ticketSansTechnicien = new TicketIncident(103, "Imprimante bloquee",
                "L'imprimante affiche une erreur papier", Priorite.BASSE,
                sidibe, salleB65, "Imprimante salle B6.5");

        boolean sansTechnicienRefuse = false;
        try {
            ticketSansTechnicien.prendreEnCharge();
        } catch (TransitionInvalideException e) {
            sansTechnicienRefuse = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "prise en charge sans technicien refusee",
                sansTechnicienRefuse && ticketSansTechnicien.getEtat() == EtatTicket.OUVERT
        );

        ticketSansTechnicien.assigner(superBob);
        ticketSansTechnicien.prendreEnCharge();

        boolean nullRefuse = false;
        try {
            ticketSansTechnicien.assigner(null);
        } catch (IllegalArgumentException e) {
            nullRefuse = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "affectation nulle refusee sans modifier le ticket",
                nullRefuse
                        && ticketSansTechnicien.getTechnicien() == superBob
                        && ticketSansTechnicien.getEtat() == EtatTicket.EN_COURS
        );

        ticketSansTechnicien.resoudre();
        Technicien autreTechnicien = new Technicien(744, "Alice", "alice@tech2IE.bf");
        boolean affectationRefusee = false;
        int historiqueAvantAffectationRefusee = ticketSansTechnicien.getHistorique().size();

        try {
            ticketSansTechnicien.assigner(autreTechnicien);
        } catch (TransitionInvalideException e) {
            affectationRefusee = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }

        verifierCondition(
                "affectation apres resolution refusee et historique inchange",
                affectationRefusee
                        && ticketSansTechnicien.getTechnicien() == superBob
                        && ticketSansTechnicien.getEtat() == EtatTicket.RESOLU
                        && ticketSansTechnicien.getHistorique().size() == historiqueAvantAffectationRefusee
        );

        // ==========================================
        // P6 : essais du registre
        // ==========================================
        System.out.println("\n=== P6 : essais du registre ===");
        RegistreTicketsEnMemoire registre = new RegistreTicketsEnMemoire();
        int essaisRegistreReussis = 0;

        if (verifierCondition("P6.1 registre vide",
                registre.taille() == 0 && registre.lister().isEmpty())) {
            essaisRegistreReussis++;
        }

        boolean ajout = registre.ajouter(101, ticketSidibe);
        if (verifierCondition("P6.2 ajout du ticket 101",
                ajout && registre.taille() == 1)) {
            essaisRegistreReussis++;
        }

        Ticket doublon = new TicketIncident(101, "Autre titre", "Autre description",
                Priorite.BASSE, sidibe, salleB65, "Autre ecran");
        boolean ajoutDoublon = registre.ajouter(101, doublon);
        Optional<Ticket> conserve = registre.chercher(101);
        if (verifierCondition("P6.3 doublon refuse",
                !ajoutDoublon
                        && registre.taille() == 1
                        && conserve.isPresent()
                        && conserve.get() == ticketSidibe)) {
            essaisRegistreReussis++;
        }

        Optional<Ticket> trouve = registre.chercher(101);
        if (verifierCondition("P6.4 recherche presente",
                trouve.isPresent() && trouve.get() == ticketSidibe && registre.taille() == 1)) {
            essaisRegistreReussis++;
        }

        if (verifierCondition("P6.5 recherche absente",
                registre.chercher(999).isEmpty()
                        && registre.taille() == 1
                        && registre.chercher(101).get() == ticketSidibe)) {
            essaisRegistreReussis++;
        }

        boolean retrait = registre.retirer(101);
        if (verifierCondition("P6.6 retrait present",
                retrait && registre.taille() == 0 && registre.chercher(101).isEmpty())) {
            essaisRegistreReussis++;
        }

        registre.ajouter(101, ticketSidibe);
        boolean retraitAbsent = registre.retirer(999);
        if (verifierCondition("P6.7 retrait absent",
                !retraitAbsent
                        && registre.taille() == 1
                        && registre.chercher(101).get() == ticketSidibe)) {
            essaisRegistreReussis++;
        }

        List<Ticket> copie = registre.lister();
        boolean listeProtegee = false;
        try {
            copie.add(ticketBernadette);
        } catch (UnsupportedOperationException e) {
            listeProtegee = true;
        }

        if (verifierCondition("P6.8 liste retournee protegee",
                listeProtegee
                        && copie.size() == 1
                        && registre.taille() == 1
                        && registre.chercher(101).get() == ticketSidibe
                        && registre.chercher(102).isEmpty())) {
            essaisRegistreReussis++;
        }

        List<Ticket> resultat = registre.rechercherParTitre("PROBLEME");
        boolean rechercheTitreCorrecte = resultat.size() == 1
                && resultat.get(0) == ticketSidibe
                && resultat.equals(registre.rechercherParTitre("probleme"))
                && registre.taille() == 1;

        resultat.clear();
        boolean registreProtege = registre.taille() == 1
                && registre.chercher(101).get() == ticketSidibe;

        if (verifierCondition("P6.9 recherche par titre et protection du registre",
                rechercheTitreCorrecte && registreProtege)) {
            essaisRegistreReussis++;
        }

        List<Ticket> aucun = registre.rechercherParTitre("introuvable");
        if (verifierCondition("P6.10 recherche par titre sans resultat",
                aucun.isEmpty()
                        && registre.taille() == 1
                        && registre.chercher(101).get() == ticketSidibe)) {
            essaisRegistreReussis++;
        }

        System.out.println("P6 : " + essaisRegistreReussis + " essais sur 10 reussis.");

        // ==========================================
        // Recherche triee et regroupement par etat
        // ==========================================
        Ticket enCours = new TicketIncident(104, "Probleme clavier", "Clavier bloque",
                Priorite.MOYENNE, sidibe, salleB65, "Clavier");
        enCours.assigner(superBob);
        enCours.prendreEnCharge();

        Ticket ouvert = new TicketDemandeService(105, "Installer Java", "Installation demandee",
                Priorite.BASSE, bernadette, salleB67, "Java");

        Ticket ouvertBis = new TicketIncident(106, "probleme CLAVIER", "Touches bloquees",
                Priorite.BASSE, sidibe, salleB65, "Autre clavier");

        registre.ajouter(104, enCours);
        registre.ajouter(105, ouvert);
        registre.ajouter(106, ouvertBis);

        List<Ticket> tries = registre.rechercherParTitre("PROBLEME");
        verifierCondition(
                "tri par titre puis numero",
                tries.equals(List.of(enCours, ouvertBis, ticketSidibe))
        );

        System.out.println("\n=== P5 : regroupement par etat ===");
        Map<EtatTicket, List<Ticket>> groupes = registre.regrouperParEtat();
        System.out.println(groupes);

        verifierCondition(
                "tickets regroupes selon leur etat",
                groupes.size() == 3
                        && groupes.get(EtatTicket.EN_COURS).equals(List.of(enCours))
                        && groupes.get(EtatTicket.RESOLU).equals(List.of(ticketSidibe))
                        && groupes.get(EtatTicket.OUVERT).size() == 2
                        && groupes.get(EtatTicket.OUVERT).containsAll(List.of(ouvert, ouvertBis))
        );

        groupes.get(EtatTicket.OUVERT).clear();
        groupes.clear();
        verifierCondition(
                "modifier les groupes retournes ne modifie pas le registre",
                registre.taille() == 4
                        && registre.regrouperParEtat().get(EtatTicket.OUVERT).size() == 2
        );

        Map<EtatTicket, List<Ticket>> groupesVides =
                new RegistreTicketsEnMemoire().regrouperParEtat();

        verifierCondition(
                "registre vide : trois groupes vides",
                groupesVides.size() == 3
                        && groupesVides.containsKey(EtatTicket.OUVERT)
                        && groupesVides.containsKey(EtatTicket.EN_COURS)
                        && groupesVides.containsKey(EtatTicket.RESOLU)
                        && groupesVides.get(EtatTicket.OUVERT).isEmpty()
                        && groupesVides.get(EtatTicket.EN_COURS).isEmpty()
                        && groupesVides.get(EtatTicket.RESOLU).isEmpty()
        );

        int refus = 0;
        try {
            registre.ajouter(null, ticketSidibe);
        } catch (IllegalArgumentException e) {
            refus++;
        }
        try {
            registre.ajouter(107, null);
        } catch (IllegalArgumentException e) {
            refus++;
        }
        try {
            registre.ajouter(107, ticketSidibe);
        } catch (IllegalArgumentException e) {
            refus++;
        }
        try {
            registre.rechercherParTitre(null);
        } catch (IllegalArgumentException e) {
            refus++;
        }

        verifierCondition(
                "arguments invalides refuses sans modification du registre",
                refus == 4 && registre.taille() == 4 && registre.chercher(107).isEmpty()
        );

        ComparaisonRecherche.executer(sidibe, salleB65);
    }

    // ==================================================
    // TESTS DU JALON 1 : MODELE ET INVARIANTS
    // ==================================================
    public static void testerInvariants() {
        System.out.println("\n===== TESTS DU JALON 1 =====");

        Utilisateur mariam = new Utilisateur(900, "Mariam", "mariam@2IE.bf");
        Lieu salleC2 = new Lieu(900, "salleC2", "Batiment Principal");

        // Test 1 : numero du ticket <= 0
        try {
            new TicketIncident(
                    0, "Probleme de climatisation", "La clim ne fonctionne pas",
                    Priorite.HAUTE, mariam, salleC2, "La clim"
            );
            System.out.println("ECHEC : numero du ticket <= 0 accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : numero du ticket <= 0 refuse");
        }

        // Test 2 : titre vide
        try {
            new TicketIncident(
                    901, "   ", "La clim ne fonctionne pas",
                    Priorite.HAUTE, mariam, salleC2, "La clim"
            );
            System.out.println("ECHEC : titre vide accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : titre vide refuse");
        }

        // Test 3 : priorite null
        try {
            new TicketIncident(
                    902, "Probleme de climatisation", "La clim ne fonctionne pas",
                    null, mariam, salleC2, "La clim"
            );
            System.out.println("ECHEC : priorite null acceptee");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : priorite null refusee");
        }

        // Test 4 : auteur null
        try {
            new TicketIncident(
                    903, "Probleme", "Description valide",
                    Priorite.HAUTE, null, salleC2, "Ecran"
            );
            System.out.println("ECHEC : auteur null accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : auteur null refuse");
        }

        // Test 5 : email vide
        try {
            new Utilisateur(905, "Mariam", "   ");
            System.out.println("ECHEC : email vide accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : email vide refuse");
        }

        // Test 6 : numero du lieu <= 0
        try {
            new Lieu(0, "SalleC2", "Batiment Principal");
            System.out.println("ECHEC : numero du lieu <= 0 accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("REUSSI : numero du lieu <= 0 refuse");
        }

        System.out.println("===== FIN DES TESTS =====\n");
    }

    // Petite methode de verification pour les tests manuels.
    // Elle utilise seulement un booleen et un if/else.
    private static boolean verifierCondition(String nomTest, boolean condition) {
        if (condition) {
            System.out.println("REUSSI : " + nomTest);
            return true;
        }

        System.out.println("ECHEC : " + nomTest);
        return false;
    }
}
