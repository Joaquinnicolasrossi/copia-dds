import java.util.List;

public class DetectorDeSpamFiltro implements DetectorDeSpam {
  List<String> palabrasSpam = List.of("gratis", "click aqui", "gana dinero"); //TODO faltan palbras

  @Override
  public boolean esSpam(String texto) {
    return palabrasSpam.stream().anyMatch(texto.toLowerCase()::contains);
  }
}
