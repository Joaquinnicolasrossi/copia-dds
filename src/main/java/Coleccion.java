import java.util.List;

public class Coleccion {
  public String titulo;
  private String descripcion;
  public Fuente fuente;
  public List<Criterio> criterios;
  private final RepoSolicitudes solicitudes;
  private FuenteAgregada fuenteAgregada;

  public Coleccion(String titulo, String descripcion,
                   Fuente fuente, List<Criterio> criterios,
                   RepoSolicitudes solicitudes) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
  }

  public List<Fuente> getFuentes() {
    return fuenteAgregada.getFuentes();
  }

  public List<Hecho> getHechos() {
    return fuenteAgregada.extraerHechos();
  }


  public String getTitulo() {
    return this.titulo;
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
}
