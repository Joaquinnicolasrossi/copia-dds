/*
  Strategy
  El hecho està consensuado si aparece, al menos, en la mitad
  de las fuentes del nodo
 */

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Mayoria_Simple")
public class MayoriaSimple extends Consenso {

  @Override
  public boolean estaConsensuado(Hecho hecho, Fuente fuenteDeNodo) {
    List<Hecho> hechos = fuenteDeNodo.extraerHechos();

    if (hechos.isEmpty()) {
      return false;
    }

    long coincidencias = hechos.stream()
        .filter(h -> h.tieneMismoContenidoQue(hecho))
        .count();
    return coincidencias >= Math.ceil(hechos.size() / 2.0);
  }
}
