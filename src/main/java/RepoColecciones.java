import java.util.ArrayList;
import java.util.List;

public class RepoColecciones {
  private List<Coleccion> colecciones = new ArrayList<>();
  private final RepoSolicitudes repoSolicitudes;

  public RepoColecciones(RepoSolicitudes repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  public void crearColeccion(String titulo, String descripcion, Fuente fuente, Criterio criterio) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterio, repoSolicitudes);
    colecciones.add(coleccion);
  }
}
