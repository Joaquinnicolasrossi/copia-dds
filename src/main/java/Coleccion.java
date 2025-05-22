import java.util.List;

public class Coleccion {
  public String titulo;
  public String descripcion;
  public Fuente fuente;
  public Criterio criterio;

  public Coleccion(String titulo, String descripcion, Fuente fuente, Criterio criterio) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterio = criterio;
  }

  public String getTitulo() {
    return this.titulo;
  }

  public List<Hecho> mostrarHechos() {
    return fuente.extraerHechos().stream()
        .filter(hecho -> criterio.seCumpleCriterio(hecho))
        .toList();
  }

  public List<Hecho> mostrarHechosFiltrados(Criterio filtro) {
    return mostrarHechos().stream()
        .filter(filtro::seCumpleCriterio)
        .toList();
  }
}
