import java.util.List;
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

  public void generarProvinciasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);

    for (String categoria : categorias) {
      Object[] fila = repo.provinciaConMasHechosPorCategoria(coleccionId, categoria);
      if (fila == null) continue;

      String provincia = (String) fila[0];
      int cantidad = ((Number) fila[1]).intValue();

      repo.guardarEstadistica(
          coleccionId,
          // PAra poner por ejemplo PROVINCIA_MAS_HECHOS_ROBO_ARMADO
          "PROVINCIA_MAS_HECHOS_" +
              categoria.toUpperCase().replaceAll("\\s+", "_"),
          provincia,
          cantidad
      );
    }
  }

  public void generarHorasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);
    for (String categoria : categorias) {
      Object[] fila = repo.horaConMasHechosPorCategoria(coleccionId, categoria);
      if (fila == null) continue;

      Integer hora = ((Number) fila[0]).intValue();
      int cantidad = ((Number) fila[1]).intValue();

      // diferenciamos el tipo por categoría para no pisar registros
      repo.guardarEstadistica(
          coleccionId,
          "HORA_MAS_HECHOS_" + categoria.toUpperCase(),
          hora.toString(),
          cantidad
      );
    }
  }

  public void generarCantidadSolicitudesSpam(Long coleccionId) {
    Long cantidad = repo.cantidadSolicitudesSpam(coleccionId);
    repo.guardarEstadistica(coleccionId, "SOLICITUDES_SPAM", "true", cantidad.intValue());
  }

}
