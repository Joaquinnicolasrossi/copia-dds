import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("AGREGADA")
public class FuenteAgregada extends Fuente {
  @Transient
  private List<Fuente> fuentes;

  @Column(name = "fuentes_combinadas")
  @Transient
  private RepoHechos repositorio;
  private String fuentesCombinadas;

  public FuenteAgregada(List<Fuente> fuentes, RepoHechos repositorio) {
    this.fuentes = fuentes;
    this.repositorio = repositorio;
    this.fuentesCombinadas = fuentes.stream()
        .map(Fuente::getIdentificador)
        .collect(Collectors.joining(","));
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
  public String getIdentificador(){
    return "agregada";
  }
}