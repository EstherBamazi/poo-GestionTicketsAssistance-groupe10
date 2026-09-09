public class Ticket {
    int numero_unique;
    string titre;
    string description;
    string priorite;
    string etat;


    public Ticket(int numero_unique, string titre, string desription, string priorite, string etat) {
        this.numero_unique = numero_unique;
        this.etat = "ouvert";
        this.titre = titre;
        this.description = desription;
    }


   public void PrendreEnCharge(numero_unique) {
       etat="pris en charge";
       System.out.println("Votre ticket est desormais pris en charge, merci pour votre patience");
   }
   public void Resolu(numero_unique){
       etat="resolu";
       System.out.println("Votre ticket est desormais resolu, merci pour votre confiance");
   }
   public void  Affiche(numero_unique){
       System.out.println("Votre ticket est desormais" + etat + ", merci pour votre patience");
   }
}
