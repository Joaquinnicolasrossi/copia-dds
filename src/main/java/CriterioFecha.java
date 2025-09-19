import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class CriterioFecha extends Criterio {
  public LocalDateTime fechaDesde;
  public LocalDateTime fechaHasta;

  public CriterioFecha(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }

  @Override
  public boolean seCumpleCriterio(Hecho hecho) {
    LocalDateTime fechaHecho = hecho.getFecha();
    return fechaHecho.isBefore(fechaHasta) && fechaHecho.isAfter(fechaDesde);
  }
}