package uniandes.dpo.cds.pruebas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import uniandes.dpo.cds.modelo.*;

import java.util.ArrayList;
import java.util.List;

public class PruebasUnitariasModeloTest {
    private ClubDeportivoSeneca club;
    private Futbol futbol;
    private Baloncesto baloncesto;
    private Tenis tenis;
    private Padel padel;

    @BeforeEach
    public void setUp() {
        club = new ClubDeportivoSeneca();
        futbol = new Futbol();
        baloncesto = new Baloncesto();
        tenis = new Tenis(2);
        padel = new Padel(2);
        club.agregarDeporte(futbol);
        club.agregarDeporte(baloncesto);
        club.agregarDeporte(tenis);
        club.agregarDeporte(padel);
    }

    @Test
    public void testSocioCategoriasYRestriccionesEquipo() {
        Socio sub13 = new Socio("s13", "123", "Niño", 12);
        Socio sub15 = new Socio("s15", "123", "Joven 15", 15);
        Socio sub17 = new Socio("s17", "123", "Joven 17", 16);
        Socio mayor = new Socio("sMay", "123", "Adulto", 21);

        assertEquals("Sub-13", sub13.getCategoriaEdad());
        assertEquals("Sub-15", sub15.getCategoriaEdad());
        assertEquals("Sub-17", sub17.getCategoriaEdad());
        assertEquals("Mayores", mayor.getCategoriaEdad());

        Equipo halcones = club.crearEquipo("Halcones", futbol, "Sub-17", 15);
        Equipo condores = club.crearEquipo("Condores", futbol, "Sub-17", 15);
        Equipo basketSub17 = club.crearEquipo("Toros Basket", baloncesto, "Sub-17", 12);

        assertFalse(halcones.inscribirJugador(mayor, "Defensa", 4), "No debe permitir socio de otra categoria de edad");
        assertTrue(halcones.inscribirJugador(sub17, "Delantero", 9), "Debe permitir socio de la misma categoria");
        assertFalse(halcones.inscribirJugador(new Socio("otro", "123", "Otro", 16), "Portero", 9), "No debe repetir dorsal");
        assertFalse(condores.inscribirJugador(sub17, "Delantero", 10), "No puede estar en 2 equipos del mismo deporte");
        assertTrue(basketSub17.inscribirJugador(sub17, "Base", 9), "Si puede estar en otro deporte distinto");
    }

    @Test
    public void testPartidoInternoReglasPorDeporteYSanciones() {
        Equipo eqA = club.crearEquipo("Halcones", futbol, "Sub-17", 15);
        Equipo eqB = club.crearEquipo("Condores", futbol, "Sub-17", 15);
        Socio goleador = null;
        for (int i = 1; i <= 7; i++) {
            Socio sA = new Socio("a" + i, "123", "JugA " + i, 16);
            Socio sB = new Socio("b" + i, "123", "JugB " + i, 16);
            if (i == 1) goleador = sA;
            eqA.inscribirJugador(sA, "Campo", i);
            eqB.inscribirJugador(sB, "Campo", i);
        }

        PartidoInterno partido = new PartidoInterno("2026-11-10", eqA, eqB);
        assertTrue(partido.puedeDisputarse());
        EstadisticaFutbol est = new EstadisticaFutbol(partido, goleador, 2, 1, 2, 0);
        partido.registrarEstadisticasIndividuales(est);
        assertTrue(partido.registrarResultado(2, 2), "Futbol permite empates");
        partido.aplicarSuspensionPorTarjetas();

        assertTrue(eqA.buscarInscripcion(goleador).tieneSuspensionActiva(), "2 amarillas generan suspension");
        assertEquals(6, eqA.obtenerJugadoresHabilitados().size());

        PartidoInterno siguiente = new PartidoInterno("2026-11-17", eqA, eqB);
        assertFalse(siguiente.puedeDisputarse(), "Con 6 habilitados no alcanza el minimo de 7 para Futbol");
        assertTrue(siguiente.isCancelado(), "El partido queda cancelado por falta de jugadores habilitados tras suspension");
        assertFalse(siguiente.registrarResultado(1, 0), "Un partido cancelado no puede registrar resultado");
    }

