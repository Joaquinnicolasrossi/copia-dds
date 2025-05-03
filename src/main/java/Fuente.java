import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class Fuente {
  public String nombreFuente;
  public String rutaCSV;

  public Fuente(String nombreFuente, String rutaCSV) {
    this.nombreFuente = nombreFuente;
    this.rutaCSV = rutaCSV;
  }

  public List<Hecho> getHechos() throws IOException {
    List<Hecho> hechos = new ArrayList<>();

    try(BufferedReader br = new BufferedReader(new FileReader(rutaCSV))) {
      String linea;
      while((linea = br.readLine()) != null) {
        String[] partes = linea.split(",");

        String titulo = partes[0].trim();
        String descripcion = partes[1].trim();
        String categoria = partes[2].trim();
        double latitud = Double.parseDouble(partes[3].trim());
        double longitud = Double.parseDouble(partes[4].trim());
        LocalDate fecha = LocalDate.parse(partes[5].trim());

        Hecho hecho = new Hecho(titulo, descripcion, categoria, new Ubicacion(latitud, longitud), fecha);
        hechos.add(hecho);
      }
    }
    return hechos;
  }
}
