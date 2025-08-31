import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoHechos {
  private final ConcurrentMap<Fuente, List<Hecho>> cache = new ConcurrentHashMap<>();
  @PersistenceContext
  EntityManager entityManager;

  public void guardarHechos(List<Hecho> hechos) {
    entityManager.getTransaction().begin();
    hechos.forEach(hecho -> {
      entityManager.persist(hecho);
      cache.merge(
          hecho.getFuenteOrigen(),
          new ArrayList<>(List.of(hecho)),
          (listaExistente, nuevos) -> {
            listaExistente.addAll(nuevos);
            return listaExistente;
          }
      );
    });

    entityManager.getTransaction().commit();
  }

  public List<Hecho> obtenerTodosLosHechos() {
    return cache.values().stream()
        .flatMap(List::stream)
        .toList();
  }

  public List<Fuente> obtenerTodasLasFuentes() {
    return new ArrayList<>(cache.keySet());
  }

  public List<Hecho> obtenerHechosDeFuentes(List<Fuente> fuentes) {
    return fuentes.stream()
        .flatMap(f -> obtenerHechosPorFuente(f).stream())
        .toList();
  }


  public List<Hecho> obtenerHechosPorFuente(Fuente fuente) {
    return cache.getOrDefault(fuente, Collections.emptyList());
  }

  public List<Hecho> buscarFullText(String texto) {
    return entityManager.createNativeQuery(
            "SELECT * FROM hecho WHERE MATCH(titulo, descripcion) AGAINST (? IN NATURAL LANGUAGE MODE)",
            Hecho.class
        ).setParameter(1, texto)
        .getResultList();
  }
}
