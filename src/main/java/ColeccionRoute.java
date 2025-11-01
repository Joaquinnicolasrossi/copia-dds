import io.javalin.Javalin;
import java.util.Map;

public class ColeccionRoute implements Router {
  private final ColeccionController controller;

  public ColeccionRoute(ColeccionController controller) {
    this.controller = controller;
  }
  public void configure(Javalin app) {
    app.get("/colecciones/nuevo", ctx->ctx.render("coleccion-form.hbs"));

    // Crear
    app.post("/colecciones", ctx -> {
      Map<String, Object> model = controller.crear(ctx);
      if("success".equals(model.get("type"))){
        ctx.redirect("/home-administrador");
      }
      ctx.render("alert.hbs", model);
    });

    // Listar
    app.get("/colecciones", ctx ->
      {Map<String, Object> model = controller.listar(ctx);
      ctx.render("coleccion-list.hbs", model);} );

    //app.get("/collecciones/mapa", ctx -> ctx.render("mapa.hbs", controller.ubicarHechos(ctx)));
  }
}
