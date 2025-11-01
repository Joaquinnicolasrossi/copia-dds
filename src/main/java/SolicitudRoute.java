import io.javalin.Javalin;
import java.util.Map;

public class SolicitudRoute implements Router {
  private final SolicitudController controller;

  public SolicitudRoute(SolicitudController controller) {
    this.controller = controller;
  }

  public void configure(Javalin app) {
    app.post("/solicitud/{id}", ctx -> {
      Map<String, Object> model = controller.crearSolicitud(ctx);
      ctx.render("alert.hbs", model);
    });
  }
}
