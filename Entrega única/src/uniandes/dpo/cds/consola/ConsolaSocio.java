package uniandes.dpo.cds.consola;

import uniandes.dpo.cds.modelo.*;
import uniandes.dpo.cds.persistencia.ManejadorPersistencia;

import java.util.List;

public class ConsolaSocio {
    private ClubDeportivoSeneca club;
    private ManejadorPersistencia persistencia;
    private LectorConsola lector;
    private Socio socioActual;

    public ConsolaSocio() {
        this.persistencia = new ManejadorPersistencia("data");
        this.club = new ClubDeportivoSeneca();
        this.persistencia.cargarTodo(this.club);
        this.lector = new LectorConsola();
    }

    public static void main(String[] args) {
        ConsolaSocio consola = new ConsolaSocio();
        consola.ejecutar();
    }

    public void ejecutar() {
        System.out.println("==================================================================");
        System.out.println("   CLUB DEPORTIVO SÉNECA (CDS) - CONSOLA DE SOCIOS");
        System.out.println("==================================================================");
        System.out.println("Datos cargados automaticamente desde la carpeta 'data/'.");
        System.out.println("Usuarios Socio de prueba precargados: socio_mateo / socio_valeria / socio_carlos (clave: pass123)");

        if (!autenticarSocio()) {
            return;
        }

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = lector.leerEntero("Seleccione una opcion: ", 0, 8);
            switch (opcion) {
                case 1:
                    consultarMiPerfilYBeneficios();
                    break;
                case 2:
                    inscribirmeEnEquipoDelClub();
                    break;
                case 3:
                    reservarInstalacionIndividual();
                    break;
                case 4:
                    registrarPartidoPracticaIndividual();
                    break;
                case 5:
                    comprarEnTiendaDeportiva();
                    break;
                case 6:
                    comprarEnCafeteria();
                    break;
                case 7:
                    gestionarInscripcionYRetiroTorneosInternos();
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
        System.out.println("\n[OK] Datos guardados automaticamente en 'data/'. ¡Hasta pronto, " + socioActual.getNombre() + "!");
    }

    private boolean autenticarSocio() {
        for (int intento = 1; intento <= 3; intento++) {
            System.out.println("\n--- Autenticacion de Socio (Intento " + intento + "/3) ---");
            String login = lector.leerTexto("Login: ");
            if (login.isEmpty()) {
                return false;
            }
            String pass = lector.leerTexto("Password: ");
            Usuario u = club.autenticarUsuario(login, pass);
            if (u instanceof Socio) {
                this.socioActual = (Socio) u;
                System.out.println("Bienvenido(a), " + socioActual.getNombre() + " [Categoria: " + socioActual.getCategoriaEdad() + "]");
                return true;
            }
            System.out.println("  [!] Credenciales invalidas o el usuario no corresponde al rol Socio.");
        }
        System.out.println("Maximo de intentos alcanzado. Saliendo de la consola.");
        return false;
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n-------------------- MENU DE SOCIO --------------------");
        System.out.println("1. Consultar mi perfil, rankings ELO, puntos de fidelidad y bonos");
        System.out.println("2. Inscribirme en un equipo del club (Deportes de Conjunto)");
        System.out.println("3. Reservar instalacion deportiva (Tenis, Padel, Tenis de Mesa)");
        System.out.println("4. Registrar partido de practica individual y actualizar ELO");
        System.out.println("5. Comprar en la Tienda Deportiva (IVA 19%, codigo empleado, puntos o bono)");
        System.out.println("6. Comprar en la Cafeteria (Alergenos, bebida caliente, propina, puntos o bono)");
        System.out.println("7. Torneos Internos: Inscribirme o retirarme antes de iniciar");
        System.out.println("8. Consultar estado e historial detallado de Torneos Internos");
        System.out.println("0. Salir y guardar automaticamente");
    }

    private void consultarMiPerfilYBeneficios() {
        System.out.println("\n=== PERFIL DEL SOCIO ===");
        System.out.println("Nombre: " + socioActual.getNombre() + " (" + socioActual.getLogin() + ")");
        System.out.println("Edad: " + socioActual.getEdad() + " anos | Categoria: " + socioActual.getCategoriaEdad());
        System.out.println("Puntos de fidelidad acumulados: " + socioActual.getPuntosFidelidad() + " pts ($" + socioActual.getPuntosFidelidad() + ")");
        System.out.println("¿Tiene bebida caliente en mano?: " + (socioActual.isTieneBebidaCaliente() ? "Si" : "No"));

        System.out.println("\n-- Equipos a los que pertenece --");
        if (socioActual.getInscripcionesEquipo().isEmpty()) {
            System.out.println("  Ninguno.");
        } else {
            for (int i = 0; i < socioActual.getInscripcionesEquipo().size(); i++) {
                InscripcionEquipo ins = socioActual.getInscripcionesEquipo().get(i);
                System.out.println("  * " + ins.getEquipo().getNombre() + " (" + ins.getEquipo().getDeporte().getNombre()
                        + ") - Posicion: " + ins.getPosicion() + " | Camiseta #" + ins.getNumeroCamisetaUnico()
                        + " | Suspendido: " + (ins.tieneSuspensionActiva() ? "Si" : "No"));
            }
        }

        System.out.println("\n-- Perfiles de Academia Individual y Ranking ELO --");
        for (int i = 0; i < socioActual.getPerfilesAcademia().size(); i++) {
            PerfilAcademiaIndividual p = socioActual.getPerfilesAcademia().get(i);
            System.out.println("  * " + p.getDisciplina().getNombre() + ": Nivel " + p.getNivel()
                    + " | ELO: " + Math.round(p.getPuntajeRankingELO()));
        }

        System.out.println("\n-- Bonos de Descuento de Torneos Recreativos --");
        if (socioActual.getBonosDescuento().isEmpty()) {
            System.out.println("  No tiene bonos registrados.");
        } else {
            for (int i = 0; i < socioActual.getBonosDescuento().size(); i++) {
                BonoDescuento b = socioActual.getBonosDescuento().get(i);
                System.out.println("  * Codigo: " + b.getCodigo() + " (" + Math.round(b.getPorcentajeDescuento() * 100)
                        + "% desc. - Torneo: " + b.getNombreTorneoOrigen() + ") -> "
                        + (b.isRedimido() ? "YA REDIMIDO" : "DISPONIBLE"));
            }
        }
    }

    private void inscribirmeEnEquipoDelClub() {
        List<Equipo> equipos = club.getEquipos();
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }
        System.out.println("\n=== EQUIPOS DEL CLUB ===");
        for (int i = 0; i < equipos.size(); i++) {
            Equipo eq = equipos.get(i);
            System.out.println((i + 1) + ". " + eq.getNombre() + " | Deporte: " + eq.getDeporte().getNombre()
                    + " | Categoria: " + eq.getCategoria() + " | Inscritos: " + eq.getNomina().size() + "/" + eq.getCupoMaximoJugadores());
        }
        int idx = lector.leerEntero("Seleccione el numero del equipo (0 para cancelar): ", 0, equipos.size());
        if (idx == 0) return;

        Equipo elegido = equipos.get(idx - 1);
        String posicion = lector.leerTexto("Ingrese la posicion en la que jugara (ej. Delantero, Defensa, Armador): ");
        int camiseta = lector.leerEntero("Ingrese el numero de camiseta deseado (1-99): ", 1, 99);

        if (elegido.inscribirJugador(socioActual, posicion, camiseta)) {
            System.out.println("  [OK] Inscripcion exitosa en " + elegido.getNombre() + " con camiseta #" + camiseta + ".");
        } else {
            System.out.println("  [X] No fue posible inscribirse. Verifique que su categoria de edad (" + socioActual.getCategoriaEdad()
                    + ") coincida con el equipo (" + elegido.getCategoria() + "), que no pertenezca ya a otro equipo del mismo deporte y que la camiseta este libre.");
        }
    }

