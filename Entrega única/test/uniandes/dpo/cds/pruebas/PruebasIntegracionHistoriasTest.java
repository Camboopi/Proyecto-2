package uniandes.dpo.cds.pruebas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import uniandes.dpo.cds.modelo.*;
import uniandes.dpo.cds.persistencia.ManejadorPersistencia;

import java.io.File;
import java.util.List;

public class PruebasIntegracionHistoriasTest {
    private ClubDeportivoSeneca club;
    private ManejadorPersistencia persistencia;

    @BeforeEach
    public void setUp() {
        String rutaTemp = "data_test_integracion";
        limpiarCarpeta(new File(rutaTemp));
        persistencia = new ManejadorPersistencia(rutaTemp);
        club = new ClubDeportivoSeneca();
        persistencia.cargarTodo(club);
    }

    private void limpiarCarpeta(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            dir.delete();
        }
    }

    @Test
    public void testHU01_InscripcionSocioEnEquipoDeConjunto() {
        Equipo condores = club.buscarEquipo("Cóndores CDS");
        Socio nuevo = new Socio("socio_nuevo", "pass", "Pedro Juvenil", 16);
        club.agregarUsuario(nuevo);
        assertTrue(condores.inscribirJugador(nuevo, "Defensa", 5));
        assertFalse(club.buscarEquipo("Halcones CDS").inscribirJugador(nuevo, "Defensa", 12));
    }

    @Test
    public void testHU02_ReservaInstalacionesDeportesIndividuales() {
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        Instalacion canchaTenis = club.buscarInstalacion("Cancha de Tenis Auxiliar");
        assertTrue(club.registrarReserva(mateo, canchaTenis, "16:00-17:00", 2, "Sencillos"));
    }

    @Test
    public void testHU03_PlaneacionSesionYRegistroAsistenciaEvaluacion() {
        Entrenador marta = (Entrenador) club.buscarUsuario("profe_marta");
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        Instalacion cancha = club.buscarInstalacion("Cancha de Tenis Principal");

        SesionEntrenamiento ses = marta.planearSesionEntrenamiento("2026-11-20", "15:00", 90, cancha);
        ses.convocarEquipo(club.buscarEquipo("Halcones CDS"));
        RegistroAsistencia reg = marta.registrarAsistencia(ses, mateo, "Asistió");
        marta.registrarEvaluacionOpcional(reg, 5, "Excelente actitud");
        assertNotNull(reg.getEvaluacion());
        assertEquals(5, reg.getEvaluacion().getNota());
    }

    @Test
    public void testHU04_PartidoInternoEquiposYSancionesDisciplinarias() {
        Equipo halcones = club.buscarEquipo("Halcones CDS");
        Equipo condores = club.buscarEquipo("Cóndores CDS");
        for (int i = 1; i <= 7; i++) {
            Socio s = new Socio("condor_test_" + i, "123", "Condor " + i, 16);
            condores.inscribirJugador(s, "Titular", 20 + i);
        }
        PartidoInterno p1 = new PartidoInterno("2026-11-21", halcones, condores);
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        p1.registrarEstadisticasIndividuales(new EstadisticaFutbol(p1, mateo, 1, 0, 1, 0));
        assertTrue(p1.registrarResultado(1, 0));
        assertEquals(1, halcones.buscarInscripcion(mateo).getTarjetasAmarillasAcumuladas());

        PartidoInterno p2 = new PartidoInterno("2026-11-22", halcones, condores);
        p2.registrarEstadisticasIndividuales(new EstadisticaFutbol(p2, mateo, 0, 1, 1, 0));
        assertTrue(p2.registrarResultado(2, 1));
        assertTrue(halcones.buscarInscripcion(mateo).tieneSuspensionActiva());

        PartidoInterno p3 = new PartidoInterno("2026-11-23", halcones, condores);
        assertFalse(p3.registrarResultado(1, 1));
        assertTrue(p3.isCancelado());
    }

    @Test
    public void testHU05_PartidoPracticaIndividualYActualizacionELO() {
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        Socio valeria = (Socio) club.buscarUsuario("socio_valeria");
        Tenis tenis = (Tenis) club.buscarDeporte("Tenis");
        ReservaInstalacion res = club.getReservas().get(0);

        double eloInicialValeria = valeria.buscarPerfil("Tenis").getPuntajeRankingELO();
        PartidoPracticaIndividual prac = new PartidoPracticaIndividual("2026-11-22", tenis, res, mateo, valeria, "Sencillos", false);
        assertTrue(prac.actualizarPuntajeRankingELO(false));
        assertTrue(valeria.buscarPerfil("Tenis").getPuntajeRankingELO() > eloInicialValeria);
    }

    @Test
    public void testHU06_InscripcionYRegistroPartidosCompetenciaExterna() {
        Competencia comp = club.getCompetencias().get(0);
        assertNotNull(comp.getEquipoInscrito());
        PartidoCompetencia pc = new PartidoCompetencia(comp, "2026-11-23", "Universidad de los Andes", "Estadio", comp.getNominaOficial());
        assertTrue(pc.registrarMarcador("2 - 1"));
    }

    @Test
    public void testHU07_CompraEnTiendaDeportivaYSincronizacionUbicaciones() {
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        ArticuloTienda raqueta = club.buscarArticuloTienda("Raqueta de Tenis");
        VentaTienda vt = new VentaTienda("2026-11-24", mateo);
        assertTrue(vt.agregarArticulo(raqueta, 3));
        club.registrarVenta(vt);
        assertEquals(7, raqueta.getCantidadDisponible());
    }

    @Test
    public void testHU08_CompraEnCafeteriaYRestriccionBebidasCalientes() {
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        Bebida cafe = (Bebida) club.buscarProductoCafeteria("Café Americano");
        Instalacion padel = club.buscarInstalacion("Cancha de Pádel Central");
        Instalacion tenis = club.buscarInstalacion("Cancha de Tenis Principal");

        VentaCafeteria vcPadel = new VentaCafeteria("2026-11-24", mateo, padel);
        assertFalse(vcPadel.agregarProductoCafeteria(cafe, 1), "Bloquea bebida caliente en cancha de Padel");

        VentaCafeteria vcTenis = new VentaCafeteria("2026-11-24", mateo, tenis);
        assertTrue(vcTenis.agregarProductoCafeteria(cafe, 1));
        club.registrarVenta(vcTenis);
        assertFalse(club.registrarReserva(mateo, padel, "19:00-20:00", 4, "Dobles"), "Bloquea reserva de Padel teniendo bebida caliente");
    }

    @Test
    public void testHU09_DescuentoEmpleadoYPuntosFidelidadSimultaneos() {
        Entrenador juan = (Entrenador) club.buscarUsuario("profe_juan");
        ArticuloTienda balon = club.buscarArticuloTienda("Balón de Fútbol");
        VentaTienda vt = new VentaTienda("2026-11-25", juan);
        assertTrue(vt.agregarArticulo(balon, 1));
        assertTrue(vt.aplicarCodigoDescuento(juan));
        assertTrue(vt.redimirPuntosComoDescuento(500));
        club.registrarVenta(vt);
        assertEquals(18000.0, vt.getDescuentoEmpleado(), 0.01);
        assertEquals(500, vt.getPuntosRedimidos());
        assertTrue(vt.getPuntosFidelidadGenerados() > 0);
    }

    @Test
    public void testHU10_SolicitudYAprobacionCambioTurnoConQuorum() {
        Administrador admin = (Administrador) club.buscarUsuario("admin");
        Fisioterapeuta laura = (Fisioterapeuta) club.buscarUsuario("fisio_laura");
        TurnoSemanal tLunes = club.obtenerOCrearTurno("Lunes", "08:00-12:00");
        TurnoSemanal tMartes = club.obtenerOCrearTurno("Martes", "08:00-12:00");

        SolicitudCambioTurno solRechazada = laura.solicitarCambioTurno(tLunes, tMartes);
        assertFalse(admin.aprobarCambioTurno(solRechazada), "Rechaza porque Laura es la unica fisioterapeuta el Lunes");

        SolicitudCambioTurno solJuan = club.getSolicitudesCambioTurno().get(0);
        assertTrue(admin.aprobarCambioTurno(solJuan), "Aprueba porque quedan 2 entrenadores y 1 fisio el Lunes");
    }

    @Test
    public void testHU11_GestionUbicacionesInventarioYSugerenciasProductos() {
        Administrador admin = (Administrador) club.buscarUsuario("admin");
        UbicacionInventario bodega = club.buscarUbicacionInventario("Bodega Principal");
        UbicacionInventario mostrador = club.buscarUbicacionInventario("Mostrador Tienda");
        ArticuloTienda malla = club.buscarArticuloTienda("Malla Reglamentaria");

        assertTrue(admin.moverArticulosUbicacionInventario(bodega, mostrador, malla, 2));
        admin.reabastecerTienda(bodega, malla, 4);
        assertEquals(12, malla.getCantidadDisponible());

        SugerenciaProducto sug = club.getSugerencias().get(0);
        admin.aprobarSugerenciaEmpleado(sug);
        assertTrue(sug.isAprobada());
    }

    @Test
    public void testHU12_GeneracionReportesAdministrativosYDeportivos() {
        ReporteAdministrativo rep = new ReporteAdministrativo("Todos", "Semestral");
        assertTrue(rep.generarReporteVentasPorRubroYPeriodo(club.getVentas()).contains("Reporte Ventas"));
        assertTrue(rep.generarReporteDeportivoAsistenciaYPartidos(club.getSesionesEntrenamiento(), club.getPartidos()).contains("Reporte Deportivo"));
        assertTrue(rep.generarReporteEvolucionRankings(club.getUsuarios()).contains("ELO"));
    }

    @Test
    public void testHU13_CreacionTorneosInternosYReserva20PorcientoPreferencial() {
        Deporte futbol = club.buscarDeporte("Fútbol");
        TorneoCompetitivo tc = club.crearTorneoCompetitivo("Copa Futbol CDS", futbol, "Sub-17", "Equipos", 10, "Domingo", 120000);
        assertEquals(2, tc.getCuposPreferencialesReservados(), "20% de 10 cupos = 2 cupos preferenciales reservados para equipos del club");
        assertTrue(tc.inscribirEquipoClub(club.buscarEquipo("Halcones CDS")));
        assertEquals(1, tc.contarCuposPreferencialesOcupados());
    }

    @Test
    public void testHU14_InscripcionYRetiroLibreAntesDeIniciarTorneo() {
        TorneoInterno abierto = club.buscarTorneoInterno("Abierto Recreativo Tenis CDS");
        Socio lucas = (Socio) club.buscarUsuario("socio_lucas");
        int antes = abierto.getParticipantes().size();
        assertTrue(abierto.inscribirMiembroIndividual(lucas, "Principiante", club.getTurnos()));
        assertEquals(antes + 1, abierto.getParticipantes().size());
        assertTrue(abierto.retirarParticipante(lucas.getNombre()));
        assertEquals(antes, abierto.getParticipantes().size(), "Al retirarse antes de iniciar, libera su cupo");
    }

    @Test
    public void testHU15_LiberacionCuposPreferencialesNoLlenados() {
        Deporte padel = club.buscarDeporte("Pádel");
        TorneoRecreativo tr = club.crearTorneoRecreativo("Padel Integracion", padel, "Intermedio", "Dobles", 5, "Domingo", 0.15);
        for (int i = 1; i <= 4; i++) {
            assertTrue(tr.inscribirParticipanteExterno("Pareja Externa " + i, "Externo", "Intermedio"));
        }
        assertFalse(tr.inscribirParticipanteExterno("Pareja Externa 5", "Externo", "Intermedio"));
        assertTrue(tr.liberarCuposPreferenciales());
        assertTrue(tr.inscribirParticipanteExterno("Pareja Externa 5", "Externo", "Intermedio"));
    }

    @Test
    public void testHU16_ParticipacionGratuitaEmpleadoSinTurnoYSinPremioEfectivo() {
        TorneoCompetitivo copa = (TorneoCompetitivo) club.buscarTorneoInterno("Copa Competitiva Tenis Séneca");
        ParticipanteTorneo partJuan = copa.buscarParticipante("Juan Perez");
        assertNotNull(partJuan);
        assertEquals(0.0, partJuan.getMontoPagado(), 0.01, "La inscripcion del empleado no tiene costo");

        copa.registrarEncuentro("Final", "Juan Perez", "Carlos Mayor", "6-4, 6-3", "Juan Perez");
        assertTrue(copa.finalizarTorneo("Juan Perez"));
        assertEquals(0.0, copa.getPremioEfectivoEntregado(), 0.01, "Empleado ganador no recibe premio en efectivo");
    }

    @Test
    public void testHU17_TorneoRecreativoOtorgaBonoDescuentoNoAcumulable() {
        Socio mateo = (Socio) club.buscarUsuario("socio_mateo");
        BonoDescuento bono = mateo.buscarBonoDisponible("BONO_BIENVENIDA");
        assertNotNull(bono);

        Instalacion canchaTenis = club.buscarInstalacion("Cancha de Tenis Principal");
        Snack croissant = (Snack) club.buscarProductoCafeteria("Croissant de Queso");
        VentaCafeteria vc = new VentaCafeteria("2026-11-26", mateo, canchaTenis);
        assertTrue(vc.agregarProductoCafeteria(croissant, 2));
        assertTrue(vc.aplicarBonoTorneoRecreativo(bono));
        assertTrue(bono.isRedimido());
        assertFalse(vc.redimirPuntosComoDescuento(200), "Bono no acumulable con otros descuentos");
    }

    @Test
    public void testHU18_ConsultaEstadoEHistorialDetalladoTorneosEnCursoYFinalizados() {
        TorneoInterno enCurso = club.buscarTorneoInterno("Copa Competitiva Tenis Séneca");
        TorneoInterno finalizado = club.buscarTorneoInterno("Torneo Relámpago Tenis de Mesa");

        assertEquals("En curso", enCurso.getEstado());
        assertEquals("Finalizado", finalizado.getEstado());

        String histEnCurso = enCurso.consultarHistorialDetallado();
        String histFinalizado = finalizado.consultarHistorialDetallado();
        assertTrue(histEnCurso.contains("Encuentros Disputados") && histEnCurso.contains("Tabla de Clasificacion"));
        assertTrue(histFinalizado.contains("Campeon: Valeria Ortiz") && histFinalizado.contains("Bono de descuento"));
    }
}
