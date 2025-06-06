import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class FuenteMetaMapaTest {

  ClienteMetaMapa cliente;
  FuenteMetaMapa fuenteMetaMapa;
  private WireMockServer wireMockServer;

  @BeforeEach
  public void inicializar() {
    wireMockServer = new WireMockServer(8080);
    wireMockServer.start();

    configureFor("localhost", 8080);

    String fechaHoy = LocalDate.now().toString();

    stubFor(get(urlEqualTo("/apiMetaMapa/hechos"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[{" +
                "\"titulo\":\"TituloA\"," +
                "\"descripcion\":\"DescripcionA\"," +
                "\"categoria\":\"CategoriaA\"," +
                "\"latitud\":-34.6037," +
                "\"longitud\":-34.6037," +
                "\"fecha\":\"2025-05-28\"}]")));

    cliente = new ClienteMetaMapa();
    fuenteMetaMapa = new FuenteMetaMapa(cliente);
  }

  @AfterEach
  public void finalizar() {
    wireMockServer.stop();
  }

  @Test
  public void seExtraenHechosDeMetaMapa() throws RuntimeException {
    List<Hecho> hechos = fuenteMetaMapa.extraerHechos();
    List<Hecho> hechosEsperados = List.of(new Hecho("TituloA", "DescripcionA", "CategoriaA", -34.6037, -34.6037, LocalDate.of(2025, 5, 28)));

    assertEquals(hechosEsperados.get(0).getTitulo(), hechos.get(0).getTitulo());
  }
}
