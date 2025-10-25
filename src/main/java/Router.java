import io.javalin.Javalin;

public interface Router {
  void configure(Javalin app);
}