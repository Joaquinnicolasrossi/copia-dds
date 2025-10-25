import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HechoController {
  private final RepoHechos repoHechos;
  public HechoController(RepoHechos repoHechos) {
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crear(Context ctx) {

      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String categoria = ctx.formParam("categoria");
      Double latitud = Double.valueOf(ctx.formParam("latitud"));
      Double longitud = Double.valueOf(ctx.formParam("longitud"));

      Hecho hecho = new Hecho.HechoBuilder()
          .setTitulo(titulo)
          .setDescripcion(descripcion)
          .setCategoria(categoria)
          .setLatitud(latitud)
          .setLongitud(longitud)
          .setFechaCarga(LocalDateTime.now())
          .setEstado(Estado.PENDIENTE)
          .build();

      repoHechos.guardarHecho(hecho);

      Map model = new HashMap<>();
      model.put("type", "success");
      model.put("message", "Hecho creado correctamente.");
      return model;
    }
}
