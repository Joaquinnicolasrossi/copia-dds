import java.util.List;

public class FuenteMetamapa implements FuenteDeDatos {

  private ClienteMetaMapa cliente;

  public FuenteMetamapa(ClienteMetaMapa cliente) {
    this.cliente = cliente;
  }

  @Override
  public List<Hecho> extraerHechos() {
    return cliente.getHechos();
  }
}