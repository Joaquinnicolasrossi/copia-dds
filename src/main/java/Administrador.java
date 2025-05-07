import java.util.ArrayList;
import java.util.List;

public class Administrador extends Persona {
  //  public Fuente crearFuente(String nombre, String csv) {
//    return new Fuente(nombre, csv);
//  }
  public List<Coleccion> colecciones = new ArrayList<>();
  public void crearColeccion(String titulo, Fuente fuente, Criterio criterio) {
    colecciones.add(new Coleccion(titulo, fuente, criterio));
  }

  public void aceptarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.aceptarSolicitud(solicitud);
  }

  public void rechazarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.eliminarSolicitud(solicitud);
  }
}
