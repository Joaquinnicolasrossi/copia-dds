import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HechoController {
  private final RepoHechos repoHechos;
  private final RepoMultimedia repoMultimedia;
  private final RepoProvincias repoProvincias;

  public HechoController(RepoHechos repoHechos, RepoMultimedia repoMultimedia, RepoProvincias repoProvincias) {
    this.repoHechos = repoHechos;
    this.repoMultimedia = repoMultimedia;
    this.repoProvincias = repoProvincias;
  }

  public Map<String, Object> crear(Context ctx) {
    Map model = modeloBase(ctx);
    Usuario usuarioActual = ctx.sessionAttribute("usuarioActual");
    try {
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String categoria = ctx.formParam("categoria");
      LocalDateTime fecha = LocalDateTime.parse(ctx.formParam("fecha"));
      Double latitud = Double.valueOf(ctx.formParam("latitud"));
      Double longitud = Double.valueOf(ctx.formParam("longitud"));
      List<UploadedFile> archivos = ctx.uploadedFiles("multimedia");

      if(!(this.validarCantidadArchivos(archivos) || this.validarTamanioArchivos(archivos))){
        model.put("type", "error");
        model.put("message", "Error: los archivos exceden los limites");
        return model;
      };
      List<Multimedia> archivosMultimedia = new ArrayList<>();
      archivos.forEach(a -> {
          Multimedia multimedia = new Multimedia();
          multimedia.setTipo(a.contentType());
          multimedia.setTamanio(a.size());
          multimedia.setUrl("uploads/" + a.filename());

          archivosMultimedia.add(multimedia);

      });

      Hecho hecho = new Hecho.HechoBuilder()
          .setTitulo(titulo)
          .setDescripcion(descripcion)
          .setCategoria(categoria)
          .setLatitud(latitud)
          .setLongitud(longitud)
          .setFecha(fecha)
          .setFechaCarga(LocalDateTime.now())
          .setEstado(Estado.PENDIENTE)
          .build(repoProvincias);
      hecho.setUsuario(usuarioActual);


      archivosMultimedia.forEach(m -> m.setHecho(hecho));
      hecho.setMultimedia(archivosMultimedia);

      repoHechos.guardarHecho(hecho);

      archivosMultimedia.forEach(repoMultimedia::crearMultimedia);


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
    long tamanioMaximo = 1024*1024;
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
}