    @Test
    public void testAcumulacionTarjetasAmarillasEnDistintosPartidosCancelaSiguientePartido() {
        Equipo eqA = club.crearEquipo("Leones", futbol, "Sub-17", 15);
        Equipo eqB = club.crearEquipo("Tigres", futbol, "Sub-17", 15);
        Socio capitan = null;
        for (int i = 1; i <= 7; i++) {
            Socio sA = new Socio("l" + i, "123", "Leon " + i, 16);
            Socio sB = new Socio("t" + i, "123", "Tigre " + i, 16);
            if (i == 1) capitan = sA;
            eqA.inscribirJugador(sA, "Titular", i);
            eqB.inscribirJugador(sB, "Titular", i);
        }

        // Partido 1: el capitan recibe 1 tarjeta amarilla (aun no queda suspendido)
        PartidoInterno partido1 = new PartidoInterno("2026-11-01", eqA, eqB);
        partido1.registrarEstadisticasIndividuales(new EstadisticaFutbol(partido1, capitan, 1, 0, 1, 0));
        assertTrue(partido1.registrarResultado(2, 1));
        assertEquals(1, eqA.buscarInscripcion(capitan).getTarjetasAmarillasAcumuladas(), "Lleva 1 amarilla acumulada");
        assertFalse(eqA.buscarInscripcion(capitan).tieneSuspensionActiva(), "Con 1 sola amarilla aun puede jugar");

        // Partido 2: el capitan recibe su 2da tarjeta amarilla acumulada -> se suspende automaticamente
        PartidoInterno partido2 = new PartidoInterno("2026-11-08", eqA, eqB);
        partido2.registrarEstadisticasIndividuales(new EstadisticaFutbol(partido2, capitan, 0, 1, 1, 0));
        assertTrue(partido2.registrarResultado(1, 1));
        assertEquals(0, eqA.buscarInscripcion(capitan).getTarjetasAmarillasAcumuladas(), "El contador se reinicia al generar la sancion");
        assertTrue(eqA.buscarInscripcion(capitan).tieneSuspensionActiva(), "Al acumular 2 amarillas en partidos distintos queda suspendido");
        assertEquals(6, eqA.obtenerJugadoresHabilitados().size(), "El equipo queda con 6 jugadores habilitados");

        // Partido 3: al intentar disputarse con 6 jugadores habilitados (minimo 7 en Futbol), se cancela
        PartidoInterno partido3 = new PartidoInterno("2026-11-15", eqA, eqB);
        assertFalse(partido3.registrarResultado(3, 0), "No puede disputarse por falta de jugadores habilitados");
        assertTrue(partido3.isCancelado(), "El tercer partido se cancela por la suspension derivada de acumulacion de amarillas");
        assertNotNull(partido3.getMotivoCancelacion());
    }

    @Test
    public void testCompetenciaUnicoEquipoYPartidos() {
        Equipo mayores = club.crearEquipo("Titanes Mayores", futbol, "Mayores", 18);
        Equipo juvenil = club.crearEquipo("Juveniles", futbol, "Sub-17", 18);
        for (int i = 1; i <= 7; i++) {
            mayores.inscribirJugador(new Socio("m" + i, "123", "Mayor " + i, 20), "Titular", i);
            juvenil.inscribirJugador(new Socio("j" + i, "123", "Juv " + i, 16), "Titular", i);
        }

        CompetenciaLiga cerros = new CompetenciaLiga("Torneo de los Cerros", "2026-II", 7);
        assertFalse(cerros.cumpleRequisitosInscripcion(juvenil), "Solo equipos de categoria Mayores");
        assertTrue(cerros.registrarNominaOficial(mayores, mayores.obtenerJugadoresHabilitados()));
        assertEquals(mayores, cerros.getEquipoInscrito(), "La competencia tiene un unico equipo representante del club");

        List<Socio> alineados = mayores.obtenerJugadoresHabilitados();
        PartidoCompetencia pc = new PartidoCompetencia(cerros, "2026-11-12", "Universidad Nacional", "Cancha CDS", alineados);
        pc.registrarEstadisticasIndividuales(new EstadisticaFutbol(pc, alineados.get(0), 3, 1, 0, 1));
        assertTrue(pc.registrarMarcador("3 - 0"));
        pc.aplicarSuspensionPorTarjetas();
        cerros.agregarPartidoCompetencia(pc);
        cerros.otorgarPuntosSegunDeporte(pc, true, false);

        assertEquals(3, cerros.consultarTablaPosiciones().get("Titanes Mayores").intValue());
        assertEquals(alineados.get(0), cerros.obtenerGoleadorODestacado());
        assertTrue(mayores.buscarInscripcion(alineados.get(0)).tieneSuspensionActiva(), "Tarjeta roja en competencia suspende al jugador");
    }

