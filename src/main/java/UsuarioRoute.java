import io.javalin.Javalin;


public class UsuarioRoute implements Router {

  private final UsuarioController usuarioController;

  public UsuarioRoute(UsuarioController usuarioController) {
    this.usuarioController = usuarioController;
  }

  public void configure(Javalin app) {
    app.post("/usuario/login",usuarioController::login);
    app.post("/usuario/singUp", usuarioController::singUp);
    app.get("/usuario/formRegistro", usuarioController::mostrarFormularioRegistro);
    app.get("/usuario/formIniciarSesion",usuarioController::mostrarFormularioIniciarSesion);
    app.get("/usuario/logout",usuarioController::logout);
  };



  }

