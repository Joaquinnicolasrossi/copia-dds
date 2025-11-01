import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;

public class HechoRoute implements Router {
  private final HechoController controller;

  public HechoRoute(HechoController controller) {
    this.controller = controller;
  }
  public void configure(Javalin app) {
    app.get("/hechos/nuevo", ctx->ctx.render("hecho-form.hbs"));
    app.post("/hechos", ctx -> {
      Map<String, Object> model = controller.crear(ctx);
      ctx.render("alert.hbs", model);
    });
    app.get("/hechos/mapa", ctx -> ctx.render("mapa.hbs", controller.ubicarHechos(ctx)));
    app.get("/hechos/{id}/solicitar-eliminacion", ctx-> {
      Map<String, Object> model = new HashMap<>();
      model.put("hecho-id", ctx.pathParam("id"));
      ctx.render("solicitud-eliminacion-form.hbs", model);
    });
  }
}
