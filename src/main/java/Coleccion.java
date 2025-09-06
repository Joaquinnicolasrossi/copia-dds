import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  @GeneratedValue
  private Long id;
  public String titulo;
  private String descripcion;
  @ManyToOne
  public Fuente fuente;
  @ElementCollection
  public List<Criterio> criterios;
  @ManyToOne
  private RepoHechos repoHechos;
  @ManyToOne
  private final RepoSolicitudes solicitudes;
  @ManyToOne
  private AlgoritmoConsenso algoritmoConsenso;
  // Guardo los hechosConsensuados en una lista para eficiencia
  private Set<Hecho> hechosConsensuados = new HashSet<>();

  // Constructor original (sin algoritmo --> para compatibilidad)
  public Coleccion(String titulo, String descripcion,
                   Fuente fuente, List<Criterio> criterios,
                   RepoSolicitudes solicitudes) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
    this.algoritmoConsenso = null;
  }

  // Constructor nuevo con algoritmo de consenso
  public Coleccion(String titulo, String descripcion,
                   Fuente fuente, List<Criterio> criterios,
                   RepoSolicitudes solicitudes, AlgoritmoConsenso algoritmoConsenso , RepoHechos repoHechos) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
    this.algoritmoConsenso = algoritmoConsenso;
    this.repoHechos = repoHechos;
  }

  public String getTitulo() {
    return this.titulo;
  }

  public Fuente getFuente() {
    return this.fuente;
  }
  public String getDescripcion() {
    return descripcion;
  }


  public List<Hecho> mostrarHechos() {
    return fuente.extraerHechos().stream()
        .filter(hecho -> cumpleCriterios(hecho))
        .filter(hecho -> !solicitudes.estaEliminado(hecho))
        .toList();
  }

  public List<Hecho> mostrarHechosFiltrados(Criterio filtro) {
    return mostrarHechos().stream()
        .filter(filtro::seCumpleCriterio)
        .toList();
  }

  private boolean cumpleCriterios(Hecho hecho) {
    return criterios.stream().allMatch(criterio -> criterio.seCumpleCriterio(hecho));
  }



  // Algoritmo de consenso
  public List<Fuente> getFuentesRepo() {
    return repoHechos.obtenerTodasLasFuentes();
  }
  public List<Hecho> getHechos() {
    return repoHechos.obtenerTodosLosHechos();
  }

  public List<Hecho> navegar(Modo modo, Criterio filtro) {
    if(modo == Modo.IRRESTRICTA) {
      return mostrarHechosFiltrados(filtro);
    } else if (modo == Modo.CURADA) {
      List<Hecho> hechosFiltrados = mostrarHechosFiltrados(filtro);
      return filtrarHechosCurados(hechosFiltrados);
    } else throw new RuntimeException("Modo de navegacion no valido");
  }
  public AlgoritmoConsenso getAlgoritmoConsenso() {
    return algoritmoConsenso;
  }

  public void setAlgoritmoConsenso(AlgoritmoConsenso algoritmo){
    this.algoritmoConsenso = algoritmo;
  }

  private List<Hecho> filtrarHechosCurados(List<Hecho> hechos) {
    return hechos.stream()
        .filter(this::estaConsensuado)
        .toList();
  }
  // Busca el hecho consensuado en la lista
  public boolean estaConsensuado(Hecho hecho) {
    if (algoritmoConsenso == null) return true;
    if (!(fuente instanceof FuenteAgregada fuenteAgregada)) return true;

    return hechosConsensuados.contains(hecho);
  }

  public void recalcularConsensos(){
    if (!(fuente instanceof FuenteAgregada fuenteAgregada)) return;
    if (algoritmoConsenso == null) return;

    List<Hecho> hechos = fuenteAgregada.extraerHechos();
    this.hechosConsensuados = hechos.stream()
        .filter(hecho -> algoritmoConsenso.estaConsensuado(hecho, fuenteAgregada))
        .collect(Collectors.toSet());
  }
}