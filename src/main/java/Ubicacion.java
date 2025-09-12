import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Ubicacion {
  @Enumerated(EnumType.STRING)
  private Provincia provincia;
}
