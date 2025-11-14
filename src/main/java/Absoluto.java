import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Absoluto")
public class Absoluto extends Consenso {

  // Si todas las fuentes del nodo contienen el mismo hecho
  @Override
  public boolean estaConsensuado(Hecho hecho, Fuente fuente) {
    List<Hecho> hechos = fuente.extraerHechos();
    if (hechos.isEmpty()) return false;

    return hechos.stream()
        .allMatch(h -> h.tieneMismoContenidoQue(hecho));
  }

  @Override
  public String getIdentificador(){
    return "absoluto";
  }
}

