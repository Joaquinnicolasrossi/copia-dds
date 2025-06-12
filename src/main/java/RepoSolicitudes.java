import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudes {
  private final List<Solicitud> solicitudes = new ArrayList<>();
  private DetectorDeSpamFiltro detectorDeSpam;

  public RepoSolicitudes(DetectorDeSpamFiltro detectorDeSpamFiltro) {
    this.detectorDeSpam = detectorDeSpamFiltro;
  }

  public void eliminarSolicitud(Solicitud solicitud) {
    solicitudes.remove(solicitud);
  }

  public void nuevaSolicitud(Hecho hecho, String descripcion) {
    Solicitud nueva = new Solicitud(hecho, descripcion, this);
    if (detectorDeSpam.esSpam(descripcion)) {
      System.out
          .println("El texto es spam");
      return;
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
