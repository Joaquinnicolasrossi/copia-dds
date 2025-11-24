import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;

public class AdminRoute implements Router {

    public void configure(Javalin app) {
      app.get("/home-administrador", ctx -> {

        var usuario = ctx.sessionAttribute("usuarioActual");

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioActual", usuario);

        ctx.render("home-admin.hbs", model);
      });
    }
}
