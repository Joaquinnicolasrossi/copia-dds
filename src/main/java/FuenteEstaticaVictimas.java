import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class FuenteEstaticaVictimas implements Fuente {

  private final String rutaCsv;

  public FuenteEstaticaVictimas(String rutaCsv) {
    this.rutaCsv = rutaCsv;
  }

  /**
   * Cada vez que se llama, abre el CSV, lo recorre y devuelve
   * la lista de Hechos con los siguientes valores:
   * - título = tipo_persona + " en " + localidad_nombre + ", " + provincia_nombre
   * - descripción = calle_nombre + hora_hecho
   * - categoría = "Accidente Vial"
   * - latitud = latitud (0.0 si vacío)
   * - longitud = longitud (0.0 si vacío)
   * - fecha = fecha_hecho (null si vacío)
   */
  @Override
  public List<Hecho> extraerHechos() {
    List<Hecho> lista = new ArrayList<>();

    // el csv esta parseado con ; en vez de ,
    CSVParser parser = new CSVParserBuilder()
        .withSeparator(';')
        .build();

    // seteo el formato de fecha
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

    try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(rutaCsv),
        StandardCharsets.UTF_8))
        .withCSVParser(parser)
        .withSkipLines(1)
        .build()) {
      String[] f;
      while ((f = reader.readNext()) != null) {
        final String tipo_persona = (f[2] != null) ? f[2].trim() : null;
        final String provincia_nombre = (f[5] != null) ? f[5].trim() : null;
        final String localidad_nombre = (f[9] != null) ? f[9].trim() : null;
        final String calle_nombre = (f[16] != null && !f[16].trim().isEmpty())
            ? f[16].trim() : "";
        final String hora_hecho = (f[15] != null && !f[15].trim().isEmpty())
            ? f[15].trim() : "";

        LocalDate fecha = null;
        if (f[14] != null && !f[14].trim().isEmpty()) {
          fecha = LocalDate.parse(f[14].trim(), formatter);
        }

        String latStr = f[10].trim();
        String lngStr = f[11].trim();

        if (latStr.equalsIgnoreCase("Perdido")
            ||
            lngStr.equalsIgnoreCase("Perdido")) {
          latStr = "0";
          lngStr = "0";
        }

        double lat = 0.0;
        if (!f[10].trim().isEmpty()) {
          lat = Double.parseDouble(latStr);
        }

        double lng = 0.0;
        if (!f[11].trim().isEmpty()) {
          lng = Double.parseDouble(lngStr);
        }

        Hecho h = new Hecho(
            tipo_persona + " en " + localidad_nombre + ", " + provincia_nombre,
            calle_nombre + " " + hora_hecho,
            "Accidente Vial",
            lat,
            lng,
            fecha,
            LocalDate.now(),
            Estado.ACEPTADA
        );

        lista.add(h);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }

    return lista;
  }
}

