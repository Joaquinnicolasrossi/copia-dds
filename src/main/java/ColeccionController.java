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

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

  // ---------------------- CREAR ----------------------
  public Map<String, Object> crear(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);


    String titulo = ctx.formParam("titulo");
    String descripcion = ctx.formParam("descripcion");
    String tipoFuente = ctx.formParam("tipoFuente");
    String algoritmoStr = ctx.formParam("algoritmoConsenso");

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
        new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");
      case "dinamica" ->
        fuenteDinamica;
      case "metamapa" ->
        null;
      case "agregada" -> new FuenteAgregada(
          List.of(
              guardarFuenteSiNoTieneID(new FuenteEstaticaIncendios("src/test/resources/fires-all.csv")),
              guardarFuenteSiNoTieneID(new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv")),
              fuenteDinamica), repoHechos);
      default -> null;
    };

    if (fuente == null) {
      model.put("type", "error");
      model.put("message", "La fuente es obligatoria.");
      return model;
    }

    fuente = guardarFuenteSiNoTieneID(fuente);

    Consenso algoritmoConsenso = null;
    if ("ninguno".equals(algoritmoStr) || algoritmoStr == null || algoritmoStr.isBlank()){
      algoritmoConsenso = null;
    } else if (!(fuente instanceof FuenteAgregada)) {
      model.put("type", "error");
      model.put("message", "Solo las fuentes agregadas pueden tener un algoritmo de consenso.");
      return model;
    } else {
      algoritmoConsenso = switch (algoritmoStr) {
        case "multiplesm" -> new MultiplesMenciones();
        case "mayoria" -> new MayoriaSimple();
        case "absoluta" -> new Absoluto();
        default -> null;
      };
    }

    List<Criterio> criterios = construirCriterios(ctx);

    repoColecciones.crearColeccion
        (titulo, descripcion, fuente, criterios, algoritmoConsenso, repoHechos);

    model.put("type", "success");
    model.put("message", "Colección creada correctamente.");
    return model;

  }

  // ---------------------- CRITERIOS ----------------------
  private List<Criterio> construirCriterios(Context ctx) {
    List<Criterio> criterios = new ArrayList<>();

    String categoria = ctx.formParam("categoria");
    if (categoria != null && !categoria.isBlank()) {
      criterios.add(new CriterioCategoria(categoria));
    }

    String fechaDesde = ctx.formParam("fechaDesde");
    String fechaHasta = ctx.formParam("fechaHasta");
    LocalDateTime desde = null;
    LocalDateTime hasta = null;
    // validaciones
    if (fechaDesde != null && !fechaDesde.isBlank()) {
      desde = LocalDate.parse(fechaDesde).atStartOfDay();
    }
    if (fechaHasta != null && !fechaHasta.isBlank()) {
      hasta = LocalDate.parse(fechaHasta).atTime(23, 59, 59);
    }
    if (desde != null || hasta != null) {
      criterios.add(new CriterioFecha(desde, hasta));
    }

    return criterios;
  }

  public Map<String, Object> mostrarFormulario(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);

    // Lista de algoritmos de consenso disponibles
    model.put("algoritmosConsenso", List.of(
        Map.of("id", "ninguno", "nombre", "fSin consenso (todos los hechos son válidos)"),
        Map.of("id", "multiplesm", "nombre", "Múltiples menciones (al menos 2 fuentes)"),
        Map.of("id", "mayoria", "nombre", "Mayoría simple (más de la mitad)"),
        Map.of("id", "absoluta", "nombre", "Absoluta (todas las fuentes coinciden)")
    ));

    return model;
  }

  // ---------------------- LISTAR ----------------------
  public Map<String, Object> listar(Context ctx){
    Map<String, Object> model = new HashMap<>();
    List<Coleccion> colecciones = repoColecciones.getColecciones();

    model.put("colecciones", colecciones);
    return model;
  }

  // ---------------------- EDITAR ----------------------
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

    // Lista de algoritmos de consenso disponibles
    List<Map<String, String>> algoritmos = new ArrayList<>();
      algoritmos.add(algoritmo("ninguno", "Sin consenso"));
      algoritmos.add(algoritmo("multiplesm", "Múltiples menciones"));
      algoritmos.add(algoritmo("mayoria", "Mayoría simple"));
      algoritmos.add(algoritmo("absoluto", "Absoluto"));
    for (Map<String, String> alg : algoritmos) {
      String selected = alg.get("id").equals(coleccion.getAlgoritmoConsensoId()) ? "selected" : "";
      alg.put("selected", selected);
    }

    model.put("algoritmosConsenso", algoritmos);

    // Tipos de fuente
    List<Map<String, String>> fuentes = new ArrayList<>();
    fuentes.add(new HashMap<>(Map.of("id", "estatica-incendios", "nombre", "Estática (Incendios)")));
    fuentes.add(new HashMap<>(Map.of("id", "dinamica", "nombre", "Dinámica")));
    fuentes.add(new HashMap<>(Map.of("id", "estatica-victimas", "nombre", "Estática (Victimas)")));
    fuentes.add(new HashMap<>(Map.of("id", "metamapa", "nombre", "MetaMapa")));
    fuentes.add(new HashMap<>(Map.of("id", "agregada", "nombre", "Agregada")));

    for (Map<String, String> f : fuentes) {
      String checked = f.get("id").equals(coleccion.getTipoFuenteId()) ? "checked" : "";
      f.put("checked", checked);
    }

    model.put("fuentes", fuentes);

    model.put("type", "success");
    model.put("message", "Colección cargada correctamente.");

    return model;
  }

  // ---------------------- ACTUALIZAR ----------------------
  public void actualizar(Context ctx){
    Long id = Long.parseLong(ctx.pathParam("id"));
    String tipoFuente = ctx.formParam("tipoFuente");

    String nuevoTitulo = ctx.formParam("titulo");
    String nuevaDescripcion = ctx.formParam("descripcion");

    Fuente nuevaFuente = switch (tipoFuente) {
        case "dinamica" -> fuenteDinamica;
        case "estatica-incendios" -> new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");
        case "estatica-victimas" -> new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv");
        case "metamapa" -> null;//fuenteMetamapa;
        case "agregada" -> new FuenteAgregada(
             List.of(
                 guardarFuenteSiNoTieneID(new FuenteEstaticaIncendios("src/test/resources/fires-all.csv")),
                 guardarFuenteSiNoTieneID(new FuenteEstaticaVictimas("src/test/resources/victimas_viales_argentina.csv")),
                 fuenteDinamica), repoHechos);
        default -> null;
      };

    Consenso nuevoAlgoritmo = switch (ctx.formParam("algoritmoConsenso")){
      case "multiplesm" -> new MultiplesMenciones();
      case "mayoria" -> new MayoriaSimple();
      case "absoluta" -> new Absoluto();
      default -> null;
    };

    nuevaFuente = guardarFuenteSiNoTieneID(nuevaFuente);

    List<Criterio> nuevosCriterios =  construirCriterios(ctx);

    repoColecciones.actualizarColeccion(id, nuevoTitulo, nuevaDescripcion, nuevaFuente, nuevosCriterios, nuevoAlgoritmo);
    ctx.redirect("/colecciones");
  }

  public Map<String, Object> verHechos(Context ctx){
    Map<String, Object> model = modeloBase(ctx);

    Long coleccionId = Long.valueOf(ctx.pathParam("id"));
    int pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(0);
    int tamanoPagina = 20;

    Coleccion coleccion = repoColecciones.buscarPorId(coleccionId);

    if (coleccion == null) {
      model.put("error", "Colección no encontrada");
      return model;
    }
    List<Hecho> hechos = repoColecciones.obtenerHechosPaginados(coleccionId, pagina, tamanoPagina);
    int totalPaginas = repoColecciones.getTotalPaginas(coleccionId, tamanoPagina);
    int totalHechos = repoColecciones.getTotalHechos(coleccionId);

    model.put("coleccion", coleccion);
    model.put("hechos", hechos);
    model.put("paginaActual", pagina);
    model.put("totalPaginas", totalPaginas);
    model.put("totalHechos", totalHechos);
    model.put("paginaAnterior", pagina - 1);
    model.put("paginaSiguiente", pagina + 1);
    model.put("tienePaginaAnterior", pagina > 0);
    model.put("tienePaginaSiguiente", pagina < totalPaginas - 1);
    model.put("numeroPaginaActual", pagina + 1); // Muestra pàgina 1 (no existe la 0)

    return model;
    }

  private Map<String, String> algoritmo(String id, String nombre) {
    return new HashMap<>(Map.of("id", id, "nombre", nombre));
  }

  private Fuente guardarFuenteSiNoTieneID(Fuente fuente) {
    if (fuente.getId() == null) {
      fuente = repoColecciones.guardarFuente(fuente);
    }
    return fuente;
  }
}
