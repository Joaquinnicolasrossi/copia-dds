import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVHelper {

  private static final String[] HEADER = {
      "id", "coleccionId", "tipo", "valor", "cantidad", "visiblePublico", "fechaActualizacion"
  };

  public static String getValue(String[] fila, int index) {
    if (index >= fila.length || fila[index] == null)
      return "";
    return fila[index].trim();
  }

  public static LocalDateTime parseFecha(String valor, java.time.format.DateTimeFormatter formatter) {
    if (valor == null || valor.isEmpty())
      return null;
    return (formatter == null)
        ? LocalDateTime.parse(valor) // ISO-8601
        : LocalDateTime.parse(valor, formatter);
  }

  public static double parseCoordenada(String valor) {
    if (valor == null || valor.isEmpty() || valor.equalsIgnoreCase("Perdido")) {
      return 0.0;
    }
    return Double.parseDouble(valor);
  }

  public static void exportarEstadisticasCSV(List<EstadisticaRegistro> registros, String filePath) throws IOException {
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
      writer.writeNext(HEADER);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      for (EstadisticaRegistro reg : registros) {
        String[] linea = {
            reg.getId() != null ? reg.getId().toString() : "",
            reg.getColeccionId() != null ? reg.getColeccionId().toString() : "",
            reg.getTipo() != null ? reg.getTipo() : "",
            reg.getValor() != null ? reg.getValor() : "",
            reg.getCantidad() != null ? reg.getCantidad().toString() : "",
            reg.getVisiblePublico() != null ? reg.getVisiblePublico().toString() : "",
            reg.getFecha_actualizacion() != null ? reg.getFecha_actualizacion().format(formatter) : ""
        };
        writer.writeNext(linea);
      }
    }
  }
}
