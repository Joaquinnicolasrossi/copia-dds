import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fuente {

  private final String rutaCsv;

  // Mapas estáticos para traducir IDs de causa y causa_desc a texto
  private static final Map<String, String> CAUSA_MAP = new HashMap<>();
  private static final Map<String, String> CAUSA_DESC_MAP = new HashMap<>();

  static {
    // Valores sacados de los extra codes de la fuente
    CAUSA_MAP.put(
        "1",
        "Fuego por rayo"
    );
    CAUSA_MAP.put(
        "2",
        "Fuego por accidente o negligencia"
    );
    CAUSA_MAP.put(
        "3",
        "Fuego por accidente o negligencia"
    );
    CAUSA_MAP.put(
        "4",
        "Fuego intencionado"
    );
    CAUSA_MAP.put(
        "5",
        "Fuego por causa desconocida"
    );
    CAUSA_MAP.put(
        "6",
        "Incendio reproducido"
    );

    // Causa detallada (fuegos por accidente o negligencia)
    CAUSA_DESC_MAP.put(
        "280",
        "quema de matorral"
    );
    CAUSA_DESC_MAP.put(
        "281",
        "quema de matorral próximo a edificaciones"
    );
    CAUSA_DESC_MAP.put(
        "282",
        "quema de matorral para limpieza de caminos o sendas"
    );
    CAUSA_DESC_MAP.put(
        "283",
        "quema de matorral en focos de animales nocivos"
    );
    CAUSA_DESC_MAP.put(
        "284",
        "quema de matorral"
    );
    CAUSA_DESC_MAP.put(
        "291",
        "actividades apícolas"
    );
    CAUSA_DESC_MAP.put(
        "292",
        "fuegos artificiales"
    );
    CAUSA_DESC_MAP.put(
        "293",
        "globos"
    );
    CAUSA_DESC_MAP.put(
        "294",
        "juegos de niños"
    );
    CAUSA_DESC_MAP.put(
        "295",
        "restos de poda de urbanización"
    );
    CAUSA_DESC_MAP.put(
        "270",
        "escape de vertedero"
    );
    CAUSA_DESC_MAP.put(
        "260",
        "quema de basura"
    );
    CAUSA_DESC_MAP.put(
        "250",
        "fumadores"
    );
    CAUSA_DESC_MAP.put(
        "210",
        "quema agrícola"
    );
    CAUSA_DESC_MAP.put(
        "211",
        "quema de rastrojos"
    );
    CAUSA_DESC_MAP.put(
        "212",
        "quema de restos de poda"
    );
    CAUSA_DESC_MAP.put(
        "213",
        "quema de lindes y bordes de fincas"
    );
    CAUSA_DESC_MAP.put(
        "214",
        "quema de bordes de acequias"
    );
    CAUSA_DESC_MAP.put(
        "215",
        "quema agrícola"
    );
    CAUSA_DESC_MAP.put(
        "220",
        "quema para regenerar pastos"
    );
    CAUSA_DESC_MAP.put(
        "240",
        "hogueras"
    );
    CAUSA_DESC_MAP.put(
        "230",
        "trabajos forestales"
    );
    CAUSA_DESC_MAP.put(
        "223",
        "quema para regenerar pastos"
    );
    CAUSA_DESC_MAP.put(
        "222",
        "quema para regenerar pastos"
    );
    CAUSA_DESC_MAP.put(
        "221",
        "quema para regenerar pastos"
    );
    CAUSA_DESC_MAP.put(
        "340",
        "maniobras militares"
    );
    CAUSA_DESC_MAP.put(
        "335",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "334",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "333",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "332",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "331",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "330",
        "motores y máquinas"
    );
    CAUSA_DESC_MAP.put(
        "320",
        "líneas eléctricas"
    );
    CAUSA_DESC_MAP.put(
        "310",
        "ferrocarril"
    );

    // Motivaciones (fuegos intencionados) --> Se modifica el formato por
    // la cantidad de caracteres por linea
    CAUSA_DESC_MAP.put(
        "41",
        "campesinos para eliminar matorral y residuos agrícolas"
    );
    CAUSA_DESC_MAP.put(
        "42",
        "pastores y ganaderos para regenerar el pasto"
    );
    CAUSA_DESC_MAP.put(
        "43",
        "venganzas"
    );
    CAUSA_DESC_MAP.put(
        "44",
        "ahuyentar animales (lobos, jabalíes)"
    );
    CAUSA_DESC_MAP.put(
        "45",
        "cazadores para facilitar la caza"
    );
    CAUSA_DESC_MAP.put(
        "46",
        "contra el acotamiento de la caza"
    );
    CAUSA_DESC_MAP.put(
        "47",
        "disensiones en cuanto a la titularidad de los montes públicos o privados"
    );
    CAUSA_DESC_MAP.put(
        "48",
        "represalia al reducirse las inversiones públicas en los montes"
    );
    CAUSA_DESC_MAP.put(
        "49",
        "obtener salarios en la extinción de los mismos o en la restauración"
    );
    CAUSA_DESC_MAP.put(
        "410",
        "pirómanos"
    );
    CAUSA_DESC_MAP.put(
        "411",
        "bajar el precio de la madera"
    );
    CAUSA_DESC_MAP.put(
        "412",
        "modificación en el uso del suelo"
    );
    CAUSA_DESC_MAP.put(
        "413",
        "grupos políticos para crear malestar social"
    );
    CAUSA_DESC_MAP.put(
        "414",
        "animadversión contra repoblaciones forestales"
    );
    CAUSA_DESC_MAP.put(
        "415",
        "delincuentes, etc. para distraer a la G. Civil o Policía"
    );
    CAUSA_DESC_MAP.put(
        "416",
        "rechazo a la creación o existencia de espacios naturales protegidos"
    );
    CAUSA_DESC_MAP.put(
        "417",
        "ritos pseudoreligiosos y satanismo"
    );
    CAUSA_DESC_MAP.put(
        "418",
        "contemplar las labores de extinción"
    );
    CAUSA_DESC_MAP.put(
        "419",
        "vandalismo"
    );
    CAUSA_DESC_MAP.put(
        "420",
        "favorecer la producción de productos del monte"
    );
    CAUSA_DESC_MAP.put(
        "421",
        "forzar resoluciones de consorcios o convenios"
    );
    CAUSA_DESC_MAP.put(
        "422",
        "resentimiento por expropiaciones"
    );
    CAUSA_DESC_MAP.put(
        "423",
        "venganzas por multas impuestas"
    );
  }

  public Fuente(String rutaCsv) {
    this.rutaCsv = rutaCsv;
  }

  /**
   * Cada vez que se llama, abre el CSV, lo recorre y devuelve
   * la lista de Hechos con los siguientes valores:
   * - título = texto de causa + " en " + municipio
   * - descripción = texto de causa_desc
   * - categoría = "Incendio Forestal"
   * - latitud  = lat (0.0 si vacío)
   * - longitud = lng (0.0 si vacío)
   * - fecha    = fecha (null si vacío)
   */
  public List<Hecho> extraerHechos() {
    List<Hecho> lista = new ArrayList<>();
    // NOTA: Se podria usar
    // new InputStreamReader(new FileInputStream(rutaCsv), StandardCharsets.UTF_8)
    // para garantizar que el archivo este en UTF-8 y
    // no usar el encoding default del sistema operativo
    try (CSVReader reader = new CSVReaderBuilder(new FileReader(rutaCsv))
        .withSkipLines(1)
        .build()) {
      String[] f;
      while ((f = reader.readNext()) != null) {
        final String causaId = (f[10] != null) ? f[10].trim() : null;
        final String descId = (f[12] != null) ? f[12].trim() : null;

        String causaTexto = (causaId != null)
            ? CAUSA_MAP.getOrDefault(causaId, "")
            : "";
        String descTexto = (descId != null)
            ? CAUSA_DESC_MAP.getOrDefault(descId, "")
            : "";
        // Agrega la causa y descripcion sacados del map a
        // la version generica del hecho del csv
        Hecho h = construirHecho(f, causaTexto, descTexto);

        lista.add(h);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }

    return lista;
  }

  private static Hecho construirHecho(String[] f, String causaTexto, String descTexto) {
    final String municipio = (f[9] != null && !f[9].trim().isEmpty())
        ? f[9].trim() : "";

    LocalDate fecha = null;
    if (f[2] != null && !f[2].trim().isEmpty()) {
      fecha = LocalDate.parse(f[2].trim());
    }

    double lat = 0.0;
    if (f[3] != null && !f[3].trim().isEmpty()) {
      lat = Double.parseDouble(f[3].trim());
    }

    double lng = 0.0;
    if (f[4] != null && !f[4].trim().isEmpty()) {
      lng = Double.parseDouble(f[4].trim());
    }

    Hecho h = new Hecho(
        causaTexto + " en " + municipio,
        descTexto,
        "Incendio Forestal",
        lat,
        lng,
        fecha
    );
    return h;
  }
}

