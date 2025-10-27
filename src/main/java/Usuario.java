import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
  @Id
  @GeneratedValue
  private Long id;
  @Column
  private String nombre;
  private String email;
  private String contrasena;
  private TipoUsuario tipoUsuario;
  public boolean estaRegistrado = false;
  @ManyToOne
  @JoinColumn(name = "fuente_id", nullable = true)
  private FuenteDinamica fuente;

  public void setTipoUsuario(TipoUsuario tipoUsuario) {
    this.tipoUsuario = tipoUsuario;
  }

  public String getContrasena() {
    return contrasena;
  }

  public String getNombre() {
    return nombre;
  }

  public Usuario(String contrasena, String email, String nombre) {
    this.contrasena = contrasena;
    this.email = email;
    this.nombre = nombre;
  }

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
