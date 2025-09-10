import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica {

  private final RepoEstadistica repo;

  public GeneradorEstadistica(RepoEstadistica repo) {
    this.repo = repo;
  }

  public void generarCategoriaConMayorHechos(Long coleccionId) {
    Object[] fila = repo.categoriaConMayorHechos(coleccionId);
    if (fila == null) return;

    String categoria = (String) fila[0];
    int cantidad = ((Number) fila[1]).intValue();

    repo.guardarEstadistica(coleccionId, "CATEGORIA_MAYOR_HECHOS", categoria, cantidad);
  }

  public void generarProvinciaConMayorHechos(Long coleccionId) {
    Object[] fila = repo.provinciaConMasHechos(coleccionId);
    if (fila == null) return;

    String provincia = (String) fila[0];
    int cantidad = ((Number) fila[1]).intValue();

    repo.guardarEstadistica(coleccionId, "PROVINCIA_MAYOR_HECHOS", provincia, cantidad);
  }
}
