import java.util.ArrayList;
import java.util.List;

public class RepoColecciones {
  private List<Coleccion> colecciones = new ArrayList<>();
  private final RepoSolicitudes repoSolicitudes;

  public RepoColecciones(RepoSolicitudes repoSolicitudes) {
    this.repoSolicitudes = repoSolicitudes;
  }

  public void crearColeccion(String titulo, String descripcion, FuenteEstaticaIncendios fuente,
                             List<Criterio> criterios) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, criterios, repoSolicitudes);
    colecciones.add(coleccion);
  }
}
