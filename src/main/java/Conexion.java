import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

public class Conexion {
  /**
   * Devuelve un mapa con los atributos de un hecho, indexados por nombre de atributo.
   * Si retorna null, significa que no hay nuevos hechos por ahora. La fecha es opcional.
   */
  Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
    return null;
  }
}
