
import java.util.ArrayList;
import java.util.List;

public class Coleccion {
  public String titulo;
  public Fuente fuente;
  public Criterio criterio;

  public Coleccion(String titulo, Fuente fuente, Criterio criterio) {
    this.titulo = titulo;
    this.fuente = fuente;
    this.criterio = criterio;
  }

  // private void mostrarHechos() { --> Le cambie para llamarla desde visualizador
  public void mostrarHechos() {
    fuente.extraerHechos().stream()
        .filter(hecho -> criterio.seCumpleCriterio(hecho))
        .forEach(System.out::println);
  }

  // Mostrar todos los hechos
  public List<Hecho> mostrarTodosLosHechos(){
    List<Hecho> hechos = fuente.extraerHechos();
    hechos.forEach(System.out::println);
    return hechos;
  }
  public List<Hecho> mostrarHechosFiltrados(Filtro filtro) {
    List<Hecho> hechos = fuente.extraerHechos().stream().filter(hecho -> filtro.cumpleFiltro(hecho));
    hechos.forEach(System.out::println);
    return hechos;
  }
}
