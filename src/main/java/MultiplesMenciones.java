import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

// si al menos dos fuentes del nodo contienen un mismo hecho
// y ninguna otra fuente del nodo contiene otro de igual título
// pero diferentes atributos, se lo considera consensuado;
@Entity
@DiscriminatorValue("Multiples_Menciones")
public class MultiplesMenciones extends Consenso {
  @Override
  public boolean estaConsensuado(Hecho hecho, Map<Fuente, List<Hecho>> hechosPorFuente) {

    long menciones = hechosPorFuente.values().stream()
        .filter(hechosDeFuente ->
            hechosDeFuente.stream()
                .anyMatch(h -> h.tieneMismoContenidoQue(hecho))
        )
        .count();

    return menciones >= 2;
  }

  @Override
  public String getIdentificador(){
    return "multiplesm";
  }
}
