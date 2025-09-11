import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class CriterioFecha extends Criterio {
  public LocalDate fechaDesde;
  public LocalDate fechaHasta;

  public CriterioFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    LocalDate fechaHecho = hecho.getFecha();
    return fechaHecho.isBefore(fechaHasta) && fechaHecho.isAfter(fechaDesde);
  }
}