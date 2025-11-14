import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HechoController {
  private final RepoHechos repoHechos;
  private final RepoMultimedia repoMultimedia;
  private final RepoProvincias repoProvincias;
  private final RepoSolicitudes repoSolicitudes;
  private final FuenteDinamica fuenteDinamica;

  public HechoController(RepoHechos repoHechos, RepoMultimedia repoMultimedia, RepoProvincias repoProvincias,
                         RepoSolicitudes reporSolicitudes, FuenteDinamica fuenteDinamica) {
    this.repoHechos = repoHechos;
    this.repoMultimedia = repoMultimedia;
    this.repoProvincias = repoProvincias;
    this.repoSolicitudes = reporSolicitudes;
    this.fuenteDinamica = fuenteDinamica;
  }

  public Map<String, Object> crear(Context ctx) {
    Map model = modeloBase(ctx);
    Usuario usuarioActual = ctx.sessionAttribute("usuarioActual");
    FuenteDinamica fuenteDinamicaAsociada = repoHechos.obtenerFuenteDinamica();
    try {
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String categoria = ctx.formParam("categoria");
      LocalDateTime fecha = LocalDateTime.parse(ctx.formParam("fecha"));
      Double latitud = Double.valueOf(ctx.formParam("latitud"));
      Double longitud = Double.valueOf(ctx.formParam("longitud"));
      String provinciaNombre = GeocodingService.obtenerProvinciaDesdeCoordenadas(latitud, longitud);
      List<UploadedFile> archivos = ctx.uploadedFiles("multimedia");

      if (!(this.validarCantidadArchivos(archivos) || this.validarTamanioArchivos(archivos))) {
        model.put("type", "error");
        model.put("message", "Error: los archivos exceden los limites");
        return model;
      }

      List<Multimedia> archivosMultimedia = new ArrayList<>();
      archivos.forEach(a -> {
        try {
          String assetsDir = System.getProperty("user.dir") + "/src/main/resources/assets/";
          Files.createDirectories(Paths.get(assetsDir));

          Path destino = Paths.get(assetsDir + a.filename());
          Files.copy(a.content(), destino, StandardCopyOption.REPLACE_EXISTING);

          Multimedia multimedia = new Multimedia();
          multimedia.setTipo(a.contentType());
          multimedia.setTamanio(a.size());
          multimedia.setUrl("assets/" + a.filename());
          archivosMultimedia.add(multimedia);

        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      Hecho hecho = new Hecho.HechoBuilder()
          .setTitulo(titulo)
          .setDescripcion(descripcion)
          .setCategoria(categoria)
          .setProvincia(provinciaNombre)
          .setLatitud(latitud)
          .setLongitud(longitud)
          .setFecha(fecha)
          .setFechaCarga(LocalDateTime.now())
          .setEstado(Estado.PENDIENTE)
          .build(repoProvincias);
      hecho.setUsuario(usuarioActual);
      hecho.setFuenteOrigen(fuenteDinamicaAsociada);

      archivosMultimedia.forEach(m -> m.setHecho(hecho));
      hecho.setMultimedia(archivosMultimedia);

      repoHechos.guardarHecho(hecho);

      archivosMultimedia.forEach(repoMultimedia::crearMultimedia);
      SolicitudRevision solicitudRevision = new SolicitudRevision(hecho, hecho.getUsuario());
      repoSolicitudes.nuevaSolicitudRevision(solicitudRevision);
      String descripcionSolicitud = "SPAM: " + hecho.getTitulo();
      repoSolicitudes.nuevaSolicitud(hecho, descripcionSolicitud);
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
    Map<String, Object> model = modeloBase(ctx);
    try {
      List<Hecho> hechos = repoHechos.obtenerTodosLosHechos();
      if (hechos.isEmpty()) {
        hechos = Collections.emptyList();
      }
      List<Map<String, Object>> hechosUbicados = hechos.stream().map(hecho -> {
        Map<String, Object> data = new HashMap<>();
        data.put("latitud", hecho.getLatitud());
        data.put("longitud", hecho.getLongitud());
        data.put("titulo", hecho.getTitulo());
        return data;
      }).toList();
      model.put("hechos", hechosUbicados);
    } catch (Exception e) {
      e.printStackTrace();
      model.put("hechos", Collections.emptyList());
    }
    return model;
  }

  private boolean validarCantidadArchivos(List<UploadedFile> archivos) {
    int cantidadMaxima = 5;
    return archivos.size() > cantidadMaxima;
  }

  private boolean validarTamanioArchivos(List<UploadedFile> archivos) {
    long tamanioMaximo = 1024 * 1024;
    long totalBytes = archivos.stream()
        .mapToLong(UploadedFile::size)
        .sum();

    return totalBytes <= tamanioMaximo;
  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

  public Map<String, Object> listar(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);
    try {
      String query = ctx.queryParam("query");
      List<Hecho> hechos = repoHechos.buscarFullText(query);
      model.put("hechos", hechos);
    } catch (Exception e) {
      e.printStackTrace();
      return model;
    }
    return model;
  }


  public void filtrarHechos(Context context) {
    String filtroParametro = context.queryParam("categoria");

    if (filtroParametro == null || filtroParametro.trim().isEmpty()) {
      context.sessionAttribute("error", "Debes ingresar una categoría.");
      context.redirect("/hechos");
      return;
    }

    filtroParametro = filtroParametro.trim(); // No lo paso a lower acá todavía

    List<Hecho> hechosFiltrados = repoHechos.obtenerPorCategoria(filtroParametro);

    Map<String, Object> model = new HashMap<>();
    model.put("categoria", filtroParametro);
    model.put("hechos", hechosFiltrados);
    model.put("sinResultados", hechosFiltrados.isEmpty()); // ✔ para la vista

    context.render("hechos-filtrados.hbs", model);
  }

  public void mostrarFormularioEditar(Context context) {

    Long id = Long.parseLong(context.pathParam("id"));

    Hecho hecho = repoHechos.obtenerPorId(id);

    Map<String, Object> model = new HashMap<>();
    model.put("id", id);
    String error = context.queryParam("error");
    if (error != null) model.put("error", error);

    String success = context.queryParam("success");
    if (success != null) model.put("mensaje", "Hecho actualizado con éxito");

    context.render("hecho-editar.hbs", model);

  }

  public void actualizarHecho(Context context) {
    try {
      FuenteDinamica fuenteDinamica = new FuenteDinamica(repoHechos, repoSolicitudes);
      Long id = Long.parseLong(context.pathParam("id"));
      Hecho hecho = repoHechos.obtenerPorId(id);

      Map<String, Object> model = new HashMap<>();
      Hecho.HechoBuilder hechoBuilder = new Hecho.HechoBuilder();

      String categoria = context.formParam("categoria");
      if (categoria != null && !categoria.isBlank()) hechoBuilder.setCategoria(categoria);

      String descripcion = context.formParam("descripcion");
      if (descripcion != null && !descripcion.isBlank()) hechoBuilder.setDescripcion(descripcion);

      String titulo = context.formParam("titulo");
      if (titulo != null && !titulo.isBlank()) hechoBuilder.setTitulo(titulo);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
      String fechaStr = context.formParam("fechaCarga");
      if (fechaStr != null && !fechaStr.isBlank()) {
        hechoBuilder.setFechaCarga(LocalDateTime.parse(fechaStr, formatter));
      }
      String latStr = context.formParam("latitud");
      String lonStr = context.formParam("longitud");
      if (latStr != null && lonStr != null && !latStr.isBlank() && !lonStr.isBlank()) {
        try {
          double latitud = Double.parseDouble(latStr);
          double longitud = Double.parseDouble(lonStr);
          hechoBuilder.setLatitud(latitud);
          hechoBuilder.setLongitud(longitud);
        } catch (NumberFormatException e) {
          context.queryParam("error");
        }
      }

      if (hecho.getUsuario() == null) {
        throw new Exception("El usuario no está registrado.");
      }

      if (!hecho.estaDentroDePlazoDeEdicion()) {
        throw new Exception("El plazo para modificar este hecho ha expirado.");
      }

      fuenteDinamica.actualizarHecho(hecho, hechoBuilder, hecho.getUsuario());
      context.redirect("/hechos/" + id + "/editar?success=true");

    } catch (Exception e) {
      Long id = Long.parseLong(context.pathParam("id"));
      String mensajeError = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
      context.redirect("/hechos/" + id + "/editar?error=" + mensajeError);
    }
  }

  public Map<String, Object> obtenerHecho(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);
    try {
      Long hechoId = Long.valueOf(ctx.pathParam("id"));
      Hecho hecho = repoHechos.obtenerPorId(hechoId);
      model.put("titulo", hecho.getTitulo());
      model.put("fecha", hecho.getFecha().toLocalDate());
      model.put("descripcion", hecho.getDescripcion());
      model.put("multimedia", hecho.getMultimedia());
      model.put("id",hecho.getId());
    } catch (Exception e) {
      e.printStackTrace();
      return model;
    }
    return model;
  }
}