import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuenteAgregada implements Fuente {
  private final List<Fuente> fuentes;

  public FuenteAgregada(List<Fuente> fuentes) { this.fuentes = fuentes; }

  @Override
  public List<Hecho> extraerHechos() {
    List<Hecho> hechos = new ArrayList<>();
    hechos = fuentes.stream()
        .flatMap(fuente -> fuente.extraerHechos().stream())
        .toList();;
    return filtrarRepetidos(hechos);
  }

  private List<Hecho> filtrarRepetidos(List<Hecho> hechos) {
    Set<String> titulosVistos = new HashSet<>();

    return hechos.stream()
        .filter(hecho -> titulosVistos.add(hecho.getTitulo()))
        .toList();
  }
}
