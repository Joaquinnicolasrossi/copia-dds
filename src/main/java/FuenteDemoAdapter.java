import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FuenteDemoAdapter implements Fuente {
  private final Conexion clienteExterno;
  private final URL urlExterna;
  private LocalDateTime ultimaConsulta = LocalDateTime.now();
  private List<Hecho> listaHechos = new ArrayList<>();

  public FuenteDemoAdapter(URL url, Conexion clienteExterno) {
    this.urlExterna = url;
    this.clienteExterno = clienteExterno;
  }

  public Long getId() { return null; }

  public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
    this.ultimaConsulta = ultimaConsulta;
  }

  @Override
  public List<Hecho> extraerHechos() {
    return listaHechos;
  }

  // carga los hechos nuevos a listaHechos
  public void actualizarHechos() {
    Map<String, Object> datos;
    // Llamo a la biblioteca externa hasta que devuelva null
    while ((datos = clienteExterno.siguienteHecho(urlExterna, ultimaConsulta)) != null) {
      Hecho h = mapToHecho(datos);
      listaHechos.add(h);
    }
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
