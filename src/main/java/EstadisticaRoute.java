import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;

public class EstadisticaRoute implements Router {
  private final EstadisticaController controller;

  public EstadisticaRoute(EstadisticaController controller) { this.controller = controller; }

  public void configure(Javalin app) {
    app.get("/estadisticas", ctx -> {
      Map<String, Object> model = controller.listar(ctx);
      ctx.render("estadisticas.hbs", model);
    });

    app.post("/estadisticas/generar", ctx -> {
      controller.generarTodas(ctx);
    });
  }

}
