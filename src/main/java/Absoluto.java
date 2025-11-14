import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Absoluto")
public class Absoluto extends Consenso {

  // Si todas las fuentes del nodo contienen el mismo hecho
  @Override
  public boolean estaConsensuado(Hecho hecho, Map<Fuente, List<Hecho>> hechosPorFuente) {
    int totalFuentes = hechosPorFuente.size();

    long menciones = hechosPorFuente.values().stream()
        .filter(hechosDeFuente ->
            hechosDeFuente.stream()
                .anyMatch(h -> h.tieneMismoContenidoQue(hecho))
        )
        .count();

    return menciones == totalFuentes;
  }

  @Override
  public String getIdentificador(){
    return "absoluto";
  }
}

