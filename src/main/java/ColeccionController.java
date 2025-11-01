import io.javalin.http.Context;
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
    String criterioCategoria = ctx.formParam("criterioCategoria");
    String fuenteId = ctx.formParam("fuenteId");
    String tipoFuente = ctx.formParam("tipoFuente");
    String criterioFuente = ctx.formParam("criterioFuente");

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

    switch (tipoFuente) {
      case "ESTATICA":
        //fuente = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
        break;
      case "DINAMICA":
        //fuente = repoFuenteDinamica.crearFuenteDinamica();
        break;
      case "PROXY":
        //fuente = new FuenteProxy();
        break;
      default:
        model.put("type", "error");
        model.put("message", "Tipo de fuente desconocido: " + tipoFuente);
        return model;
    }

    FuenteEstaticaIncendios fuenteEstaticaIncendios = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");

    List<Criterio> criterios = new java.util.ArrayList<>();

    if (criterioCategoria != null && !criterioCategoria.isBlank()) {

      CriterioCategoria cat = new CriterioCategoria(criterioCategoria);
      criterios.add(cat);
    }

    repoColecciones.crearColeccion(titulo, descripcion, fuenteEstaticaIncendios, criterios);

    model.put("type", "success");
    model.put("message", "Colección creada correctamente.");
    return model;

  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

  public Map<String, Object> listar(Context ctx){
    Map<String, Object> model = new HashMap<>();
    List<Coleccion> colecciones = repoColecciones.getColecciones();

    model.put("colecciones", colecciones);
    return model;
  }

}
