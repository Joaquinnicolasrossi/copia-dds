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
@Table(name = "hecho")
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
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinColumn(name = "provincia_id")
  private Provincia provincia;
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

  public Usuario getUsuario() {
    return usuario;
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

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  public void setFechaCarga(LocalDateTime fechaCarga) {
    this.fechaCarga = fechaCarga;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
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

    boolean mismaCategoria = this.categoria != null
        && this.categoria.equalsIgnoreCase(otro.getCategoria());

    boolean coordenadasSimilares =
        Math.abs(this.latitud - otro.getLatitud()) < 0.01
            && Math.abs(this.longitud - otro.getLongitud()) < 0.01;

    return mismaCategoria && coordenadasSimilares;
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
    private String provinciaNombre;

    public String getCategoria() {
      return categoria;
    }

    public String getDescripcion() {
      return descripcion;
    }

    public Estado getEstado() {
      return estado;
    }

    public LocalDateTime getFecha() {
      return fecha;
    }

    public Double getLatitud() {
      return latitud;
    }

    public LocalDateTime getFechaCarga() {
      return fechaCarga;
    }

    public Double getLongitud() {
      return longitud;
    }

    public String getProvinciaNombre() {
      return provinciaNombre;
    }

    public String getTitulo() {
      return titulo;
    }

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

    public HechoBuilder setProvincia(String provinciaNombre) {
      this.provinciaNombre = provinciaNombre;
      return this;
    }

    public Hecho build(RepoProvincias repoProvincias) {
      Hecho hecho = new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha, fechaCarga,
          estado);

      if (this.provinciaNombre != null && !this.provinciaNombre.isBlank()) {
        Provincia provinciaEntidad = repoProvincias.findOrCreate(this.provinciaNombre);
        hecho.setProvincia(provinciaEntidad);
      }

      return hecho;
    }
  }

  public void actualizarHecho(Hecho original, Hecho.HechoBuilder actualizacion, RepoProvincias repoProvincias) {
    Hecho.HechoBuilder combinado = new Hecho.HechoBuilder()
        .setTitulo(actualizacion.titulo != null ? actualizacion.titulo : original.getTitulo())
        .setDescripcion(actualizacion.descripcion != null ? actualizacion.descripcion : original.getDescripcion())
        .setCategoria(actualizacion.categoria != null ? actualizacion.categoria : original.getCategoria())
        .setLatitud(actualizacion.latitud != null ? actualizacion.latitud : original.getLatitud())
        .setLongitud(actualizacion.longitud != null ? actualizacion.longitud : original.getLongitud())
        .setFecha(actualizacion.fecha != null ? actualizacion.fecha : original.getFecha())
        .setFechaCarga(actualizacion.fechaCarga != null ? actualizacion.fechaCarga : original.getFechaCarga())
        .setEstado(original.getEstado());

    if (actualizacion.provinciaNombre != null) {
      combinado.setProvincia(actualizacion.provinciaNombre);
    } else if (original.getProvincia() != null) {
      combinado.setProvincia(original.getProvincia().getNombre());
    }

    Hecho combinadoHecho =  combinado.build(repoProvincias);
    original.setTitulo(combinadoHecho.getTitulo());
    original.setDescripcion(combinadoHecho.getDescripcion());
    original.setCategoria(combinadoHecho.getCategoria());
    original.setLatitud(combinadoHecho.getLatitud());
    original.setLongitud(combinadoHecho.getLongitud());
    original.setFechaCarga(combinadoHecho.getFechaCarga());
    original.setProvincia(combinadoHecho.getProvincia());
  }

  public Provincia getProvincia() {
    return provincia;
  }

  public void setProvincia(Provincia provincia) {
    this.provincia = provincia;
  }
}