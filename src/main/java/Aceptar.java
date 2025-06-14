
public class Aceptar implements EstadoRevision {
  @Override
  public void aplicar(Hecho hecho, String sugerencia) {
    hecho.aceptar();
  }
}

