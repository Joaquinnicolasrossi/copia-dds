import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;
import java.time.*;
import java.time.LocalDate;

@Path("/hechos")
public class ServidorMetaMapa {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Hecho> getHechos() {
    return List.of(
        new Hecho("TituloA", "DescripcionA", "CategoriaA", -34.6037,
            -34.6037, LocalDate.of(2025, 5, 28)),
        new Hecho("TituloB", "DescripcionB", "CategoriaB", -34.6037,
            -34.6037, LocalDate.of(2025, 5, 28))
    );

  }

}