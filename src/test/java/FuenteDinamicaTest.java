import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteDinamicaTest {

  private RepoFuenteDinamica repoFuenteDinamica;
  private FuenteDinamica fuenteDinamica;
  private RepoSolicitudesRevision repoSolicitudesRevision;
  private Hecho incendio;
  Hecho.HechoBuilder hechoBuilder = new Hecho.HechoBuilder();

  @BeforeEach
  void setUp() throws Exception {
    repoFuenteDinamica = new RepoFuenteDinamica();
    repoSolicitudesRevision = new RepoSolicitudesRevision();
    fuenteDinamica = new FuenteDinamica(repoFuenteDinamica, repoSolicitudesRevision);

    incendio = new Hecho(
        "Incendio en zona rural",
        "Se detectó un foco de incendio de gran magnitud en una zona forestal.",
        "Incendio",
        -34.6037,
        -58.3816,
        LocalDate.of(2025, 6, 6)
    );

    fuenteDinamica.subirHecho(incendio);

  }

  @Test
  public void subirHecho_deberiaGuardarElHechoEnElRepositorio() {

    List<Hecho> hechosGuardados = repoFuenteDinamica.getHechos();



    assertFalse(hechosGuardados.isEmpty());
    assertTrue(hechosGuardados.contains(incendio));
  }




  @Test
  public void SeActualizaElHechoConUsuarioRegistrado() throws Exception {
    incendio.setUsuario(true);
    hechoBuilder.setDescripcion("Nueva descripcion test");
    fuenteDinamica.actualizarHecho("Incendio en zona rural", hechoBuilder);
    Hecho hechoNuevo = repoFuenteDinamica.saveUpdate(incendio, hechoBuilder);

    assertTrue(incendio.isUsuario(),"El hecho debe estar marcado como usuario registrado");
    assertEquals( "Nueva descripcion test" ,hechoNuevo.getDescripcion());
    assertNotNull(hechoNuevo,"Se guarda hecho en el repo");

  }
  @Test
  public void NoSeDeberiaActualizarElHechoUsuarioAnonimo()  {

    assertThrows(Exception.class, () ->
        fuenteDinamica.actualizarHecho("Incendio en zona rural", hechoBuilder )

    );


  }



//  @Test
//  public void revisarHecho() {
//    Revision revision = new Revision(Estado.ACEPTADA, "Ninguna", incendio);
//    fuenteDinamica.revisarHecho("Incendio en zona rural", revision);
//    repoSolicitudesRevision.save(revision);
//
//  }
//
//  @Test
//  public void revisarHechoConSugerenciaCambiarCategoria() {
//    Revision revision = new Revision(Estado.ACEPTADA_CON_CAMBIOS, "Cambiar la categoria", incendio);
//    fuenteDinamica.revisarHecho("Incendio en zona rural", revision);
//    repoSolicitudesRevision.save(revision);
//
//  }
}
