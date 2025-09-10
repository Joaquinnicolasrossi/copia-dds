import java.time.LocalDate;

public class SolicitudRevision {
  private final Hecho hecho;
  private LocalDate fechaRevision;

  public SolicitudRevision(Hecho hecho) {
    this.hecho = hecho;
    this.fechaRevision = LocalDate.now();
  }

  public Hecho getHecho() {
    return hecho;
  }
}