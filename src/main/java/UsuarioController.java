import io.javalin.http.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class UsuarioController {

  private RepoUsuario repoUsuario;
  private RepoHechos repoHechos;

  public UsuarioController(RepoHechos repoHechos, RepoUsuario repoUsuario) {
    this.repoHechos = repoHechos;
    this.repoUsuario = repoUsuario;
  }

  public void singUp(Context ctx) {

    String contrasena = ctx.formParam("contraseña");
    String email = ctx.formParam("email");
    String nombre = ctx.formParam("nombre");
    Usuario usuario = new Usuario(contrasena, email, nombre);
    repoUsuario.save(usuario);
    usuario.setTipoUsuario(TipoUsuario.CONTRIBUYENTE);
    ctx.sessionAttribute("usuarioActual", usuario);
    ctx.sessionAttribute("nombre", usuario.getNombre());
    ctx.sessionAttribute("id", usuario.getId());
    ctx.sessionAttribute("tipoUsuario", usuario.getTipoUsuario());

    ctx.redirect("/");
  }

  public void login(Context ctx) {
    String email = ctx.formParam("email");
    String contrasena = ctx.formParam("contrasena");


        var usuario = repoUsuario.findByUser(email);

        if (usuario == null) {
            ctx.sessionAttribute("error", "Usuario no encontrado");
            ctx.redirect("/usuario/formIniciarSesion");
            return;
        }


        if (!usuario.getContrasena().equals(contrasena)) {
            ctx.sessionAttribute("error", "Contraseña incorrecta");
            ctx.redirect("/usuario/formIniciarSesion");
            return;
        }

        ctx.sessionAttribute("usuarioActual", usuario);
        if (usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioActual", usuario);
            ctx.render("home-admin.hbs", model);
        } else {
            Map<String, Object> model = new HashMap<>();
            model.put("userId", usuario.getId());
            model.put("nombre", usuario.getNombre());
            model.put("usuarioActual", usuario);
            ctx.render("home.hbs", model);
        }
    }

    public void logout(Context ctx) {
        ctx.sessionAttribute("usuarioActual", null);
        ctx.redirect("/");
    }


    public void mostrarFormularioRegistro(Context ctx) {
        ctx.render("registro-form.hbs", new HashMap<>());
    }

    public void mostrarFormularioIniciarSesion(Context ctx) {
        String error = ctx.sessionAttribute("error");
        ctx.sessionAttribute("error", null);
        Map<String, Object> model = new HashMap<>();
        model.put("error", error);
        ctx.render("iniciar-sesion-form.hbs", model);
    }

    public List<Hecho> obtenerHechosDeUsuarioPaginados(Long usuarioId, int pagina, int tamanoPagina) {

        List<Hecho> todos = repoHechos.obtenerHechosPorUsuario(usuarioId);

        int inicio = pagina * tamanoPagina;
        int fin = Math.min(inicio + tamanoPagina, todos.size());

        if (inicio >= todos.size()) {
            return Collections.emptyList();
        }

        return todos.subList(inicio, fin);
    }

    public int getTotalHechosDeUsuario(Long usuarioId) {
        return repoHechos.obtenerHechosPorUsuario(usuarioId).size();
    }

    public int getTotalPaginasHechosUsuario(Long usuarioId, int tamanoPagina) {
        int total = getTotalHechosDeUsuario(usuarioId);
        return (int) Math.ceil((double) total / tamanoPagina);
    }

    public void listarHechosDeUsuario(Context context) {
        Long usuarioId = Long.parseLong(context.pathParam("id"));

        int pagina = context.queryParamAsClass("pagina", Integer.class).getOrDefault(0);
        int tamanoPagina = 4;

        List<Hecho> hechosPaginados = obtenerHechosDeUsuarioPaginados(usuarioId, pagina, tamanoPagina);
        int totalPaginas = getTotalPaginasHechosUsuario(usuarioId, tamanoPagina);

        Map<String, Object> model = new HashMap<>();
        model.put("hechos", hechosPaginados);

        model.put("tienePaginaAnterior", pagina > 0);
        model.put("tienePaginaSiguiente", pagina < totalPaginas - 1);

        model.put("paginaAnterior", pagina - 1);
        model.put("paginaSiguiente", pagina + 1);

        model.put("numeroPaginaActual", pagina + 1); // 1-based
        model.put("totalPaginas", totalPaginas);

        model.put("usuarioActual", context.sessionAttribute("usuarioActual"));
        model.put("usuarioId", usuarioId);

        context.render("hecho-detalle.hbs", model);
    }

}
