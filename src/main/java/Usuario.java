public class Usuario extends Persona {
  public Rol rol = new Visualizador();

  @Override
  public void subirHecho() {
    this.rol = rol.subirHecho();
  }
}