    @Test
    public void testDisciplinaIndividualReservasNivelesYCalculoELO() {
        Socio sAvanzado = new Socio("av", "123", "Socio Avanzado", 19);
        Socio sIntermedio = new Socio("in", "123", "Socio Intermedio", 19);
        Socio sPrincipiante = new Socio("pr", "123", "Socio Principiante", 19);
        Administrador admin = new Administrador("adm", "123", "Admin");

        sAvanzado.obtenerOCrearPerfil(tenis, "Avanzado", 1200.0);
        sIntermedio.obtenerOCrearPerfil(tenis, "Intermedio", 1100.0);
        sPrincipiante.obtenerOCrearPerfil(tenis, "Principiante", 1000.0);

        CanchaTenis cTenis = new CanchaTenis("Tenis 1");
        CanchaPadel cPadel = new CanchaPadel("Padel 1");
        club.agregarInstalacion(cTenis);
        club.agregarInstalacion(cPadel);

        assertFalse(club.registrarReserva(admin, cTenis, "09:00-10:00", 2, "Sencillos"), "Admin no puede reservar");
        assertFalse(club.registrarReserva(sAvanzado, cPadel, "09:00-10:00", 2, "Sencillos"), "Padel exige Dobles de 4");
        assertTrue(club.registrarReserva(sAvanzado, cPadel, "09:00-10:00", 4, "Dobles"));
        assertTrue(club.registrarReserva(sAvanzado, cTenis, "10:00-11:00", 2, "Sencillos"));

        ReservaInstalacion res = club.getReservas().get(1);
        PartidoPracticaIndividual invalido = new PartidoPracticaIndividual("2026-11-10", tenis, res, sAvanzado, sPrincipiante, "Sencillos", false);
        assertFalse(invalido.puedeDisputarse(), "Avanzado vs Principiante sin entrenador no es valido");

        PartidoPracticaIndividual conProfe = new PartidoPracticaIndividual("2026-11-10", tenis, res, sAvanzado, sPrincipiante, "Dobles", true);
        assertTrue(conProfe.puedeDisputarse(), "Con entrenador se permite cualquier combinacion de niveles");

        PartidoPracticaIndividual valido = new PartidoPracticaIndividual("2026-11-10", tenis, res, sAvanzado, sIntermedio, "Sencillos", false);
        assertTrue(valido.actualizarPuntajeRankingELO(true));
        assertTrue(sAvanzado.buscarPerfil("Tenis").getPuntajeRankingELO() > 1200.0);
        assertTrue(sIntermedio.buscarPerfil("Tenis").getPuntajeRankingELO() < 1100.0);
    }

    @Test
    public void testVentaTiendaYCafeteriaEmpleadoDescuentoYPuntosSimultaneosYSincronizacionInventario() {
        UbicacionInventario bodega = new UbicacionInventario("Bodega Principal");
        UbicacionInventario mostrador = new UbicacionInventario("Mostrador Tienda");
        club.agregarUbicacionInventario(bodega);
        club.agregarUbicacionInventario(mostrador);

        ArticuloTienda raqueta = new ArticuloTienda("Raqueta Pro", "Raquetas", 100000, 10);
        club.agregarArticuloTienda(raqueta);
        bodega.registrarExistenciaInicial(raqueta, 5);
        mostrador.registrarExistenciaInicial(raqueta, 5);

        Entrenador profe = new Entrenador("profe1", "123", "Profe Uno", "DESC_P1");
        profe.setPuntosFidelidad(5000);

        VentaTienda vt = new VentaTienda("2026-11-12", profe);
        assertTrue(vt.agregarArticulo(raqueta, 4));
        assertTrue(vt.aplicarCodigoDescuento(profe), "Empleado aplica 15% de descuento propio");
        assertTrue(vt.redimirPuntosComoDescuento(2000), "Empleado TAMBIEN puede redimir puntos de fidelidad simultaneamente");
        club.registrarVenta(vt);

        assertEquals(6, raqueta.getCantidadDisponible(), "Quedan 6 raquetas en stock general");
        int totalEnUbicaciones = bodega.getCantidadesPorArticulo().get(raqueta) + mostrador.getCantidadesPorArticulo().get(raqueta);
        assertEquals(6, totalEnUbicaciones, "Las ubicaciones fisicas quedan sincronizadas en 6 unidades (1 en mostrador + 5 en bodega)");
        assertTrue(profe.getPuntosFidelidad() > 3000, "El empleado tambien acumula el 2% de puntos de fidelidad sobre su compra");
    }

