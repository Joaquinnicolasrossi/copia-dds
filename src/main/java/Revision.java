import java.time.LocalDate;

public class Revision {


  private Estado estado;
  private String sugerencia;
  private LocalDate fecha;
  private Hecho hecho;


  public Revision(Estado estado, String sugerencia, Hecho hecho) {
    this.estado = estado;
    this.sugerencia = sugerencia;
    this.fecha = LocalDate.now();
    this.hecho = hecho;
    hecho.aplicarRevision(this);
  }

  @Override
  public String toString() {
    return "Revision{"
        +
        "estado=" + estado
        +
        ", sugerencia='" + sugerencia
        + '\''
        +
        ", fecha=" + fecha
        +
        ", hecho=" + hecho
        +
        '}';
  }

  public void setEstado(Estado estado) {
    this.estado = estado;
  }

  public void setSugerencia(String sugerencia) {
    this.sugerencia = sugerencia;
  }

  public Estado getEstado() {
    return estado;
  }

  public String getSugerencia() {
    return sugerencia;
  }


}