import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Usuario {
  @Id
  @GeneratedValue
  private Long id;

  private FuenteDinamica fuente;
  public boolean estaRegistrado = false;

  public Usuario(FuenteDinamica fuente) {
    this.fuente = fuente;
  }

  public Long getId() {
    return id;
  }

  public void subirHecho(String titulo, String descripcion, String categoria,
                         double latitud, double longitud, LocalDate fecha) {
    Hecho hecho = new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha,
        LocalDate.now(), Estado.PENDIENTE);
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
