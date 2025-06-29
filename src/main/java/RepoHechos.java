import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RepoHechos {
  private final ConcurrentMap<Fuente, List<Hecho>> cache = new ConcurrentHashMap<>();

  public void guardarHechos(List<Hecho> hechos) {
    hechos.forEach(hecho ->
        cache.merge(
            hecho.getFuenteOrigen(),
            new ArrayList<>(List.of(hecho)),
            (listaExistente, nuevos) -> {
              listaExistente.addAll(nuevos);
              return listaExistente;
            }
        )
    );
  }

  public List<Hecho> obtenerHechosPorFuente(Fuente fuente) {
    return cache.getOrDefault(fuente, Collections.emptyList());
  }
}
