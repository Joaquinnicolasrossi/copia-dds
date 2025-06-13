import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SolicitudTest {

  @Test
  void seCreaCorrectamenteConDescripcionValida() {
   DetectorDeSpamFiltro detectorDeSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpamFiltro);
    Hecho hecho = new Hecho("titulo", "desc", "categoria", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    Solicitud solicitud = new Solicitud(hecho, descripcion, repoSolicitudes);

    assertEquals(hecho, solicitud.hecho);
    assertEquals(descripcion, solicitud.getDescripcion());
    assertFalse(solicitud.eliminado);
  }

  @Test
  void eliminarSolicitudLaQuitaDelRepo() throws Exception {
    DetectorDeSpamFiltro detectorDeSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detectorDeSpamFiltro );
    Hecho hecho = new Hecho("titulo", "desc", "categoria", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    // Agregamos la solicitud
    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    // La buscamos
    Solicitud solicitud = repoSolicitudes.getSolicitudes().get(0);

    // La quitamos
    solicitud.eliminarSolicitud();

    assertTrue(repoSolicitudes.getSolicitudes().isEmpty());

  }
}