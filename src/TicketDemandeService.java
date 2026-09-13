Public class TicketDemandeService extends Ticket {
    private String serviceDemande;

    public TicketDemandeService(String titre, String description, String priorite,
                                Utilisateur auteur, Lieu lieu, String serviceDemande) {
        super(titre, description, priorite, auteur, lieu);
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

