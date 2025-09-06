import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoHechos {
  @PersistenceContext
  EntityManager entityManager;

  public void guardarHechos(List<Hecho> hechos) {
    entityManager.getTransaction().begin();
    hechos.forEach(hecho -> entityManager.persist(hecho));
    entityManager.getTransaction().commit();
  }

  public List<Hecho> obtenerTodosLosHechos() {
    return entityManager.createNativeQuery("SELECT * FROM hecho", Hecho.class)
        .getResultList();
  }

  public List<Fuente> obtenerTodasLasFuentes() {
    return entityManager.createNativeQuery("SELECT DISTINCT f.* FROM fuente f " +
            "INNER JOIN hecho h ON h.fuenteOrigen = f.id", Fuente.class)
        .getResultList();
  }

  public List<Hecho> obtenerHechosDeFuentes(List<Fuente> fuentes) {
    return fuentes.stream()
        .flatMap(f -> obtenerHechosPorFuente(f).stream())
        .toList();
  }


  public List<Hecho> obtenerHechosPorFuente(Fuente fuente) {
    return entityManager.createNativeQuery("SELECT h.* FROM hecho h" +
            "WHERE h.FuenteOrigen =  ?", Hecho.class)
        .setParameter(1, fuente.getId())
        .getResultList();
  }

  public List<Hecho> buscarFullText(String texto) {
    return entityManager.createNativeQuery(
            "SELECT * FROM hecho WHERE MATCH(titulo, descripcion) AGAINST (? IN NATURAL LANGUAGE MODE)",
            Hecho.class
        ).setParameter(1, texto)
        .getResultList();
  }
}
