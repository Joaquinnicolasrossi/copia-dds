
import java.util.ArrayList;
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

  // Mostrar todos los hechos que pertencen a la coleccion
  // ya que cumplen su criterio
  public void mostrarHechos() {
    fuente.extraerHechos().stream()
        .filter(hecho -> criterio.seCumpleCriterio(hecho))
        .forEach(hecho -> System.out.println(hecho.getTitulo()));
  }

  // Mostrar hechos segun el criterio del user
  public void mostrarHechosFiltrados(Criterio criterio) {
    fuente.extraerHechos().stream()
        .filter(hecho -> criterio.seCumpleCriterio(hecho))
        .forEach(hecho -> System.out.println(hecho.getTitulo()));
  }

  // Mostrar todos los hechos (de la fuente)
  // No cumple el enunciado pero sirve para debug
  public void mostrarTodosLosHechos() {
    fuente.extraerHechos().forEach(hecho -> System.out.println(hecho.getTitulo()));
  }
}
