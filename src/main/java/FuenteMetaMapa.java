import java.util.List;

public class FuenteMetaMapa implements FuenteDeDatos {

  private ClienteMetaMapa cliente;

  public FuenteMetaMapa(ClienteMetaMapa cliente) {
    this.cliente = cliente;
  }

  @Override
  public List<Hecho> extraerHechos() {
    try {
      return cliente.getHechos();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}