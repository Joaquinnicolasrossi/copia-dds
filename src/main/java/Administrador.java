import java.util.ArrayList;
import java.util.List;

public class Administrador extends Persona {

  private Fuente crearFuente(String csv) {
    return new Fuente(csv); //para extraer los hechos y crear la coleccion
  }

  //arbitrariamente: cada vez que creo una fuente es para crear una coleccion sobre esta.
  public void crearColeccion(String titulo, String descripcion, String csv, Criterio criterio) {
    Fuente fuente = this.crearFuente(csv);
    GestorColecciones.getInstancia().crearColeccion(titulo, descripcion, fuente, criterio);
  }

  public void aceptarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.aceptarSolicitud(solicitud);
  }

  public void rechazarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.eliminarSolicitud(solicitud);
  }

}
