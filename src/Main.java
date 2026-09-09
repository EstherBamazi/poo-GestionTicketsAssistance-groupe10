//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Lieu salleB65 = new Lieu(65, "salleB6.5(Projet Java)", "Batiment Principal");
        Lieu salleB67 = new Lieu(67, "salleB6.7(Projet Python)", "Batiment Principal");

        Utilisateur sidibe = new Utilisateur(56, "Sidibe", "sidibe@2IE.bf");
        Utilisateur bernadette = new Utilisateur(75, "Bernadette", "bernadette@2IE.bf");

        Technicien superBob = new Technicien(743, "Bob le depanneur", "bob@tech2IE.bf");

        Ticket ticketSidibe = new Ticket(325842, "probleme d'affichage, L'affichage de l'ecran est pixelise", "Urgent", "ouvert");
        Ticket ticketBernadette = new Ticket(648656, "plus d'internet", "le wifi est une theorie ou une realite dans le Batiment B", "necessaire", "ouvert");

        System.out.println("Evolution du ticket de Sidibe");

        ticketSidibe.PrendreEnCharge(325842);
        ticketSidibe.Affiche(325842);

        ticketSidibe.Resolu(325842);
        ticketSidibe.Affiche(325842);

        System.out.println("Evolution du ticket de Bernadette");

        ticketBernadette.PrendreEnCharge(648656);
        ticketBernadette.Affiche(648656);

        ticketBernadette.Resolu(648656);
        ticketBernadette.Affiche(648656);

    }
}