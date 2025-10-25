import io.javalin.Javalin;

public class HechoRoute implements Router {
  private final HechoController controller;

  public HechoRoute(HechoController controller) {
    this.controller = controller;
  }
  public void configure(Javalin app) {
    app.get("/hechos/nuevo", ctx->ctx.render("hecho-form.hbs"));
    app.post("/hechos", ctx -> ctx.render("alert.hbs", controller.crear(ctx)));
  }
}
