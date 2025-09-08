import java.time.LocalDate;

public class CSVHelper {
  
    public static String getValue(String[] fila, int index) {
      if (index >= fila.length || fila[index] == null) return "";
      return fila[index].trim();
    }

    public static LocalDate parseFecha(String valor, java.time.format.DateTimeFormatter formatter) {
      if (valor == null || valor.isEmpty()) return null;
      return (formatter == null)
          ? LocalDate.parse(valor) // ISO-8601
          : LocalDate.parse(valor, formatter);
    }

    public static double parseCoordenada(String valor) {
      if (valor == null || valor.isEmpty() || valor.equalsIgnoreCase("Perdido")) {
        return 0.0;
      }
      return Double.parseDouble(valor);
    }
}
