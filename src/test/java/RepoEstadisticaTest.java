import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RepoEstadisticaTest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoEstadistica repo;
  private RepoProvincias repoProvincias;

  @BeforeAll
  static void init() {
    emf = Persistence.createEntityManagerFactory("testPU");
  }

  @AfterAll
  static void close() {
    if (emf != null) {
      emf.close();
    }
  }

  @BeforeEach
  void setUp() {
    em = emf.createEntityManager();
    repo = new RepoEstadistica(em);
    repoProvincias = new RepoProvincias(em);
    em.getTransaction().begin();
  }

  @AfterEach
  void tearDown() {
    if (em != null) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      em.close();
    }
  }

  @Test
  void testGuardarYLeerEstadistica() {
    Coleccion coleccion = new Coleccion();
    em.persist(coleccion);

    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccion(coleccion);
    registro.setTipo("CATEGORIA_MAYOR_HECHOS");
    registro.setValor("Robo");
    registro.setCantidad(5);
    registro.setFecha_actualizacion(LocalDateTime.now());
    registro.setVisiblePublico(true);

    repo.guardarEstadistica(registro);

    List<EstadisticaRegistro> resultados = repo.buscarPorTipo("CATEGORIA_MAYOR_HECHOS");
    assertFalse(resultados.isEmpty());

    EstadisticaRegistro ultima = resultados.get(resultados.size() - 1);
    assertEquals("Robo", ultima.getValor());
    assertEquals(5, ultima.getCantidad());
  }

  // 1) De una colección, ¿en que provincia se agrupan
  // la mayor cantidad de hechos reportados?

  @Test
  void testProvinciaConMasHechos() {
    Coleccion coleccion;

    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    CriterioCategoria criterio = new CriterioCategoria("Incendio Forestal");
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    coleccion = new Coleccion("Colección test", "Prueba provincias",
        fuente, List.of(criterio), repoSolicitudes);
    em.persist(coleccion);

    Hecho hecho1 = new Hecho.HechoBuilder()
        .setTitulo("Robo en almacén")
        .setProvincia("Buenos Aires")
        .build(repoProvincias);

    hecho1.setFuenteOrigen(fuente);
    em.persist(hecho1);

    Hecho hecho2 = new Hecho.HechoBuilder()
        .setTitulo("Incendio en zona rural")
        .setProvincia("Córdoba")
        .build(repoProvincias);

    hecho2.setFuenteOrigen(fuente);
    em.persist(hecho2);

    Hecho hecho3 = new Hecho.HechoBuilder()
        .setTitulo("Otro incendio")
        .setProvincia("Córdoba")
        .build(repoProvincias);

    hecho3.setFuenteOrigen(fuente);
    em.persist(hecho3);

    EstadisticaRegistro registro = repo.provinciaConMasHechos(coleccion.getId());

    assertNotNull(registro);
    assertEquals("Córdoba", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }


  // 2) Categorìa con màs hechos reportas
  @Test
  void testCategoriaConMasHechos() {
    Coleccion coleccion;

    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    coleccion = new Coleccion(
        "Coleccion test",
        "Prueba categorias",
        fuente,
        List.of(new CriterioCategoria("Robo")),
        repoSolicitudes
    );
    em.persist(coleccion);

    Hecho h1 = new Hecho.HechoBuilder()
        .setTitulo("Robo 1")
        .setCategoria("Robo")
        .build(repoProvincias);
    h1.setFuenteOrigen(fuente);
    em.persist(h1);

    Hecho h2 = new Hecho.HechoBuilder()
        .setTitulo("Robo 2")
        .setCategoria("Robo")
        .build(repoProvincias);
    h2.setFuenteOrigen(fuente);
    em.persist(h2);

    Hecho h3 = new Hecho.HechoBuilder()
        .setTitulo("Incendio")
        .setCategoria("Incendio")
        .build(repoProvincias);
    h3.setFuenteOrigen(fuente);
    em.persist(h3);

    EstadisticaRegistro registro = repo.categoriaConMayorHechos(coleccion.getId());

    assertNotNull(registro);
    assertEquals("Robo", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }

  // 3) ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
  @Test
  void testProvinciaConMasHechosPorCategoria() {
    Coleccion coleccion;

    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    coleccion = new Coleccion(
        "Coleccion test",
        "Prueba provincia x categoria",
        fuente,
        List.of(new CriterioCategoria("Incendio")),
        repoSolicitudes
    );
    em.persist(coleccion);

    Hecho h1 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setProvincia("Buenos Aires")
        .build(repoProvincias);
    h1.setFuenteOrigen(fuente);
    em.persist(h1);

    Hecho h2 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setProvincia("Córdoba")
        .build(repoProvincias);
    h2.setFuenteOrigen(fuente);
    em.persist(h2);

    Hecho h3 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setProvincia("Córdoba")
        .build(repoProvincias);
    h3.setFuenteOrigen(fuente);
    em.persist(h3);

    EstadisticaRegistro registro = repo.provinciaConMasHechosPorCategoria(
        coleccion.getId(), "Incendio");
    assertNotNull(registro);
    assertEquals("Córdoba", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }

  // 4) ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
  @Test
  void testHoraConMayorCantidadDeHechosPorCategoria() {
    Coleccion coleccion;

    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    coleccion = new Coleccion(
        "Coleccion test",
        "Prueba hechos x categoria por hora",
        fuente,
        List.of(new CriterioCategoria("Incendio")),
        repoSolicitudes
    );
    em.persist(coleccion);

    Hecho h1 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setFecha(LocalDateTime.of(2025, 6, 7, 10, 40))
        .build(repoProvincias);
    h1.setFuenteOrigen(fuente);
    em.persist(h1);

    Hecho h2 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setFecha(LocalDateTime.of(2025, 6, 7, 15, 30))
        .build(repoProvincias);
    h2.setFuenteOrigen(fuente);
    em.persist(h2);

    Hecho h3 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setFecha(LocalDateTime.of(2025, 6, 7, 20, 30))
        .build(repoProvincias);
    h3.setFuenteOrigen(fuente);
    em.persist(h3);

    Hecho h4 = new Hecho.HechoBuilder()
        .setCategoria("Incendio")
        .setFecha(LocalDateTime.of(2025, 6, 7, 20, 50))
        .build(repoProvincias);
    h4.setFuenteOrigen(fuente);
    em.persist(h4);

    EstadisticaRegistro registro = repo.horaConMasHechosPorCategoria(coleccion.getId(),
        "Incendio");
    assertNotNull(registro);
    assertEquals("20", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }

  // 5) ¿Cuantas solicitudes de eliminación son Spam?
  @Test
  void testCantidadSolicitudesSpam() {
    Coleccion coleccion;

    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    coleccion = new Coleccion(
        "Incendios test",
        "Hechos de prueba",
        fuente,
        List.of(new CriterioCategoria("Incendio Forestal")),
        new RepoSolicitudes(new DetectorDeSpamFiltro())
    );
    em.persist(coleccion);

    Hecho hecho = new Hecho.HechoBuilder()
        .setTitulo("Robo en almacén")
        .setCategoria("Robo")
        .build(repoProvincias);

    hecho.setFuenteOrigen(fuente);
    em.persist(hecho);

    String descripcion = "x".repeat(500);
    Solicitud solicitud = new Solicitud(
        hecho,
        descripcion,
        new RepoSolicitudes(new DetectorDeSpamFiltro()),
        true // es_spam
    );
    em.persist(solicitud);

    EstadisticaRegistro registro = repo.cantidadSolicitudesSpam(coleccion.getId());

    assertNotNull(registro);
    assertEquals("SPAM", registro.getValor());
    assertEquals(1, registro.getCantidad());
  }
}