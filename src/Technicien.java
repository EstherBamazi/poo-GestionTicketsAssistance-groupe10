public class Technicien extends Personne{
    int numero;
    String nom;
    String email;


    public Technicien(int numero, String nom, String email) {
        super(numero,nom,email);
    }

    @Override
    public String role(){
        return "Technicien";
    }
}