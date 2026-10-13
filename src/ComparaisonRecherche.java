import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// P7 : mesure pedagogique approximative, sans bibliotheque de benchmark.
public class ComparaisonRecherche {
    public static void executer(Utilisateur auteur, Lieu lieu) {
        List<Ticket> liste = new ArrayList<>();
        Map<Integer, Ticket> map = new HashMap<>();
        for (int numero = 1; numero <= 10000; numero++) {
            Ticket ticket = new TicketIncident(numero, "Ticket " + numero,
                    "Incident de demonstration", "BASSE", auteur, lieu, "Ordinateur");
            liste.add(ticket);
            map.put(numero, ticket);
        }

        // 1000 identifiants distincts et presents, repartis dans les 10000 tickets.
        // Les deux structures contiennent les memes objets et recoivent les memes recherches.
        int[] identifiants = new int[1000];
        long sommeAttendue = 0;
        for (int i = 0; i < identifiants.length; i++) {
            identifiants[i] = i * 10 + 1;
            sommeAttendue += identifiants[i];
        }

        // Quelques passages avant la mesure laissent Java commencer a optimiser le code.
        for (int passage = 0; passage < 5; passage++) {
            verifierSommes(sommeListe(liste, identifiants), sommeMap(map, identifiants), sommeAttendue);
        }

        System.out.println("\n=== P7 : 1000 recherches presentes dans 10000 tickets ===");
        System.out.println("Creation exclue ; 5 passages de preparation ; 5 mesures ; ordre alterne.");
        long totalListe = 0;
        long totalMap = 0;
        for (int repetition = 1; repetition <= 5; repetition++) {
            long debut;
            long dureeListe;
            long dureeMap;
            long resultatListe;
            long resultatMap;

            // Alterner evite de toujours favoriser la meme structure par l'ordre de passage.
            if (repetition % 2 == 1) {
                debut = System.nanoTime();
                resultatListe = sommeListe(liste, identifiants);
                dureeListe = System.nanoTime() - debut;
                debut = System.nanoTime();
                resultatMap = sommeMap(map, identifiants);
                dureeMap = System.nanoTime() - debut;
            } else {
                debut = System.nanoTime();
                resultatMap = sommeMap(map, identifiants);
                dureeMap = System.nanoTime() - debut;
                debut = System.nanoTime();
                resultatListe = sommeListe(liste, identifiants);
                dureeListe = System.nanoTime() - debut;
            }
            // Utiliser les resultats et les verifier, sans inclure l'affichage dans la mesure.
            verifierSommes(resultatListe, resultatMap, sommeAttendue);
            totalListe += dureeListe;
            totalMap += dureeMap;
            System.out.printf(Locale.ROOT, "Mesure %d : liste = %.3f ms ; map = %.3f ms%n",
                    repetition, dureeListe / 1000000.0, dureeMap / 1000000.0);
        }
        System.out.printf(Locale.ROOT, "Moyenne : liste = %.3f ms ; map = %.3f ms%n",
                totalListe / 5.0 / 1000000.0, totalMap / 5.0 / 1000000.0);
        System.out.println("Somme des numeros retrouves par chaque structure : " + sommeAttendue);
        System.out.println("Mesures indicatives : elles varient avec la machine, la JVM et les autres programmes.");
        System.out.println("La liste parcourt les tickets ; la HashMap accede par cle en temps moyen quasi constant.");
    }

    private static long sommeListe(List<Ticket> liste, int[] identifiants) {
        long somme = 0;
        for (int identifiant : identifiants) {
            for (Ticket ticket : liste) {
                if (ticket.getNumero() == identifiant) {
                    somme += ticket.getNumero();
                    break;
                }
            }
        }
        return somme;
    }

    private static long sommeMap(Map<Integer, Ticket> map, int[] identifiants) {
        long somme = 0;
        for (int identifiant : identifiants) {
            Ticket ticket = map.get(identifiant);
            if (ticket != null) {
                somme += ticket.getNumero();
            }
        }
        return somme;
    }

    private static void verifierSommes(long sommeListe, long sommeMap, long sommeAttendue) {
        if (sommeListe != sommeAttendue || sommeMap != sommeAttendue) {
            throw new AssertionError("Les recherches ne retrouvent pas les numeros attendus.");
        }
    }
}
