import java.time.LocalDate;

public class CriterioPorFecha implements Criterio {
  private LocalDate desde;
  private LocalDate hasta;

  public CriterioPorFecha(LocalDate desde, LocalDate hasta) {
    this.desde = desde;
    this.hasta = hasta;
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    LocalDate fechaHecho = hecho.getFecha();
    boolean cumpleDesde = (desde == null || !fechaHecho.isBefore(desde));
    boolean cumpleHasta = (hasta == null || !fechaHecho.isAfter(hasta));
    return cumpleDesde && cumpleHasta;
  }
}