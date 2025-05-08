import java.util.List;

public class Visualizador implements Rol {

  public Rol subirHecho() {
    return new Contribuyente();
  }

  /*
  * Consulto por todos los hechos sin filtros y devuelvo una lista
  */
  public List<Hecho> verTodosLosHechos(Coleccion coleccion) {
    return coleccion.mostrarTodosLosHechos();
  }

  // Entiendo que deberia ir una lista, pero lo dejamos asi ahora
  // public List<Hecho> verHechosFiltrados(Coleccion coleccion){
  public void verHechosFiltrados(Coleccion coleccion, Filtro filtro) {
      coleccion.mostrarHechosFiltrados(filtro);
  }
}
