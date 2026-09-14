public abstract class Ticket {
    private final int numero;
    private final String titre;
    private final String description;
    private final String priorite;
    private final Utilisateur auteur;
    private final Lieu lieu;
    private String etat;
    private Technicien technicien;


    public Ticket(int numero, String titre, String description, String priorite, Utilisateur auteur, Lieu lieu) {
        if (numero<=0){
            throw new IllegalArgumentException("Le numero doit etre positif");
        }
        if (titre == null || titre.isBlank()){
            throw new IllegalArgumentException("Le titre de doit pas etre vide");
        }
        if (description == null || description.isBlank()){
            throw new IllegalArgumentException("La description de doit pas etre vide");
        }
        if (priorite == null || priorite.isBlank()){
            throw new IllegalArgumentException("La priorite ne de doit pas etre vide");
        }
        if (auteur == null || lieu==null){
            throw new IllegalArgumentException("auteur est lieu sont obligatoire");
        }
        this.numero = numero;
        this.etat = "ouvert";
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.auteur = auteur;
        this.lieu = lieu;
        this.technicien=technicien;
    }

    public String getDescription() {
        return description;
    }

    public int getNumero() {
        return numero;
    }

    public String getTitre(){
        return titre;
    }

    public String getPriorite(){
        return priorite;
    }

    public String getEtat() {
        return etat;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public Technicien getTechnicien() {
        return technicien;
    }

    public abstract int delaiCibleHeures();

    public void prendreEnCharge(Technicien technicien) {
        if (etat.equals("ouvert")&& technicien!= null){
            this.technicien = technicien;
            this.etat = "pris en charge";
        }

    }

    public void resoudre() {
        if (etat.equals("en cours")){
            this.etat = "resolu";
        }

    }

    @Override
    public String toString() {
        return "Ticket " + numero
                + "|" +titre
                +"|priorite: " + priorite
                + " | auteur : " + auteur.getNom()
                + " | lieu : " + lieu.getNom()
                + " | etat : " + etat
                + " | technicien: " + (technicien==null? "aucun":technicien.getNom())
                + " | delai cible : " + delaiCibleHeures() + "h";
    }
}