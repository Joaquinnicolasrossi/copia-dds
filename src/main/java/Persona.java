public abstract class Persona {
  public void subirHecho() {

  }

  public void solicitarEliminarHecho(Hecho hecho, String descripcion, GestorSolicitudes gestor) {
    gestor.nuevaSolicitud(new Solicitud(hecho, descripcion));
  }
}
