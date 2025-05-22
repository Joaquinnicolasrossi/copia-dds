import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudes {
  private List<Solicitud> solicitudes = new ArrayList<>();

  public void eliminarSolicitud(Solicitud solicitud) {
    solicitudes.remove(solicitud);
  }

  public void nuevaSolicitud(Hecho hecho, String descripcion) {
    solicitudes.add(new Solicitud(hecho, descripcion, this));
  }

  public List<Solicitud> getSolicitudes() {
    return new ArrayList<>(solicitudes);
  }
}
