public abstract class Personne {
    private final int numero;
    private final String nom;
    private final String email;

    public Personne (int numero, String nom, String email){
        if (numero<=0){
            throw new IllegalArgumentException("Le numero doit etre positif");
        }
        if (nom==null || nom.isBlank()){
            throw new IllegalArgumentException("Le nom ne peut pas etre vide");
        }
        if (email==null || email.isBlank()){
            throw new IllegalArgumentException("l'adresse electronique ne peut pqs etre vide");
        }
        this.numero=numero;
        this.nom=nom;
        this.email=email;
    }

    public String getNom() {
        return nom;
    }

    public int getNumero() {
        return numero;
    }

    public String getEmail() {
        return email;
    }

    public abstract String role ();

    @Override
    public String toString(){
        return role() + "Cette personne s'appelle " + nom + "et a ete attribue le numero " + numero + "son email est" + email;
    }


}
