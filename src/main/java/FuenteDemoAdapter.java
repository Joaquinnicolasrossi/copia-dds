import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;

@Entity
public class FuenteDemoAdapter extends Fuente {
  private final Conexion clienteExterno;
  private final URL urlExterna;
  private LocalDateTime ultimaConsulta;
  private RepoHechos repositorio;

  public FuenteDemoAdapter(URL url, Conexion clienteExterno, LocalDateTime ultimaConsultaInicial,
      RepoHechos repositorio) {
    this.urlExterna = url;
    this.clienteExterno = clienteExterno;
    this.repositorio = repositorio;
    if (ultimaConsultaInicial != null) {
      this.ultimaConsulta = ultimaConsultaInicial;
    }
  }

  @Override
  public List<Hecho> extraerHechos() {
    return repositorio.obtenerHechosPorFuente(this);
  }

  // carga los hechos nuevos a listaHechos
  public void actualizarHechos() {
    Map<String, Object> datos;
    List<Hecho> hechosAGuardar = new ArrayList<>();
    // Llamo a la biblioteca externa hasta que devuelva null
    while ((datos = clienteExterno.siguienteHecho(urlExterna, ultimaConsulta)) != null) {
      Hecho h = mapToHecho(datos);
      hechosAGuardar.add(h);
    }
    repositorio.guardarHechos(hechosAGuardar);
    ultimaConsulta = LocalDateTime.now();
  }

  private Hecho mapToHecho(Map<String, Object> raw) {
    Hecho.HechoBuilder builder = new Hecho.HechoBuilder();

    // Se asumen los tipos de datos
    // y nombres de las keys
    Object tituloObj = raw.get("titulo");
    builder.setTitulo((String) tituloObj);

    Object descObj = raw.get("descripcion");
    builder.setDescripcion((String) descObj);

    Object catObj = raw.get("categoria");
    builder.setCategoria((String) catObj);

    Object latObj = raw.get("latitud");
    builder.setLatitud((double) latObj);

    Object longObj = raw.get("longitud");
    builder.setLongitud((double) longObj);

    // asumo java.util.LocalDate
    Object fechaRaw = raw.get("fecha");
    LocalDate fechaHecho = (LocalDate) fechaRaw;
    builder.setFecha(fechaHecho);

    builder.setFechaCarga(LocalDate.now());

    Object estadoObj = raw.get("estado");
    if (estadoObj instanceof String) {
      try {
        Estado e = Estado.valueOf(((String) estadoObj).toUpperCase());
        builder.setEstado(e);
      } catch (IllegalArgumentException ex) {
        builder.setEstado(Estado.PENDIENTE);
      }
    } else {
      builder.setEstado(Estado.PENDIENTE);
    }
    return builder.build();
  }
}
