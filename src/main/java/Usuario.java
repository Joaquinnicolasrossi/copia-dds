public class Usuario extends Persona {
  public Rol rol = new Visualizador();
  private Registro registro;

  public Usuario(Rol rol, Registro registro) {
    this.rol = rol;
    this.registro = registro;
  }

  //   @Override
  //   public void subirHecho() {
  //     this.rol = rol.subirHecho();
  //   }

  public void cambiarRol(Rol rol) {
    this.rol = rol;
  }

  public void registrarse(Registro registro) {
    this.registro = registro;
  }
}
