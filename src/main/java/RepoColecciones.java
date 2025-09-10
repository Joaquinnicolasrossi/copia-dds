import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoColecciones {
  private final RepoSolicitudes repoSolicitudes;
  @PersistenceContext
  EntityManager entityManager;

  public RepoColecciones(RepoSolicitudes repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  public void crearColeccion(String titulo, String descripcion, FuenteEstaticaIncendios fuente,
                             List<Criterio> criterios) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterios, repoSolicitudes);
    entityManager.getTransaction().begin();
    entityManager.persist(coleccion);
    entityManager.getTransaction().commit();
  }

  public List<Coleccion> getColecciones() {
      return entityManager.createNativeQuery("SELECT * FROM Coleccion", Coleccion.class)
          .getResultList();
    };

  public List<Long> getIdsColecciones() {
        List<?> resultados = entityManager.createNativeQuery(
            "SELECT id FROM coleccion")
            .getResultList();

        List<Long> ids = new ArrayList<>();
        for (Object obj : resultados) {
          ids.add(((Number) obj).longValue()); // convierte BigInteger → Long
        }
        return ids;
    }
}
