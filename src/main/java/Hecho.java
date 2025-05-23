import java.time.LocalDate;

public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private LocalDate fecha;


  public Hecho(String titulo, String descripcion, String categoria, double latitud,
               double longitud, LocalDate fecha) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fecha = fecha;
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

  public String getDescripcion() { return descripcion; }

  public double getLatitud() { return latitud; }

  public double getLongitud() { return longitud; }

  public LocalDate Fecha() { return fecha;  }

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
