import java.util.ArrayList;
import java.util.List;

public class RepoColecciones {
  private List<Coleccion> colecciones = new ArrayList<>();

  public void crearColeccion(String titulo, String descripcion, Fuente fuente, Criterio criterio) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterio, this);
    colecciones.add(coleccion);
  }
}