    @Test
    public void testTorneoInternoCuposPreferenciales20PorcientoYLiberacion() {
        TorneoRecreativo torneo = club.crearTorneoRecreativo("Abierto Tenis", tenis, "Intermedio", "Sencillos", 5, "Sabado", 0.20);
        assertEquals(1, torneo.getCuposPreferencialesReservados(), "20% de 5 cupos = 1 cupo preferencial reservado");
        assertEquals(4, torneo.getCuposGeneralesMaximos(), "4 cupos generales para externos");

        assertTrue(torneo.inscribirParticipanteExterno("Ext 1", "Externo", "Intermedio"));
        assertTrue(torneo.inscribirParticipanteExterno("Ext 2", "Externo", "Intermedio"));
        assertTrue(torneo.inscribirParticipanteExterno("Ext 3", "Externo", "Intermedio"));
        assertTrue(torneo.inscribirParticipanteExterno("Ext 4", "Externo", "Intermedio"));

        assertFalse(torneo.inscribirParticipanteExterno("Ext 5", "Externo", "Intermedio"),
                "El 5to cupo esta reservado como preferencial para miembros del club");

        assertTrue(torneo.liberarCuposPreferenciales());
        assertTrue(torneo.inscribirParticipanteExterno("Ext 5", "Externo", "Intermedio"),
                "Tras liberar los cupos preferenciales, el participante externo puede inscribirse");
    }

    @Test
    public void testTorneoRecreativoBonoNoAcumulableYTorneoCompetitivoReglaEmpleado() {
        Socio socio = new Socio("s1", "123", "Mateo", 18);
        socio.obtenerOCrearPerfil(tenis, "Avanzado", 1200.0);
        Entrenador profeTurnoLunes = new Entrenador("p1", "123", "Juan", "DESC_J");
        TurnoSemanal turnoLunes = club.obtenerOCrearTurno("Lunes", "08:00-12:00");
        turnoLunes.asignarEmpleado(profeTurnoLunes);

        // 1. Torneo Recreativo otorga BonoDescuento no acumulable
        TorneoRecreativo tRec = club.crearTorneoRecreativo("Copa Recreativa", tenis, "Avanzado", "Sencillos", 5, "Sabado", 0.25);
        assertTrue(tRec.inscribirMiembroIndividual(socio, "Avanzado", club.getTurnos()));
        assertTrue(tRec.inscribirMiembroIndividual(profeTurnoLunes, "Avanzado", club.getTurnos()));
        assertTrue(tRec.iniciarTorneo());
        assertNotNull(tRec.registrarEncuentro("Final", socio.getNombre(), profeTurnoLunes.getNombre(), "6-2, 6-3", socio.getNombre()));
        assertTrue(tRec.finalizarTorneo(socio.getNombre()));
        assertEquals(1, socio.getBonosDescuento().size());

        BonoDescuento bono = socio.getBonosDescuento().get(0);
        ArticuloTienda balon = new ArticuloTienda("Balon", "Balones", 100000, 5);
        VentaTienda ventaConBono = new VentaTienda("2026-11-15", socio);
        ventaConBono.agregarArticulo(balon, 1);
        assertTrue(ventaConBono.aplicarBonoTorneoRecreativo(bono));
        assertFalse(ventaConBono.aplicarCodigoDescuento(profeTurnoLunes), "El bono de torneo recreativo NO es acumulable con descuento de empleado");
        assertFalse(ventaConBono.redimirPuntosComoDescuento(500), "El bono de torneo recreativo NO es acumulable con otros descuentos");

        // 2. Torneo Competitivo: Empleado bloqueado si esta de turno; gratis si no esta de turno; $0 premio si gana
        TorneoCompetitivo tLunes = club.crearTorneoCompetitivo("Torneo Lunes", tenis, "Avanzado", "Sencillos", 5, "Lunes", 50000);
        assertFalse(tLunes.inscribirMiembroIndividual(profeTurnoLunes, "Avanzado", club.getTurnos()),
                "Empleado de turno el Lunes no puede inscribirse en torneo del Lunes");

        TorneoCompetitivo tSabado = club.crearTorneoCompetitivo("Torneo Sabado", tenis, "Avanzado", "Sencillos", 5, "Sabado", 50000);
        assertTrue(tSabado.inscribirMiembroIndividual(socio, "Avanzado", club.getTurnos()));
        assertTrue(tSabado.inscribirMiembroIndividual(profeTurnoLunes, "Avanzado", club.getTurnos()));
        assertEquals(50000.0, tSabado.getTotalRecaudado(), 0.01, "El socio paga $50.000 y el empleado se inscribe gratis ($0)");

        assertTrue(tSabado.iniciarTorneo());
        tSabado.registrarEncuentro("Final", profeTurnoLunes.getNombre(), socio.getNombre(), "6-4, 6-4", profeTurnoLunes.getNombre());
        assertTrue(tSabado.finalizarTorneo(profeTurnoLunes.getNombre()));
        assertEquals(0.0, tSabado.getPremioEfectivoEntregado(), 0.01, "Si un empleado gana un torneo competitivo, NO recibe el premio en efectivo");
    }
}
