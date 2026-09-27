public class Main {
    public static void main(String[] args) {

        // --- Création des objets de base ---
        
        Lieu salleB65 = new Lieu(65, "salleB6.5(Projet Java)", "Batiment Principal");
        Lieu salleB67 = new Lieu(67, "salleB6.7(Projet Python)", "Batiment Principal");

        Utilisateur sidibe = new Utilisateur(56, "Sidibe", "sidibe@2IE.bf");
        Utilisateur bernadette = new Utilisateur(75, "Bernadette", "bernadette@2IE.bf");

        Technicien superBob = new Technicien(743, "Bob le depanneur", "bob@tech2IE.bf");

        Ticket ticketSidibe = new TicketIncident(101, "Probleme d'affichage",
                "L'affichage de l'ecran est pixelise", "HAUTE",
                sidibe, salleB65, "Ecran salle B6.5");

        Ticket ticketBernadette = new TicketDemandeService(102, "Plus d'internet",
                "Le wifi ne fonctionne plus dans le batiment B", "MOYENNE",
                bernadette, salleB67, "Reparation reseau Wifi");


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
        } catch (IllegalStateException e) {
            System.out.println("Message de l'exception : " + e.getMessage());
        }
        System.out.println();

        // Refus 2 : prendreEnCharge() sur un ticket déjà résolu
        
        System.out.println("=== c2. Refus : Prise en charge sur un ticket déjà résolu ===");
        try {
            ticketSidibe.prendreEnCharge();
        } catch (Exception e) {
            System.out.println("Message de l'exception : " + e.getMessage());
        }


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
        } catch (IllegalStateException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        System.out.println("Etat inchange : " + ticketSidibe.getEtat());
        System.out.println();



        // cas 5 (limite) : prise en charge sans technicien


        System.out.println("c 5 : prise en charge refusee sans technicien assigne ===");
        Ticket ticketSansTechnicien = new TicketIncident(103, "Imprimante bloquee",
                "L'imprimante affiche une erreur papier", "BASSE",
                sidibe, salleB65, "Imprimante salle B6.5");
        try {
            ticketSansTechnicien.prendreEnCharge();
            System.out.println("Erreur : la prise en charge aurait du etre refusee !");
        } catch (IllegalStateException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        System.out.println("Etat inchange : " + ticketSansTechnicien.getEtat());
    }
}


