public abstract class Persona {

  public void visualizarHechos(String nombreColeccion) {
    Coleccion coleccion = GestorColecciones.getInstancia().obtenerColeccion(nombreColeccion);
    coleccion.mostrarHechos();
  }

  public void visualizarHechosFiltrados(String nombreColeccion, Criterio criterio) {
    Coleccion coleccion = GestorColecciones.getInstancia().obtenerColeccion(nombreColeccion);
    coleccion.mostrarHechosFiltrados(criterio);
  }

  //  public abstract void subirHecho();

  public void solicitarEliminarHecho(Hecho hecho, String descripcion, GestorSolicitudes gestor) {
    gestor.nuevaSolicitud(new Solicitud(hecho, descripcion));
  }
}