    private void reservarInstalacionIndividual() {
        List<Instalacion> insts = club.getInstalaciones();
        System.out.println("\n=== INSTALACIONES DISPONIBLES ===");
        for (int i = 0; i < insts.size(); i++) {
            Instalacion ins = insts.get(i);
            System.out.println((i + 1) + ". " + ins.getNombre() + " [" + ins.getNombreDisciplina()
                    + "] | Permite bebida caliente: " + (ins.permiteBebidaCaliente() ? "Si" : "No"));
        }
        int idx = lector.leerEntero("Seleccione instalacion (0 para cancelar): ", 0, insts.size());
        if (idx == 0) return;

        Instalacion elegida = insts.get(idx - 1);
        String bloque = lector.leerTexto("Ingrese bloque horario (ej. 10:00-11:00): ");
        String modalidad = lector.leerTexto("Ingrese modalidad (Sencillos / Dobles): ");
        int cantJug = lector.leerEntero("Ingrese cantidad de jugadores (2 o 4): ", 2, 4);

        if (club.registrarReserva(socioActual, elegida, bloque, cantJug, modalidad)) {
            System.out.println("  [OK] Reserva confirmada en " + elegida.getNombre() + " para el bloque " + bloque + ".");
        } else {
            System.out.println("  [X] Reserva rechazada. Verifique modalidad/jugadores (Padel exige Dobles de 4 jugadores), cupo simultaneo o restriccion de bebida caliente.");
        }
    }

