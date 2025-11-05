import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_fuente", discriminatorType = DiscriminatorType.STRING)
@Table(name = "fuente")
public abstract class Fuente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(insertable = false, updatable = false)
  private String tipo_fuente;

  public Long getId() {
    return id;
  }
  protected void setId(Long id) {
    this.id = id;
  }

  public abstract List<Hecho> extraerHechos();

  public abstract String getIdentificador();

}