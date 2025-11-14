import java.time.LocalDate;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.uqbarproject.jpa.java8.extras.convert.LocalDateConverter;

@Entity
@Table(name = "solicitudRevision")
public class SolicitudRevision {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "hecho_id")
  private  Hecho hecho;
  @Convert(converter = LocalDateConverter.class)
  private LocalDate fechaRevision;
  @ManyToOne
  private Usuario usuario;

  public SolicitudRevision(Hecho hecho , Usuario usuario) {
    this.hecho = hecho;
    this.fechaRevision = LocalDate.now();
    this.usuario = usuario;
  }

  public LocalDate getFechaRevision() {
    return fechaRevision;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public SolicitudRevision() {
  }

  public Hecho getHecho() {
    return hecho;
  }
}