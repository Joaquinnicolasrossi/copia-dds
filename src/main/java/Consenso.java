import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_algoritmo", discriminatorType = DiscriminatorType.STRING)
public abstract class Consenso implements AlgoritmoConsenso {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(insertable = false, updatable = false)
  private String tipo_algoritmo;

  public String getNombreAlgoritmo() {
    return tipo_algoritmo != null ? tipo_algoritmo : "Ninguno";
  }

  public abstract String getIdentificador();

}
