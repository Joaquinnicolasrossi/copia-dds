import java.time.LocalDate;

public class Criterio {
  public String categoria;
  public String ubicacion;
  public LocalDate fechaDesde;
  public LocalDate fechaHasta;

  public Criterio(String categoria, String ubicacion, LocalDate fechaDesde, LocalDate fechaHasta) {
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }


  public boolean seCumpleCriterio(Hecho hecho) {
    return cumpleCategoria(hecho) && cumpleLugar(hecho) && cumpleFecha(hecho);
  }

  private boolean cumpleCategoria(Hecho hecho) {
    return categoria.isEmpty() || hecho.getCategoria().equals(categoria);
  }

  private boolean cumpleLugar(Hecho hecho) {
    return ubicacion.isEmpty() || hecho.getUbicacion().equals(ubicacion);
  }

  private boolean cumpleFecha(Hecho hecho) {
    return (fechaDesde == null || hecho.getFecha().isAfter(fechaDesde))
        && (fechaHasta == null || hecho.getFecha().isBefore(fechaHasta));
  }

}