    private void registrarPartidoPracticaIndividual() {
        if (club.getReservas().isEmpty()) {
            System.out.println("Primero debe existir al menos una reserva de instalacion.");
            return;
        }
        String nomDisc = lector.leerTexto("Disciplina (Tenis / Pádel / Tenis de Mesa): ");
        Deporte d = club.buscarDeporte(nomDisc);
        if (!(d instanceof DisciplinaIndividual)) {
            System.out.println("  [!] Disciplina individual no encontrada.");
            return;
        }
        DisciplinaIndividual disc = (DisciplinaIndividual) d;
        String loginRival = lector.leerTexto("Login del socio rival (ej. socio_valeria, socio_lucas): ");
        Usuario uRival = club.buscarUsuario(loginRival);
        if (!(uRival instanceof Socio) || uRival == socioActual) {
            System.out.println("  [!] Debe indicar otro Socio valido como rival.");
            return;
        }
        Socio rival = (Socio) uRival;
        boolean conEntrenador = lector.leerSiNo("¿Participa un Entrenador en el partido (permite cualquier combinacion de niveles)?");
        String fecha = lector.leerTexto("Fecha del partido (YYYY-MM-DD): ");
        ReservaInstalacion res = club.getReservas().get(club.getReservas().size() - 1);

        PartidoPracticaIndividual partido = new PartidoPracticaIndividual(fecha, disc, res, socioActual, rival, "Sencillos", conEntrenador);
        if (!partido.puedeDisputarse()) {
            System.out.println("  [X] El partido no puede disputarse: los socios no tienen niveles iguales o adyacentes en " + disc.getNombre() + ".");
            return;
        }
        boolean ganeYo = lector.leerSiNo("¿Gano usted (" + socioActual.getNombre() + ") el encuentro?");
        partido.actualizarPuntajeRankingELO(ganeYo);
        club.getPartidos().add(partido);
        System.out.println("  [OK] Partido registrado. Ranking ELO actualizado correctamente.");
    }

    private void comprarEnTiendaDeportiva() {
        List<ArticuloTienda> arts = club.getArticulosTienda();
        System.out.println("\n=== CATALOGO TIENDA DEPORTIVA (IVA 19%) ===");
        for (int i = 0; i < arts.size(); i++) {
            ArticuloTienda a = arts.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " [" + a.getCategoria() + "] - $"
                    + Math.round(a.getPrecio()) + " + IVA | Stock disponible: " + a.getCantidadDisponible());
        }
        int idx = lector.leerEntero("Seleccione articulo a comprar (0 para cancelar): ", 0, arts.size());
        if (idx == 0) return;

        ArticuloTienda elegido = arts.get(idx - 1);
        int cant = lector.leerEntero("Cantidad a comprar: ", 1, 100);
        String fecha = lector.leerTexto("Fecha de compra (YYYY-MM-DD): ");

