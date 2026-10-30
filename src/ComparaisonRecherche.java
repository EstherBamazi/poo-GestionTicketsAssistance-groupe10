import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparaisonRecherche {

    public static void executer(Utilisateur auteur, Lieu lieu) {
        List<Ticket> liste = new ArrayList<>();
        Map<Integer, Ticket> map = new HashMap<>();

        for (int numero = 1; numero <= 10000; numero++) {
            Ticket ticket = new TicketIncident(
                    numero,
                    "Ticket " + numero,
                    "Incident de demonstration",
                    Priorite.BASSE,
                    auteur,
                    lieu,
                    "Ordinateur"
            );

            liste.add(ticket);
            map.put(numero, ticket);
        }

        int trouvesListe = 0;
        long debutListe = System.nanoTime();

        for (int numero = 10; numero <= 10000; numero += 10) {
            for (Ticket ticket : liste) {
                if (ticket.getNumero() == numero) {
                    trouvesListe++;
                    break;
                }
            }
        }

        long dureeListe = System.nanoTime() - debutListe;

        int trouvesMap = 0;
        long debutMap = System.nanoTime();

        for (int numero = 10; numero <= 10000; numero += 10) {
            Ticket ticket = map.get(numero);
            if (ticket != null) {
                trouvesMap++;
            }
        }

        long dureeMap = System.nanoTime() - debutMap;

        if (trouvesListe == 1000 && trouvesMap == 1000) {
            System.out.println("REUSSI : les deux structures ont retrouve 1000 tickets.");
        } else {
            System.out.println("ECHEC : les deux structures devaient retrouver 1000 tickets.");
            return;
        }

        double tempsListe = dureeListe / 1_000_000.0;
        double tempsMap = dureeMap / 1_000_000.0;

        System.out.println("=== Comparaison des recherches ===");
        System.out.println("Tickets retrouves dans la liste : " + trouvesListe);
        System.out.println("Tickets retrouves dans la Map : " + trouvesMap);
        System.out.println("Temps liste : " + tempsListe + " ms");
        System.out.println("Temps Map : " + tempsMap + " ms");
        System.out.println("Mesures indicatives : les temps varient entre les executions.");
    }
}
