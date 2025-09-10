import Exceptions.HechoMalGeneradoException;
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
import javax.persistence.Entity;

@Entity
public class FuenteEstaticaVictimas extends Fuente {

  private String rutaCsv;

  protected FuenteEstaticaVictimas(){}

  public FuenteEstaticaVictimas(String rutaCsv) {
    this.rutaCsv = rutaCsv;
  }

  @Override
  public List<Hecho> extraerHechos() {
    List<Hecho> lista = new ArrayList<>();

    CSVParser parser = new CSVParserBuilder()
        .withSeparator(';')
        .build();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

    try (CSVReader reader = new CSVReaderBuilder(
        new InputStreamReader(new FileInputStream(rutaCsv), StandardCharsets.UTF_8))
        .withCSVParser(parser)
        .withSkipLines(1)
        .build()) {

      String[] fila;
      while ((fila = reader.readNext()) != null) {
        String tipoPersona = CSVHelper.getValue(fila, 2);
        String provincia = CSVHelper.getValue(fila, 5);
        String localidad = CSVHelper.getValue(fila, 9);
        String calle = CSVHelper.getValue(fila, 16);
        String hora = CSVHelper.getValue(fila, 15);

        LocalDate fecha = CSVHelper.parseFecha(CSVHelper.getValue(fila, 14), formatter);

        double lat = CSVHelper.parseCoordenada(CSVHelper.getValue(fila, 10));
        double lng = CSVHelper.parseCoordenada(CSVHelper.getValue(fila, 11));

        Hecho hecho = new Hecho(
            tipoPersona + " en " + localidad + ", " + provincia,
            calle + " " + hora,
            "Accidente Vial",
            lat,
            lng,
            fecha,
            LocalDate.now(),
            Estado.ACEPTADA);

        lista.add(hecho);
      }
    } catch (IOException | CsvException e) {
      throw new HechoMalGeneradoException();
    }

    return lista;
  }
}