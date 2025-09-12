import java.time.LocalDateTime;
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
    registro.setFecha_actualizacion(LocalDateTime.now());

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
        LocalDate.of(2025, 6, 7),
        LocalDate.now(),
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
