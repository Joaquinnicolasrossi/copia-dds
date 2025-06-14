import java.time.LocalDate;

public class Usuario {
  private FuenteDinamica fuente;
  public boolean estaRegistrado = false;

  public Usuario(FuenteDinamica fuente) {
    this.fuente = fuente;
  }

  public void subirHecho(String titulo, String descripcion, String categoria,
                         double latitud, double longitud, LocalDate fecha) {
    Hecho hecho = new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha, LocalDate.now(), Estado.PENDIENTE);
    if(estaRegistrado) { hecho.setUsuario(this); }
    fuente.subirHecho(hecho);
  }

  public void registrarse() {
    estaRegistrado = true;
  }

  public void actualizarHecho(Hecho hecho, Hecho.HechoBuilder builder) throws Exception {
    fuente.actualizarHecho(hecho, builder, this);
  }
}
