import javax.persistence.Entity;

@Entity
public class CriterioCategoria extends Criterio {
  public String categoria;

  public CriterioCategoria(String categoria) {
    this.categoria = categoria;
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    return categoria.equals(hecho.getCategoria());
  }

}