package uniandes.dpo.cds.consola;

import uniandes.dpo.cds.modelo.*;
import uniandes.dpo.cds.persistencia.ManejadorPersistencia;

import java.util.List;

public class ConsolaAdministrador {
    private ClubDeportivoSeneca club;
    private ManejadorPersistencia persistencia;
    private LectorConsola lector;
    private Administrador adminActual;

    public ConsolaAdministrador() {
        this.persistencia = new ManejadorPersistencia("data");
        this.club = new ClubDeportivoSeneca();
        this.persistencia.cargarTodo(this.club);
        this.lector = new LectorConsola();
    }

    public static void main(String[] args) {
        ConsolaAdministrador consola = new ConsolaAdministrador();
        consola.ejecutar();
    }

    public void ejecutar() {
        System.out.println("==================================================================");
        System.out.println("   CLUB DEPORTIVO SÉNECA (CDS) - CONSOLA DE ADMINISTRADOR");
        System.out.println("==================================================================");
        System.out.println("Datos cargados automaticamente desde la carpeta 'data/'.");
        System.out.println("Administrador precargado: admin (clave: admin123)");

        if (!autenticarAdmin()) {
            return;
        }

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = lector.leerEntero("Seleccione una opcion: ", 0, 8);
            switch (opcion) {
                case 1:
                    gestionarEquiposYPartidosInternos();
                    break;
                case 2:
                    gestionarCompetenciasExternas();
                    break;
                case 3:
                    gestionarInventarioTienda();
                    break;
                case 4:
                    aprobarTurnosYSugerencias();
                    break;
                case 5:
                    crearTorneoInterno();
                    break;
                case 6:
                    administrarTorneoInterno();
                    break;
                case 7:
                    consultarHistorialTorneosInternos();
                    break;
                case 8:
                    generarReportesAdministrativosYDeportivos();
                    break;
                case 0:
                    continuar = false;
                    break;
            }
        }

