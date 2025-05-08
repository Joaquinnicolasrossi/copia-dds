import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {
  public List<Solicitud> solicitudes = new ArrayList<>();

  public void eliminarSolicitud(Solicitud solicitud) {
    solicitudes.remove(solicitud);
  }

  public void aceptarSolicitud(Solicitud solicitud) {
    solicitud.aceptarse();
    solicitudes.remove(solicitud);
  }

  public void nuevaSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }
}
