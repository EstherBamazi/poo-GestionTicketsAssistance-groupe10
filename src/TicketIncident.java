public class TicketIncident extends Ticket {
    private final String equipementConcerne;

    public TicketIncident(int numero, String titre, String description, String priorite,
                          Utilisateur auteur, Lieu lieu, String equipementConcerne) {
        super(numero, titre, description, priorite, auteur, lieu);
        if (equipementConcerne==null || equipementConcerne.isBlank()){
            throw new IllegalArgumentException("L'equipement ne doit pas etre vide");
        }
        this.equipementConcerne = equipementConcerne;
    }

    public String getEquipementConcerne(){
        return getEquipementConcerne();
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
