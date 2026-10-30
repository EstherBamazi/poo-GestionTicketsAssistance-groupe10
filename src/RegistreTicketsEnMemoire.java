import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<Ticket> rechercherParTitre(String motCle) {
        if (motCle == null) {
            throw new IllegalArgumentException("Le mot-cle est obligatoire.");
        }

        String recherche = motCle.toLowerCase();
        List<Ticket> resultat = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            if (ticket.getTitre().toLowerCase().contains(recherche)) {
                resultat.add(ticket);
            }
        }

        // Tri par titre, puis par numero si les titres sont identiques.
        for (int i = 0; i < resultat.size() - 1; i++) {
            for (int j = i + 1; j < resultat.size(); j++) {
                Ticket premier = resultat.get(i);
                Ticket second = resultat.get(j);

                int comparaisonTitre = premier.getTitre().compareToIgnoreCase(second.getTitre());
                boolean echanger = comparaisonTitre > 0
                        || (comparaisonTitre == 0 && premier.getNumero() > second.getNumero());

                if (echanger) {
                    resultat.set(i, second);
                    resultat.set(j, premier);
                }
            }
        }

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
