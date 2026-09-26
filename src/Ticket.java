public abstract class Ticket implements Assignable {
    private final int numero;
    private final String titre;
    private final String description;
    private final String priorite;
    private final Utilisateur auteur;
    private final Lieu lieu;
    private EtatTicket etat;
    private Technicien technicien;

    public Ticket(int numero, String titre, String description, String priorite, Utilisateur auteur, Lieu lieu) {
        if (numero <= 0) {
            throw new IllegalArgumentException("Le numero doit etre positif");
        }
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre ne doit pas etre vide");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La description ne doit pas etre vide");
        }
        if (priorite == null || priorite.isBlank()) {
            throw new IllegalArgumentException("La priorite ne doit pas etre vide");
        }
        if (auteur == null || lieu == null) {
            throw new IllegalArgumentException("auteur et lieu sont obligatoires");
        }
        this.numero = numero;
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.auteur = auteur;
        this.lieu = lieu;
        this.etat = EtatTicket.OUVERT;
        this.technicien = null;
    }

    public String getDescription() {
        return description;
    }

    public int getNumero() {
        return numero;
    }

    public String getTitre() {
        return titre;
    }

    public String getPriorite() {
        return priorite;
    }

    public EtatTicket getEtat() {
        return etat;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public abstract int delaiCibleHeures();

    // --- Assignable ---

    @Override
    public void assigner(Technicien technicien) {
        if (this.etat == EtatTicket.RESOLU) {
            throw new IllegalStateException(
                    "Impossible d'assigner un technicien a un ticket deja resolu.");
        }
        this.technicien = technicien;
    }

    @Override
    public Technicien getTechnicien() {
        return technicien;
    }

    @Override
    public boolean estAssigne() {
        return technicien != null;
    }

    // --- Transitions d'etat ---

    public void prendreEnCharge() {
        if (etat != EtatTicket.OUVERT) {
            throw new IllegalStateException(
                    "Impossible de prendre en charge un ticket qui n'est pas OUVERT (etat actuel : " + etat + ").");
        }
        if (!estAssigne()) {
            throw new IllegalStateException(
                    "Impossible de prendre en charge un ticket sans technicien assigne.");
        }
        this.etat = EtatTicket.EN_COURS;
    }

    public void resoudre() {
        if (etat != EtatTicket.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible de resoudre un ticket qui n'est pas EN_COURS (etat actuel : " + etat + ").");
        }
        this.etat = EtatTicket.RESOLU;
    }

    @Override
    public String toString() {
        return "Ticket " + numero
                + "|" + titre
                + "|priorite: " + priorite
                + " | auteur : " + auteur.getNom()
                + " | lieu : " + lieu.getNom()
                + " | etat : " + etat
                + " | technicien: " + (technicien == null ? "aucun" : technicien.getNom())
                + " | delai cible : " + delaiCibleHeures() + "h";
    }
}
