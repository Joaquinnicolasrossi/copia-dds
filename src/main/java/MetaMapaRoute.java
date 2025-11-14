import io.javalin.Javalin;
import java.time.LocalDateTime;
import java.util.List;

public class MetaMapaRoute implements Router {

  @Override
  public void configure(Javalin app) {

    app.get("/apiMetaMapa/hechos", ctx -> {
      ctx.json(List.of(
          new Hecho(
              "Incendio 1968290001",
              "Superficie: 14 ha",
              "INDETERMINADO",
              0.0,
              0.0,
              LocalDateTime.of(1968, 1, 1, 0, 0),
              LocalDateTime.now(),
              Estado.ACEPTADA
          ),
          new Hecho(
              "Incendio 1968430003",
              "Superficie: 3 ha",
              "INDETERMINADO",
              0.0,
              0.0,
              LocalDateTime.of(1968, 1, 3, 0, 0),
              LocalDateTime.now(),
              Estado.ACEPTADA
          ),
          new Hecho(
              "Incendio 1968290006",
              "Superficie: 2 ha",
              "INDETERMINADO",
              0.0,
              0.0,
              LocalDateTime.of(1968, 1, 6, 0, 0),
              LocalDateTime.now(),
              Estado.ACEPTADA
          )
      ));
    });
  }
}
