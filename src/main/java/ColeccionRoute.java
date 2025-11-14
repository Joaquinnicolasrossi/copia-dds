import io.javalin.Javalin;
import java.util.Map;

public class ColeccionRoute implements Router {
  private final ColeccionController controller;

  public ColeccionRoute(ColeccionController controller) {
    this.controller = controller;
  }
  public void configure(Javalin app) {

    app.get("/colecciones/nuevo", ctx -> {
      Map<String, Object> model = controller.mostrarFormulario(ctx);
      ctx.render("coleccion-form.hbs", model);
    });

    // Crear
    app.post("/colecciones", ctx -> {
      Map<String, Object> model = controller.crear(ctx);
      if("success".equals(model.get("type"))){
        ctx.redirect("/colecciones");
      } else {
        model.putAll(controller.mostrarFormulario(ctx));
        ctx.render("coleccion-form.hbs", model);
      }
    });

    // Listar
    app.get("/colecciones", ctx ->
      {Map<String, Object> model = controller.listar(ctx);
      ctx.render("coleccion-list.hbs", model);} );
    // Editar
    app.get("/colecciones/{id}/editar", ctx -> {
      Map<String, Object> model = controller.editar(ctx);
      ctx.render("editar-coleccion.hbs", model);
    });
    // Actualizar
    app.post("/colecciones/{id}/actualizar", ctx -> {
      controller.actualizar(ctx);
      ctx.redirect("/colecciones");
    });
    // Ver Hechos
    app.get("/colecciones/{id}/hechos", ctx -> {
      Map<String, Object> model = controller.verHechos(ctx);
    ctx.render("coleccion-hechos.hbs", model);
    });
    //app.get("/collecciones/mapa", ctx -> ctx.render("mapa.hbs", controller.ubicarHechos(ctx)));
  }
}
