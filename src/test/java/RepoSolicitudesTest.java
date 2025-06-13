import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepoSolicitudesTest {

   private DetectorDeSpamFiltro detectorDeSpamFiltro = new DetectorDeSpamFiltro();
  private RepoSolicitudes repoSolicitudes;

  @BeforeEach
  void setUp() {
    DetectorDeSpamFiltro detector = new DetectorDeSpamFiltro();
    repoSolicitudes = new RepoSolicitudes(detector);
  }
  @Test
  void nuevaSolicitudLaAgregaCorrectamenteAlRepositorio() throws Exception {



    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    List<Solicitud> solicitudes = repoSolicitudes.getSolicitudes();
    assertEquals(1, solicitudes.size());
    Solicitud solicitud = solicitudes.get(0);
    assertEquals(hecho, solicitud.hecho);
    assertEquals(descripcion, solicitud.getDescripcion());
    assertFalse(solicitud.eliminado);

  }

  @Test
  void eliminarSolicitudLaQuitaDelRepositorio() throws Exception {


    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);
    Solicitud solicitud = repoSolicitudes.getSolicitudes().get(0);
    solicitud.eliminarSolicitud();

    assertTrue(repoSolicitudes.getSolicitudes().isEmpty());
  }

  @Test
  void hechoEliminadoSoloEsTrueSiSeAcepta() throws Exception {


    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    // aun no fue aceptada --> Devuelve false
    assertFalse(repoSolicitudes.estaEliminado(hecho));

    repoSolicitudes.getSolicitudes().get(0).aceptarSolicitud();
    assertTrue(repoSolicitudes.estaEliminado(hecho));
  }
}