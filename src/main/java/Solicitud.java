import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
//@Table(name = "solicitud")
@Entity
public class Solicitud {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne  @JoinColumn(name = "hecho_id")
  public Hecho hecho;
  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;
  public Boolean eliminado = false;
  @Column(name = "es_spam")
  private Boolean esSpam = false;

  public Solicitud() {}
//  private final RepoSolicitudes repoSolicitudes;
  @Transient
  private RepoSolicitudes repoSolicitudes;
  public Solicitud(Hecho hecho, String descripcion, RepoSolicitudes repoSolicitudes, Boolean esSpam) {
    descripcionValida(descripcion);
    this.hecho = hecho;
    this.descripcion = descripcion;
    this.repoSolicitudes = repoSolicitudes;
    this.esSpam = esSpam;
  }

  public void aceptarSolicitud() {
    eliminado = true;
  }

  public void eliminarSolicitud() {
    repoSolicitudes.eliminarSolicitud(this);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    return hecho == this.hecho && eliminado;
  }

  public void descripcionValida(String descripcion) throws IllegalArgumentException {
    if (descripcion.length() < 500) {
      throw new IllegalArgumentException("La solicitud debe contener una descripcion "
          + "de al menos 500 caracteres.");
    }
  }

  public void marcarComoSpam() {
    this.esSpam = true;
    this.eliminado = true;
  }

  public Boolean esSpam() {
    return esSpam;
  }

  public String getDescripcion() {
    return descripcion;
  }


}
