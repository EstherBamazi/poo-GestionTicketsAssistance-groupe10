public class Main {
    public static void main(String[] args) {
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

        System.out.println("Evolution du ticket de Sidibe");
        System.out.println(ticketSidibe);
        ticketSidibe.assigner(superBob);
        ticketSidibe.prendreEnCharge();
        System.out.println(ticketSidibe);
        ticketSidibe.resoudre();
        System.out.println(ticketSidibe);

        System.out.println();
        System.out.println("Un ticket ouvert ne peut pas etre resolu directement");
        System.out.println("Etat de t2 avant :" + ticketBernadette.getEtat());
        //jai modifier le djidji a la ligne 30
        try {
            ticketBernadette.resoudre();
            System.out.println("Erreur : la resolution aurait du etre refusee !");
        } catch (IllegalStateException e) {
            System.out.println("Resolution refusee comme prevu : " + e.getMessage());
        }

        System.out.println("Etat du ticket de Bernadette apres :" + ticketBernadette.getEtat());
    }
}
