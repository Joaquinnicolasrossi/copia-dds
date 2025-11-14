import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import java.util.List;

public class Server {
  private int getRailwayAssignedPort() {
    String railway = System.getenv("PORT");
    if (railway != null) {
      return Integer.parseInt(railway);
    }
    // Si no encuentra la variable de Railway, usa 7000 (para correr local)
    return 7000;
  }

  public void start() {
    Javalin app = Javalin.create(config -> {
      initializeStaticFiles(config);
      initializeTemplating(config);
    });
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    RepoProvincias repoProvincias = new RepoProvincias();
    RepoHechos repoHechos = new RepoHechos(repoProvincias);
    RepoEstadistica repoEstadistica = new RepoEstadistica();
    RepoColecciones repoColecciones = new RepoColecciones(repoSolicitudes, repoHechos);
    GeneradorEstadistica generadorEstadistica = new GeneradorEstadistica(repoEstadistica, repoColecciones);
    RepoMultimedia repoMultimedia = new RepoMultimedia();
    FuenteDinamica fuenteDinamica = repoHechos.obtenerFuenteDinamica();
    HechoController hechoController = new HechoController(repoHechos, repoMultimedia, repoProvincias, repoSolicitudes, fuenteDinamica);
    RepoUsuario repoUsuario = new RepoUsuario();
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
        new EstadisticaRoute(estadisticaController),
        new MetaMapaRoute()

    );

    for (Router router : routers) {
      router.configure(app);
    }

    app.start(getRailwayAssignedPort());

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