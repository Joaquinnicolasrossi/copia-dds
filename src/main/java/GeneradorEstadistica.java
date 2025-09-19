import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica {

  private final RepoEstadistica repo;

  public GeneradorEstadistica(RepoEstadistica repo) {
    this.repo = repo;
  }

  public void generarCategoriaConMayorHechos(Long coleccionId) {
    EstadisticaRegistro registro = repo.categoriaConMayorHechos(coleccionId);
    if (registro != null) {
      repo.guardarEstadistica(registro);
    }
  }

  public void generarProvinciaConMayorHechos(Long coleccionId) {
    EstadisticaRegistro registro = repo.provinciaConMasHechos(coleccionId);
    if (registro != null) {
      repo.guardarEstadistica(registro);
    }
  }

  public void generarProvinciasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);

    for (String categoria : categorias) {
      EstadisticaRegistro registro = repo.provinciaConMasHechosPorCategoria(coleccionId, categoria);
      if (registro != null) {
        // ajustamos el tipo para diferenciar por categoría
        registro.setTipo(
            "PROVINCIA_MAS_HECHOS_" +
                categoria.toUpperCase().replaceAll("\\s+", "_")
        );
        repo.guardarEstadistica(registro);
      }
    }
  }

  public void generarHorasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);

    for (String categoria : categorias) {
      EstadisticaRegistro registro = repo.horaConMasHechosPorCategoria(coleccionId, categoria);
      if (registro != null) {
        // diferenciamos el tipo por categoría para no pisar registros
        registro.setTipo("HORA_MAS_HECHOS_" + categoria.toUpperCase());
        repo.guardarEstadistica(registro);
      }
    }
  }

  public void generarCantidadSolicitudesSpam(Long coleccionId) {
    EstadisticaRegistro registro = repo.cantidadSolicitudesSpam(coleccionId);
    if (registro != null) {
      repo.guardarEstadistica(registro);
    }
  }

  // Para CRON
  public void generarTodas(Long coleccionId) {
    generarCategoriaConMayorHechos(coleccionId);
    generarProvinciaConMayorHechos(coleccionId);
    generarProvinciasConMasHechosPorCategorias(coleccionId);
    generarHorasConMasHechosPorCategorias(coleccionId);
    generarCantidadSolicitudesSpam(coleccionId);
  }

}
