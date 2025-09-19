import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Usuario {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private FuenteDinamica fuente;
  public boolean estaRegistrado = false;

  public Usuario(FuenteDinamica fuente) {
    this.fuente = fuente;
  }

  protected Usuario(){}

  public Long getId() {
    return id;
  }

  public void subirHecho(String titulo, String descripcion, String categoria,
      double latitud, double longitud, LocalDateTime fecha) {
    Hecho hecho = new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha,
        LocalDateTime.now(), Estado.PENDIENTE);
    if (estaRegistrado) {
      hecho.setUsuario(this);
    }
    fuente.subirHecho(hecho);
  }

  public void registrarse() {
    estaRegistrado = true;
  }

  public void actualizarHecho(Hecho hecho, Hecho.HechoBuilder builder) throws Exception {
    fuente.actualizarHecho(hecho, builder, this);
  }

}
