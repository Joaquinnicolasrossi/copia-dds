import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoColecciones implements WithSimplePersistenceUnit {
  private final RepoSolicitudes repoSolicitudes;

  public RepoColecciones(RepoSolicitudes repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  //public void crearColeccion(String titulo, String descripcion, FuenteEstaticaIncendios fuente,
  public void crearColeccion(String titulo, String descripcion, Fuente fuente,
      List<Criterio> criterios, Consenso algoritmoConsenso, RepoHechos repoHechos) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterios, repoSolicitudes, algoritmoConsenso, repoHechos);
    withTransaction( () -> entityManager().persist(coleccion));
  }

  public List<Coleccion> getColecciones() {
    return entityManager().createNativeQuery("SELECT * FROM Coleccion", Coleccion.class)
        .getResultList();
  };

  public List<Long> getIdsColecciones() {
    List<?> resultados = entityManager().createNativeQuery(
        "SELECT id FROM coleccion")
        .getResultList();

    List<Long> ids = new ArrayList<>();
    for (Object obj : resultados) {
      ids.add(((Number) obj).longValue()); // convierte BigInteger → Long
    }
    return ids;
  }
}
