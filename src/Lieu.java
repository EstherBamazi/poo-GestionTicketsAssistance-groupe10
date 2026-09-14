public class Lieu {
    private final int numero;
    private final String nom;
    private final String batiment;


    public Lieu(int numero, String nom, String batiment) {
        if (numero <=0){
            throw new IllegalArgumentException("Le numero doit etre positif");
        }
        if (nom == null || nom.isBlank()){
            throw new IllegalArgumentException("Le nom ne peut pas etre vide");
        }
        if (batiment == null || batiment.isBlank()){
            throw new IllegalArgumentException("Le batiment ne peut pas etre vide");
        }
        this.numero = numero;
        this.nom = nom;
        this.batiment = batiment;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getBatiment() {
        return batiment;
    }

    @Override
    public String toString(){
        return nom + "," + batiment;
    }
}