import io.javalin.Javalin;
import java.util.Map;

public class SolicitudRoute implements Router {
  private final SolicitudController controller;

  public SolicitudRoute(SolicitudController controller) {
    this.controller = controller;
  }

  public void configure(Javalin app) {
    app.get("/solicitud", ctx -> ctx.render("solicitudes.hbs", controller.listar(ctx)));
    app.post("/solicitud/{id}", ctx -> {
      Map<String, Object> model = controller.crearSolicitud(ctx);
      ctx.render("alert.hbs", model);
    });
    app.get("/solicitud/{id}/form", controller::mostrarFormularioSolicitud);
    app.patch("/solicitud/{id}/aceptar", ctx -> ctx.render("solicitudes.hbs", controller.listar(ctx)));
    app.delete("/solicitud/{id}/eliminar", ctx -> ctx.render("solicitudes.hbs", controller.listar(ctx)));
  }
}
