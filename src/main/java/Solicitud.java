import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Solicitud {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  public Hecho hecho;
  private String descripcion;
  public Boolean eliminado = false;
  private final RepoSolicitudes repoSolicitudes;

  public Solicitud(Hecho hecho, String descripcion, RepoSolicitudes repoSolicitudes) {
    descripcionValida(descripcion);
    this.hecho = hecho;
    this.descripcion = descripcion;
    this.repoSolicitudes = repoSolicitudes;
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

  public String getDescripcion() {
    return descripcion;
  }
}
