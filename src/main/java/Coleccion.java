import java.util.List;

public class Coleccion {
  public String titulo;
  public String descripcion;
  public Fuente fuente;
  public Criterio criterio;
  private final RepoSolicitudes solicitudes;

  public Coleccion(String titulo, String descripcion, Fuente fuente, Criterio criterio, RepoSolicitudes solicitudes) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterio = criterio;
    this.solicitudes = solicitudes;
  }

  public String getTitulo() {
    return this.titulo;
  }

  public List<Hecho> mostrarHechos() {
    return fuente.extraerHechos().stream()
        .filter(criterio::seCumpleCriterio)
        .filter(hecho -> !solicitudes.estaEliminado(hecho))
        .toList();
  }

  public List<Hecho> mostrarHechosFiltrados(Criterio filtro) {
    return mostrarHechos().stream()
        .filter(filtro::seCumpleCriterio)
        .toList();
  }
}
