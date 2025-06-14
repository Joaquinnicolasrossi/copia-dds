public class AceptarConSugerencia implements EstadoRevision {

  @Override
  public void aplicar(Hecho hecho, String sugerencia) {
    hecho.aceptarConSugerencias();
  }
}
