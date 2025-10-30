import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class SolicitudController implements Router {
  private final RepoSolicitudes repoSolicitudes;
  private final RepoHechos repoHechos;

  public SolicitudController(RepoSolicitudes repoSolicitudes, RepoHechos repoHechos) {
    this.repoSolicitudes = repoSolicitudes;
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crearSolicitud(Context ctx) {
    Map<String, Object> model = new HashMap<>();
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
}
