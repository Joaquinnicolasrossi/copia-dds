import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CATEGORIA")
public class CriterioCategoria extends Criterio {
  public String categoria;

  public CriterioCategoria(String categoria) {
    this.categoria = categoria;
  }

  public CriterioCategoria() {}

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    return categoria.equals(hecho.getCategoria());
  }

}