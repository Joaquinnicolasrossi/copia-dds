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
    Solicitud nueva = new Solicitud(hecho, descripcion, this);
    if (detectorDeSpam.esSpam(descripcion)) {
      throw new Exception("La solicitud es spam");
    }
    solicitudes.add(nueva);
  }

  public List<Solicitud> getSolicitudes() {
    return new ArrayList<>(solicitudes);
  }

  public Boolean estaEliminado(Hecho hecho) {
    return solicitudes.stream()
        .anyMatch(solicitud -> solicitud.hechoEliminado(hecho));
  }
}
