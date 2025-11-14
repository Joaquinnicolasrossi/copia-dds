import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitudController {
  private final RepoSolicitudes repoSolicitudes;
  private final RepoHechos repoHechos;

  public SolicitudController(RepoSolicitudes repoSolicitudes, RepoHechos repoHechos) {
    this.repoSolicitudes = repoSolicitudes;
    this.repoHechos = repoHechos;
  }

  public Map<String, Object> crearSolicitud(Context ctx) {
    Map<String, Object> model = modeloBase(ctx);
    try {
      String descripcion = ctx.formParam("descripcion");
      Long hechoId = Long.parseLong(ctx.pathParam("id"));
      Hecho hecho = repoHechos.obtenerPorId(hechoId);
      if (hecho == null) {
        model.put("type", "error");
        model.put("message", "Hecho no encontrado");
        return model;
      }
      repoSolicitudes.nuevaSolicitud(hecho, descripcion);
      model.put("type", "success");
      model.put("message", "Solicitud creada correctamente.");
      return model;

    } catch (Exception e) {
      model.put("type", "danger");
      model.put("message", "Error interno: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      return model;
    }
  }

  public Map<String, Object> listar(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    List<Solicitud> solicitudes = new ArrayList<>();
    try {
      solicitudes = repoSolicitudes.getSolicitudesPendientes();
    } catch (Exception e) {
      model.put("solicitudes", Collections.emptyList());
    }
    model.put("solicitudes", solicitudes);
    return model;
  }

  public Map<String, Object> modeloBase(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuarioActual", ctx.attribute("usuarioActual"));
    model.put("nombre", ctx.attribute("nombre"));
    return model;
  }

  public void mostrarFormularioSolicitud(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("id", ctx.pathParam("id"));
    ctx.render("solicitud-eliminacion-form.hbs", model);
  }

  public void aceptarSolicitud(Context ctx) {
    Solicitud solicitud = repoSolicitudes.obtenerPorId(Long.valueOf(ctx.pathParam("id")));
    repoSolicitudes.aceptarSolicitud(solicitud);
  }

  public void eliminarSolicitud(Context ctx) {
    Solicitud solicitud = repoSolicitudes.obtenerPorId(Long.valueOf(ctx.pathParam("id")));
    repoSolicitudes.eliminarSolicitud(solicitud);
  }

  public void MostrarSolicitudRevision(Context context) {
    List<SolicitudRevision> solicitudesRevision = repoSolicitudes.getRevisiones();
    Map<String, Object> modelo = new HashMap<>();
    modelo.put("solicitudesRevision", solicitudesRevision);
    modelo.put("usuarioActual", context.sessionAttribute("usuarioActual"));
    context.render("solicitudes-revision.hbs", modelo);
  }

  public void aceptarSolicitudRevision(Context context) throws Exception {
    FuenteDinamica fuenteDinamica = new FuenteDinamica(repoHechos, repoSolicitudes);
    Aceptar aceptar = new Aceptar(repoHechos);
    Long id = Long.parseLong(context.pathParam("id"));
    SolicitudRevision solicitudRevision = repoSolicitudes.findById(id);
    fuenteDinamica.revisarSolicitud(solicitudRevision.getHecho(), aceptar);
    context.redirect("/solicitud/revision");
  }

  public void rechazarrSolicitudRevision(Context context) throws Exception {
    FuenteDinamica fuenteDinamica = new FuenteDinamica(repoHechos, repoSolicitudes);
    Rechazar rechazar = new Rechazar(repoHechos);
    Long id = Long.parseLong(context.pathParam("id"));
    SolicitudRevision solicitudRevision = repoSolicitudes.findById(id);
    fuenteDinamica.revisarSolicitud(solicitudRevision.getHecho(), rechazar);
    context.redirect("/solicitud/revision");

  }

  public void aceptarConSugrenciaSolicitudRevision(Context context) {
    try {
      FuenteDinamica fuenteDinamica = new FuenteDinamica(repoHechos, repoSolicitudes);
      Long id = Long.parseLong(context.pathParam("id"));
      Hecho hecho = repoHechos.obtenerPorId(id);

      if (hecho == null) {
        context.status(404).result("Hecho no encontrado");
        return;
      }

      Hecho.HechoBuilder hechoBuilder = new Hecho.HechoBuilder();

      String categoria = context.formParam("categoria");
      if (categoria != null && !categoria.isBlank()) hechoBuilder.setCategoria(categoria);

      String descripcion = context.formParam("descripcion");
      if (descripcion != null && !descripcion.isBlank()) hechoBuilder.setDescripcion(descripcion);

      String titulo = context.formParam("titulo");
      if (titulo != null && !titulo.isBlank()) hechoBuilder.setTitulo(titulo);

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

      String fechaStr = context.formParam("fechaCarga");
      if (fechaStr != null && !fechaStr.isBlank()) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        hechoBuilder.setFechaCarga(LocalDateTime.parse(fechaStr, formatter));
      }
      AceptarConSugerencia aceptarConSugerencia = new AceptarConSugerencia(fuenteDinamica, hechoBuilder, repoHechos, hecho.getUsuario());
      fuenteDinamica.actualizarHecho(hecho, hechoBuilder, hecho.getUsuario());
      fuenteDinamica.revisarSolicitud(hecho, aceptarConSugerencia);

      context.redirect("/solicitud/revision");

    } catch (Exception e) {
      e.printStackTrace();
      context.status(500).result("Error al aplicar sugerencias: " + e.getMessage());
    }
  }

  public void formularioSugerenciasDeCambio(Context context) {
    Long idSolicitud = Long.parseLong(context.pathParam("id"));
    SolicitudRevision solicitudRevision = repoSolicitudes.findById(idSolicitud);

    if (solicitudRevision == null || solicitudRevision.getHecho() == null) {
      context.status(404).result("Solicitud o hecho no encontrado");
      return;
    }

    Hecho hecho = solicitudRevision.getHecho();

    Map<String, Object> model = new HashMap<>();
    model.put("id", hecho.getId());
    model.put("titulo", hecho.getTitulo());
    model.put("descripcion", hecho.getDescripcion());
    model.put("categoria", hecho.getCategoria());
    model.put("latitud", hecho.getLatitud());
    model.put("longitud", hecho.getLongitud());
    model.put("fechaCarga", hecho.getFechaCarga());

    context.render("aplicar-sugerencia-cambio.hbs", model);
  }
}