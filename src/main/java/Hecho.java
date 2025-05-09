import java.time.LocalDate;

public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private LocalDate fecha;
  private boolean eliminado = false;

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

  public void eliminarse() {
    eliminado = true;
  }
}
