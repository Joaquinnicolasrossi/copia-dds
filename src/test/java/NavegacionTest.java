import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NavegacionTest {

  @Test
  void NavegarModoCurada(){

  }
  @Test
  void NavegarModoIrrestricta(){
    Coleccion coleccion = crearColeccionBase();
    List<Hecho> hechosNavegados = coleccion.navegar(Modo.IRRESTRICTA, new CriterioCategoria("Incendio Forestal"));

    assertEquals(coleccion.mostrarHechos().size(), hechosNavegados.size());
  }
  private Coleccion crearColeccionBase() {
    FuenteEstaticaIncendios fuenteEstaticaIncendios = new FuenteEstaticaIncendios("src/test/resources/fires-all.csv");

    CriterioCategoria criterio1 = new CriterioCategoria("Incendio Forestal");
    LocalDateTime desde = LocalDateTime.of(2018, 8, 23,10,5);
    LocalDateTime hasta = LocalDateTime.of(2018, 9, 25,10,5);
    CriterioFecha criterio2 = new CriterioFecha(desde, hasta);
    List<Criterio> criterios = List.of(criterio1, criterio2);
    DetectorDeSpamFiltro deSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(deSpamFiltro);

    return new Coleccion(
        "Incendios test", "Hechos de prueba", fuenteEstaticaIncendios, criterios, repoSolicitudes);
  }
}







