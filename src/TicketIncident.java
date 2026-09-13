public class TicketIncident extends Ticket {
    private String equipementConcerne;

    public TicketIncident(String titre, String description, String priorite,
                          Utilisateur auteur, Lieu lieu, String equipementConcerne) {
        super(titre, description, priorite, auteur, lieu);
        this.equipementConcerne = equipementConcerne;
    }

    @Override
    public int delaiCibleHeures() {
        return 4;
    }

    @Override
    public String toString() {
        return super.toString() + " | equipement : " + equipementConcerne;
    }
}
