import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import java.util.List;

public class Server {
  private int getRenderAssignedPort() {
    String renderPort = System.getenv("PORT");
    if (renderPort != null) {
      return Integer.parseInt(renderPort);
    }
    // Si no encuentra la variable de Render, usa 7000 (para correr local)
    return 7000;
  }

  public void start() {
    Javalin app = Javalin.create(config -> {
      initializeStaticFiles(config);
      initializeTemplating(config);
    });
    RepoProvincias repoProvincias = new RepoProvincias();
    RepoHechos repoHechos = new RepoHechos(repoProvincias);
    RepoEstadistica repoEstadistica = new RepoEstadistica();
    GeneradorEstadistica generadorEstadistica = new GeneradorEstadistica(repoEstadistica);
    RepoMultimedia repoMultimedia = new RepoMultimedia();
    HechoController hechoController = new HechoController(repoHechos, repoMultimedia, repoProvincias);
    RepoUsuario repoUsuario = new RepoUsuario();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    RepoColecciones repoColecciones = new RepoColecciones(repoSolicitudes);
    FuenteDinamica fuenteDinamica = new FuenteDinamica();
    UsuarioController usuarioController = new UsuarioController(repoHechos,repoUsuario);
    EstadisticaController estadisticaController = new EstadisticaController(repoEstadistica, generadorEstadistica, repoColecciones);

    ColeccionController coleccionController = new ColeccionController(repoSolicitudes, repoColecciones, fuenteDinamica, repoHechos);
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
        new HomeRoute(),
        new EstadisticaRoute(estadisticaController)

    );

    for (Router router : routers) {
      router.configure(app);
    }

    app.start(getRenderAssignedPort());
  }

  private void initializeTemplating(JavalinConfig config) {
    config.fileRenderer(
        new JavalinRenderer().register("hbs", new JavalinHandlebars())
    );
  }

  private static void initializeStaticFiles(JavalinConfig config) {
    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/assets";
      staticFileConfig.directory = "assets";
    });
  }
}
