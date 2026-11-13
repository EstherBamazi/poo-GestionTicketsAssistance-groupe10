import java.util.List;


public interface DepotTickets extends Catalogue<Ticket, Integer>{
    List<Ticket> rechercherParEtat(EtatTicket etat);
    List<Ticket> rechercherParPriorite(Priorite priorite);
}
