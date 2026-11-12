import java.util.List;


public interface DepotTicket extends Catalogue<Ticket, Integer>{
    List<Ticket> rechercherParEtat(EtatTicket etat);
    List<Ticket> rechercherParPriorite(Priorite priorite);
}
