public class Evenement {

    private final String action;
    private final EtatTicket etatApres;

    public Evenement(String action, EtatTicket etatApres) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException(
                    "L'action doit être renseignée"
            );
        }

        if (etatApres == null) {
            throw new IllegalArgumentException(
                    "L'état doit être renseigné"
            );
        }

        this.action = action;
        this.etatApres = etatApres;
    }

    public String getAction() {
        return action;
    }

    public EtatTicket getEtatApres() {
        return etatApres;
    }

    @Override
    public String toString() {
        return action + " -> " + etatApres;
    }
}