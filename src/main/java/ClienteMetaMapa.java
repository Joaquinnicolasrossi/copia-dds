import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClienteMetaMapa {

  private static final String url = "http://localhost:8080/apiMetaMapa";
  private final HttpClient client;
  private final ObjectMapper mapper;

  public ClienteMetaMapa() {
    this.client = HttpClient.newHttpClient();
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }

  public List<Hecho> getHechos() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url + "/hechos"))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      return mapper.readValue(response.body(), new TypeReference<List<Hecho>>() {
      });
    } else {
      throw new RuntimeException("Error en la petición: " + response.statusCode());
    }
  }
}