        VentaTienda venta = new VentaTienda(fecha, socioActual);
        if (!venta.agregarArticulo(elegido, cant)) {
            System.out.println("  [X] Stock insuficiente para completar la compra.");
            return;
        }
        aplicarBeneficiosEnVenta(venta);
        club.registrarVenta(venta);
        mostrarResumenFactura(venta);
    }

    private void comprarEnCafeteria() {
        List<Instalacion> insts = club.getInstalaciones();
        System.out.println("\nSeleccione la instalacion donde consumira el pedido:");
        for (int i = 0; i < insts.size(); i++) {
            System.out.println((i + 1) + ". " + insts.get(i).getNombre());
        }
        int idxInst = lector.leerEntero("Instalacion (1-" + insts.size() + "): ", 1, insts.size());
        Instalacion instConsumo = insts.get(idxInst - 1);

        List<ProductoCafeteria> prods = club.getProductosCafeteria();
        System.out.println("\n=== MENU DE CAFETERIA (Impoconsumo 8% + Propina sugerida 10%) ===");
        for (int i = 0; i < prods.size(); i++) {
            ProductoCafeteria p = prods.get(i);
            String extra = (p instanceof Snack) ? ((Snack) p).informarAlergenosAlSocio()
                    : (((Bebida) p).isEsCaliente() ? "Bebida Caliente" : "Bebida Fria");
            System.out.println((i + 1) + ". " + p.getNombre() + " - $" + Math.round(p.getPrecio()) + " (" + extra + ")");
        }
        int idxProd = lector.leerEntero("Seleccione producto (0 para cancelar): ", 0, prods.size());
        if (idxProd == 0) return;

        ProductoCafeteria prod = prods.get(idxProd - 1);
        int cant = lector.leerEntero("Cantidad: ", 1, 50);
        String fecha = lector.leerTexto("Fecha (YYYY-MM-DD): ");

        VentaCafeteria venta = new VentaCafeteria(fecha, socioActual, instConsumo);
        if (!venta.agregarProductoCafeteria(prod, cant)) {
            System.out.println("  [X] Seguridad: No se permite despachar bebidas calientes en " + instConsumo.getNombre() + ".");
            return;
        }
        if (lector.leerSiNo("¿Desea modificar el porcentaje de propina sugerida (10%)?")) {
            double nuevaProp = lector.leerDoublePositivo("Ingrese nuevo porcentaje en decimal (ej. 0.05 o 0.0): ");
            venta.modificarPropina(nuevaProp);
        }
        aplicarBeneficiosEnVenta(venta);
        club.registrarVenta(venta);
        mostrarResumenFactura(venta);
    }

    private void aplicarBeneficiosEnVenta(Venta venta) {
        if (!socioActual.getBonosDescuento().isEmpty() && lector.leerSiNo("¿Desea redimir un Bono de Descuento de Torneo Recreativo (no acumulable con otros descuentos)?")) {
            String codBono = lector.leerTexto("Ingrese el codigo del bono: ");
            BonoDescuento b = socioActual.buscarBonoDisponible(codBono);
            if (b != null && venta.aplicarBonoTorneoRecreativo(b)) {
                System.out.println("  [OK] Bono " + b.getCodigo() + " aplicado (" + Math.round(b.getPorcentajeDescuento() * 100) + "% de descuento).");
                return;
            } else {
                System.out.println("  [!] Bono no encontrado o ya redimido.");
            }
        }
        if (lector.leerSiNo("¿Tiene codigo de descuento compartido por un Empleado (8% de descuento)?")) {
            String loginOCod = lector.leerTexto("Ingrese login del empleado o codigo (ej. profe_juan o DESC_JUAN): ");
            Empleado emp = buscarEmpleadoPorLoginOCodigo(loginOCod);
            if (emp != null && venta.aplicarCodigoDescuento(emp)) {
                System.out.println("  [OK] Descuento del 8% aplicado con codigo de " + emp.getNombre() + ".");
            } else {
                System.out.println("  [!] Codigo de empleado no valido.");
            }
        }
        if (socioActual.getPuntosFidelidad() > 0 && lector.leerSiNo("Tiene " + socioActual.getPuntosFidelidad() + " puntos. ¿Desea redimir puntos como descuento?")) {
            int pts = lector.leerEntero("Cantidad de puntos a redimir (max " + socioActual.getPuntosFidelidad() + "): ", 1, socioActual.getPuntosFidelidad());
            if (venta.redimirPuntosComoDescuento(pts)) {
                System.out.println("  [OK] Se redimieron " + pts + " puntos ($" + pts + ").");
            }
        }
    }

    private Empleado buscarEmpleadoPorLoginOCodigo(String texto) {
        for (int i = 0; i < club.getUsuarios().size(); i++) {
            Usuario u = club.getUsuarios().get(i);
            if (u instanceof Empleado) {
                Empleado e = (Empleado) u;
                if (e.getLogin().equalsIgnoreCase(texto) || e.getCodigoDescuento().equalsIgnoreCase(texto)) {
                    return e;
                }
            }
        }
        return null;
    }

    private void mostrarResumenFactura(Venta v) {
        System.out.println("\n--- RESUMEN DE COMPRA (" + v.getRubroContable() + ") ---");
        System.out.println("Subtotal:               $" + Math.round(v.getSubtotal()));
        System.out.println("Descuento empleado:     $" + Math.round(v.getDescuentoEmpleado()));
        System.out.println("Descuento bono torneo:  $" + Math.round(v.getDescuentoBono()));
        System.out.println("Puntos redimidos:       $" + v.getPuntosRedimidos());
        System.out.println("VALOR TOTAL PAGADO:     $" + Math.round(v.getValorTotal()));
        System.out.println("Puntos ganados (2%):    +" + v.getPuntosFidelidadGenerados() + " pts (Nuevo saldo: " + socioActual.getPuntosFidelidad() + " pts)");
    }

    private void gestionarInscripcionYRetiroTorneosInternos() {
        List<TorneoInterno> torneos = club.getTorneosInternos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos internos registrados.");
            return;
        }
        System.out.println("\n=== TORNEOS INTERNOS DEL CLUB ===");
        for (int i = 0; i < torneos.size(); i++) {
            TorneoInterno t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " [" + t.getTipoTorneo() + " - " + t.getDeporte().getNombre()
                    + " - " + t.getModalidad() + "] | Estado: " + t.getEstado()
                    + " | Cupos: " + t.getParticipantes().size() + "/" + t.getMaxCupos());
        }
        int idx = lector.leerEntero("Seleccione un torneo (0 para cancelar): ", 0, torneos.size());
        if (idx == 0) return;

        TorneoInterno t = torneos.get(idx - 1);
        System.out.println("1. Inscribirme (o inscribir mi equipo del club) en este torneo");
        System.out.println("2. Retirarme de este torneo (solo antes de que inicie)");
        int acc = lector.leerEntero("Accion: ", 1, 2);

        if (acc == 1) {
            if (t.getDeporte() instanceof DeporteConjunto) {
                String nomEq = lector.leerTexto("Nombre del equipo del club a inscribir (ej. Halcones CDS, Titanes Mayores CDS): ");
                Equipo eq = club.buscarEquipo(nomEq);
                if (t.inscribirEquipoClub(eq)) {
                    System.out.println("  [OK] Equipo '" + eq.getNombre() + "' inscrito exitosamente en " + t.getNombre() + ".");
                } else {
                    System.out.println("  [X] No se pudo inscribir el equipo (verifique estado del torneo, deporte, categoria, minimo de jugadores o cupos).");
                }
            } else {
                String niv = lector.leerTexto("Nivel declarado para el torneo (" + t.getCategoriaONivel() + "): ");
                if (t.inscribirMiembroIndividual(socioActual, niv, club.getTurnos())) {
                    System.out.println("  [OK] Inscripcion individual exitosa en " + t.getNombre() + ".");
                } else {
                    System.out.println("  [X] No fue posible inscribirse (verifique nivel, cupos disponibles o si ya estaba inscrito).");
                }
            }
        } else {
            String nomRetiro = socioActual.getNombre();
            if (t.getDeporte() instanceof DeporteConjunto) {
                nomRetiro = lector.leerTexto("Nombre del equipo a retirar: ");
            }
            if (t.retirarParticipante(nomRetiro)) {
                System.out.println("  [OK] Participante '" + nomRetiro + "' retirado. El cupo ha sido liberado.");
            } else {
                System.out.println("  [X] No se pudo retirar (el torneo ya inicio o el participante no figura inscrito).");
            }
        }
    }

    private void consultarEstadoEHistorialTorneos() {
        List<TorneoInterno> torneos = club.getTorneosInternos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos internos registrados.");
            return;
        }
        System.out.println("\n=== ESTADO E HISTORIAL DETALLADO DE TORNEOS INTERNOS ===");
        for (int i = 0; i < torneos.size(); i++) {
            System.out.println("\n" + torneos.get(i).consultarHistorialDetallado());
        }
    }
}
