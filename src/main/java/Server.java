import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import java.util.List;

public class Server {
  public void start() {
    Javalin app = Javalin.create(config -> {
      initializeStaticFiles(config);
      initializeTemplating(config);
    });

    RepoHechos repoHechos = new RepoHechos();
    RepoMultimedia repoMultimedia = new RepoMultimedia();
    HechoController hechoController = new HechoController(repoHechos, repoMultimedia);
    RepoUsuario repoUsuario = new RepoUsuario();
    UsuarioController usuarioController = new UsuarioController(repoUsuario);
    ColeccionController coleccionController = new ColeccionController();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    SolicitudController solicitudController = new SolicitudController(repoSolicitudes, repoHechos);

    app.before(ctx -> {
      Usuario usuario = ctx.sessionAttribute("usuarioActual");
      ctx.attribute("usuarioActual", usuario);
    });

    List<Router> routers = List.of(
        new AdminRoute(),
        new HechoRoute(hechoController),
        new UsuarioRoute(usuarioController),
        new ColeccionRoute(coleccionController),
        new SolicitudRoute(solicitudController),
        new HomeRoute()

    );

    for (Router router : routers) {
      router.configure(app);
    }



    app.start(7000);

  }

  private void initializeTemplating(JavalinConfig config) {
    config.fileRenderer(
        new JavalinRenderer().register("hbs", new JavalinHandlebars())
    );
  }

  private static void initializeStaticFiles(JavalinConfig config) {
    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/assets";
      staticFileConfig.directory = "/assets";
    });
  }

}
