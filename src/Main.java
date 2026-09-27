import java.util.ArrayList;
import java.util.List;

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
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticketSidibe);
        tickets.add(ticketBernadette);

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
        } catch (Exception e) {
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
    }
}
