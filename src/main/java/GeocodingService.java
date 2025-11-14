import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeocodingService {

  public static String obtenerProvinciaDesdeCoordenadas(Double latitud, Double longitud) {
    try {
      // API de Nominatim (OpenStreetMap)
      String url = String.format(
          "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=8&addressdetails=1",
          latitud, longitud
      );

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("User-Agent", "TuAplicacion/1.0")
          .GET()
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        // Buscar provincia en diferentes campos
        JsonNode address = root.get("address");
        if (address != null) {
          // Probar diferentes nombres de campos
          if (address.has("state")) {
            return address.get("state").asText();
          }
          if (address.has("province")) {
            return address.get("province").asText();
          }
          if (address.has("region")) {
            return address.get("region").asText();
          }
        }
      }

      return null;

    } catch (Exception e) {
      // Ocurrió un error al obtrener la provincia
      return null;
    }
  }
}