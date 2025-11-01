import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColeccionController  {

  private final RepoSolicitudes repoSolicitudes;
  private final RepoColecciones repoColecciones;
  private final RepoFuenteDinamica repoFuenteDinamica;

  public ColeccionController() {
    this.repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    this.repoColecciones = new RepoColecciones(repoSolicitudes);
    this.repoFuenteDinamica = new RepoFuenteDinamica();
  }

  public Map<String, Object> crear(Context ctx){
    Map<String, Object> model = modeloBase(ctx);

    String titulo = ctx.formParam("titulo");
    String descripcion = ctx.formParam("descripcion");
    String tipoFuente = ctx.formParam("tipoFuente");
    String categoria = ctx.formParam("categoria");
    String provincia = ctx.formParam("provincia");
    String fechaDesde = ctx.formParam("fechaDesde");
    String fechaHasta = ctx.formParam("fechaHasta");

    if (titulo == null || titulo.isBlank() || descripcion == null || descripcion.isBlank()) {
      model.put("type", "error");
      model.put("message", "El título y la descripción son obligatorios.");
      return model;
    }

    if (tipoFuente == null){
      model.put("type", "error");
      model.put("message", "Seleccionar una fuente es boligatorio");
      return model;
    }

    Fuente fuente = null;

    switch (tipoFuente) {
      case "estatica":
        fuente = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
        break;
      case "dinamica":
        //fuente = repoFuenteDinamica.();
        break;
      case "metamapa":
        //fuente = new FuenteMetaMapa();
        break;
      default:
        model.put("type", "error");
        model.put("message", "Tipo de fuente desconocido: " + tipoFuente);
        return model;
    }

    List<Criterio> criterios = new ArrayList<>();

    if (categoria != null && !categoria.isBlank()) {
      criterios.add(new CriterioCategoria((categoria)));
    }
    //if (provincia != null && !provincia.isBlank()) {
    //  criterios.add(new CriterioProvincia(provincia));
    //}
    //if (fechaDesde != null && !fechaDesde.isBlank()) {
    //  criterios.add(new CriterioFecha(fechaDesde, fechaHasta));
    //}

    repoColecciones.crearColeccion(titulo, descripcion, fuente, criterios);

    model.put("type", "success");
    model.put("message", "Colección creada correctamente.");
    return model;

  }

  public Map<String, Object> listar(Context ctx){
    Map<String, Object> model = new HashMap<>();
    List<Coleccion> colecciones = repoColecciones.getColecciones();

    model.put("colecciones", colecciones);
    return model;
  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

}
