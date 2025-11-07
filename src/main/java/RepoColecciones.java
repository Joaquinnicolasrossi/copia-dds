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
    List<Coleccion> colecciones = entityManager().createQuery("FROM Coleccion", Coleccion.class)
        .getResultList();

    // Forzamos que se actualice
    colecciones.forEach(c -> entityManager().refresh(c));
    return colecciones;
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

  public void actualizarColeccion(Long id, String nuevoTitulo, String nuevaDescripcion, Fuente nuevaFuente, List<Criterio> nuevosCriterios, Consenso nuevoAlgoritmo){
    withTransaction( () -> {
      Coleccion coleccion = entityManager().find(Coleccion.class, id);

      if (coleccion != null){
        coleccion.actualizarConfiguracion(nuevoTitulo, nuevaDescripcion, nuevaFuente, nuevosCriterios, nuevoAlgoritmo);
        entityManager().merge(coleccion);
      }
    });
  }

  public Coleccion buscarPorId(Long id) {
    return entityManager().find(Coleccion.class, id);
  }

  public Fuente buscarFuentePorTipo(String tipo) {
    if (tipo == null || tipo.isBlank()) return null;
    String tipoDiscriminador = tipo.toUpperCase().replace("-", "_");
    List<Fuente> resultados = entityManager()
        .createQuery("FROM Fuente f WHERE f.tipo_fuente = :tipo", Fuente.class)
        .setParameter("tipo", tipoDiscriminador)
        .setMaxResults(1)
        .getResultList();

    return resultados.isEmpty() ? null : resultados.get(0);
  }

}
