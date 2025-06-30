import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FuenteAgregada implements Fuente {
  private final List<Fuente> fuentes;
  private final RepoHechos repositorio;

  public FuenteAgregada(List<Fuente> fuentes, RepoHechos repositorio) {
    this.fuentes = fuentes;
    this.repositorio = repositorio;
  }

  @Override
  public List<Hecho> extraerHechos() {
    return fuentes.stream()
        .flatMap(fuente -> repositorio.obtenerHechosPorFuente(fuente).stream())
        .toList();
  }

  private List<Hecho> filtrarRepetidos(List<Hecho> hechos) {
    Set<String> titulosVistos = new HashSet<>();

    return hechos.stream()
        .filter(hecho -> titulosVistos.add(hecho.getTitulo()))
        .toList();
  }

  public void actualizarRepositorio() {
    List<Hecho> hechos = extrerHechosActualizados();
    repositorio.guardarHechos(hechos);
  }

  private List<Hecho> extrerHechosActualizados() {
    ExecutorService executor = Executors.newFixedThreadPool(fuentes.size());
    try {
      List<Callable<List<Hecho>>> tareas = fuentes.stream()
          .<Callable<List<Hecho>>>map(fuente -> () ->
              fuente.extraerHechos().stream()
                  .peek(hecho -> hecho.setFuenteOrigen(fuente))
                  .toList()
          )
          .toList();

      List<Future<List<Hecho>>> resultados = executor.invokeAll(tareas);

      List<Hecho> hechos = resultados.stream()
          .map(futuro -> {
            try {
              return futuro.get();
            } catch (Exception e) {
              return List.<Hecho>of();
            }
          })
          .flatMap(List::stream)
          .toList();

      return filtrarRepetidos(hechos);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Consulta interrumpida", e);
    } finally {
      executor.shutdown();
    }
  }
}
