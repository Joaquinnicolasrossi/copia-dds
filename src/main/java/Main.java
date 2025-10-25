import io.javalin.Javalin;

public class Main {
  public static void main() {
    RepoHechos repoHechos = new RepoHechos();
    HechoController hechoController = new HechoController(repoHechos);
    HechoRoute hechoRoute = new HechoRoute(hechoController);
    Javalin app = Javalin.create();
    app.start(7000);
    hechoRoute.register(app);
  }
}
