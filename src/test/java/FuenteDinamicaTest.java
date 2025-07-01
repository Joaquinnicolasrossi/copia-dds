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
  private Usuario usuario;
  Hecho.HechoBuilder hechoBuilder = new Hecho.HechoBuilder();

  @BeforeEach
  void setUp() throws Exception {
    repoFuenteDinamica = new RepoFuenteDinamica();
    repoSolicitudesRevision = new RepoSolicitudesRevision();
    fuenteDinamica = new FuenteDinamica(repoFuenteDinamica, repoSolicitudesRevision);
    usuario = new Usuario(fuenteDinamica);

    incendio = new Hecho(
        "Incendio en zona rural",
        "Se detectó un foco de incendio de gran magnitud en una zona forestal.",
        "Incendio",
        -34.6037,
        -58.3816,
        LocalDate.of(2025, 6, 6),
        LocalDate.now(),
        Estado.PENDIENTE
    );


  }

  @Test
  public void testSubirHecho() {

    fuenteDinamica.subirHecho(incendio);

    assertEquals(1, repoFuenteDinamica.getHechos().size());
    assertEquals("Incendio en zona rural", incendio.getTitulo());
    assertEquals("Se detectó un foco de incendio de gran magnitud en una zona forestal.", incendio.getDescripcion());
    assertEquals("Incendio", incendio.getCategoria());
    assertEquals(-34.6037, incendio.getLatitud());
    assertEquals(-58.3816, incendio.getLongitud());
    assertEquals(Estado.PENDIENTE, incendio.getEstado());
    assertNotNull(incendio.getFechaCarga());


  }

  @Test
  public void testActualizarHechoUsuarioRegistrado() throws Exception {


    incendio.setUsuario(usuario);
    usuario.registrarse();
    fuenteDinamica.subirHecho(incendio);
    hechoBuilder.setDescripcion("nueva descripcion");
    fuenteDinamica.actualizarHecho(incendio, hechoBuilder, usuario);

    Hecho hechoActualizado = repoFuenteDinamica.findByTitulo(incendio.getTitulo());

    assertEquals("nueva descripcion", hechoActualizado.getDescripcion());
    assertTrue(hechoActualizado.estaDentroDePlazoDeEdicion());
    assertNotNull(hechoActualizado.getTitulo());
    assertTrue(usuario.estaRegistrado,"El usuario esta registrado");
  }

  @Test
  public void testActualizarHechoUsuarioUsuarioNoRegistrado() {

    fuenteDinamica.subirHecho(incendio);

    Exception ex = assertThrows(Exception.class, () ->
        fuenteDinamica.actualizarHecho(incendio, hechoBuilder, usuario)
    );
    assertEquals("El usuario no esta registrado", ex.getMessage());
  }

  @Test
  public void testActualizarHechoFueraDePlazo() {
    incendio = new Hecho(
        "Incendio en zona rural",
        "Se detectó un foco de incendio de gran magnitud en una zona forestal.",
        "Incendio",
        -34.6037,
        -58.3816,
        LocalDate.of(2025, 6, 1),
        LocalDate.now().minusDays(8),
        Estado.PENDIENTE
    );
    incendio.setUsuario(usuario);
    usuario.registrarse();

    fuenteDinamica.subirHecho(incendio);

    Exception ex = assertThrows(Exception.class, () ->
        fuenteDinamica.actualizarHecho(incendio, hechoBuilder, usuario)
    );

    assertEquals("El plazo para modificar este hecho ha expirado.", ex.getMessage());
  }
  @Test
  public void Revision_HechoEsAceptadoConSugerencias() {

    AceptarConSugerencia aceptarConSugerencia = new AceptarConSugerencia();
    fuenteDinamica.revisarSolicitud(incendio, aceptarConSugerencia, "cambiar coordenadas de ubicacion");

    assertEquals(0, repoSolicitudesRevision.getRevisiones().size());
    assertEquals("ACEPTADA_CON_CAMBIOS", incendio.getEstado().name());

  }


}

