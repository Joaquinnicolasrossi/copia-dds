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
    String tituloColeccion = "Incendios en España";
    String descripcion = "Coleccion de incendios registrados en españa desde una fuente estatica";

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
    Criterio criterioTitulo = new CriterioPorTitulo("rayo");
    System.out.println("\n--- Hechos filtrados por titulo que contiene la palabra rayo ---");
    visualizador.visualizarHechosFiltrados(tituloColeccion, criterioTitulo);

    // ===============================
    // 4. Usuario visualiza los hechos filtrador por fecha
    // ===============================
    Criterio criterioFecha = new CriterioPorFecha(
        LocalDate.of(2020, 1, 1),
        LocalDate.of(2020, 12, 31)
    );

    System.out.println("\n--- Hechos filtrados por fecha (año 2020)---");
    visualizador.visualizarHechosFiltrados(tituloColeccion, criterioFecha);

    // ===============================
    // 5. Contribuyente solicita eliminacion de un hecho
    // ===============================
    Registro registroContribuyente = new Registro("Carla", "Rojas", 23);
    Usuario contribuyente = new Usuario(new Contribuyente(), registroContribuyente);

    Coleccion coleccion = GestorColecciones.getInstancia().obtenerColeccion(tituloColeccion);
    Hecho hechoaEliminar = coleccion.fuente.extraerHechos().get(0);
    /* A corregir:
       El hecho no deberia estar en memoria, hay que buscar otra forma de eliminarlo
       sin mantenerlo en memoria, quiza con alguna flag o algo cuando se lee. (Para que directamente
       no se extraiga de la fuente, pero para los propositos de la demo lo hacemos asi).
     */

    RepoSolicitudes gestor = new RepoSolicitudes();
    // La solicitud debe tener mas de 500 caracteres
    contribuyente.solicitarEliminarHecho(hechoaEliminar,
        "Solicito la eliminación de este hecho de incendio forestal porque,"
            + " luego de una revisión más detallada, se comprobó que se trató de una falsa alarma. "
            + "La información fue registrada inicialmente a partir de un reporte preliminar, "
            + "pero los organismos responsables confirmaron que no hubo fuego en la zona indicada. "
            + "Mantener este hecho en la base de datos podría "
            + "llevar a errores en los análisis estadísticos "
            + "y a una interpretación equivocada del riesgo ambiental en esa región. "
            + "Es importante conservar la calidad y precisión de los datos almacenados.", gestor);
    System.out.println("\nCarlos solicito eliminar un hecho.");

    // ===============================
    // 6. Admin acepta solicitud --> Para este tenemos que implementar un nuevo metodo
    // ===============================
    Solicitud solicitud = gestor.getSolicitudes().get(0);
    admin.aceptarSolicitud(solicitud, gestor);
    System.out.println("\nAdmin acepto la solicitud.");

    // ===============================
    // 7. Verificar estado del hecho
    // ==============================
    System.out.println("\n Estado del hecho");
    System.out.println("Titulo: " + hechoaEliminar.getTitulo());
    System.out.println("¿Esta eliminado? " + hechoaEliminar.isEliminado());

    System.out.println("\n==== Fin de la ejecucion ====");

  }

}

