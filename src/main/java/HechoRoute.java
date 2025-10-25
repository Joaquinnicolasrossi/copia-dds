import io.javalin.Javalin;

public class HechoRoute {
  private final HechoController controller;
  public HechoRoute(HechoController controller) {
    this.controller = controller;
  }
  public void register(Javalin app) {
    app.post("/hechos", ctx -> ctx.render("alert.hbs", controller.crear(ctx)));
  }
}
