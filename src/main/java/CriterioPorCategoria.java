public class CriterioPorCategoria implements Criterio {
  private String categoria;

  public CriterioPorCategoria(String categoria) {
    this.categoria = categoria.toLowerCase();
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    return hecho.getCategoria().toLowerCase().contains(categoria);
  }
}