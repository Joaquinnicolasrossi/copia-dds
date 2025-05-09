import java.time.LocalDate;

public class Main {

  public static void main(String[] args) {

    System.out.println("===== MetaMapa - Prueba General =====");

    // ===============================
    // 1. Admin crea una coleccion desde un archivo estatico
    // ===============================
    Administrador admin = new Administrador();

    // Crear un criterio
    Criterio criterio = new CriterioPorCategoria("incendio");

    // Crear una coleccion con el CSV
    String rutaCsv = "src/resources/fires-all.csv";
    String tituloColeccion = "Incendios Argentina";
    String descripcion = "Coleccion de incendios registrados desde una fuente estatica";

    admin.crearColeccion(tituloColeccion, descripcion, rutaCsv, criterio);
    System.out.println("Coleccion creada: " + tituloColeccion);

    // ===============================
    // 2. Usuario visualizador navega los hechos
    // ===============================

    // Crear un usuario visualizador (con datos de registro)
    Registro registro = new Registro("Jose", "Gonzalez", 33);
    Usuario visualizador = new Usuario(new Visualizador(), registro);

    System.out.println("\nUsuario registrado: " + registro.getNombre() + " "
        + registro.getApellido()
        + " \nEdad: " + registro.getEdad() + " \nRol: "
        + visualizador.rol.getClass().getSimpleName());

    System.out.println("\nHechos visibles (por criterio de coleccion");
    visualizador.visualizarHechos(tituloColeccion);

    // ===============================
    // 3. Usuario visualiza los hechos filtrador por
    // ===============================
    Criterio criterioTitulo = new CriterioPorTitulo("córdoba");
    System.out.println("\n--- Hechos filtrador por tituloq ue contiene cordoba ---");
    visualizador.visualizarHechosFiltrados(tituloColeccion, criterioTitulo);

    // ===============================
    // 4. Usuario visualiza los hechos filtrador por fecha
    // ===============================
    Criterio criterioFecha = new CriterioPorFecha(
        LocalDate.of(2023, 1, 1),
        LocalDate.of(2023, 12, 31)
    );

    System.out.println("\n--- Hechos filtrador por fecha (2023)---");
    visualizador.visualizarHechosFiltrados(tituloColeccion, criterioFecha);

    // ===============================
    // 5. Contribuyente solicita eliminacion de un hecho
    // ===============================
    Registro registroContribuyente = new Registro("Carla", "Rojas", 23);
    Usuario contribuyente = new Usuario(new Contribuyente(), registroContribuyente);

    Coleccion coleccion = GestorColecciones.getInstancia().obtenerColeccion(tituloColeccion);
    Hecho hechoaEliminar = coleccion.fuente.extraerHechos().get(0);

    GestorSolicitudes gestor = new GestorSolicitudes();

    contribuyente.solicitarEliminarHecho(hechoaEliminar,
        "Este hecho esta mal geolocalizado", gestor);
    System.out.println("\nCarlos solicito eliminar un hecho.");

    // ===============================
    // 6. Admin acepta solicitud --> Para este tenemos que implementar un nuevo metodo
    // ===============================
    //Solicitud solicitud = gestor.obtenerSolicitudes().get(0);
    //System.out.println("\nAdmin acepto la solicitud.");

    // ===============================
    // 7. Verificar estado del hecho
    // ==============================
    System.out.println("\n Estado del hecho");
    System.out.println("Titulo: " + hechoaEliminar.getTitulo());
    System.out.println("¿Esta eliminado? " + hechoaEliminar.isEliminado());

    System.out.println("\n==== Fin de la ejecucion ====");

  }

}

