public class Rechazar implements EstadoRevision {

  @Override
  public void aplicar(Hecho hecho, String sugerencia) {
    hecho.rechazar();
  }
}