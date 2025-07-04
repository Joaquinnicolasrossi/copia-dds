import java.util.List;
import java.util.ArrayList;

public class Coleccion {
  public String titulo;
  private String descripcion;
  public Fuente fuente;
  public List<Criterio> criterios;
  private final RepoSolicitudes solicitudes;
  private AlgoritmoConsenso algoritmoConsenso;

  public Coleccion(RepoSolicitudes solicitudes) {
    this.solicitudes = solicitudes;
  }

  public AlgoritmoConsenso getTipoAlgoritmoConsenso() {
    return algoritmoConsenso;
  }

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
                   RepoSolicitudes solicitudes, AlgoritmoConsenso algoritmoConsenso) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
    this.algoritmoConsenso = algoritmoConsenso;
  }

  public String getTitulo() {
    return this.titulo;
  }

  public List<Fuente> getFuentes() {
    List<Fuente> fuentes = new ArrayList<>();
    fuentes.add(fuente);
    return fuentes;
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

  public String getDescripcion() {
    return descripcion;
  }

  // Algoritmo de consenso

  public AlgoritmoConsenso getAlgoritmoConsenso() {
    return algoritmoConsenso;
  }

  public Fuente getFuente() {
    return fuente;
  }

  public void setAlgoritmoConsenso(AlgoritmoConsenso algoritmo){
    this.algoritmoConsenso = algoritmo;
  }

  public boolean estaConsensuado(Hecho hecho) {
    if (algoritmoConsenso == null) return true;
    List<Fuente> fuentes = new ArrayList<>();
    fuentes.add(fuente);
    return algoritmoConsenso.estaConsensuado(hecho, fuentes);
  }
}