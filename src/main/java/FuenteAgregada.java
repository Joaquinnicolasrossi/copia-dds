import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("Agregada")
public class FuenteAgregada extends Fuente {
  @Transient
  private List<Fuente> fuentes;
  @Transient
  private RepoHechos repositorio; // copia local

  public FuenteAgregada(List<Fuente> fuentes, RepoHechos repositorio) {
    this.fuentes = fuentes;
    this.repositorio = repositorio;
  }

  public FuenteAgregada(){}
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