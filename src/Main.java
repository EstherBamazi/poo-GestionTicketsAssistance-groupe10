public class Main {
    public static void main(String[] args) {
        Lieu salleB65 = new Lieu(65, "salleB6.5(Projet Java)", "Batiment Principal");
        Lieu salleB67 = new Lieu(67, "salleB6.7(Projet Python)", "Batiment Principal");

        Utilisateur sidibe = new Utilisateur(56, "Sidibe", "sidibe@2IE.bf");
        Utilisateur bernadette = new Utilisateur(75, "Bernadette", "bernadette@2IE.bf");

        Technicien superBob = new Technicien(743, "Bob le depanneur", "bob@tech2IE.bf");

        Ticket ticketSidibe = new TicketIncident("probleme d'affichage",
                "L'affichage de l'ecran est pixelise", "Urgent", sidibe, salleB65,
                "Ecran salle B6.5");

        Ticket ticketBernadette = new TicketDemandeService("plus d'internet",
                "le wifi est une theorie ou une realite dans le Batiment B", "necessaire",
                bernadette, salleB67, "Reparation reseau Wifi");

        System.out.println("Evolution du ticket de Sidibe");
        ticketSidibe.affiche();
        ticketSidibe.prendreEnCharge(superBob);
        ticketSidibe.affiche();
        ticketSidibe.resolu();
        ticketSidibe.affiche();

        System.out.println();
        System.out.println("Evolution du ticket de Bernadette");
        ticketBernadette.affiche();
        ticketBernadette.prendreEnCharge(superBob);
        ticketBernadette.affiche();
        ticketBernadette.resolu();
        ticketBernadette.affiche();

        System.out.println();
        System.out.println("Parcours polymorphe de tous les tickets");
        Ticket[] tickets = { ticketSidibe, ticketBernadette };
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }
}