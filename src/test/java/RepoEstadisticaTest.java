import org.junit.jupiter.api.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepoEstadisticaTest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoEstadistica repo;

  @BeforeAll
  static void init() {
    // Para probar en memoria
    //emf = Persistence.createEntityManagerFactory("testPU");
    // Para probar en MySqle
    emf = Persistence.createEntityManagerFactory("simple-persistence-unit");

  }

  @AfterAll
  static void close() {
    if (emf != null) emf.close();
  }

  @BeforeEach
  void setUp() {
    em = emf.createEntityManager();
    repo = new RepoEstadistica(em);

    // Se limpia la base de datos
    em.getTransaction().begin();
    em.createNativeQuery("DELETE FROM solicitud").executeUpdate();
    em.createNativeQuery("DELETE FROM estadistica").executeUpdate();
    em.createNativeQuery("DELETE FROM hecho").executeUpdate();
    em.createNativeQuery("DELETE FROM coleccion").executeUpdate();
    em.createNativeQuery("DELETE FROM fuente_dinamica").executeUpdate();
    em.createNativeQuery("DELETE FROM fuente").executeUpdate();
    em.getTransaction().commit();

    em.getTransaction().begin();

    // Fuente dinámica
    FuenteDinamica fuente = new FuenteDinamica();
    em.persist(fuente);

    // Colección base con criterios y repoSolicitudes
    Coleccion coleccion = crearColeccionBase(fuente);
    em.persist(coleccion);

    // Hechos asociados a la fuente
    Hecho robo = new Hecho(
        "Robo en almacén",
        "Un grupo de delincuentes ingresó a un comercio y sustrajo mercadería.",
        "Robo",
        -34.61, -58.38,
        LocalDate.of(2025, 6, 7),
        LocalDate.now(),
        Estado.PENDIENTE
    );
    robo.setFuenteOrigen(fuente);
    em.persist(robo);

    Hecho incendio = new Hecho(
        "Incendio en zona rural",
        "Se detectó un foco de incendio de gran magnitud en una zona forestal.",
        "Incendio",
        -34.6037, -58.3816,
        LocalDate.of(2025, 6, 6),
        LocalDate.now(),
        Estado.PENDIENTE
    );
    incendio.setFuenteOrigen(fuente);
    em.persist(incendio);

    em.getTransaction().commit();
  }

  private Coleccion crearColeccionBase(Fuente fuente) {
    CriterioCategoria criterio1 = new CriterioCategoria("Incendio Forestal");
    LocalDate desde = LocalDate.of(2018, 8, 23);
    LocalDate hasta = LocalDate.of(2018, 9, 25);
    CriterioFecha criterio2 = new CriterioFecha(desde, hasta);

    List<Criterio> criterios = List.of(criterio1, criterio2);

    DetectorDeSpamFiltro deSpamFiltro = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(deSpamFiltro);

    return new Coleccion("Incendios test", "Hechos de prueba", fuente, criterios, repoSolicitudes);
  }

  @AfterEach
  void tearDown() {
    if (em != null) em.close();
  }

//  @Test
//  void testCategoriaConMayorHechos() {
//    EstadisticaRegistro fila = repo.categoriaConMayorHechos(1L);
//    assertNotNull(fila);
//    assertTrue(fila.getValor().equals("Robo") || fila.getValor().equals("Incendio"));
//    assertEquals(1L, (fila.getCantidad()).longValue());
//  }

  @Test
  void testCategoriaConMayorHechos() {
    EstadisticaRegistro registro = repo.categoriaConMayorHechos(1L);
    assertNotNull(registro);
    assertTrue(registro.getValor().equals("Robo") || registro.getValor().equals("Incendio"));
    assertEquals(1, registro.getCantidad());
  }


  @Test
  void testGuardarYLeerEstadistica() {
    EstadisticaRegistro registro = new EstadisticaRegistro();
    registro.setColeccionId(1L);
    registro.setTipo("CATEGORIA_MAYOR_HECHOS");
    registro.setValor("Robo");
    registro.setCantidad(5);
    registro.setFechaActualizacion(java.time.LocalDateTime.now());

    repo.guardarEstadistica(registro);

    EstadisticaRegistro ultima = repo.ultimaCategoriaConMasHechos(1L);
    assertNotNull(ultima);
    assertEquals("Robo", ultima.getValor());
    assertEquals(5, ultima.getCantidad());
  }

  @Test
  void testCategoriasPorColeccion() {
    List<EstadisticaRegistro> categorias = repo.categoriasPorColeccion(1L);
    assertTrue(categorias.stream().anyMatch(c -> c.getValor().equals("Robo")));
    assertTrue(categorias.stream().anyMatch(c -> c.getValor().equals("Incendio")));
  }

  @Test
  void testCantidadSolicitudesSpam() {
    // 1) Obtener la colección creada en setUp y su fuente
    Coleccion coleccion = em.createQuery("from Coleccion", Coleccion.class)
        .setMaxResults(1)
        .getSingleResult();
    Long coleccionId = coleccion.getId();

    // 2) Crear un Hecho asociado a la MISMA fuente de la colección
    Hecho hecho = crearHechoSimple();
    hecho.setFuenteOrigen(coleccion.getFuente());

    // 3) Crear la solicitud marcada como spam (descripcion >= 500)
    DetectorDeSpamFiltro detector = new DetectorDeSpamFiltro();
    RepoSolicitudes repoSolicitudes = new RepoSolicitudes(detector);
    String descripcion = "x".repeat(500);

    Solicitud solicitud = new Solicitud(
        hecho,
        descripcion,
        repoSolicitudes,
        true // es_spam = true
    );

    em.getTransaction().begin();
    em.persist(hecho);
    em.persist(solicitud);
    em.getTransaction().commit();
    em.clear();

    // 4) Llamar con el ID real de la colección
    EstadisticaRegistro registro = repo.cantidadSolicitudesSpam(coleccionId);

    assertNotNull(registro);
    assertEquals("SPAM", registro.getValor());
    assertEquals(1, registro.getCantidad());
  }

  private Hecho crearHechoSimple() {
    return new Hecho("Incendio", "desc", "Incendio Forestal", -0.5, -0.5, LocalDate.now(), LocalDate.now(), Estado.PENDIENTE);
  }



}
