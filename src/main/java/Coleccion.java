import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  @GeneratedValue
  private Long id;
  private String titulo;
  private String descripcion;
  @ManyToOne
  @JoinColumn(name = "provincia_id")
  private Provincia provincia;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fuente_id")
  public Fuente fuente;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "coleccion_id")
  public List<Criterio> criterios;
  @Transient
  private RepoHechos repoHechos;
  @Transient
  private RepoSolicitudes solicitudes;
  @Transient
  private EstadisticaRegistro estadisticaProvinciaMayor;
  @Transient
  private Map<String, EstadisticaRegistro> estadisticaProvinciaMasHechosPorCategoria;
  @Transient
  private Map<String, EstadisticaRegistro> estadisticaHoraMasHechosPorCategoria;
  @Transient
  private EstadisticaRegistro estadisticaCategoriaMayor;
  @Transient
  private EstadisticaRegistro estadisticaCantidadSpam;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "algoritmo_consenso_id")
  private Consenso algoritmoConsenso;
  // Guardo los hechosConsensuados en una lista para eficiencia
  @ManyToMany
  @JoinTable(
      name = "coleccion_hechos", // Nombre de la tabla de unión
      joinColumns = @JoinColumn(name = "coleccion_id"), // Columna que referencia a Coleccion
      inverseJoinColumns = @JoinColumn(name = "hecho_id") // Columna que referencia a Hecho
  )
  private Set<Hecho> hechosConsensuados = new HashSet<>();
  public Coleccion () {}
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
      RepoSolicitudes solicitudes, Consenso algoritmoConsenso, RepoHechos repoHechos) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
    this.algoritmoConsenso = algoritmoConsenso;
    this.repoHechos = repoHechos;
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

  public List<Hecho> navegar(Modo modo, Criterio filtro) {
    if (modo == Modo.IRRESTRICTA) {
      return mostrarHechosFiltrados(filtro);
    } else if (modo == Modo.CURADA) {
      List<Hecho> hechosFiltrados = mostrarHechosFiltrados(filtro);
      return filtrarHechosCurados(hechosFiltrados);
    } else
      throw new RuntimeException("Modo de navegacion no valido");
  }

  private List<Hecho> filtrarHechosCurados(List<Hecho> hechos) {
    return hechos.stream()
        .filter(this::estaConsensuado)
        .toList();
  }

  // Busca el hecho consensuado en la lista
  public boolean estaConsensuado(Hecho hecho) {
    if (algoritmoConsenso == null)
      return true;
    if (!(fuente instanceof FuenteAgregada fuenteAgregada))
      return true;

    return hechosConsensuados.contains(hecho);
  }

  public void recalcularConsensos() {
    if (!(fuente instanceof FuenteAgregada fuenteAgregada))
      return;
    if (algoritmoConsenso == null)
      return;

    List<Hecho> hechos = fuenteAgregada.extraerHechos();
    this.hechosConsensuados = hechos.stream()
        .filter(hecho -> algoritmoConsenso.estaConsensuado(hecho, fuenteAgregada))
        .collect(Collectors.toSet());
  }

  public void actualizarConfiguracion(String nuevoTitulo, String nuevaDescripcion, Fuente nuevaFuente, List<Criterio> nuevosCriterios, Consenso nuevoAlgoritmo){

    if (nuevoTitulo != null && !nuevoTitulo.isBlank()){
      this.titulo = nuevoTitulo;
    }

    if (nuevaDescripcion != null && !nuevaDescripcion.isBlank()){
      this.descripcion = nuevaDescripcion;
    }

    if (nuevaFuente != null){
      this.fuente = nuevaFuente;
    }
    if (nuevosCriterios != null && !nuevosCriterios.isEmpty()) {
      this.criterios = nuevosCriterios;
    }
    if (nuevoAlgoritmo != null){
      this.algoritmoConsenso = nuevoAlgoritmo;
    }
  }

  // Getters
  public Long getId() {
    return id;
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
  public String getAlgoritmoConsensoId() {
    if (algoritmoConsenso == null) return "ninguno";
    return algoritmoConsenso.getIdentificador();
  }
  public String getTipoFuenteId() {
    if (fuente == null) return "ninguna";
    return fuente.getIdentificador();
  }
  public List<Fuente> getFuentesRepo() {
    return repoHechos.obtenerTodasLasFuentes();
  }
  public List<Hecho> getHechos() {
    return repoHechos.obtenerTodosLosHechos();
  }
  public Consenso getAlgoritmoConsenso() {
    return algoritmoConsenso;
  }
  public EstadisticaRegistro getEstadisticaProvinciaMayor() { return estadisticaProvinciaMayor; }
  public EstadisticaRegistro getEstadisticaCategoriaMayor() { return estadisticaCategoriaMayor; }
  public Map<String, EstadisticaRegistro> getEstadisticaProvinciaMasHechosPorCategoria() { return estadisticaProvinciaMasHechosPorCategoria; }
  public Map<String, EstadisticaRegistro> getEstadisticaHoraMasHechosPorCategoria() { return estadisticaHoraMasHechosPorCategoria; }
  public EstadisticaRegistro getEstadisticaCantidadSpam() { return estadisticaCantidadSpam; }
  // Setters
  public void setTitulo(String titulo) { this.titulo = titulo; }
  public void setFuente(Fuente fuente) { this.fuente = fuente; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion;  }
  public void setAlgoritmoConsenso(Consenso algoritmo) {
    this.algoritmoConsenso = algoritmo;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public void setEstadisticaProvinciaMayor(EstadisticaRegistro estadisticaProvincia) {
    this.estadisticaProvinciaMayor = estadisticaProvincia;
  }
  public void setEstadisticaCategoriaMayor(EstadisticaRegistro estadisticaCategoria) {
    this.estadisticaCategoriaMayor = estadisticaCategoria;
  }
  public void setEstadisticaProvinciaMasHechosPorCategoria(Map<String, EstadisticaRegistro> estadisticaProvincia) {
    this.estadisticaProvinciaMasHechosPorCategoria = estadisticaProvincia;
  }
  public void setEstadisticaHoraMasHechosPorCategoria(Map<String, EstadisticaRegistro> estadisticaHoraMayor) {
    this.estadisticaHoraMasHechosPorCategoria = estadisticaHoraMayor;
  }
  public void setEstadisticaCantidadSpam(EstadisticaRegistro estadisticaCantidadSpam) {
    this.estadisticaCantidadSpam = estadisticaCantidadSpam;
  }

  public void setRepoHechos(RepoHechos repoHechos) {
    this.repoHechos = repoHechos;
  }

  public Set<Hecho> getHechosConsensuados() {
    return hechosConsensuados;
  }

  public void setHechosConsensuados(Set<Hecho> hechosConsensuados) {
    this.hechosConsensuados = hechosConsensuados;
  }


}