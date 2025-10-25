import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HechoController {
  private final RepoHechos repoHechos;

  public HechoController(RepoHechos repoHechos) {
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crear(Context ctx) {
    Map model = new HashMap<>();
    try {
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String categoria = ctx.formParam("categoria");
      LocalDateTime fecha = LocalDateTime.parse(ctx.formParam("fecha"));
      Double latitud = Double.valueOf(ctx.formParam("latitud"));
      Double longitud = Double.valueOf(ctx.formParam("longitud"));

      Hecho hecho = new Hecho.HechoBuilder()
          .setTitulo(titulo)
          .setDescripcion(descripcion)
          .setCategoria(categoria)
          .setLatitud(latitud)
          .setLongitud(longitud)
          .setFecha(fecha)
          .setFechaCarga(LocalDateTime.now())
          .setEstado(Estado.PENDIENTE)
          .build();

      repoHechos.guardarHecho(hecho);
      model.put("type", "success");
      model.put("message", "Hecho creado correctamente.");
      return model;

    } catch (Exception e) {
      e.printStackTrace();
      model.put("type", "danger");
      model.put("message", "Error interno: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      return model;
    }


  }
}
