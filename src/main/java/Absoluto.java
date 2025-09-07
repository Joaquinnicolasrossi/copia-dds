import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ABSOLUTO")
public class Absoluto extends Consenso {

  // Si todas las fuentes del nodo contienen el mismo hecho

  @Override
  public boolean estaConsensuado(Hecho hecho, Fuente fuenteDeNodo) {
    List<Hecho> hechos = fuenteDeNodo.extraerHechos();
    if(hechos.isEmpty()){ return false; }
    return hechos.stream()
        // Mas eficiente - Devuelve True si cumple con la condiciòn
        .allMatch(h -> h.tieneMismoContenidoQue(hecho));

  }
}

