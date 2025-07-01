public class ModoFactory {
  public Navegador obtenerModo(Modo modo) {

    if (modo == Modo.CURADA) {
      return new Curada();
    }
    if (modo == Modo.IRRESTRICTA) {
      return new Irrestricta();
    }

    return null;
  }
}
