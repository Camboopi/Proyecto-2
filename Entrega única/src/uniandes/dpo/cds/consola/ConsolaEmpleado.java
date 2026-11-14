package uniandes.dpo.cds.consola;

import uniandes.dpo.cds.modelo.*;
import uniandes.dpo.cds.persistencia.ManejadorPersistencia;

import java.util.List;

public class ConsolaEmpleado {
    private ClubDeportivoSeneca club;
    private ManejadorPersistencia persistencia;
    private LectorConsola lector;
    private Empleado empleadoActual;

    public ConsolaEmpleado() {
        this.persistencia = new ManejadorPersistencia("data");
        this.club = new ClubDeportivoSeneca();
        this.persistencia.cargarTodo(this.club);
        this.lector = new LectorConsola();
    }

    public static void main(String[] args) {
        ConsolaEmpleado consola = new ConsolaEmpleado();
        consola.ejecutar();
    }

    public void ejecutar() {
        System.out.println("==================================================================");
        System.out.println("   CLUB DEPORTIVO SÉNECA (CDS) - CONSOLA DE EMPLEADOS");
        System.out.println("==================================================================");
        System.out.println("Datos cargados automaticamente desde la carpeta 'data/'.");
        System.out.println("Empleados precargados: profe_juan / profe_marta / fisio_laura (clave: pass123)");

        if (!autenticarEmpleado()) {
            return;
        }

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = lector.leerEntero("Seleccione una opcion: ", 0, 8);
            switch (opcion) {
                case 1:
                    consultarMisDatosYTurnos();
                    break;
                case 2:
                    planearSesionDeEntrenamiento();
                    break;
                case 3:
                    registrarAsistenciaYEvaluacion();
                    break;
                case 4:
                    solicitarCambioDeTurno();
                    break;
                case 5:
                    sugerirProductoTiendaOCafeteria();
                    break;
                case 6:
                    comprarConDescuentoEmpleadoYPuntos();
                    break;
                case 7:
                    inscribirseORetirarseTorneoIndividual();
                    break;
                case 8:
                    consultarEstadoEHistorialTorneos();
                    break;
                case 0:
                    continuar = false;
                    break;
            }
        }

