import java.util.ArrayList;
import java.util.List;

public class Administrador extends Persona {
  //  public Fuente crearFuente(String nombre, String csv) {
  //    return new Fuente(nombre, csv);
  //  }

  public List<Coleccion> colecciones = new ArrayList<>();
  // Lista para traer los hechos desde el csv
  public List<Hecho> hechosImportados = new ArrayList<>();

  public void crearColeccion(String titulo, Fuente fuente, Criterio criterio) {
    colecciones.add(new Coleccion(titulo, fuente, criterio));
  }

  public void aceptarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.aceptarSolicitud(solicitud);
  }

  public void rechazarSolicitud(Solicitud solicitud, GestorSolicitudes gestor) {
    gestor.eliminarSolicitud(solicitud);
  }

  /*
  * -- Verificar y cambiar de ser necesario --
  * Crea una colección a partir de una fuente de datos y un criterio dado,
  * importados desde una fuente y la agrega a la lista de colecciones
  */
  public List<Hecho> importarHechos(String titulo, Fuente fuente, Criterio criterio) {
    List<Hecho> hechos = fuente.extraerHechos();
    hechosImportados.addAll(hechos);
    this.crearColeccion(titulo, fuente, criterio);
    return hechos;
  }

}
