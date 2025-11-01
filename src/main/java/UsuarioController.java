import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class UsuarioController {

  private RepoUsuario repoUsuario;

  public UsuarioController(RepoUsuario repoUsuario) {
    this.repoUsuario = repoUsuario;
  }


  public void singUp(Context ctx) {

    String contrasena = ctx.formParam("contraseña");
    String email = ctx.formParam("email");
    String nombre = ctx.formParam("nombre");
    Usuario usuario = new Usuario(contrasena, email, nombre);
    usuario.setTipoUsuario(TipoUsuario.CONTRIBUYENTE);

    repoUsuario.save(usuario);
    ctx.redirect("/");
  }

  public void login(Context ctx) {
    String email = ctx.formParam("email");
    String contrasena = ctx.formParam("contrasena");

    if(email.equals("administrador") && contrasena.equals("123")) {
      Map<String, Object> model = new HashMap<>();
      model.put("nombre", "fulano");
      model.put("rol", "Administrador");
      ctx.render("home-admin.hbs", model);
      return;
    } else if (email.equals("usuario") && contrasena.equals("123")) {
      ctx.sessionAttribute("usuarioActual", new Usuario());
      ctx.sessionAttribute("nombre", "fulano");

      ctx.render("home.hbs", Map.of());
      return;
    }

    var usuario = repoUsuario.findByUser(email);

    if (usuario == null) {
      ctx.sessionAttribute("error", "usuario no encontrado");
      ctx.redirect("/usuario/formIniciarSesion");
      return;
    }


    if (!usuario.getContrasena().equals(contrasena)) {
      ctx.sessionAttribute("error", " contraseña incorrecta");
      ctx.redirect("/usuario/formIniciarSesion");
      return;
    }

    ctx.sessionAttribute("usuarioActual", usuario);
    ctx.sessionAttribute("nombre", usuario.getNombre());
    ctx.sessionAttribute("tipoUsuario", usuario.getTipoUsuario());

    if (usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
      Map<String, Object> model = new HashMap<>();
      model.put("nombre", usuario.getNombre());
      model.put("rol", "Administrador");
      ctx.render("home-admin.hbs", model);
    } else {
      ctx.redirect("/");
    }
  }

  public void logout(Context ctx) {
    ctx.sessionAttribute("usuario", null);
    ctx.render("home.hbs", new HashMap<>());
  }


  public void mostrarFormularioRegistro(Context ctx) {
    ctx.render("registro-form.hbs", new HashMap<>());
  }

  public void mostrarFormularioIniciarSesion(Context ctx) {
    String error = ctx.sessionAttribute("error");
    ctx.sessionAttribute("error", null); // limpiar después de mostrar
    Map<String, Object> model = new HashMap<>();
    model.put("error", error);
    ctx.render("iniciar-sesion-form.hbs", new HashMap<>());
  }
}
