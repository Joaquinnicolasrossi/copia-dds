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
    String contrasena = ctx.formParam("contraseña");

    var usuario = repoUsuario.findByUser(email);

    if (usuario == null) {
      ctx.sessionAttribute("error", "Usuario o contraseña incorrectos");
      ctx.redirect("/login");
      return;
    }


    if (!usuario.getContrasena().equals(contrasena)) {
      ctx.sessionAttribute("error", "Usuario o contraseña incorrectos");
      ctx.redirect("/login");
      return;
    }

    ctx.sessionAttribute("usuarioActual", usuario);
    ctx.redirect("/");

  }

  public void logout(Context ctx) {

  }


  public void mostrarFormularioRegistro(Context ctx) {
    ctx.render("registro-form.hbs", new HashMap<>());
  }

  public void mostrarFormularioIniciarSesion(Context ctx) {
    ctx.render("inicarSesion-form.hbs", new HashMap<>());
  }
}
