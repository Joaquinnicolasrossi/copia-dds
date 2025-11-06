import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/agregadorAPI") // nodo independiente --> solo funciones de agregacion
public class AgregadorResource {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<Hecho> getHechosFromFuentes(List<Fuente> fuentes) {
    RepoProvincias repoProvincias = new RepoProvincias();
    Fuente fuenteAgregada = new FuenteAgregada(fuentes, new RepoHechos(repoProvincias));
    return fuenteAgregada.extraerHechos();
  }
}