import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitudController {
  private final RepoSolicitudes repoSolicitudes;
  private final RepoHechos repoHechos;

  public SolicitudController(RepoSolicitudes repoSolicitudes, RepoHechos repoHechos) {
    this.repoSolicitudes = repoSolicitudes;
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crearSolicitud(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);
    try {
      String descripcion = ctx.formParam("descripcion");
      Long hechoId = Long.parseLong(ctx.pathParam("id"));
      Hecho hecho = repoHechos.obtenerPorId(hechoId);
      if (hecho == null) {
        model.put("type", "error");
        model.put("message", "Hecho no encontrado");
        return model;
      }
      repoSolicitudes.nuevaSolicitud(hecho, descripcion);
      model.put("type", "success");
      model.put("message", "Solicitud creada correctamente.");
      return model;

    } catch (Exception e) {
      model.put("type", "danger");
      model.put("message", "Error interno: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      return model;
    }
  }

  public Map<String, Object> listar(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    List<Solicitud> solicitudes = new ArrayList<>();
    try {
      solicitudes = repoSolicitudes.getSolicitudesPendientes();
    } catch (Exception e) {
      model.put("solicitudes", Collections.emptyList());
    }
    model.put("solicitudes", solicitudes);
    return model;
  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }
}
