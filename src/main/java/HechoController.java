import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

  public Map<String, Object> ubicarHechos(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    try {
      List<Hecho> hechos = repoHechos.obtenerTodosLosHechos();
      if (hechos.isEmpty()) {
        hechos = Collections.emptyList();
      }
      List<Map<String, Object>> hechosUbicados = hechos.stream().map(hecho -> {
        Map<String, Object> data = new HashMap<>();
        data.put("latitud", hecho.getLatitud());
        data.put("longitud", hecho.getLongitud());
        data.put("descripcion", hecho.getTitulo());
        return data;
      }).toList();
      model.put("hechos", hechosUbicados);
    } catch (Exception e) {
      e.printStackTrace();
      model.put("hechos", Collections.emptyList());
    }
    return model;
  }
}
