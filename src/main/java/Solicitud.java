public class Solicitud {
  public Hecho hecho;
  public String descripcion;

  public Solicitud(Hecho hecho, String descripcion) {
    descripcionValida(descripcion);
    this.hecho = hecho;
    this.descripcion = descripcion;
  }

  public void aceptarse() {
    hecho.eliminarse();
  }

  public void descripcionValida(String descripcion) throws IllegalArgumentException {
    if (descripcion.length() < 500) {
      throw new IllegalArgumentException("La solicitud debe contener una descripcion " +
          "de al menos 500 caracteres.");
    }
  }
}
