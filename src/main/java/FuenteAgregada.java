import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FuenteAgregada implements Fuente {
  private final List<Fuente> fuentes;

  public FuenteAgregada(List<Fuente> fuentes) { this.fuentes = fuentes; }

  @Override
  public List<Hecho> extraerHechos() {

    try (ExecutorService executor = Executors.newFixedThreadPool(fuentes.size())) {
      List<Callable<List<Hecho>>> tareas = fuentes.stream()
          .<Callable<List<Hecho>>>map(fuente -> fuente::extraerHechos)
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
    }
  }

  private List<Hecho> filtrarRepetidos(List<Hecho> hechos) {
    Set<String> titulosVistos = new HashSet<>();

    return hechos.stream()
        .filter(hecho -> titulosVistos.add(hecho.getTitulo()))
        .toList();
  }
}
