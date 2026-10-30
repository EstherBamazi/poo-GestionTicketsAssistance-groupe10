import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        testerInvariants();

        // --- Création des objets de base ---

        Lieu salleB65 = new Lieu(65, "salleB6.5(Projet Java)", "Batiment Principal");
        Lieu salleB67 = new Lieu(67, "salleB6.7(Projet Python)", "Batiment Principal");
        Lieu chambre14 = new Lieu(14, "chambre14(dortoir)", "Residance Campus Filles");


        Utilisateur sidibe = new Utilisateur(56, "Sidibe", "sidibe@2IE.bf");
        Utilisateur bernadette = new Utilisateur(75, "Bernadette", "bernadette@2IE.bf");
        Utilisateur test = new Utilisateur(01, "test", "test@2IE.bf");

        Technicien superBob = new Technicien(743, "Bob le depanneur", "bob@tech2IE.bf");
        Technicien Marianne = new Technicien(747, "Mari-Anne ", "marie.anne@tech2IE.bf");

        Ticket ticketSidibe = new TicketIncident(101, "Probleme d'affichage",
                "L'affichage de l'ecran est pixelise", Priorite.HAUTE,
                sidibe, salleB65, "Ecran salle B6.5");

        Ticket ticketBernadette = new TicketDemandeService(102, "Plus d'internet",
                "Le wifi ne fonctionne plus dans le batiment B", Priorite.MOYENNE,
                bernadette, salleB67, "Reparation reseau Wifi");

        // ==========================================
        // Tests pour changer Priorite
        // ==========================================
        System.out.println("=== Tests pour changerPriorite ===");

        // Creation d'un ticket de test
        Ticket ticketTest = new TicketIncident(101, "Probleme de plomberie",
                "Les toilettes sont bouchees!!", Priorite.HAUTE,
                test, chambre14, "WC chambre 14");
        ticketTest.assigner(Marianne);
        ticketTest.prendreEnCharge();

        System.out.println("Test sur le ticket " + ticketTest.toString());

        Priorite prioriteAvant = ticketTest.getPriorite();

        // 1. Refus d'une priorité nulle
        try {
            ticketTest.changerPriorite(null);

        } catch (IllegalArgumentException e) {
            System.out.println("OK : priorité nulle refusée -> " + e.getMessage());
        }
        System.out.println(
                "Priorité inchangée : " + ticketTest.getPriorite()
        );
        System.out.println();


        // 2. Changement valide
        ticketTest.changerPriorite(Priorite.BASSE);

        if (ticketTest  .getPriorite() != Priorite.BASSE) {
            throw new AssertionError(
                    "La priorité aurait dû devenir BASSE"
            );
        }

        System.out.println(
                "OK : priorité modifiée -> " + ticketTest.getPriorite()
        );
        System.out.println();


        // 3. Refus sur ticket résolu
        ticketTest.resoudre();

        Priorite prioriteAvantRefus = ticketTest.getPriorite();

        try {
            ticketTest.changerPriorite(Priorite.HAUTE);
            throw new AssertionError(
                    "Une TransitionInvalideException était attendue"
            );
        } catch (TransitionInvalideException e) {
            System.out.println(
                    "OK : changement refusé sur ticket résolu -> "
                            + e.getMessage()
            );
        }

        if (ticketTest.getPriorite() != prioriteAvantRefus) {
            throw new AssertionError(
                    "La priorité a changé malgré le refus"
            );
        }

        System.out.println(
                "Priorité inchangée : " + ticketSidibe.getPriorite()
        );


        // ==========================================
        // a) Le polymorphisme
        // ==========================================

        System.out.println("=== a.Démonstration du polymorphisme ===");
        Ticket[] tickets = {ticketSidibe, ticketBernadette};

        for (Ticket t : tickets) {
            System.out.println(t);
        }
        System.out.println();


        // ==========================================
        // b) Un scénario normal
        // ==========================================

        System.out.println("=== b. Scénario normal ===");
        System.out.println("État initial : " + ticketSidibe.getEtat());

        ticketSidibe.assigner(superBob);
        ticketSidibe.prendreEnCharge();
        System.out.println("État après prise en charge : " + ticketSidibe.getEtat());

        ticketSidibe.resoudre();
        System.out.println("État après résolution : " + ticketSidibe.getEtat());
        System.out.println();


        // ==========================================
        // c) Deux refus
        // ==========================================

        // Refus 1 : resoudre() sur un ticket jamais pris en charge

        System.out.println("=== c1. Refus : Résolution sur un ticket non pris en charge ===");
        try {
            ticketBernadette.resoudre();
            System.out.println("Erreur");
        } catch (TransitionInvalideException e) {
            System.out.println("Message de l'exception : " + e.getMessage());
        }
        System.out.println();

        // Refus 2 : prendreEnCharge() sur un ticket déjà résolu

        System.out.println("=== c2. Refus : Prise en charge sur un ticket déjà résolu ===");

        try {
            ticketSidibe.prendreEnCharge();
        } catch (TransitionInvalideException e) {
            System.out.println("Message de l'exception : " + e.getMessage());
        }

        System.out.println("Etat inchange : " + ticketSidibe.getEtat());
        System.out.println();

        System.out.println("=== c3 : cas d'un TicketDemandeService ===");
        Assignable aAffecter = ticketBernadette;
        aAffecter.assigner(superBob);
        System.out.println("Technicien assigne : " + aAffecter.estAssigne()
                + " (" + aAffecter.getTechnicien().getNom() + ")");

        ticketBernadette.prendreEnCharge();
        System.out.println("Etat apres prise en charge : " + ticketBernadette.getEtat());

        ticketBernadette.resoudre();
        System.out.println("Etat apres resolution : " + ticketBernadette.getEtat());
        System.out.println();



        // Cas : reprise d'un ticket deja resolu


        System.out.println("=== c4 : reprise refusee sur un ticket deja resolu ===");
        try {
            ticketSidibe.prendreEnCharge();
            System.out.println("Erreur : la reprise aurait du etre refusee !");
        } catch (TransitionInvalideException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        System.out.println("Etat inchange : " + ticketSidibe.getEtat());
        System.out.println();



        // Cas (limite) : prise en charge sans technicien


        System.out.println("==c 5 : prise en charge refusee sans technicien assigne ===");
        Ticket ticketSansTechnicien = new TicketIncident(103, "Imprimante bloquee",
                "L'imprimante affiche une erreur papier", Priorite.BASSE,
                sidibe, salleB65, "Imprimante salle B6.5");
        try {
            ticketSansTechnicien.prendreEnCharge();
            System.out.println("Erreur : la prise en charge aurait du etre refusee !");
        } catch (TransitionInvalideException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        System.out.println("Etat inchange : " + ticketSansTechnicien.getEtat());

        // Refuser null ne doit pas retirer le technicien d'un ticket en cours.
        ticketSansTechnicien.assigner(superBob);
        ticketSansTechnicien.prendreEnCharge();
        boolean nullRefuse = false;
        try {
            ticketSansTechnicien.assigner(null);
        } catch (IllegalArgumentException e) {
            nullRefuse = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }
        if (!nullRefuse || ticketSansTechnicien.getTechnicien() != superBob
                || ticketSansTechnicien.getEtat() != EtatTicket.EN_COURS) {
            throw new AssertionError("Echec : affectation nulle ou ticket modifie.");
        }
        System.out.println("OK : null refuse, technicien et etat conserves.");

        // Une affectation apres resolution doit lever notre exception.
        ticketSansTechnicien.resoudre();
        Technicien autreTechnicien = new Technicien(744, "Alice", "alice@tech2IE.bf");
        boolean affectationRefusee = false;
        try {
            ticketSansTechnicien.assigner(autreTechnicien);
        } catch (TransitionInvalideException e) {
            affectationRefusee = true;
            System.out.println("Refus attendu : " + e.getMessage());
        }
        if (!affectationRefusee || ticketSansTechnicien.getTechnicien() != superBob
                || ticketSansTechnicien.getEtat() != EtatTicket.RESOLU) {
            throw new AssertionError("Echec : affectation acceptee apres resolution.");
        }
        System.out.println("OK : affectation refusee, ticket resolu inchange.");

        // P6 : les dix essais sont directement dans main.
        System.out.println("\n=== P6 : essais du registre ===");
        RegistreTicketsEnMemoire registre = new RegistreTicketsEnMemoire();

        System.out.println("P6.1 - Registre vide");
        if (registre.taille() != 0 || !registre.lister().isEmpty()) {
            throw new AssertionError("Le registre doit etre vide.");
        }
        System.out.println("OK : taille 0 et liste vide.");

        System.out.println("P6.2 - Ajout du ticket 101");
        boolean ajout = registre.ajouter(101, ticketSidibe);
        if (!ajout || registre.taille() != 1) {
            throw new AssertionError("L'ajout doit reussir et la taille doit valoir 1.");
        }
        System.out.println("OK : ajout true, taille 1.");

        System.out.println("P6.3 - Doublon de numero 101");
        Ticket doublon = new TicketIncident(101, "Autre titre", "Autre description",
                Priorite.BASSE, sidibe, salleB65, "Autre ecran");
        boolean ajoutDoublon = registre.ajouter(101, doublon);
        Optional<Ticket> conserve = registre.chercher(101);
        if (ajoutDoublon || registre.taille() != 1
                || conserve.isEmpty() || conserve.get() != ticketSidibe) {
            throw new AssertionError("Le doublon ne doit pas remplacer le ticket initial.");
        }
        System.out.println("OK : doublon false, taille 1, meme objet initial conserve.");

        System.out.println("P6.4 - Recherche presente : 101");
        Optional<Ticket> trouve = registre.chercher(101);
        if (!trouve.isPresent() || trouve.get() != ticketSidibe || registre.taille() != 1) {
            throw new AssertionError("Le ticket 101 doit etre retrouve sans modification.");
        }
        System.out.println("OK : ticket 101 present.");

        System.out.println("P6.5 - Recherche absente : 999");
        if (!registre.chercher(999).isEmpty() || registre.taille() != 1
                || registre.chercher(101).get() != ticketSidibe) {
            throw new AssertionError("La recherche absente doit laisser le registre inchange.");
        }
        System.out.println("OK : Optional vide, registre inchange.");

        System.out.println("P6.6 - Retrait present : 101");
        boolean retrait = registre.retirer(101);
        if (!retrait || registre.taille() != 0 || registre.chercher(101).isPresent()) {
            throw new AssertionError("Le ticket doit etre retire et la taille diminuer.");
        }
        System.out.println("OK : retrait true, taille 0.");

        System.out.println("P6.7 - Retrait absent : 999");
        // Un ticket temoin permet de verifier qu'un retrait absent le preserve.
        registre.ajouter(101, ticketSidibe);
        boolean retraitAbsent = registre.retirer(999);
        if (retraitAbsent || registre.taille() != 1
                || registre.chercher(101).get() != ticketSidibe) {
            throw new AssertionError("Le retrait absent doit laisser le registre inchange.");
        }
        System.out.println("OK : retrait false, taille 1 et ticket initial conserve.");

        System.out.println("P6.8 - Ajout interdit dans la liste retournee");
        List<Ticket> copie = registre.lister();
        boolean listeProtegee = false;
        try {
            copie.add(ticketBernadette);
        } catch (UnsupportedOperationException e) {
            listeProtegee = true;
        }
        if (!listeProtegee || copie.size() != 1 || registre.taille() != 1
                || registre.chercher(101).get() != ticketSidibe
                || registre.chercher(102).isPresent()) {
            throw new AssertionError("La liste doit etre non modifiable et le registre preserve.");
        }
        System.out.println("OK : UnsupportedOperationException, taille du registre inchangee.");

        System.out.println("P6.9 - Recherche par titre : PROBLEME");
        List<Ticket> resultat = registre.rechercherParTitre("PROBLEME");
        if (resultat.size() != 1 || resultat.get(0) != ticketSidibe
                || !resultat.equals(registre.rechercherParTitre("probleme"))
                || registre.taille() != 1) {
            throw new AssertionError("PROBLEME doit retrouver Probleme d'affichage.");
        }
        resultat.clear();
        if (registre.taille() != 1 || registre.chercher(101).get() != ticketSidibe) {
            throw new AssertionError("Modifier le resultat ne doit pas toucher au registre.");
        }
        System.out.println("OK : Probleme d'affichage retrouve, casse ignoree, registre preserve.");

        System.out.println("P6.10 - Recherche par titre sans resultat");
        List<Ticket> aucun = registre.rechercherParTitre("introuvable");
        if (!aucun.isEmpty() || registre.taille() != 1
                || registre.chercher(101).get() != ticketSidibe) {
            throw new AssertionError("Le resultat doit etre vide et le registre inchange.");
        }
        System.out.println("OK : liste vide, registre inchange.");
        System.out.println("P6 : 10 essais sur 10 reussis.");

        // P5 : les tickets des scenarios precedents sont resolus.

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
        if (!tries.equals(List.of(enCours, ouvertBis, ticketSidibe))) {
            throw new AssertionError("Le tri doit suivre le titre puis le numero.");
        }
        System.out.println("OK : tri par titre puis numero.");

        System.out.println("\n=== P5 : regroupement par etat ===");
        Map<EtatTicket, List<Ticket>> groupes = registre.regrouperParEtat();
        System.out.println(groupes);
        if (groupes.size() != 3 || !groupes.get(EtatTicket.EN_COURS).equals(List.of(enCours))
                || !groupes.get(EtatTicket.RESOLU).equals(List.of(ticketSidibe))
                || groupes.get(EtatTicket.OUVERT).size() != 2
                || !groupes.get(EtatTicket.OUVERT).containsAll(List.of(ouvert, ouvertBis))) {
            throw new AssertionError("Les tickets doivent etre regroupes selon leur etat.");
        }
        groupes.get(EtatTicket.OUVERT).clear();
        groupes.clear();
        if (registre.taille() != 4
                || registre.regrouperParEtat().get(EtatTicket.OUVERT).size() != 2) {
            throw new AssertionError("Modifier les groupes ne doit pas toucher au registre.");
        }
        Map<EtatTicket, List<Ticket>> groupesVides =
                new RegistreTicketsEnMemoire().regrouperParEtat();
        if (groupesVides.size() != 3
                || !groupesVides.containsKey(EtatTicket.OUVERT)
                || !groupesVides.containsKey(EtatTicket.EN_COURS)
                || !groupesVides.containsKey(EtatTicket.RESOLU)
                || !groupesVides.get(EtatTicket.OUVERT).isEmpty()
                || !groupesVides.get(EtatTicket.EN_COURS).isEmpty()
                || !groupesVides.get(EtatTicket.RESOLU).isEmpty()) {
            throw new AssertionError("Un registre vide doit donner trois groupes vides.");
        }
        System.out.println("OK : registre vide, trois cles et trois listes vides.");
        System.out.println("OK : trois etats representes et registre protege.");

        int refus = 0;
        try { registre.ajouter(null, ticketSidibe); } catch (IllegalArgumentException e) { refus++; }
        try { registre.ajouter(107, null); } catch (IllegalArgumentException e) { refus++; }
        try { registre.ajouter(107, ticketSidibe); } catch (IllegalArgumentException e) { refus++; }
        try { registre.rechercherParTitre(null); } catch (IllegalArgumentException e) { refus++; }
        if (refus != 4 || registre.taille() != 4 || registre.chercher(107).isPresent()) {
            throw new AssertionError("Les arguments invalides doivent etre refuses sans modification.");
        }
        System.out.println("OK : null et cle incoherente refuses sans modification.");

        ComparaisonRecherche.executer(sidibe, salleB65);
    }
     // ==================================================
// TESTS DU JALON 1 : MODELES ET INVARIANTS
// ==================================================

        public static void testerInvariants() {

            System.out.println("\n===== TESTS DU JALON 1 =====");

            Utilisateur Mariam =
                    new Utilisateur(900, "Mariam", "mariam@2IE.bf");

            Lieu salleC2 =
                    new Lieu(900, "salleC2", "Batiment Principal");

            // Test 1 : numéro du ticket <= 0
            verifierRejet("Numero du ticket <= 0", () ->
                    new TicketIncident(
                            0, "Probme de climatisation", "la clim ne fonctionne pas",
                            "HAUTE", Mariam, salleC2, "la clim"
                    )
            );

            // Test 2 : titre vide
            verifierRejet("Titre vide", () ->
                    new TicketIncident(
                            901, "   ", "la clim ne fonctionne pas",
                            "HAUTE", Mariam, salleC2, "la clim"
                    )
            );

            // Test 3 : priorité null
            verifierRejet("Priorite null", () ->
                    new TicketIncident(
                            902, "Probleme de climatisation", "la clim ne fonctionne pas",
                            null, Mariam, salleC2, "la clim"
                    )
            );

            // Test 4 : auteur null
            verifierRejet("Auteur null", () ->
                    new TicketIncident(
                            903, "Probleme", "Description valide",
                            "HAUTE", null, salleC2, "Ecran"
                    )
            );


            // Test 5 : email vide
            verifierRejet("Email vide", () ->
                    new Utilisateur(
                            905, "Mariam", "   "
                    )
            );

            // Test 6 : numéro du lieu <= 0
            verifierRejet("Numero du lieu <= 0", () ->
                    new Lieu(
                            0, "SalleC2", "Batiment Principal"
                    )
            );

            System.out.println("===== FIN DES TESTS =====\n");
        }


        // Méthode qui vérifie qu'une donnée invalide est refusée
        private static void verifierRejet(String nomTest, Runnable operation) {

            try {
                operation.run();

                System.out.println("ECHEC : " + nomTest
                        + " - aucune exception n'a ete levee.");

            } catch (IllegalArgumentException e) {
                System.out.println("REUSSI : " + nomTest);

            } catch (Exception e) {
                System.out.println("ECHEC : " + nomTest
                        + " - exception inattendue : "
                        + e.getClass().getSimpleName());
            }
        }
    }
