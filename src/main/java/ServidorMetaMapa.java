import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;

@Path("/hechos")
public class ServidorMetaMapa {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Hecho> getHechos() {
    return List.of(
        new Hecho("TituloA", "DescripcionA", "CategoriaA", -34.6037,
            -34.6037, LocalDateTime.of(2025, 5, 28, 10 ,0), LocalDateTime.now(),
            Estado.ACEPTADA),
        new Hecho("TituloB", "DescripcionB", "CategoriaB", -34.6037,
            -34.6037, LocalDateTime.of(2025, 5, 28, 10 ,0), LocalDateTime.now(),
            Estado.ACEPTADA));
  }
}