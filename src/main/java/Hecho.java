import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "fuente_origen")
  private Fuente fuenteOrigen;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  //@Embedded
  //private Ubicacion ubicacion;
  @Column(name = "provincia")
  private String provincia;
  @Column(name = "fecha")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime fecha;
  @Column(name = "fechaCarga")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime fechaCarga;
  @Enumerated(EnumType.STRING)
  private Estado estado;
  @ManyToOne
  private Usuario usuario = null;
  @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Multimedia> multimedia = new ArrayList<>();

  public Hecho() {

  }

  public Long getId() {
    return id;
  }

  public Hecho(String titulo, String descripcion, String categoria, Double latitud,
               Double longitud, LocalDateTime fecha, LocalDateTime fechaCarga, Estado estado) {
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

  public LocalDateTime getFecha() {
    return fecha;
  }

  public LocalDateTime getFechaCarga() {
    return fechaCarga;
  }

  public Estado getEstado() {
    return estado;
  }

  public List<Multimedia> getMultimedia() {
    return multimedia;
  }

  public void setFuenteOrigen(Fuente fuenteOrigen) {
    this.fuenteOrigen = fuenteOrigen;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public void setMultimedia(List<Multimedia> multimedia) {
    this.multimedia = multimedia;
    multimedia.forEach(m -> m.setHecho(this));
  }

  public boolean estaDentroDePlazoDeEdicion() {
    return fechaCarga != null && fechaCarga.isAfter(LocalDateTime.now().minusDays(7));
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
    private LocalDateTime fecha;
    private LocalDateTime fechaCarga;
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

    public HechoBuilder setFecha(LocalDateTime fecha) {
      this.fecha = fecha;
      return this;
    }

    public HechoBuilder setFechaCarga(LocalDateTime fechaCarga) {
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

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
    if (provincia == null || provincia.isBlank()) {
      this.provincia = null;
      return;
    }

    // PAsamos a minùsculas
    String lower = provincia.toLowerCase();

    // Capitalizar la primera letra
    this.provincia = Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }

}
