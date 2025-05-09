import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


class AdministradorTest {

  @Test
  public void testAlgo() {
    // código de prueba
  }

//   private Administrador admin;
//
//   @BeforeEach
//   void setUp() {
//     admin = new Administrador();
//   }
//
//   @Test
//   void crearColeccion() {
//   }
//
//   @Test
//   void aceptarSolicitud() {
//   }
//
//   @Test
//   void rechazarSolicitud() {
//   }

//   @Test
//   /*
//   * Deberia leer el csv y luego crear la colección
//   * */
//   void importarHechosDesdeCsv() throws IOException {
//
//     // Obtener la ruta del archivo csv dentro de resources
//     URL recurso = getClass().getClassLoader().getResource("SAT-MV-BU_2017-2023.csv");
//     String rutaCsv = recurso.getPath();
//
//     // Fuente de prueba que devuelve un hecho simulado
//     Fuente fuente = new Fuente(rutaCsv);
//
//     Criterio criterio = new Criterio("", "", null, null);
//
//     List<Hecho> hechos = admin.importarHechos("Hechos 2023", fuente, criterio);
//     System.out.println("Hechos: " + hechos.size());
//
//     // Validaciones
//
//     // Se devuelve 1 hecho
//     assertEquals(0, hechos.size());
//     // Se creó una colección
//     assertEquals(admin.colecciones.size(), 1);
//     // El título es correcto
//     assertEquals("Hechos 2023", admin.colecciones.get(0).titulo);
//
//
//   }
}