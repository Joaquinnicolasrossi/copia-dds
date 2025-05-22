import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Criterio {
  private String categoria;
  private LocalDate fechaDesde;
  private LocalDate fechaHasta;

  public boolean seCumpleCriterio(Hecho hecho) {
    return perteneceACategoria(hecho) && perteneceAFecha(hecho);
  };

  private boolean perteneceACategoria(Hecho hecho) {
    return hecho.getCategoria() == categoria;
  }

  private boolean perteneceAFecha(Hecho hecho) {
    LocalDate fechaHecho = hecho.getFecha();
    return fechaHecho.isBefore(fechaHasta) && fechaHecho.isAfter(fechaDesde);
  }
}