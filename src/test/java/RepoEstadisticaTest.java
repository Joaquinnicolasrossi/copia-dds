import org.junit.jupiter.api.*;
import javax.persistence.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RepoEstadisticaTest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepoEstadistica repo;

  @BeforeAll
  static void init() {
    emf = Persistence.createEntityManagerFactory("testPU");
  }

  @AfterAll
  static void close() {
    if (emf != null) emf.close();
  }

  @BeforeEach
  void setUp() {
    em = emf.createEntityManager();
    repo = new RepoEstadistica(em);

    em.getTransaction().begin();
    em.createNativeQuery("DELETE FROM estadistica").executeUpdate();
    em.createNativeQuery("DELETE FROM hecho").executeUpdate();
    em.createNativeQuery("DELETE FROM coleccion").executeUpdate();
    em.getTransaction().commit();

    // Insertar datos base
    em.getTransaction().begin();
    em.createNativeQuery("INSERT INTO coleccion (id, fuente_id) VALUES (1, 100)").executeUpdate();
    em.createNativeQuery("INSERT INTO hecho (id, categoria, provincia, fecha, fuente_id)" +
        " VALUES (1, 'Robo', 'Buenos Aires', CURRENT_TIMESTAMP, 100)").executeUpdate();
    em.createNativeQuery("INSERT INTO hecho (id, categoria, provincia, fecha, fuente_id) " +
        "VALUES (2, 'Incendio', 'Córdoba', CURRENT_TIMESTAMP, 100)").executeUpdate();
    em.getTransaction().commit();
  }

  @AfterEach
  void tearDown() {
    if (em != null) em.close();
  }

  @Test
  void testCategoriaConMayorHechos() {
    Object[] fila = repo.categoriaConMayorHechos(1L);
    assertNotNull(fila);
    assertEquals("Robo", fila[0]);
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
