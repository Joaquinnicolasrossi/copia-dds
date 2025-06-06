import java.util.List;

public class Coleccion {
  public String titulo;
  public String descripcion;
  public InterfaceFuente fuente;
  public List<Criterio> criterios;
  private final RepoSolicitudes solicitudes;

  public Coleccion(String titulo, String descripcion,
                   InterfaceFuente fuente, List<Criterio> criterios,
                   RepoSolicitudes solicitudes) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = criterios;
    this.solicitudes = solicitudes;
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
}
