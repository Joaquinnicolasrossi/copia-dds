import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class EstadisticaController {
  private final RepoEstadistica repoEstadistica;
  private final GeneradorEstadistica generadorEstadistica;
  private final RepoColecciones repoColecciones;

  public EstadisticaController(RepoEstadistica repoEstadistica, GeneradorEstadistica generadorEstadistica,
                               RepoColecciones repoColecciones) {
    this.repoEstadistica = repoEstadistica;
    this.generadorEstadistica = generadorEstadistica;
    this.repoColecciones = repoColecciones;
  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

  public Map<String, Object> listar(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);

    // Obtenemos todas las colecciones y ejecutamos todas las estadisticas
    List<Coleccion> colecciones = repoColecciones.getColecciones();

    colecciones.forEach(coleccion -> {
      generadorEstadistica.generarTodas(coleccion.getId());
    });

    Map<String, List<EstadisticaRegistro>> estadisticasPorTipo = new HashMap<>();

    estadisticasPorTipo.put("provinciaMayorHechos",
        repoEstadistica.buscarPorTipo("PROVINCIA_MAYOR_HECHOS"));
    estadisticasPorTipo.put("categoriaMayorHechos",
        repoEstadistica.buscarPorTipo("CATEGORIA_MAYOR_HECHOS"));
    estadisticasPorTipo.put("provinciaConMasHechosPorCategoria",
        repoEstadistica.buscarPorTipo("PROVINCIA_MAYOR_HECHOS_CATEGORIA"));
    estadisticasPorTipo.put("horaConMasHechosPorCategoria",
        repoEstadistica.buscarPorTipo("HORA_MAS_HECHOS_POR_CATEGORIA"));
    estadisticasPorTipo.put("cantidadSpam",
        repoEstadistica.buscarPorTipo("CANTIDAD_SOLICITUDES_SPAM"));

    model.put("estadistica", estadisticasPorTipo);
    model.put("colecciones", colecciones);
    return model;
  }

  public void generarTodas(Context ctx){
    List<Coleccion> colecciones =  repoColecciones.getColecciones();
    for (Coleccion coleccion : colecciones) {
      generadorEstadistica.generarTodas(coleccion.getId());
    }
    ctx.redirect("/estadisticas");
  }

}
