import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private Ticket ticket;
    private Technicien technicien;

    @BeforeEach
    void setUp() {
        Utilisateur auteur = new Utilisateur(1, "Awa", "awa@2ie.edu");
        Lieu lieu = new Lieu(1, "Salle info", "Bâtiment A");
        technicien = new Technicien(2, "Moussa", "moussa@2ie.edu");
        ticket = new TicketIncident(1, "PC en panne", "Le PC ne démarre plus",
                Priorite.HAUTE, auteur, lieu, "PC-12");
    }

    @Test
    void unNouveauTicketEstOuvertEtNonAssigne() {
        assertEquals(EtatTicket.OUVERT, ticket.getEtat());
        assertFalse(ticket.estAssigne());
    }

    @Test
    void cycleDeVieCompletOuvertEnCoursResolu() {
        ticket.assigner(technicien);
        ticket.prendreEnCharge();
        assertEquals(EtatTicket.EN_COURS, ticket.getEtat());

        ticket.resoudre();
        assertEquals(EtatTicket.RESOLU, ticket.getEtat());
    }

    @Test
    void prendreEnChargeSansTechnicienLeveUneException() {
        assertThrows(TransitionInvalideException.class, () -> ticket.prendreEnCharge());
    }

    @Test
    void numeroNegatifRefuse() {
        Utilisateur auteur = new Utilisateur(1, "Awa", "awa@2ie.edu");
        Lieu lieu = new Lieu(1, "Salle info", "Bâtiment A");
        assertThrows(IllegalArgumentException.class, () ->
                new TicketIncident(-1, "t", "d", Priorite.BASSE, auteur, lieu, "PC"));
    }
}