        persistencia.guardarTodo(club);
        System.out.println("\n[OK] Datos guardados automaticamente en 'data/'. ¡Hasta pronto, " + adminActual.getNombre() + "!");
    }

    private boolean autenticarAdmin() {
        for (int intento = 1; intento <= 3; intento++) {
            System.out.println("\n--- Autenticacion de Administrador (Intento " + intento + "/3) ---");
            String login = lector.leerTexto("Login: ");
            if (login.isEmpty()) {
                return false;
            }
            String pass = lector.leerTexto("Password: ");
            Usuario u = club.autenticarUsuario(login, pass);
            if (u instanceof Administrador) {
                this.adminActual = (Administrador) u;
                System.out.println("Bienvenido, " + adminActual.getNombre() + " [Rol: Administrador]");
                return true;
            }
            System.out.println("  [!] Credenciales invalidas o el usuario no es Administrador.");
        }
        System.out.println("Maximo de intentos alcanzado. Saliendo de la consola.");
        return false;
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n-------------------- MENU DE ADMINISTRADOR --------------------");
        System.out.println("1. Gestionar Equipos del Club (crear, dar de baja, partido interno)");
        System.out.println("2. Gestionar Competencias Externas (Torneo de los Cerros / ASCUN)");
        System.out.println("3. Gestionar Inventario de Tienda (ubicaciones Bodega/Mostrador y reabastecimiento)");
        System.out.println("4. Aprobar Solicitudes de Cambio de Turno y Sugerencias de Empleados");
        System.out.println("5. Crear nuevo Torneo Interno (Recreativo o Competitivo, 20% cupos preferenciales)");
        System.out.println("6. Administrar Torneo Interno (liberar cupos, inscribir externos, iniciar, encuentros, finalizar)");
        System.out.println("7. Consultar estado e historial detallado de Torneos Internos");
        System.out.println("8. Generar Reportes Administrativos y Deportivos");
        System.out.println("0. Salir y guardar automaticamente");
    }

    private void gestionarEquiposYPartidosInternos() {
        System.out.println("\n1. Listar equipos | 2. Crear equipo | 3. Dar de baja equipo | 4. Registrar partido interno");
        int op = lector.leerEntero("Opcion: ", 1, 4);
        if (op == 1) {
            for (int i = 0; i < club.getEquipos().size(); i++) {
                Equipo eq = club.getEquipos().get(i);
                System.out.println("  * " + eq.getNombre() + " (" + eq.getDeporte().getNombre() + " - " + eq.getCategoria()
                        + ") | Jugadores: " + eq.getNomina().size() + "/" + eq.getCupoMaximoJugadores());
            }
        } else if (op == 2) {
            String nom = lector.leerTexto("Nombre del nuevo equipo: ");
            String depNom = lector.leerTexto("Deporte de conjunto (Fútbol / Baloncesto / Voleibol): ");
            Deporte d = club.buscarDeporte(depNom);
            if (!(d instanceof DeporteConjunto)) {
                System.out.println("  [!] Deporte de conjunto no valido.");
                return;
            }
            String cat = lector.leerTexto("Categoria (Sub-13 / Sub-15 / Sub-17 / Mayores): ");
            int cupo = lector.leerEntero("Cupo maximo de jugadores (6-30): ", 6, 30);
            club.crearEquipo(nom, (DeporteConjunto) d, cat, cupo);
            System.out.println("  [OK] Equipo '" + nom + "' creado.");
        } else if (op == 3) {
            String nom = lector.leerTexto("Nombre del equipo a dar de baja: ");
            if (club.darDeBajaEquipo(nom)) {
                System.out.println("  [OK] Equipo '" + nom + "' eliminado.");
            } else {
                System.out.println("  [!] Equipo no encontrado.");
            }
        } else if (op == 4) {
            String eqL = lector.leerTexto("Nombre equipo local: ");
            String eqV = lector.leerTexto("Nombre equipo visitante: ");
            Equipo local = club.buscarEquipo(eqL);
            Equipo vis = club.buscarEquipo(eqV);
            String fecha = lector.leerTexto("Fecha (YYYY-MM-DD): ");
            PartidoInterno pi = new PartidoInterno(fecha, local, vis);
            if (!pi.puedeDisputarse()) {
                System.out.println("  [X] " + pi.getMotivoCancelacion());
                return;
            }
            int ptsL = lector.leerEntero("Puntos/Goles Local: ", 0, 200);
            int ptsV = lector.leerEntero("Puntos/Goles Visitante: ", 0, 200);
            if (local != null && local.getDeporte() instanceof Futbol && lector.leerSiNo("¿Desea registrar tarjetas amarillas/rojas a un jugador en este partido?")) {
                String loginJug = lector.leerTexto("Login del socio amonestado: ");
                Usuario u = club.buscarUsuario(loginJug);
                if (u instanceof Socio) {
                    int amarillas = lector.leerEntero("Tarjetas amarillas en este partido (0-2): ", 0, 2);
                    int rojas = lector.leerEntero("Tarjetas rojas en este partido (0-1): ", 0, 1);
                    pi.registrarEstadisticasIndividuales(new EstadisticaFutbol(pi, (Socio) u, 0, 0, amarillas, rojas));
                }
            }
            if (pi.registrarResultado(ptsL, ptsV)) {
                club.getPartidos().add(pi);
                System.out.println("  [OK] Partido interno registrado: " + pi.getMarcadorFinal());
            } else {
                String motivo = pi.isCancelado() ? pi.getMotivoCancelacion() : "Empate no permitido en este deporte.";
                System.out.println("  [X] No se pudo disputar: " + motivo);
            }
        }
    }

    private void gestionarCompetenciasExternas() {
        System.out.println("\n=== COMPETENCIAS EXTERNAS (UN UNICO EQUIPO REPRESENTANTE DEL CLUB POR COMPETENCIA) ===");
        for (int i = 0; i < club.getCompetencias().size(); i++) {
            Competencia c = club.getCompetencias().get(i);
            String nomEq = c.getEquipoInscrito() != null ? c.getEquipoInscrito().getNombre() : "Sin inscribir";
            System.out.println((i + 1) + ". " + c.getNombre() + " [" + c.getEntidadOrganizadora() + " - " + c.getAnioTemporada()
                    + "] | Equipo del club inscrito: " + nomEq + " (" + c.getNominaOficial().size() + " jugadores)");
        }
        if (lector.leerSiNo("¿Desea registrar un nuevo partido en la primera competencia externa?")) {
            if (club.getCompetencias().isEmpty()) return;
            Competencia comp = club.getCompetencias().get(0);
            String rival = lector.leerTexto("Universidad rival (ej. Universidad Javeriana): ");
            String marcador = lector.leerTexto("Marcador (ej. 2 - 1): ");
            boolean victoria = lector.leerSiNo("¿Fue victoria para el Club Deportivo Séneca?");
            PartidoCompetencia pc = new PartidoCompetencia(comp, "2026-11-15", rival, "Cancha Principal CDS", comp.getNominaOficial());
            if (pc.registrarMarcador(marcador)) {
                comp.agregarPartidoCompetencia(pc);
                if (comp instanceof CompetenciaLiga) {
                    ((CompetenciaLiga) comp).otorgarPuntosSegunDeporte(pc, victoria, false);
                    System.out.println("  [OK] Tabla de posiciones actualizada: " + ((CompetenciaLiga) comp).consultarTablaPosiciones());
                }
            }
        }
    }

    private void gestionarInventarioTienda() {
        UbicacionInventario bodega = club.buscarUbicacionInventario("Bodega Principal");
        UbicacionInventario mostrador = club.buscarUbicacionInventario("Mostrador Tienda");
        System.out.println("\n=== INVENTARIO DE TIENDA Y UBICACIONES FISICAS ===");
        for (int i = 0; i < club.getArticulosTienda().size(); i++) {
            ArticuloTienda a = club.getArticulosTienda().get(i);
            int cb = bodega != null && bodega.getCantidadesPorArticulo().containsKey(a) ? bodega.getCantidadesPorArticulo().get(a) : 0;
            int cm = mostrador != null && mostrador.getCantidadesPorArticulo().containsKey(a) ? mostrador.getCantidadesPorArticulo().get(a) : 0;
            System.out.println((i + 1) + ". " + a.getNombre() + " -> Stock Total: " + a.getCantidadDisponible()
                    + " (Bodega Principal: " + cb + " | Mostrador Tienda: " + cm + ")");
        }
        System.out.println("\n1. Mover articulos de Bodega a Mostrador | 2. Reabastecer Bodega | 0. Volver");
        int acc = lector.leerEntero("Accion: ", 0, 2);
        if (acc == 0) return;

        int idx = lector.leerEntero("Seleccione articulo (1-" + club.getArticulosTienda().size() + "): ", 1, club.getArticulosTienda().size());
        ArticuloTienda art = club.getArticulosTienda().get(idx - 1);
        int cant = lector.leerEntero("Cantidad de unidades: ", 1, 500);
        if (acc == 1) {
            if (adminActual.moverArticulosUbicacionInventario(bodega, mostrador, art, cant)) {
                System.out.println("  [OK] Se trasladaron " + cant + " unidades al Mostrador Tienda.");
            } else {
                System.out.println("  [X] No hay suficientes unidades en Bodega Principal.");
            }
        } else {
            adminActual.reabastecerTienda(bodega, art, cant);
            System.out.println("  [OK] Reabastecimiento exitoso. Nuevo stock total: " + art.getCantidadDisponible());
        }
    }

    private void aprobarTurnosYSugerencias() {
        System.out.println("\n=== SOLICITUDES DE CAMBIO DE TURNO (Quorum: min 2 entrenadores y 1 fisio) ===");
        for (int i = 0; i < club.getSolicitudesCambioTurno().size(); i++) {
            SolicitudCambioTurno s = club.getSolicitudesCambioTurno().get(i);
            System.out.println((i + 1) + ". Empleado: " + s.getSolicitante().getNombre()
                    + " | De: " + s.getTurnoOrigen().getDia() + " -> A: " + s.getTurnoDestino().getDia()
                    + " | Aprobada: " + s.isAprobada());
            if (!s.isAprobada() && lector.leerSiNo("  ¿Aprobar esta solicitud de cambio de turno?")) {
                if (adminActual.aprobarCambioTurno(s)) {
                    System.out.println("    [OK] Solicitud aprobada (se mantiene el quorum minimo).");
                } else {
                    System.out.println("    [X] Rechazada automaticamente: dejaria el turno origen sin 2 entrenadores y 1 fisioterapeuta.");
                }
            }
        }
        System.out.println("\n=== SUGERENCIAS DE PRODUCTOS DE EMPLEADOS ===");
        for (int i = 0; i < club.getSugerencias().size(); i++) {
            SugerenciaProducto sug = club.getSugerencias().get(i);
            System.out.println((i + 1) + ". " + sug.getNombrePropuesto() + " [" + sug.getTipo() + "] por "
                    + sug.getEmpleadoProponente().getNombre() + " | Aprobada: " + sug.isAprobada());
            if (!sug.isAprobada() && lector.leerSiNo("  ¿Aprobar esta sugerencia?")) {
                adminActual.aprobarSugerenciaEmpleado(sug);
                System.out.println("    [OK] Sugerencia aprobada.");
            }
        }
    }

    private void crearTorneoInterno() {
        System.out.println("\n=== CREAR NUEVO TORNEO INTERNO ===");
        int tipo = lector.leerEntero("Tipo (1 = Torneo Recreativo [gratis, premio bono] | 2 = Torneo Competitivo [pago, premio efectivo]): ", 1, 2);
        String nombre = lector.leerTexto("Nombre del torneo: ");
        String nomDep = lector.leerTexto("Deporte (Fútbol / Baloncesto / Voleibol / Tenis / Pádel / Tenis de Mesa): ");
        Deporte dep = club.buscarDeporte(nomDep);
        if (dep == null) {
            System.out.println("  [!] Deporte no encontrado.");
            return;
        }
        String catNivel = lector.leerTexto("Categoria de edad o Nivel requerido (ej. Sub-17, Mayores, Principiante, Intermedio, Avanzado, Libre): ");
        String mod = (dep instanceof DeporteConjunto) ? "Equipos" : lector.leerTexto("Modalidad (Sencillos / Dobles): ");
        int maxCupos = lector.leerEntero("Numero maximo de cupos (2-64): ", 2, 64);
        String dia = lector.leerTexto("Dia de realizacion del torneo (ej. Lunes, Martes, Sabado): ");

        if (tipo == 1) {
            double pctBono = lector.leerDoublePositivo("Porcentaje del bono de descuento para el ganador en decimal (ej. 0.20 para 20%): ");
            TorneoRecreativo tr = club.crearTorneoRecreativo(nombre, dep, catNivel, mod, maxCupos, dia, pctBono);
            System.out.println("  [OK] Torneo Recreativo creado. Cupos preferenciales reservados (20%): "
                    + tr.getCuposPreferencialesReservados() + "/" + tr.getMaxCupos());
        } else {
            double tarifa = lector.leerDoublePositivo("Tarifa de inscripcion por participante/equipo ($): ");
            TorneoCompetitivo tc = club.crearTorneoCompetitivo(nombre, dep, catNivel, mod, maxCupos, dia, tarifa);
            System.out.println("  [OK] Torneo Competitivo creado. Cupos preferenciales reservados (20%): "
                    + tc.getCuposPreferencialesReservados() + "/" + tc.getMaxCupos());
        }
    }

    private void administrarTorneoInterno() {
        List<TorneoInterno> torneos = club.getTorneosInternos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos internos registrados.");
            return;
        }
        for (int i = 0; i < torneos.size(); i++) {
            TorneoInterno t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " [" + t.getTipoTorneo() + " - Estado: " + t.getEstado()
                    + " | Cupos: " + t.getParticipantes().size() + "/" + t.getMaxCupos() + "]");
        }
        int idx = lector.leerEntero("Seleccione torneo a administrar (0 para cancelar): ", 0, torneos.size());
        if (idx == 0) return;

        TorneoInterno t = torneos.get(idx - 1);
        System.out.println("1. Inscribir participante/equipo externo o de la universidad");
        System.out.println("2. Liberar cupos preferenciales no llenados (antes de iniciar)");
        System.out.println("3. Iniciar torneo (pasar a estado 'En curso')");
        System.out.println("4. Registrar resultado de un encuentro del torneo");
        System.out.println("5. Finalizar torneo y otorgar premio/bono al ganador");
        int acc = lector.leerEntero("Seleccione accion: ", 1, 5);

        if (acc == 1) {
            String nomExt = lector.leerTexto("Nombre del participante o equipo externo: ");
            if (t.getDeporte() instanceof DeporteConjunto) {
                int cantJ = lector.leerEntero("Cantidad de jugadores del equipo externo: ", 1, 30);
                if (t.inscribirEquipoExterno(nomExt, t.getCategoriaONivel(), cantJ)) {
                    System.out.println("  [OK] Equipo externo inscrito.");
                } else {
                    System.out.println("  [X] No se pudo inscribir (verifique cupos generales disponibles o libere los preferenciales).");
                }
            } else {
                if (t.inscribirParticipanteExterno(nomExt, "Estudiante Universidad", t.getCategoriaONivel())) {
                    System.out.println("  [OK] Participante externo inscrito.");
                } else {
                    System.out.println("  [X] No se pudo inscribir (verifique cupos generales disponibles o libere los preferenciales).");
                }
            }
        } else if (acc == 2) {
            if (t.liberarCuposPreferenciales()) {
                System.out.println("  [OK] Cupos preferenciales liberados para cualquier participante elegible.");
            } else {
                System.out.println("  [X] Solo se pueden liberar antes de iniciar el torneo.");
            }
        } else if (acc == 3) {
            if (t.iniciarTorneo()) {
                System.out.println("  [OK] El torneo '" + t.getNombre() + "' ahora esta 'En curso'.");
            } else {
                System.out.println("  [X] Se requieren al menos 2 inscritos y que el torneo este en 'Inscripciones Abiertas'.");
            }
        } else if (acc == 4) {
            String ronda = lector.leerTexto("Fase o Ronda (ej. Semifinal, Final): ");
            String pA = lector.leerTexto("Nombre exacto del Participante A: ");
            String pB = lector.leerTexto("Nombre exacto del Participante B: ");
            String marc = lector.leerTexto("Marcador (ej. 6-4, 6-3 o 2-1): ");
            String gan = lector.leerTexto("Nombre exacto del ganador: ");
            EncuentroTorneo enc = t.registrarEncuentro(ronda, pA, pB, marc, gan);
            if (enc != null) {
                System.out.println("  [OK] Encuentro registrado: " + enc.obtenerResumenEncuentro());
            } else {
                System.out.println("  [X] Verifique que el torneo este 'En curso' y los nombres coincidan con los inscritos.");
            }
        } else if (acc == 5) {
            String camp = lector.leerTexto("Nombre exacto del participante o equipo campeon: ");
            if (t.finalizarTorneo(camp)) {
                System.out.println("  [OK] Torneo finalizado. " + t.obtenerResumenPremio());
            } else {
                System.out.println("  [X] No se pudo finalizar (verifique que este 'En curso' y el campeon este inscrito).");
            }
        }
    }

    private void consultarHistorialTorneosInternos() {
        for (int i = 0; i < club.getTorneosInternos().size(); i++) {
            System.out.println("\n" + club.getTorneosInternos().get(i).consultarHistorialDetallado());
        }
    }

    private void generarReportesAdministrativosYDeportivos() {
        ReporteAdministrativo repTienda = new ReporteAdministrativo("Tienda", "Actual");
        ReporteAdministrativo repCafe = new ReporteAdministrativo("Cafetería", "Actual");
        ReporteAdministrativo repTodos = new ReporteAdministrativo("Todos", "Actual");
        System.out.println("\n=== REPORTES ADMINISTRATIVOS Y DEPORTIVOS ===");
        System.out.println(" * " + repTienda.generarReporteVentasPorRubroYPeriodo(club.getVentas()));
        System.out.println(" * " + repCafe.generarReporteVentasPorRubroYPeriodo(club.getVentas()));
        System.out.println(" * " + repTodos.generarReporteVentasPorRubroYPeriodo(club.getVentas()));
        System.out.println(" * " + repTodos.generarReporteDeportivoAsistenciaYPartidos(club.getSesionesEntrenamiento(), club.getPartidos()));
        System.out.println(" * " + repTodos.generarReporteEvolucionRankings(club.getUsuarios()));
    }
}
