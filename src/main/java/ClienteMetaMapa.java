import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;

public class ClienteMetaMapa {
  public List<Hecho> getHechos() throws Exception{
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI("/hechos"))
        .GET()
        .build();
  }
}
