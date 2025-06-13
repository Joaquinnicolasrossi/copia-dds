import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private LocalDate fecha;
  private LocalDate fechaCarga;
  private Estado estado;

  public Hecho() {

    this.fechaCarga = LocalDate.now();
    this.estado = Estado.PENDIENTE;
  }

  public Hecho(String titulo, String descripcion, String categoria, double latitud,
               double longitud, LocalDate fecha) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fecha = fecha;
    this.estado = Estado.PENDIENTE;
    this.fechaCarga = LocalDate.now();
  }

  public boolean estaDentroDePlazoDeEdicion() {
    return fechaCarga != null && fechaCarga.isAfter(LocalDate.now().minusDays(7));
  }

  public void aplicarRevision(Revision revision) {
    this.estado = revision.getEstado();
  }

  public static class HechoBuilder implements InterfaceBuilder<Hecho> {
    private String titulo;
    private String descripcion;
    private String categoria;
    private double latitud;
    private double longitud;
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

    public HechoBuilder setLatitud(double latitud) {
      this.latitud = latitud;
      return this;
    }

    public HechoBuilder setLongitud(double longitud) {
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

    public String getTitulo() {
      return this.titulo;
    }

    public String getDescripcion() {
      return this.descripcion;
    }

    public String getCategoria() {
      return this.categoria;
    }

    public Double getLatitud() {
      return this.latitud;
    }

    public Double getLongitud() {
      return this.longitud;
    }

    public LocalDate getFecha() {
      return this.fecha;
    }

    public LocalDate getFechaCarga() {
      return this.fechaCarga;
    }

    public Estado getEstado() {
      return this.estado;
    }

    @Override
    public Hecho build() {
      return new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha);
    }
  }


  public String getCategoria() {
    return categoria;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public double getLatitud() {
    return latitud;
  }

  public double getLongitud() {
    return longitud;
  }

  public Estado getEstado() {
    return estado;
  }

  public Hecho actualizarHechoConBuilderParcial(Hecho original, Hecho.HechoBuilder parcial) {
    Hecho.HechoBuilder combinado = new Hecho.HechoBuilder()
        .setTitulo(parcial.getTitulo() != null ? parcial.getTitulo() : original.getTitulo())
        .setDescripcion(parcial.getDescripcion() != null ? parcial.getDescripcion() : original
            .getDescripcion())
        .setCategoria(parcial.getCategoria() != null ? parcial.getCategoria() : original
            .getCategoria())
        .setLatitud(parcial.getLatitud() != 0 ? parcial.getLatitud() : original.getLatitud())
        .setLongitud(parcial.getLongitud() != 0 ? parcial.getLongitud() : original.getLongitud())
        .setFecha(parcial.getFecha() != null ? parcial.getFecha() : original.getFecha());
    return combinado.build();
  }


}
