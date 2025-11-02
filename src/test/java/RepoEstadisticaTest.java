import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
import javax.persistence.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepoEstadisticaTest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoEstadistica repo;

  @BeforeAll
  static void init() {
    // Para probar en memoria
    emf = Persistence.createEntityManagerFactory("testPU");
    // Para probar en MySql
//    emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
  }

  @AfterAll
  static void close() {
    if (emf != null) emf.close();
  }

  @BeforeEach
  void setUp() {
    em = emf.createEntityManager();
    repo = new RepoEstadistica(em);

  }

  @AfterEach
  void tearDown() {
    if (em != null) em.close();
  }

  @Test
  void testGuardarYLeerEstadistica() {
    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(1L);
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
    em.getTransaction().begin();

    // 1) Crear fuente y colección
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    CriterioCategoria criterio = new CriterioCategoria("Incendio Forestal");
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    Coleccion coleccion = new Coleccion("Colección test", "Prueba provincias",
        fuente, List.of(criterio), repoSolicitudes);
    em.persist(coleccion);

    // 2) Crear hechos con provincia
    Hecho hecho1 = new Hecho(
        "Robo en almacén",
        "Delincuentes ingresaron a un comercio y sustrajeron mercadería.",
        "Robo",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 12, 30),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    hecho1.setFuenteOrigen(fuente);
    hecho1.setProvincia("Buenos Aires"); // <-- agregar campo provincia
    em.persist(hecho1);

    Hecho hecho2 = new Hecho(
        "Incendio en zona rural",
        "Se detectó un foco de incendio en una zona forestal.",
        "Incendio",
        -31.42, -64.18,
        LocalDateTime.of(2025, 6, 6, 21, 3),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    hecho2.setFuenteOrigen(fuente);
    hecho2.setProvincia("Córdoba"); // <-- agregar provincia distinta
    em.persist(hecho2);

    Hecho hecho3 = new Hecho(
        "Otro incendio",
        "Incendio de gran magnitud.",
        "Incendio",
        -31.44, -64.19,
        LocalDateTime.of(2025, 6, 6, 16, 20),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    hecho3.setFuenteOrigen(fuente);
    hecho3.setProvincia("Córdoba"); // <-- Córdoba ahora queda con 2 hechos
    em.persist(hecho3);

    em.getTransaction().commit();
    em.clear();

    // 3) Ejecutar la query de estadística
    EstadisticaRegistro registro = repo.provinciaConMasHechos(coleccion.getId());

    // 4) Verificaciones
    assertNotNull(registro);
    assertEquals("Córdoba", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }


  // 2) Categorìa con màs hechos reportas
  @Test
  void testCategoriaConMasHechos() {
    em.getTransaction().begin();
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    Coleccion coleccion = new Coleccion(
        "Coleccion test",
        "Prueba categorias",
        fuente,
        List.of(new CriterioCategoria("Robo")),
        repoSolicitudes
    );
    em.persist(coleccion);

    Hecho h1 = new Hecho("Robo 1", "Descripción", "Robo",
        -34.61, -58.38, LocalDateTime.of(2025, 6, 7, 9, 23),
        LocalDateTime.now(), Estado.PENDIENTE);
    h1.setFuenteOrigen(fuente);
    em.persist(h1);

    Hecho h2 = new Hecho("Robo 2", "Descripción", "Robo",
        -34.62, -58.39, LocalDateTime.of(2025, 6, 8, 12, 12),
        LocalDateTime.now(), Estado.PENDIENTE);
    h2.setFuenteOrigen(fuente);
    em.persist(h2);

    Hecho h3 = new Hecho("Incendio", "Descripción", "Incendio",
        -34.63, -58.40, LocalDateTime.of(2025, 6, 9, 2, 34),
        LocalDateTime.now(), Estado.PENDIENTE);
    h3.setFuenteOrigen(fuente);
    em.persist(h3);

    em.getTransaction().commit();
    em.clear();

    EstadisticaRegistro registro = repo.categoriaConMayorHechos(coleccion.getId());

    assertNotNull(registro);
    assertEquals("Robo", registro.getValor()); // categoria mayoritaria
    assertEquals(2, registro.getCantidad()); // Se esperan 2 (Robos) vs 1 (incendio)

  }

  // 3) ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
  @Test
  void testProvinciaConMasHechosPorCategoria() {
    em.getTransaction().begin();
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    Coleccion coleccion = new Coleccion(
        "Coleccion test",
        "Prueba provincia x categoria",
        fuente,
        List.of(new CriterioCategoria("Incendio")),
        repoSolicitudes
    );
    em.persist(coleccion);

    // Generamos algunos hechos de incendio en distintas provincas
    Hecho h1 = new Hecho("Incendio chico", "desc", "Incendio",
        -34.61, -58.38, LocalDateTime.of(2025, 6, 7, 8, 43),
        LocalDateTime.now(), Estado.PENDIENTE);
    h1.setFuenteOrigen(fuente);
    h1.setProvincia("Buenos Aires");
    em.persist(h1);

    Hecho h2 = new Hecho("Incendio grande", "desc", "Incendio",
        -31.42, -64.18, LocalDateTime.of(2025, 6, 6, 6, 6),
        LocalDateTime.now(), Estado.PENDIENTE);
    h2.setFuenteOrigen(fuente);
    h2.setProvincia("Córdoba");
    em.persist(h2);

    Hecho h3 = new Hecho("Otro incendio", "desc", "Incendio",
        -31.44, -64.19, LocalDateTime.of(2025, 6, 6, 7, 7),
        LocalDateTime.now(), Estado.PENDIENTE);
    h3.setFuenteOrigen(fuente);
    h3.setProvincia("Córdoba"); // Córdoba queda con 2
    em.persist(h3);

    em.getTransaction().commit();
    em.clear();

    EstadisticaRegistro registro = repo.provinciaConMasHechosPorCategoria(
        coleccion.getId(), "Incendio");
    assertNotNull(registro);
    assertEquals("Córdoba", registro.getValor());
    assertEquals(2, registro.getCantidad()); // Cantidad de incendios en Córdoba
  }

  // 4) ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
  @Test
  void testHoraConMayorCantidadDeHechosPorCategoria() {
    em.getTransaction().begin();
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(new DetectorDeSpamFiltro());
    Coleccion coleccion = new Coleccion(
        "Coleccion test",
        "Prueba hechos x categoria por hora",
        fuente,
        List.of(new CriterioCategoria("Incendio")),
        repoSolicitudes
    );
    em.persist(coleccion);

    // Hecho a las 10
    Hecho h1 = new Hecho("Incendio mañana", "desc", "Incendio",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 10, 40),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    h1.setFuenteOrigen(fuente);
    em.persist(h1);

    // Hecho 15hs
    Hecho h2 = new Hecho("Incendio tarde", "desc", "Incendio",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 15, 30),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    h2.setFuenteOrigen(fuente);
    em.persist(h2);

    // Hecho 20hs
    Hecho h3 = new Hecho("Incendio noche", "desc", "Incendio",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 20, 30),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    h3.setFuenteOrigen(fuente);
    em.persist(h3);

    // Hecho 20hs
    Hecho h4 = new Hecho("Incendio noche", "desc", "Incendio",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 20, 50),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    h4.setFuenteOrigen(fuente);
    em.persist(h4);

    em.getTransaction().commit();
    em.clear();

    EstadisticaRegistro registro = repo.horaConMasHechosPorCategoria(coleccion.getId(),
        "Incendio");
    assertNotNull(registro);
    assertEquals("20", registro.getValor());
    assertEquals(2, registro.getCantidad());
  }

  // 5) ¿Cuantas solicitudes de eliminación son Spam?
  @Test
  void testCantidadSolicitudesSpam() {
    // 1) Crear fuente y colección
    em.getTransaction().begin();
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    Coleccion coleccion = new Coleccion(
        "Incendios test",
        "Hechos de prueba",
        fuente,
        List.of(new CriterioCategoria("Incendio Forestal")),
        new RepoSolicitudes(new DetectorDeSpamFiltro())
    );
    em.persist(coleccion);
    em.getTransaction().commit();

    // 2) Crear hecho asociado a la misma fuente
    Hecho hecho = new Hecho(
        "Robo en almacén",
        "Un grupo de delincuentes ingresó a un comercio y sustrajo mercadería.",
        "Robo",
        -34.61, -58.38,
        LocalDateTime.of(2025, 6, 7, 4, 1),
        LocalDateTime.now(),
        Estado.PENDIENTE
    );
    hecho.setFuenteOrigen(fuente);

    // 3) Crear solicitud marcada como spam
    String descripcion = "x".repeat(500);
    Solicitud solicitud = new Solicitud(
        hecho,
        descripcion,
        new RepoSolicitudes(new DetectorDeSpamFiltro()),
        true // es_spam
    );

    em.getTransaction().begin();
    em.persist(hecho);
    em.persist(solicitud);
    em.getTransaction().commit();
    em.clear();

    // 4) Ejecutar query de estadística
    EstadisticaRegistro registro = repo.cantidadSolicitudesSpam(coleccion.getId());

    // 5) Assertions
    assertNotNull(registro);
    assertEquals("SPAM", registro.getValor());
    assertEquals(1, registro.getCantidad());
  }
}
