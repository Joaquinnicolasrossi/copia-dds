import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuenteAgregada extends Fuente {
  private final List<Fuente> fuentes;
  private final RepoHechos repositorio; // copia local

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
    List<Hecho> hechos = extraerHechosActualizados();
    repositorio.guardarHechos(hechos);
  }

  public List<Hecho> extraerHechosActualizados() {
    List<Hecho> hechos = fuentes.stream()
        .flatMap(fuente -> fuente.extraerHechos().stream())
        .toList();

    return filtrarRepetidos(hechos);
  }
}