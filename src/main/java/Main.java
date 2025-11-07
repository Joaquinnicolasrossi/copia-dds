import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class Main {

  private static void configurePersistence() {
    try {
      // 1. Leer la variable de entorno de Render
      String databaseUrl = System.getenv("DATABASE_URL");

      if (databaseUrl != null) {
        System.out.println("Configurando persistencia con DATABASE_URL...");
        URI dbUri = new URI(databaseUrl);

        // 2. Parsear la URL: mysql://user:pass@host:port/db
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        // 3. Crear las propiedades para sobreescribir el persistence.xml
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.url", dbUrl);
        properties.setProperty("javax.persistence.jdbc.user", username);
        properties.setProperty("javax.persistence.jdbc.password", password);
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        // 4. Configurar la biblioteca de la cátedra
        WithSimplePersistenceUnit.configure(
            config -> config.putAll(properties)
        );
        System.out.println("Persistencia configurada para deploy.");
      } else {
        System.out.println("DATABASE_URL no encontrada. Usando persistence.xml (local).");
      }
    } catch (Exception e) {
      System.err.println("ERROR CONFIGURANDO PERSISTENCIA: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      // 6. LLAMAR A LA CONFIGURACIÓN ANTES DE INICIAR EL SERVIDOR
      configurePersistence();

      // Iniciar el servidor (como antes)
      new Server().start();

    } catch (Exception e) {
      System.err.println("Error al iniciar el servidor: " + e.getMessage());
      e.printStackTrace();
    }
  }
}