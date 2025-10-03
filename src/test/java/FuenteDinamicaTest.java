import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
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
        LocalDateTime.of(2025, 6, 6,10,5),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );


  }

  @Test
  public void testSubirHecho() {

    fuenteDinamica.subirHecho(incendio);


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
        LocalDateTime.of(2025, 6, 1,10,5),
        LocalDateTime.now().minusDays(8),
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
  public void Revision_HechoEsAceptadoConSugerencias() throws Exception {
    incendio.setUsuario(usuario);
    usuario.registrarse();
    fuenteDinamica.subirHecho(incendio);
    hechoBuilder.setDescripcion("nueva descripcion");
    hechoBuilder.setCategoria("Nueva categoria");

    AceptarConSugerencia aceptarConSugerencia = new AceptarConSugerencia(fuenteDinamica,hechoBuilder,repoFuenteDinamica,usuario);
    fuenteDinamica.revisarSolicitud(incendio,aceptarConSugerencia);
    Hecho hechoActualizado = repoFuenteDinamica.findByTitulo(incendio.getTitulo());

    assertEquals("nueva descripcion", hechoActualizado.getDescripcion());
    assertEquals("Nueva categoria",hechoActualizado.getCategoria());
    assertTrue(hechoActualizado.estaDentroDePlazoDeEdicion());
    assertNotNull(hechoActualizado.getTitulo());
    assertTrue(usuario.estaRegistrado,"El usuario esta registrado");

  }
  @Test
  public void Revision_Hecho_Aceptado() throws Exception {
    Aceptar aceptar = new Aceptar(repoFuenteDinamica);

    fuenteDinamica.revisarSolicitud(incendio,aceptar);

    assertEquals(1,repoFuenteDinamica.getHechos().size());
    assertEquals(0, repoSolicitudesRevision.getRevisiones().size());
  }

@Test
  public void Revision_Hecho_Rechazado() throws Exception {
    Rechazar rechazar = new Rechazar(repoFuenteDinamica);

  fuenteDinamica.revisarSolicitud(incendio,rechazar);
  assertEquals(0,repoFuenteDinamica.getHechos().size());
  assertEquals(0, repoSolicitudesRevision.getRevisiones().size());
  assertEquals("RECHAZADA", incendio.getEstado().name());
}

  //test para subir un hecho y persistr en mysql
  @Test
  public void subirHecho () throws Exception {
    Aceptar aceptar = new Aceptar(repoFuenteDinamica);
    fuenteDinamica.revisarSolicitud(incendio,aceptar);
  }

}



