import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "solicitudRevision")
public class SolicitudRevision {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "hecho_id")
  private final Hecho hecho;
  private LocalDate fechaRevision;

  public SolicitudRevision(Hecho hecho) {
    this.hecho = hecho;
    this.fechaRevision = LocalDate.now();
  }

  public Hecho getHecho() {
    return hecho;
  }
}