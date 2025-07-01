import java.time.LocalDate;

//TODO esto podria guardar el historial de revisiones
public class Revision {
  private final EstadoRevision estado;
  private final String sugerencia;
  private final LocalDate fecha;

  public Revision(EstadoRevision estado, String sugerencia) {
    this.estado = estado;
    this.sugerencia = sugerencia;
    this.fecha = LocalDate.now();
  }

}