        persistencia.guardarTodo(club);
        System.out.println("\n[OK] Datos guardados automaticamente en 'data/'. ¡Hasta pronto, " + empleadoActual.getNombre() + "!");
    }

    private boolean autenticarEmpleado() {
        for (int intento = 1; intento <= 3; intento++) {
            System.out.println("\n--- Autenticacion de Empleado (Intento " + intento + "/3) ---");
            String login = lector.leerTexto("Login: ");
            if (login.isEmpty()) {
                return false;
            }
            String pass = lector.leerTexto("Password: ");
            Usuario u = club.autenticarUsuario(login, pass);
            if (u instanceof Empleado) {
                this.empleadoActual = (Empleado) u;
                String cargo = (u instanceof Entrenador) ? "Entrenador" : "Fisioterapeuta";
                System.out.println("Bienvenido(a), " + empleadoActual.getNombre() + " [" + cargo + "]");
                return true;
            }
            System.out.println("  [!] Credenciales invalidas o el usuario no es un Empleado.");
        }
        System.out.println("Maximo de intentos alcanzado. Saliendo de la consola.");
        return false;
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n-------------------- MENU DE EMPLEADO --------------------");
        System.out.println("1. Consultar mi codigo de descuento, puntos de fidelidad y turnos");
        System.out.println("2. Planear sesion de entrenamiento (Entrenadores)");
        System.out.println("3. Registrar asistencia y evaluacion 1 a 5 en sesion (Entrenadores)");
        System.out.println("4. Solicitar cambio de turno semanal");
        System.out.println("5. Sugerir nuevo articulo de tienda o snack al Administrador");
        System.out.println("6. Comprar en Tienda o Cafeteria (15% desc. propio + puntos simultaneos)");
        System.out.println("7. Torneos Internos Individuales: Inscribirme ($0) o retirarme");
        System.out.println("8. Consultar estado e historial detallado de Torneos Internos");
        System.out.println("0. Salir y guardar automaticamente");
    }

    private void consultarMisDatosYTurnos() {
        String cargo = (empleadoActual instanceof Entrenador) ? "Entrenador" : "Fisioterapeuta";
        System.out.println("\n=== FICHA DEL EMPLEADO ===");
        System.out.println("Nombre: " + empleadoActual.getNombre() + " (" + empleadoActual.getLogin() + ") - Cargo: " + cargo);
        System.out.println("Codigo de descuento: " + empleadoActual.getCodigoDescuento() + " (15% uso propio / 8% para socios)");
        System.out.println("Puntos de fidelidad acumulados: " + empleadoActual.getPuntosFidelidad() + " pts ($" + empleadoActual.getPuntosFidelidad() + ")");
        System.out.println("\n-- Turnos Semanales Asignados --");
        for (int i = 0; i < club.getTurnos().size(); i++) {
            TurnoSemanal t = club.getTurnos().get(i);
            if (t.getEmpleadosAsignados().contains(empleadoActual)) {
                System.out.println("  * " + t.getDia() + " (" + t.getHorarioAtencion() + ")");
            }
        }
    }

    private void planearSesionDeEntrenamiento() {
        if (!(empleadoActual instanceof Entrenador)) {
            System.out.println("  [!] Solo los Entrenadores pueden planear sesiones de entrenamiento.");
            return;
        }
        Entrenador ent = (Entrenador) empleadoActual;
        String fecha = lector.leerTexto("Fecha de la sesion (YYYY-MM-DD): ");
        String hora = lector.leerTexto("Hora (ej. 16:00): ");
        int duracion = lector.leerEntero("Duracion en minutos (30-180): ", 30, 180);

        List<Instalacion> insts = club.getInstalaciones();
        for (int i = 0; i < insts.size(); i++) {
            System.out.println((i + 1) + ". " + insts.get(i).getNombre());
        }
        int idxInst = lector.leerEntero("Seleccione instalacion: ", 1, insts.size());
        SesionEntrenamiento sesion = ent.planearSesionEntrenamiento(fecha, hora, duracion, insts.get(idxInst - 1));

        if (!club.getEquipos().isEmpty() && lector.leerSiNo("¿Desea convocar a un equipo completo del club?")) {
            for (int i = 0; i < club.getEquipos().size(); i++) {
                System.out.println((i + 1) + ". " + club.getEquipos().get(i).getNombre());
            }
            int idxEq = lector.leerEntero("Equipo a convocar: ", 1, club.getEquipos().size());
            sesion.convocarEquipo(club.getEquipos().get(idxEq - 1));
        }
        club.getSesionesEntrenamiento().add(sesion);
        System.out.println("  [OK] Sesion planeada exitosamente con " + sesion.getConvocados().size() + " socios convocados.");
    }

    private void registrarAsistenciaYEvaluacion() {
        if (!(empleadoActual instanceof Entrenador)) {
            System.out.println("  [!] Solo los Entrenadores pueden registrar asistencia y evaluaciones.");
            return;
        }
        Entrenador ent = (Entrenador) empleadoActual;
        if (club.getSesionesEntrenamiento().isEmpty()) {
            System.out.println("No hay sesiones planeadas. Cree una sesion primero en la opcion 2.");
            return;
        }
        SesionEntrenamiento sesion = club.getSesionesEntrenamiento().get(club.getSesionesEntrenamiento().size() - 1);
        String loginSocio = lector.leerTexto("Ingrese el login del socio (ej. socio_mateo): ");
        Usuario u = club.buscarUsuario(loginSocio);
        if (!(u instanceof Socio)) {
            System.out.println("  [!] Socio no encontrado.");
            return;
        }
        String estado = lector.leerTexto("Estado de asistencia (Asistió / Llegó tarde / Faltó): ");
        RegistroAsistencia reg = ent.registrarAsistencia(sesion, (Socio) u, estado);
        if (lector.leerSiNo("¿Desea agregar evaluacion de desempeno opcional (1 a 5)?")) {
            int nota = lector.leerEntero("Nota (1 a 5): ", 1, 5);
            String com = lector.leerTexto("Comentario formativo: ");
            ent.registrarEvaluacionOpcional(reg, nota, com);
        }
        System.out.println("  [OK] Asistencia registrada para " + u.getNombre() + ".");
    }

    private void solicitarCambioDeTurno() {
        List<TurnoSemanal> turnos = club.getTurnos();
        if (turnos.size() < 2) {
            System.out.println("Se requieren al menos 2 turnos registrados.");
            return;
        }
        for (int i = 0; i < turnos.size(); i++) {
            TurnoSemanal t = turnos.get(i);
            System.out.println((i + 1) + ". " + t.getDia() + " (" + t.getHorarioAtencion() + ") - Empleados: " + t.getEmpleadosAsignados().size());
        }
        int orig = lector.leerEntero("Seleccione turno actual de origen: ", 1, turnos.size());
        int dest = lector.leerEntero("Seleccione turno nuevo de destino: ", 1, turnos.size());
        SolicitudCambioTurno sol = empleadoActual.solicitarCambioTurno(turnos.get(orig - 1), turnos.get(dest - 1));
        club.getSolicitudesCambioTurno().add(sol);
        System.out.println("  [OK] Solicitud de cambio de turno enviada al Administrador para aprobacion.");
    }

    private void sugerirProductoTiendaOCafeteria() {
        String nombre = lector.leerTexto("Nombre del articulo o snack sugerido: ");
        String tipo = lector.leerTexto("Tipo (Articulo Tienda / Snack Cafeteria): ");
        SugerenciaProducto sug = empleadoActual.sugerirArticuloOSnack(nombre, tipo);
        club.getSugerencias().add(sug);
        System.out.println("  [OK] Sugerencia '" + nombre + "' registrada para revision del Administrador.");
    }

    private void comprarConDescuentoEmpleadoYPuntos() {
        List<ArticuloTienda> arts = club.getArticulosTienda();
        System.out.println("\n=== COMPRA DE EMPLEADO EN TIENDA (15% DESC. PROPIO + PUNTOS FIDELIDAD) ===");
        for (int i = 0; i < arts.size(); i++) {
            ArticuloTienda a = arts.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " - $" + Math.round(a.getPrecio()) + " | Stock: " + a.getCantidadDisponible());
        }
        int idx = lector.leerEntero("Seleccione articulo (0 para cancelar): ", 0, arts.size());
        if (idx == 0) return;

        ArticuloTienda art = arts.get(idx - 1);
        int cant = lector.leerEntero("Cantidad: ", 1, 50);
        String fecha = lector.leerTexto("Fecha (YYYY-MM-DD): ");

        VentaTienda venta = new VentaTienda(fecha, empleadoActual);
        if (!venta.agregarArticulo(art, cant)) {
            System.out.println("  [X] Stock insuficiente.");
            return;
        }
        if (lector.leerSiNo("¿Desea aplicar su codigo de descuento propio del 15% (" + empleadoActual.getCodigoDescuento() + ")?")) {
            venta.aplicarCodigoDescuento(empleadoActual);
        }
        if (empleadoActual.getPuntosFidelidad() > 0 && lector.leerSiNo("Tiene " + empleadoActual.getPuntosFidelidad() + " puntos. ¿Desea redimirlos ADEMAS del descuento de empleado?")) {
            int pts = lector.leerEntero("Puntos a redimir (max " + empleadoActual.getPuntosFidelidad() + "): ", 1, empleadoActual.getPuntosFidelidad());
            venta.redimirPuntosComoDescuento(pts);
        }
        club.registrarVenta(venta);
        System.out.println("\n--- FACTURA DE COMPRA DE EMPLEADO ---");
        System.out.println("Subtotal:             $" + Math.round(venta.getSubtotal()));
        System.out.println("Descuento propio 15%: $" + Math.round(venta.getDescuentoEmpleado()));
        System.out.println("Puntos redimidos:     $" + venta.getPuntosRedimidos());
        System.out.println("Total con IVA (19%):  $" + Math.round(venta.getValorTotal()));
        System.out.println("Puntos ganados (2%):  +" + venta.getPuntosFidelidadGenerados() + " pts (Nuevo saldo: " + empleadoActual.getPuntosFidelidad() + " pts)");
    }

    private void inscribirseORetirarseTorneoIndividual() {
        List<TorneoInterno> torneos = club.getTorneosInternos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos internos registrados.");
            return;
        }
        System.out.println("\n=== TORNEOS INTERNOS (EMPLEADOS: SOLO INDIVIDUALES, SIN TURNO EL DIA DEL TORNEO, COSTO $0) ===");
        for (int i = 0; i < torneos.size(); i++) {
            TorneoInterno t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " [" + t.getTipoTorneo() + " - " + t.getDeporte().getNombre()
                    + " | Dia: " + t.getDiaTorneo() + " | Estado: " + t.getEstado() + "]");
        }
        int idx = lector.leerEntero("Seleccione torneo (0 para cancelar): ", 0, torneos.size());
        if (idx == 0) return;

        TorneoInterno t = torneos.get(idx - 1);
        int acc = lector.leerEntero("1. Inscribirme ($0) | 2. Retirarme antes de iniciar: ", 1, 2);
        if (acc == 1) {
            if (t.inscribirMiembroIndividual(empleadoActual, t.getCategoriaONivel(), club.getTurnos())) {
                System.out.println("  [OK] Empleado inscrito sin costo ($0) en '" + t.getNombre() + "'.");
            } else {
                System.out.println("  [X] Inscripcion rechazada: verifique que sea un torneo de disciplina individual, que usted NO este de turno el dia " + t.getDiaTorneo() + " y que el torneo tenga cupos abiertos.");
            }
        } else {
            if (t.retirarParticipante(empleadoActual.getNombre())) {
                System.out.println("  [OK] Retiro exitoso. Su cupo ha sido liberado.");
            } else {
                System.out.println("  [X] No fue posible retirarse.");
            }
        }
    }

    private void consultarEstadoEHistorialTorneos() {
        for (int i = 0; i < club.getTorneosInternos().size(); i++) {
            System.out.println("\n" + club.getTorneosInternos().get(i).consultarHistorialDetallado());
        }
    }
}
