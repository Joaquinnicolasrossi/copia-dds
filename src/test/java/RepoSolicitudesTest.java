import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepoSolicitudesTest {

  @Test
  void nuevaSolicitudLaAgregaCorrectamenteAlRepositorio() {

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes();
    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    List<Solicitud> solicitudes = repoSolicitudes.getSolicitudes();
    assertEquals(1, solicitudes.size());
    Solicitud solicitud = solicitudes.get(0);
    assertEquals(hecho, solicitud.hecho);
    assertEquals(descripcion, solicitud.descripcion);
    assertFalse(solicitud.eliminado);

  }

  @Test
  void eliminarSolicitudLaQuitaDelRepositorio() {

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes();
    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);
    Solicitud solicitud = repoSolicitudes.getSolicitudes().get(0);
    solicitud.eliminarSolicitud();

    assertTrue(repoSolicitudes.getSolicitudes().isEmpty());
  }

  @Test
  void hechoEliminadoSoloEsTrueSiSeAcepta(){

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes();
    Hecho hecho = new Hecho("Incendio", "desc", "Incendio Forestal", 0, 0, LocalDate.now());
    String descripcion = "x".repeat(500);

    repoSolicitudes.nuevaSolicitud(hecho, descripcion);

    // aun no fue aceptada --> Devuelve false
    assertFalse(repoSolicitudes.estaEliminado(hecho));

    repoSolicitudes.getSolicitudes().get(0).aceptarSolicitud();
    assertTrue(repoSolicitudes.estaEliminado(hecho));
  }
}