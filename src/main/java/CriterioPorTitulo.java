public class CriterioPorTitulo implements Criterio {
  private String palabra;

  public CriterioPorTitulo(String palabra) {
    this.palabra = palabra.toLowerCase();
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    return hecho.getTitulo().toLowerCase().contains(palabra);
  }
}