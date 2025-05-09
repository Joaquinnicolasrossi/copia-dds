import java.util.ArrayList;
import java.util.List;

public class GestorColecciones {

  private static GestorColecciones instancia;  // SINGLETON : única instancia estática (global)
  private List<Coleccion> colecciones = new ArrayList<>(); // lista de todas las colecciones


  public static GestorColecciones getInstancia() {
    if (instancia == null) {
      instancia = new GestorColecciones();
    }
    return instancia;
  }

  public void crearColeccion(String titulo, String descripcion, Fuente fuente, Criterio criterio) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterio);
    colecciones.add(coleccion);
  }

  public List<Coleccion> obtenerColecciones() {
    return colecciones;
  }

  public Coleccion obtenerColeccion(String nombre) {
    return colecciones.stream()
        .filter(c -> c.getTitulo().equals(nombre))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
  }
}
