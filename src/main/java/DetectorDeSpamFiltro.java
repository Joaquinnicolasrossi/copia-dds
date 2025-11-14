import java.util.List;

public class DetectorDeSpamFiltro implements DetectorDeSpam {
  List<String> palabrasSpam = List.of(
      "gratis", "gratuito", "oferta", "descuento", "promocion", "rebaja",
      "liquidacion", "barato", "regalo", "sorteo", "ganador", "premio",
      "gana dinero", "dinero facil", "millonario", "ingresos", "ganar plata",
      "efectivo", "prestamo", "credito facil", "sin interes",
      "urgente", "ahora", "ultimo momento", "rapido", "inmediato",
      "solo hoy", "ultimas unidades", "por tiempo limitado",
      "click aqui", "haz click", "clickea", "entra aqui", "mira esto",
      "no lo vas a creer", "increible", "asombroso", "sorprendente",
      "viagra", "cialis", "pastillas", "adelgazar", "bajar peso",
      "dieta milagrosa", "cura", "tratamiento",
      "trabajo desde casa", "multinivel", "inversion", "negocio propio",
      "se tu propio jefe", "libertad financiera",
      "casino", "apuesta", "poker", "loteria", "bingo", "ruleta",
      "compra ya", "aprovecha", "limitado", "exclusivo", "garantizado",
      "100% seguro", "sin riesgo", "prueba gratis", "registrate",
      "suscribete", "whatsapp", "contactame"
  );

  @Override
  public boolean esSpam(String texto) {
    // Normalizamos
    String textoNormalizado = texto.toLowerCase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n");

    return palabrasSpam.stream().anyMatch(textoNormalizado::contains);
  }
}
