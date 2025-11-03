import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColeccionController  {

  private final RepoSolicitudes repoSolicitudes;
  private final RepoColecciones repoColecciones;
  private final FuenteDinamica fuenteDinamica;
  private final RepoHechos repoHechos;

  public ColeccionController(RepoSolicitudes repoSolicitudes,
                             RepoColecciones repoColecciones,
                             FuenteDinamica fuenteDinamica,
                             RepoHechos repoHechos) {
    this.repoSolicitudes = repoSolicitudes;
    this.repoColecciones = repoColecciones;
    this.fuenteDinamica = fuenteDinamica;
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crear(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);


    String titulo = ctx.formParam("titulo");
    String descripcion = ctx.formParam("descripcion");
    String tipoFuente = ctx.formParam("tipoFuente");

    if (titulo == null || titulo.isBlank()) {
      model.put("type", "error");
      model.put("message", "El título es obligatorio.");
      return model;
    }

    if (descripcion == null || descripcion.isBlank()) {
      model.put("type", "error");
      model.put("message", "La descripción es obligatoria.");
      return model;
    }

    if (tipoFuente == null) {
      model.put("type", "error");
      model.put("message", "Seleccionar una fuente es obligatorio");
      return model;
    }

    Fuente fuente = switch (tipoFuente) {
      case "estatica-incendios" ->
          new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
      case "estatica-victimas" ->
        new FuenteEstaticaIncendios("src/test/resources/victimas_viales_argentina.csv");
      case "dinamica" ->
        fuenteDinamica;
      case "metamapa" ->
        null;
      default -> null;
    };

    if (fuente == null) {
      model.put("type", "error");
      model.put("message", "La fuente es obligatoria.");
      return model;
    }

    List<Criterio> criterios = construirCriterios(ctx);

    Consenso algoritmoConsenso = obtenerAlgoritmoConsenso(ctx);

    repoColecciones.crearColeccion
        (titulo, descripcion, fuente, criterios, algoritmoConsenso, repoHechos);

    model.put("type", "success");
    model.put("message", "Colección creada correctamente.");
    return model;

  }

  private List<Criterio> construirCriterios(Context ctx) {
    List<Criterio> criterios = new ArrayList<>();

    String categoria = ctx.formParam("categoria");
    if (categoria != null && !categoria.isBlank()) {
      criterios.add(new CriterioCategoria());
    }

    String fechaDesde = ctx.formParam("fechaDesde");
    String fechaHasta = ctx.formParam("fechaHasta");
    if (fechaDesde != null && !fechaDesde.isBlank() &&
        fechaHasta != null && !fechaHasta.isBlank()) {
      LocalDateTime desde = LocalDate.parse(fechaDesde).atStartOfDay();
      LocalDateTime hasta = LocalDate.parse(fechaHasta).atTime(23, 59, 59);
      criterios.add(new CriterioFecha(desde, hasta));
    }

    return criterios;
  }

  private Consenso obtenerAlgoritmoConsenso(Context ctx) {
    String algoritmoStr = ctx.formParam("algoritmoConsenso");
    if (algoritmoStr == null || algoritmoStr.equals("ninguno")) {
      return null;
    }
    return switch (algoritmoStr) {
      case "multiplesm" -> new MultiplesMenciones();
      case "mayoria" -> new MayoriaSimple();
      case "absoluta" -> new Absoluto();
      default -> null;
    };
  }

  public Map<String, Object> mostrarFormulario(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);

    // Lista de algoritmos de consenso disponibles
    model.put("algoritmosConsenso", List.of(
        Map.of("id", "ninguno", "nombre", "Sin consenso (todos los hechos son válidos)"),
        Map.of("id", "multiplesm", "nombre", "Múltiples menciones (al menos 2 fuentes)"),
        Map.of("id", "mayoria", "nombre", "Mayoría simple (más de la mitad)"),
        Map.of("id", "absoluta", "nombre", "Absoluta (todas las fuentes coinciden)")
    ));

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

  public Map<String, Object> editar(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);

    String idParam = ctx.pathParam("id");
    if (idParam == null || idParam.isBlank()) {
      model.put("type", "error");
      model.put("message", "ID de colección no válido.");
      return model;
    }

    Long id = Long.parseLong(idParam);
    Coleccion coleccion = repoColecciones.buscarPorId(id);

    if (coleccion == null) {
      model.put("type", "error");
      model.put("message", "No se encontró la colección con ID " + id);
      return model;
    }

    model.put("coleccion", coleccion);
    return model;
  }

  public void actualizar(Context ctx){
    Long id = Long.parseLong(ctx.pathParam("id"));

    String nuevoTitulo = ctx.formParam("titulo");
    String nuevaDescripcion = ctx.formParam("descripcion");


    String tipoFuente =  ctx.formParam("tipoFuente");
    Fuente nuevaFuente = switch (tipoFuente) {
      case "dinamica" -> fuenteDinamica;
      case "estatica-incendios" -> new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
      case "estatica-victimas" -> new FuenteEstaticaIncendios("src/test/resources/victimas_viales_argentina.csv");
      case "metamapa" -> null;//fuenteMetamapa;
      default -> null;
    };

    Consenso nuevoAlgoritmo = switch (ctx.formParam("algoritmoConsenso")){
      case "multiplesm" -> new MultiplesMenciones();
      case "mayoria" -> new MayoriaSimple();
      case "absoluta" -> new Absoluto();
      default -> null;
    };

    List<Criterio> nuevosCriterios =  construirCriterios(ctx);

    repoColecciones.actualizarColeccion(id, nuevoTitulo, nuevaDescripcion, nuevaFuente, nuevosCriterios, nuevoAlgoritmo);
    ctx.redirect("/colecciones");
  }
}
