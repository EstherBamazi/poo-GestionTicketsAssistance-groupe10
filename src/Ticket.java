import java.util.UUID;

public class Ticket {
    String numero_unique;
    String titre;
    String description;
    String priorite;
    String etat;

    Utilisateur auteur;
    Lieu lieu;
    Technicien technicien;

    public Ticket(String titre, String description, String priorite, Utilisateur auteur, Lieu lieu) {
        this.numero_unique = UUID.randomUUID().toString();
        this.etat = "ouvert";
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.auteur = auteur;
        this.lieu = lieu;
    }

    public String getDescription() {
        return description;
    }

    public String getNumero_unique() {
        return numero_unique;
    }

    public void prendreEnCharge(Technicien technicien) {
        this.technicien = technicien;
        this.etat = "pris en charge";
        System.out.println(technicien.nom + " a pris en charge le ticket de "
                + auteur.getNom() + ", merci pour votre patience");
    }

    public void resolu() {
        this.etat = "resolu";
        System.out.println("Le ticket de " + auteur.getNom()
                + " est desormais resolu, merci pour votre confiance");
    }

    public void affiche() {
        System.out.println("Ticket " + numero_unique
                + " | auteur : " + auteur.getNom()
                + " | lieu : " + lieu.nom
                + " | etat : " + etat);
    }
}