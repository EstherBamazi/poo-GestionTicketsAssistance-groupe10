public class Utilisateur extends Personne{
    int numero;
    String nom;
    String email;


    public Utilisateur(int numero, String nom, String email) {
        super(numero,nom,email);git pull
    }

    @Override
    public String role(){
        return "Utilisateur";
    }
}

