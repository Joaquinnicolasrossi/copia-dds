import io.javalin.Javalin;
import java.util.Map;

public class HomeRoute implements Router {
  public void configure(Javalin app) {
    app.get("/", ctx -> {ctx.render("home.hbs", Map.of());});
  }
}
