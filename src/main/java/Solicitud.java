import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table (name = "solicitud")
public class Solicitud {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne  @JoinColumn(name = "hecho_id")
  public Hecho hecho;
  @Column(name = "descripcion", length = 2000)
  private String descripcion;
  public Boolean eliminado = false;
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

  public void setHecho(Hecho hecho) {
    this.hecho = hecho;
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

  public Long getId() { return id; }
  public String getDescripcion() {
    return descripcion;
  }


}
