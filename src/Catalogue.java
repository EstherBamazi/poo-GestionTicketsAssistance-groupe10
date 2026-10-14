import java.util.List;
import java.util.Optional;

public interface Catalogue< K, T> {
    boolean ajouter(K cle, T element);
    Optional<T> chercher(K cle);
    boolean retirer(K cle);
    List<T> lister();
    int taille();
}
