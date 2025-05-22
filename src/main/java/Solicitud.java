public class Solicitud {
  public Hecho hecho;
  public String descripcion;
  public Boolean eliminado = false;

  public Solicitud(Hecho hecho, String descripcion, RepoSolicitudes repoSolicitudes) {
    descripcionValida(descripcion);
    this.hecho = hecho;
    this.descripcion = descripcion;
  }

  public void aceptar() {
    eliminado = true;
  }

  public void eliminar() {
    repoSolicitudes.eliminarSolicitud(this);
  }

  public void descripcionValida(String descripcion) throws IllegalArgumentException {
    if (descripcion.length() < 500) {
      throw new IllegalArgumentException("La solicitud debe contener una descripcion "
          + "de al menos 500 caracteres.");
    }
  }
}
