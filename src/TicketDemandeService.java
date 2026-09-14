public class TicketDemandeService extends Ticket {
    private final String serviceDemande;

    public TicketDemandeService(int numero, String titre, String description, String priorite,
                                Utilisateur auteur, Lieu lieu, String serviceDemande) {
        super(numero, titre, description, priorite, auteur, lieu);
        if (serviceDemande == null || serviceDemande.trim().isEmpty()){
            throw new IllegalArgumentException("Le service demande ne doit pas etre vide");
        }
        this.serviceDemande = serviceDemande;
    }

    @Override
    public int delaiCibleHeures() {
        return 24;
    }

    @Override
    public String toString() {
        return super.toString() + " | service : " + serviceDemande;
    }
}

