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

    for (Coleccion coleccion: colecciones){
      Long coleccionId = coleccion.getId();

      EstadisticaRegistro estadisticaProvinciaMayor = repoEstadistica.provinciaConMasHechos(coleccionId);
      EstadisticaRegistro categoriaMayorHechos = repoEstadistica.categoriaConMayorHechos(coleccionId);
      EstadisticaRegistro cantidadSpam = repoEstadistica.cantidadSolicitudesSpam(coleccionId);

      List<String> categorias = repoEstadistica.categoriasPorColeccion(coleccionId);

      Map<String, EstadisticaRegistro> horasPorCategoria = new HashMap<>();
      Map<String, EstadisticaRegistro> provinciasPorCategoria = new HashMap<>();

      for (String categoria : categorias){
        EstadisticaRegistro provincia = repoEstadistica.provinciaConMasHechosPorCategoria(coleccionId, categoria);
        if  (provincia != null){
          provinciasPorCategoria.put(categoria, provincia);
        }

        EstadisticaRegistro hora = repoEstadistica.horaConMasHechosPorCategoria(coleccionId, categoria);
        if (hora != null) {
          horasPorCategoria.put(categoria, hora);
        }
      }

      coleccion.setEstadisticaProvinciaMayor(estadisticaProvinciaMayor);
      coleccion.setEstadisticaCategoriaMayor(categoriaMayorHechos);
      coleccion.setEstadisticaCantidadSpam(cantidadSpam);
      coleccion.setEstadisticaProvinciaMasHechosPorCategoria(provinciasPorCategoria);
      coleccion.setEstadisticaHoraMasHechosPorCategoria(horasPorCategoria);

    }

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
