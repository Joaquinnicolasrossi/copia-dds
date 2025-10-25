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

    Hecho hechoRequest = ctx.bodyAsClass(Hecho.class);
    Hecho hecho = new Hecho.HechoBuilder()
        .setTitulo(hechoRequest.getTitulo())
        .setDescripcion(hechoRequest.getDescripcion())
        .setCategoria(hechoRequest.getCategoria())
        .setLatitud(hechoRequest.getLatitud())
        .setLongitud(hechoRequest.getLongitud())
        .setFecha(hechoRequest.getFecha())
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
