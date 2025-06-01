import ar.edu.utn.frba.dds.Enum.Estado;
import java.time.LocalDate;

public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private LocalDate fecha;
  private LocalDate fechaCarga;
  private Estado estado;


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

  public LocalDate Fecha() {
    return fecha;
  }

  @Override
  public String toString() {
    return "Hecho{" +
        "titulo='" + titulo + '\'' +
        ", descripcion='" + descripcion + '\'' +
        ", categoria='" + categoria + '\'' +
        ", latitud=" + latitud +
        ", longitud=" + longitud +
        ", fecha=" + fecha +
        '}';
  }
}
