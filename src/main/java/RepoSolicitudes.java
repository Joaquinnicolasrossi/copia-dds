import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudes {
  private final List<Solicitud> solicitudes = new ArrayList<>();
  private DetectorDeSpam detectorDeSpam;

  public RepoSolicitudes(DetectorDeSpam detectorDeSpam) {
    this.detectorDeSpam = detectorDeSpam;
  }

  public void eliminarSolicitud(Solicitud solicitud) {
    solicitudes.remove(solicitud);
  }

  public void nuevaSolicitud(Hecho hecho, String descripcion) throws Exception {
    boolean spam = detectorDeSpam.esSpam(descripcion);

    Solicitud nueva = new Solicitud(hecho, descripcion, this, spam);

    solicitudes.add(nueva);
  }

  public List<Solicitud> getSolicitudes() {
    return new ArrayList<>(solicitudes);
  }

  public Boolean estaEliminado(Hecho hecho) {
    return solicitudes.stream()
        .anyMatch(solicitud -> solicitud.hechoEliminado(hecho));
  }

  public long cantidadSolicitudesSpam() {
    return solicitudes.stream()
        .filter(Solicitud::esSpam)
        .count();
  }
}
