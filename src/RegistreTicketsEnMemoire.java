import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class RegistreTicketsEnMemoire implements Catalogue<Ticket, Integer> {

    private final Map<Integer, Ticket> tickets = new HashMap<>();

    @Override
    public boolean ajouter(Integer cle, Ticket element) {

        if (cle == null || element == null) {
            throw new IllegalArgumentException("La cle et le ticket sont obligatoires.");
        }

        if (cle != element.getNumero()) {
            throw new IllegalArgumentException("La cle doit correspondre au numero du ticket.");
        }
        if (tickets.containsKey(cle)) {
            return false;
        }
        tickets.put(cle, element);
        return true;
    }

    @Override
    public Optional<Ticket> chercher(Integer cle) {
        if (cle == null) {
            throw new IllegalArgumentException("La cle est obligatoire.");
        }
        return Optional.ofNullable(tickets.get(cle));
    }

    @Override
    public boolean retirer(Integer cle) {
        if (cle == null) {
            throw new IllegalArgumentException("La cle est obligatoire.");
        }
        return tickets.remove(cle) != null;
    }

    @Override
    public List<Ticket> lister() {
        return List.copyOf(tickets.values());
    }

    @Override
    public int taille() {
        return tickets.size();
    }

    // Renvoie une nouvelle liste des titres contenant le mot-cle, triee par titre puis numero.
    // La recherche et le tri ignorent la casse ; null est refuse, un mot-cle vide retrouve tout.
    public List<Ticket> rechercherParTitre(String motCle) {
        if (motCle == null) {
            throw new IllegalArgumentException("Le mot-cle est obligatoire.");
        }
        String recherche = motCle.toLowerCase(Locale.ROOT);
        List<Ticket> resultat = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getTitre().toLowerCase(Locale.ROOT).contains(recherche)) {
                resultat.add(ticket);
            }
        }
        resultat.sort((premier, second) -> {
            int comparaison = premier.getTitre().compareToIgnoreCase(second.getTitre());
            if (comparaison != 0) {
                return comparaison;
            }
            return Integer.compare(premier.getNumero(), second.getNumero());
        });
        return resultat;
    }


    public Map<EtatTicket, List<Ticket>> regrouperParEtat() {
        Map<EtatTicket, List<Ticket>> groupes = new HashMap<>();
        for (EtatTicket etat : EtatTicket.values()) {
            groupes.put(etat, new ArrayList<>());
        }
        for (Ticket ticket : tickets.values()) {
            groupes.get(ticket.getEtat()).add(ticket);
        }
        return groupes;
    }
}