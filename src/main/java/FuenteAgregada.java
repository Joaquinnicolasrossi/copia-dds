import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("Agregada")
public class FuenteAgregada extends Fuente {
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "fuente_agregada_fuentes", // Nombre de la nueva tabla intermedia
      joinColumns = @JoinColumn(name = "fuente_agregada_id"), // Columna para "esta" clase
      inverseJoinColumns = @JoinColumn(name = "fuente_id") // Columna para las fuentes de la lista
  )
  private List<Fuente> fuentes;
  @Transient
  private RepoHechos repositorio;

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