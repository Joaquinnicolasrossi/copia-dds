import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
//@Inheritance(strategy = InheritanceType.JOINED) --> No recuerdo si tambièn deberia ser single
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_criterio", discriminatorType = DiscriminatorType.STRING)
public abstract class Criterio {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "coleccion_id")
  private Coleccion coleccion;

  public abstract boolean seCumpleCriterio(Hecho hecho);
}