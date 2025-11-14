import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GeneradorEstadistica {

  private final RepoEstadistica repo;
  private final RepoColecciones repoColecciones;

  public GeneradorEstadistica(RepoEstadistica repo, RepoColecciones repoColecciones) {
    this.repo = repo;
    this.repoColecciones = repoColecciones;
  }

  public void generarCategoriaConMayorHechos(Long coleccionId) {
    EstadisticaRegistro registro = repo.categoriaConMayorHechos(coleccionId);
    if (registro != null) {
      Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);
      registro.setColeccion(coleccion);
      registro.setTipo("CATEGORIA_MAS_HECHOS");
      registro.setFecha_actualizacion(LocalDateTime.now());
      registro.setVisiblePublico(true);
      repo.guardarEstadistica(registro);
    }
  }

  public void generarProvinciaConMayorHechos(Long coleccionId) {
    EstadisticaRegistro registro = repo.provinciaConMasHechos(coleccionId);
    if (registro != null) {
      Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);
      registro.setColeccion(coleccion);
      registro.setTipo("PROVINCIA_MAS_HECHOS");
      registro.setFecha_actualizacion(LocalDateTime.now());
      registro.setVisiblePublico(true);
      repo.guardarEstadistica(registro);
    }
  }

  public void generarProvinciasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);
    Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);

    for (String categoria : categorias) {
      EstadisticaRegistro registro = repo.provinciaConMasHechosPorCategoria(coleccionId, categoria);
      if (registro != null) {
        registro.setColeccion(coleccion);
        registro.setTipo("PROVINCIA_MAS_HECHOS_" + categoria.toUpperCase().replaceAll("\\s+", "_"));
        registro.setFecha_actualizacion(LocalDateTime.now());
        registro.setVisiblePublico(true);
        repo.guardarEstadistica(registro);
      }
    }
  }

  public void generarHorasConMasHechosPorCategorias(Long coleccionId) {
    List<String> categorias = repo.categoriasPorColeccion(coleccionId);

    for (String categoria : categorias) {
      EstadisticaRegistro registro = repo.horaConMasHechosPorCategoria(coleccionId, categoria);
      Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);

      if (registro != null) {
        registro.setColeccion(coleccion);
        registro.setTipo("HORA_MAS_HECHOS_" + categoria.toUpperCase());
        registro.setFecha_actualizacion(LocalDateTime.now());
        registro.setVisiblePublico(true);
        repo.guardarEstadistica(registro);
      }
    }
  }

  public void generarCantidadSolicitudesSpam(Long coleccionId) {
    EstadisticaRegistro registro = repo.cantidadSolicitudesSpam(coleccionId);
    if (registro != null) {
      Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);
      registro.setColeccion(coleccion);
      registro.setTipo("SOLICITUDES_SPAM");
      registro.setFecha_actualizacion(LocalDateTime.now());
      registro.setVisiblePublico(true);

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