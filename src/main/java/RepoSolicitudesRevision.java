import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudesRevision {
  List<SolicitudRevision> revisiones = new ArrayList<>();

  public void nuevaSolicitud(Hecho hecho) {
    revisiones.add(new SolicitudRevision(hecho));
  }

  public void eliminarSolcitud(SolicitudRevision solicitudRevision) {
    revisiones.remove(solicitudRevision);
  }

  public SolicitudRevision getSolicitudPorHecho(Hecho hecho) {
    return revisiones.stream()
        .filter(r -> r.getHecho().getTitulo().equals(hecho.getTitulo()))
        .findFirst()
        .orElse(null);
  }

  public List<SolicitudRevision> getRevisiones() {
    return new ArrayList<>(revisiones);
  }
}