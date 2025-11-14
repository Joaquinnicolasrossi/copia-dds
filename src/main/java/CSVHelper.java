import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
// Con esta función fallaba el LocalDateTime, lo modificamos para que muestre los hechos en la colección csv
//  public static LocalDateTime parseFecha(String valor, java.time.format.DateTimeFormatter formatter) {
//    if (valor == null || valor.isEmpty())
//      return null;
//    return (formatter == null)
//        ? LocalDateTime.parse(valor) // ISO-8601
//        : LocalDateTime.parse(valor, formatter);
//  }

  public static LocalDateTime parseFecha(String valor, java.time.format.DateTimeFormatter formatter) {
    if (valor == null || valor.isEmpty())
      return null;

    if (formatter == null) {
      return valor.length() == 10
          ? LocalDate.parse(valor).atStartOfDay()
          : LocalDateTime.parse(valor);
    }

    return LocalDate.parse(valor, formatter).atStartOfDay();
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
        String coleccionId = "";
        if (reg.getColeccion() != null && reg.getColeccion().getId() != null) {
          coleccionId = reg.getColeccion().getId().toString();
        }

        String[] linea = {
            reg.getId() != null ? reg.getId().toString() : "",
            coleccionId,
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