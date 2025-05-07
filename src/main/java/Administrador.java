public class Administrador extends Persona {
  //  public Fuente crearFuente(String nombre, String csv) {
//    return new Fuente(nombre, csv);
//  }
  public void crearColeccion() {

  }

  public void aceptarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.aceptarSolicitud(solicitud);
  }

  public void rechazarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.eliminarSolicitud(solicitud);
  }
}
