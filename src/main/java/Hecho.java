import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "fuente_id")
  private Fuente fuenteOrigen;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @Embedded
  private Ubicacion ubicacion;
  private LocalDate fecha;
  private LocalDate fechaCarga;
  @Enumerated(EnumType.STRING)
  private Estado estado;
  @Transient
  private Usuario usuario = null;
  @Transient
  private List<ContenidoMultimedia> contenidoMultimedia;

  public Hecho() {

  }

  public Hecho(String titulo, String descripcion, String categoria, Double latitud,
      Double longitud, LocalDate fecha, LocalDate fechaCarga, Estado estado) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fecha = fecha;
    this.fechaCarga = fechaCarga;
    this.estado = estado;
  }

  public Fuente getFuenteOrigen() {
    return fuenteOrigen;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getCategoria() {
    return categoria;
  }

  public Double getLatitud() {
    return latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public LocalDate getFechaCarga() {
    return fechaCarga;
  }

  public Estado getEstado() {
    return estado;
  }

  public void setFuenteOrigen(Fuente fuenteOrigen) {
    this.fuenteOrigen = fuenteOrigen;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public boolean estaDentroDePlazoDeEdicion() {
    return fechaCarga != null && fechaCarga.isAfter(LocalDate.now().minusDays(7));
  }

  public boolean perteneceA(Usuario usuario) {
    return this.usuario != null && this.usuario == usuario;
  }

  public void setEstado(Estado estado) {
    this.estado = estado;
  }

  public boolean tieneMismoContenidoQue(Hecho otro) {

    if (otro == null)
      return false;

    return this.titulo.equals(otro.getTitulo())
        && this.descripcion.equals(otro.getDescripcion())
        && this.categoria.equals(otro.getCategoria())
        && this.latitud.equals(otro.getLatitud())
        && this.longitud.equals(otro.getLongitud());
  }

  public static class HechoBuilder {
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private LocalDate fecha;
    private LocalDate fechaCarga;
    private Estado estado;

    public HechoBuilder setTitulo(String titulo) {
      this.titulo = titulo;
      return this;
    }

    public HechoBuilder setDescripcion(String descripcion) {
      this.descripcion = descripcion;
      return this;
    }

    public HechoBuilder setCategoria(String categoria) {
      this.categoria = categoria;
      return this;
    }

    public HechoBuilder setLatitud(Double latitud) {
      this.latitud = latitud;
      return this;
    }

    public HechoBuilder setLongitud(Double longitud) {
      this.longitud = longitud;
      return this;
    }

    public HechoBuilder setFecha(LocalDate fecha) {
      this.fecha = fecha;
      return this;
    }

    public HechoBuilder setFechaCarga(LocalDate fechaCarga) {
      this.fechaCarga = fechaCarga;
      return this;
    }

    public HechoBuilder setEstado(Estado estado) {
      this.estado = estado;
      return this;
    }

    public Hecho build() {
      return new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha, fechaCarga,
          estado);
    }
  }

  public Hecho actualizarHecho(Hecho original, Hecho.HechoBuilder actualizacion) {
    Hecho parcial = actualizacion.build();

    Hecho.HechoBuilder combinado = new Hecho.HechoBuilder()
        .setTitulo(parcial.getTitulo() != null ? parcial.getTitulo() : original.getTitulo())
        .setDescripcion(parcial.getDescripcion() != null ? parcial.getDescripcion()
            : original
                .getDescripcion())
        .setCategoria(parcial.getCategoria() != null ? parcial.getCategoria()
            : original
                .getCategoria())
        .setLatitud(parcial.getLatitud() != null ? parcial.getLatitud() : original.getLatitud())
        .setLongitud(parcial.getLongitud() != null ? parcial.getLongitud() : original.getLongitud())
        .setFecha(parcial.getFecha() != null ? parcial.getFecha() : original.getFecha())
        .setFechaCarga(original.getFechaCarga())
        .setEstado(original.getEstado());
    return combinado.build();
  }

}
