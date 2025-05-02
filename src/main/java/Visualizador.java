public class Visualizador implements Rol {
  public void subirHecho(Usuario usuario) {
    usuario.setRol(new Contribuyente());
  }
}
