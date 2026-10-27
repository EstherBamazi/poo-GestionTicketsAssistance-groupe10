import java.util.HashMap;
import java.util.Map;

public class Historique{
    private Map<Utilisateur, Ticket> historique= new HashMap<>();

    public Map<Utilisateur, Ticket> getHistorique() {
        return historique;
    }

    public void setHistorique(Map<Utilisateur, Ticket> historique) {
        this.historique = historique;
    }
}
