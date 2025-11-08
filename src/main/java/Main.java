import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class Main {
  public static void main(String[] args) {
    configurePersistence();
    new Server().start(); // solo inicia Javalin
  }

  private static void configurePersistence() {
    try {
      String dbUrl = System.getenv("DATABASE_URL");
      if (dbUrl != null) {
        System.out.println("Usando configuración de deploy Railway");
        java.net.URI uri = new java.net.URI(dbUrl);
        String[] userInfo = uri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];
        String jdbcUrl = "jdbc:mysql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath() + "?useSSL=false";

        java.util.Properties props = new java.util.Properties();
        props.setProperty("hibernate.connection.url", jdbcUrl);
        props.setProperty("hibernate.connection.username", username);
        props.setProperty("hibernate.connection.password", password);
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        props.setProperty("hibernate.dialect.storage_engine", "innodb");

        // Inicializa una única vez la unidad de persistencia
        WithSimplePersistenceUnit.configure(cfg -> cfg.putAll(props));
      } else {
        System.out.println("Modo local: usando persistence.xml");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
