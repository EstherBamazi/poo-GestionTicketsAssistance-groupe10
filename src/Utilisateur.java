public class Utilisateur extends Personne{
    public Utilisateur(int numero, String nom, String email) {
        super(numero,nom,email);
    }

    @Override
    public String role(){
        return "Utilisateur";
    }
}

