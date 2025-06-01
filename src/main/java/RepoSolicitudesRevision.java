import java.util.ArrayList;
import java.util.List;

public class RepoSolicitudesRevision {
  List<Revision> revisiones = new ArrayList<>();

  public void save(Revision revision){
    revisiones.add(revision);
  }
}