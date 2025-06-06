import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FuenteDemoAdapter implements InterfaceFuente {
  private final Conexion clienteExterno;
  private final URL urlExterna;
  private LocalDateTime ultimaConsulta;
  private List<Hecho> listaHechos = new ArrayList<>();
  private ScheduledExecutorService scheduler; // Scheduler que periódicamente ejecuta extraerHechos
  // () y acumula resultados
  private final long periodoPolling; // cada cuánto debe llamar a extraerHechos()
  private final TimeUnit unidadPeriodo; // Por defecto: 1 hora, pero en tests o en otra instancia
  // se puede parametrizar

  public FuenteDemoAdapter(URL url, LocalDateTime ultimaConsulta, Conexion clienteExterno, long
      periodoPolling, TimeUnit unidadPeriodo) {
    this.urlExterna = url;
    this.ultimaConsulta = ultimaConsulta;
    this.clienteExterno = clienteExterno;
    this.periodoPolling = periodoPolling;
    this.unidadPeriodo = unidadPeriodo;
    // Scheduler no se inicia automáticamente: se arranca con startScheduler()
    this.scheduler = null;
  }

  public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
    this.ultimaConsulta = ultimaConsulta;
  }

  @Override
  public List<Hecho> extraerHechos() {
    return actualizarHechos();
  }

  // carga los hechos nuevos a listaHechos
  public List<Hecho> actualizarHechos() {
    Map<String, Object> datos;
    // Llamo a la biblioteca externa hasta que devuelva null
    while ((datos = clienteExterno.siguienteHecho(urlExterna, ultimaConsulta)) != null) {
      Hecho h = mapToHecho(datos);
      listaHechos.add(h);
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

  // Inicia un ScheduledExecutorService
  // cada cierto periodoPolling/unidadPeriodo invoca extraerHechos() y añade los resultados
  public synchronized void startScheduler() {
    if (scheduler != null && !scheduler.isShutdown()) {
      // Ya estaba corriendo
      return;
    }
    // Crea un scheduler
    scheduler = Executors.newSingleThreadScheduledExecutor();
    // Programamos la primera ejecución al cabo de “periodoPolling” unidades, y luego repetimos
    scheduler.scheduleAtFixedRate(() -> {
      try {
        extraerHechos(); // actualiza listaHechos internamente
      } catch (Exception ex) {
        ex.printStackTrace(); // en un caso real, se loguearía
      }
    }, periodoPolling, periodoPolling, unidadPeriodo);
  }

  // Detiene el scheduler, si está corriendo.
  public synchronized void stopScheduler() {
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdownNow();
    }
    scheduler = null;
  }

  public List<Hecho> getListaHechos() {
    return listaHechos;
  }
}
