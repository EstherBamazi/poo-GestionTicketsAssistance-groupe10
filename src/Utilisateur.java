public class Utilisateur {
    int numero;
    String nom;
    String adrresse_electronique;


    public Utilisateur(int numero, String nom, String adrresse_electronique) {
        this.numero = numero;
        this.nom = nom;
        this.adrresse_electronique = adrresse_electronique;
    }

    public String getNom() {
        return nom;
    }
}

