import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;
import java.time.*;
import java.time.LocalDate;

public class ServidorMetaMapa {

  @Path("/hechos")
  public class ApiMetaMapa {

    private final FuenteMetamapa fuente = new FuenteMetamapa();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public class getHechos(){



    }

  }


}