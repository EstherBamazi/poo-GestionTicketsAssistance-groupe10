public class Ticket {
    float numero_unique;
    string titre;
    string description;
    string priorite;
    string etat;
    this.etat=ouvert;
   public void PrisEnCharge(NumeroUniqueDuTicket){
       etat="pris en charge";
       System.out.println('Votre ticket est desormais pris en charge, merci pour votre patience');
   }
   public void Resolu(NumeroUniqueDuTicket){
       etat="resolu";
       System.out.println('Votre ticket est desormais resolu, merci pour votre confiance');
   }
   public void  Affiche(NumeroUniqueDuTicket){
       System.out.println('Votre ticket est desormais' + etat + ', merci pour votre patience');
   }
}
