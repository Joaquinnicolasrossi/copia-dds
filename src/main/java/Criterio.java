import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Criterio {
  private String categoria;
  private LocalDate fechaDesde;
  private LocalDate fechaHasta;

  public Criterio(String categoria, LocalDate fechaDesde, LocalDate fechaHasta) {
    this.categoria = categoria;
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }

  public boolean seCumpleCriterio(Hecho hecho) {
    return perteneceACategoria(hecho) && perteneceAFecha(hecho);
  };

  private boolean perteneceACategoria(Hecho hecho) {
    return Objects.equals(hecho.getCategoria(), categoria);
  }

  private boolean perteneceAFecha(Hecho hecho) {
    LocalDate fechaHecho = hecho.getFecha();
    return fechaHecho.isBefore(fechaHasta) && fechaHecho.isAfter(fechaDesde);
  }
}