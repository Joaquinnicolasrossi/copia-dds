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
    emf = Persistence.createEntityManagerFactory("testPU");
    // Para probar en MySql
    //emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
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

  @Test
  void testCategoriaConMayorHechos() {
    Object[] fila = repo.categoriaConMayorHechos(1L);
    assertNotNull(fila);
    assertTrue(fila[0].equals("Robo") || fila[0].equals("Incendio"));
    assertEquals(1L, ((Number) fila[1]).longValue());
  }

  @Test
  void testGuardarYLeerEstadistica() {
    repo.guardarEstadistica(1L, "CATEGORIA_MAYOR_HECHOS", "Robo", 5);
    String ultima = repo.ultimaCategoriaConMasHechos(1L);
    assertEquals("Robo", ultima);
  }

  @Test
  void testCategoriasPorColeccion() {
    List<String> categorias = repo.categoriasPorColeccion(1L);
    assertTrue(categorias.contains("Robo"));
    assertTrue(categorias.contains("Incendio"));
  }
}
