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
  public boolean estaConsensuado(Hecho hecho, Fuente fuente) {

    List<Hecho> hechos = fuente.extraerHechos();
    if (hechos.isEmpty()) return false;

    long coincidencias = hechos.stream()
        .filter(h -> h.tieneMismoContenidoQue(hecho))
        .count();

    boolean hayConflicto = hechos.stream()
        .filter(h -> h.getTitulo().equals(hecho.getTitulo()))
        .anyMatch(h -> !h.tieneMismoContenidoQue(hecho));

    return coincidencias >= 2 && !hayConflicto;
  }

  @Override
  public String getIdentificador(){
    return "multiplesm";
  }
}
