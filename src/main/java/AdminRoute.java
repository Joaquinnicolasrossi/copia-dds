import io.javalin.Javalin;
import java.util.Map;

public class AdminRoute implements Router {
  public void configure(Javalin app) {
    app.get("/home-administrador", ctx -> {ctx.render("home-admin.hbs", Map.of());});
  }
}
