import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RepoColecciones implements WithSimplePersistenceUnit {
  private final RepoSolicitudes repoSolicitudes;
  private final RepoHechos repoHechos;

  public RepoColecciones(RepoSolicitudes repoSolicitudes, RepoHechos repoHechos) {
    this.repoSolicitudes = repoSolicitudes;
    this.repoHechos = repoHechos;
  }

  //public void crearColeccion(String titulo, String descripcion, FuenteEstaticaIncendios fuente,
  public void crearColeccion(String titulo, String descripcion, Fuente fuente,
                             List<Criterio> criterios, Consenso algoritmoConsenso, RepoHechos repoHechos) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterios, repoSolicitudes, algoritmoConsenso, repoHechos);
    //merge --> Crea/recupera una copia administrada del objeto
    withTransaction( () -> {
      if (!entityManager().contains(fuente)) {
        Fuente fuenteManaged = entityManager().merge(fuente);
        coleccion.setFuente(fuenteManaged);
      }
      entityManager().persist(coleccion);
      });
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

  // Paginacion hechos en colecciones
  public List<Hecho> obtenerHechosPaginados(Long coleccionId, int pagina, int tamanoPagina) {
    Coleccion coleccion = buscarPorId(coleccionId);
    if (coleccion == null) {
      return Collections.emptyList();
    }
    Fuente fuente = coleccion.getFuente();
    inyectarReposAFuente(fuente, coleccion);
    if (coleccion.getAlgoritmoConsenso() != null) {
      coleccion.recalcularConsensos();
    }

    List<Hecho> todosLosHechos = coleccion.mostrarHechos();

    int inicio = pagina * tamanoPagina;
    int fin = Math.min(inicio + tamanoPagina, todosLosHechos.size());

    if (inicio >= todosLosHechos.size()) {
      return Collections.emptyList();
    }

    return todosLosHechos.subList(inicio, fin);
  }

  public int getTotalHechos(Long coleccionId) {
    Coleccion coleccion = buscarPorId(coleccionId);
    if (coleccion == null) {
      return 0;
    }

    Fuente fuente = coleccion.getFuente();
    inyectarReposAFuente(fuente, coleccion);

    return coleccion.mostrarHechos().size();
  }

  public int getTotalPaginas(Long coleccionId, int tamanoPagina) {
    int totalHechos = getTotalHechos(coleccionId);
    return (int) Math.ceil((double) totalHechos / tamanoPagina);
  }

  private void inyectarReposAFuente(Fuente fuente, Coleccion coleccion) {
    if (fuente instanceof FuenteDinamica) {
      ((FuenteDinamica) fuente).setRepoHechos(repoHechos);
      ((FuenteDinamica) fuente).setRepoSolicitudes(repoSolicitudes);
    } else if (fuente instanceof FuenteAgregada) {
      FuenteAgregada fa = (FuenteAgregada) fuente;

      List<Fuente> fuentesInternas = new ArrayList<>();
      FuenteDinamica fd = new FuenteDinamica(repoHechos, repoSolicitudes);
      fuentesInternas.add(fd);

      // Agregar fuentes estáticas
      fuentesInternas.add(new FuenteEstaticaIncendios("src/test/resources/fuente_test_consenso.csv"));
      fuentesInternas.add(new FuenteEstaticaIncendios("src/test/resources/fuente_test_consenso2.csv"));
      //fuentesInternas.add(new FuenteEstaticaIncendios("src/test/resources/fires-all.csv"));
      //fuentesInternas.add(new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv"));
      fa.setFuentes(fuentesInternas);
      fa.setRepoHechos(repoHechos);
      fa.limpiarCache(); // para no traer valores anteriores
    }
    else if (fuente instanceof FuenteMetaMapa) {
      ((FuenteMetaMapa) fuente).inicializarClienteSiFalta();
    }

    coleccion.setSolicitudes(repoSolicitudes);
    coleccion.setRepoHechos(repoHechos);
  }

  public Fuente guardarFuente(Fuente fuente) {
    withTransaction(() -> {
      if (fuente.getId() == null) {
        entityManager().persist(fuente);
      } else {
        entityManager().merge(fuente);
      }
    });
    return fuente;
  }

}
