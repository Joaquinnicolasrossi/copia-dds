/*
  Strategy
  El hecho està consensuado si aparece, al menos, en la mitad
  de las fuentes del nodo
 */

import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Mayoria_Simple")
public class MayoriaSimple extends Consenso {

  @Override
  public boolean estaConsensuado(Hecho hecho, Map<Fuente, List<Hecho>> hechosPorFuente) {
    int totalFuentes = hechosPorFuente.size();

    long menciones = hechosPorFuente.values().stream()
        .filter(hechosDeFuente ->
            hechosDeFuente.stream()
                .anyMatch(h -> h.tieneMismoContenidoQue(hecho))
        )
        .count();

    return menciones > (totalFuentes / 2.0);
  }


  @Override
  public String getIdentificador(){
    return "mayoria";
  }
}
