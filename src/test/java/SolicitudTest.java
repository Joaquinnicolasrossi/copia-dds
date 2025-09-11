import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SolicitudTest {

  @Test
  void seCreaCorrectamenteConDescripcionValida() {
   DetectorDeSpamFiltro detectorDeSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpamFiltro);
    Hecho hecho = crearHechoSimple();
    String descripcion = "x".repeat(500);

    Solicitud solicitud = new Solicitud(hecho, descripcion, repoSolicitudes, false);

    assertEquals(hecho, solicitud.hecho);
    assertEquals(descripcion, solicitud.getDescripcion());
    assertFalse(solicitud.eliminado);
  }

  @Test
  void eliminarSolicitudLaQuitaDelRepo() throws Exception {
    DetectorDeSpamFiltro detectorDeSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpamFiltro );
    Hecho hecho = crearHechoSimple();
    String descripcion = "x".repeat(500);

    // Agregamos la solicitud
    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    // La buscamos
    Solicitud solicitud = repoSolicitudes.getSolicitudes().get(0);

    // La quitamos
    solicitud.eliminarSolicitud();

    assertTrue(repoSolicitudes.getSolicitudes().isEmpty());
  }
  private Hecho crearHechoSimple() {
    return new Hecho("Incendio", "desc", "Incendio Forestal", -0.5, -0.5, LocalDate.now(), LocalDate.now(), Estado.PENDIENTE);
  }
}