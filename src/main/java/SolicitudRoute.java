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
    app.post("/solicitud/{id}/aceptar", ctx -> {
      controller.aceptarSolicitud(ctx);
      ctx.render("solicitudes.hbs", controller.listar(ctx));
    });
    app.post("/solicitud/{id}/eliminar", ctx -> {
      controller.eliminarSolicitud(ctx);
      ctx.render("solicitudes.hbs", controller.listar(ctx));
    });
    app.get("solicitud/revision",controller::crearSolicitudRevision);
    app.post("solicitud/estado",controller::estadoSolicitud);
  }
}
