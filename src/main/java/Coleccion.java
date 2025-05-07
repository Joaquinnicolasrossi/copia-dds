public class Coleccion {
  public String titulo;
  public Fuente fuente;
  public Criterio criterio;

  public Coleccion(String titulo, Fuente fuente, Criterio criterio) {
    this.titulo = titulo;
    this.fuente = fuente;
    this.criterio = criterio;
  }

  private void mostrarHechos() {
    fuente.extraerHechos().stream()
        .filter(hecho -> criterio.seCumpleCriterio(hecho))
        .forEach(System.out::println);
  }
}
