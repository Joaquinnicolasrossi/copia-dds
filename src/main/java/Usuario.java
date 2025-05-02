public class Usuario extends Persona {
  public Rol rol = new Visualizador();

  @Override
  public void subirHecho() {
    rol.subirHecho(this);
  }

  public void setRol(Rol rol) {
    this.rol = rol;
  }
}
