import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Ticket implements Assignable {
    private final int numero;
    private final String titre;
    private final String description;
    private Priorite priorite;
    private final Utilisateur auteur;
    private final Lieu lieu;
    private EtatTicket etat;
    private Technicien technicien;
    private final List<Evenement> historique = new ArrayList<>();

    public Ticket(int numero, String titre, String description, Priorite priorite,
                  Utilisateur auteur, Lieu lieu) {
        if (numero <= 0) {
            throw new IllegalArgumentException("Le numero doit etre positif");
        }
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre ne doit pas etre vide");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La description ne doit pas etre vide");
        }
        if (priorite == null) {
            throw new IllegalArgumentException("La priorite doit etre renseignee");
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

        historique.add(new Evenement("Creation du ticket", this.etat));
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

    public Priorite getPriorite() {
        return priorite;
    }

    public void changerPriorite(Priorite p) {
        if (p == null) {
            throw new IllegalArgumentException("La priorite doit etre renseignee");
        }

        if (this.etat == EtatTicket.RESOLU) {
            throw new TransitionInvalideException(
                    "La priorite d'un ticket resolu ne peut pas etre modifiee"
            );
        }

        this.priorite = p;
        historique.add(new Evenement("Changement de priorite : " + p, this.etat));
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

    public List<Evenement> getHistorique() {
        return List.copyOf(historique);
    }

    @Override
    public void assigner(Technicien technicien) {
        if (technicien == null) {
            throw new IllegalArgumentException("Le technicien ne doit pas etre null.");
        }

        if (this.etat == EtatTicket.RESOLU) {
            throw new TransitionInvalideException(
                    "Impossible d'assigner un technicien a un ticket deja resolu."
            );
        }

        this.technicien = technicien;
        historique.add(new Evenement(
                "Affectation du technicien " + technicien.getNom(),
                this.etat
        ));
    }

    public void resoudre() {
        if (etat != EtatTicket.EN_COURS) {
            throw new TransitionInvalideException(
                    "Impossible de resoudre un ticket qui n'est pas EN_COURS (etat actuel : " + etat + ")."
            );
        }

        this.etat = EtatTicket.RESOLU;
        historique.add(new Evenement("Resolution du ticket", this.etat));
    }

    @Override
    public Technicien getTechnicien() {
        return technicien;
    }

    @Override
    public boolean estAssigne() {
        return technicien != null;
    }

    public void prendreEnCharge() {
        if (etat != EtatTicket.OUVERT) {
            throw new TransitionInvalideException(
                    "Impossible de prendre en charge un ticket qui n'est pas OUVERT (etat actuel : " + etat + ")."
            );
        }

        if (!estAssigne()) {
            throw new TransitionInvalideException(
                    "Impossible de prendre en charge un ticket sans technicien assigne."
            );
        }

        this.etat = EtatTicket.EN_COURS;
        historique.add(new Evenement("Prise en charge du ticket", this.etat));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket ticket = (Ticket) o;
        return numero == ticket.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
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

