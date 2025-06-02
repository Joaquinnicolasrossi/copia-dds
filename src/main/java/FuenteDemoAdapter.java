import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.net.URL;

public class FuenteDemoAdapter implements IFuente {
  private final Conexion clienteExterno;
  private final URL urlExterna;
  private LocalDateTime ultimaConsulta;
  private List<Hecho> listaHechos = new ArrayList<>();

  public FuenteDemoAdapter(URL url, LocalDateTime ultimaConsulta) {
    this.clienteExterno = new Conexion();
    this.urlExterna = url;
    this.ultimaConsulta = ultimaConsulta; // inicializo
  }

  public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
    this.ultimaConsulta = ultimaConsulta;
  }

  @Override
  public List<Hecho> extraerHechos() {
    /* TODO: el codigo comentado deberia ser una funcion actualizarHechos
       para que extraer hechos siempre devuelva toda la lista */
    List<Hecho> lista = new ArrayList<>();
    Map<String, Object> datos;
    // Llamo a la biblioteca externa hasta que devuelva null
    while ((datos = clienteExterno.siguienteHecho(urlExterna, ultimaConsulta)) != null) {
      Hecho h = mapToHecho(datos);
      lista.add(h);
    }
    ultimaConsulta = LocalDateTime.now();
    return listaHechos;
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
    builder.setLatitud((double)latObj);

    Object longObj = raw.get("longitud");
    builder.setLongitud((double)longObj);

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
