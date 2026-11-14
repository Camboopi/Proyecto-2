package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public class ClubDeportivoSeneca {
    private String nombre;
    private int minEntrenadoresPorTurno;
    private int minFisioterapeutasPorTurno;

    private List<Usuario> usuarios;
    private List<Deporte> deportes;
    private List<Equipo> equipos;
    private List<Instalacion> instalaciones;
    private List<ReservaInstalacion> reservas;
    private List<SesionEntrenamiento> sesionesEntrenamiento;
    private List<Partido> partidos;
    private List<Competencia> competencias;
    private List<TorneoInterno> torneosInternos;
    private List<ArticuloTienda> articulosTienda;
    private List<UbicacionInventario> ubicacionesInventario;
    private List<ProductoCafeteria> productosCafeteria;
    private List<Venta> ventas;
    private List<TurnoSemanal> turnos;
    private List<SolicitudCambioTurno> solicitudesCambioTurno;
    private List<SugerenciaProducto> sugerencias;

    public ClubDeportivoSeneca() {
        this.nombre = "Club Deportivo Séneca";
        this.minEntrenadoresPorTurno = 2;
        this.minFisioterapeutasPorTurno = 1;
        this.usuarios = new ArrayList<Usuario>();
        this.deportes = new ArrayList<Deporte>();
        this.equipos = new ArrayList<Equipo>();
        this.instalaciones = new ArrayList<Instalacion>();
        this.reservas = new ArrayList<ReservaInstalacion>();
        this.sesionesEntrenamiento = new ArrayList<SesionEntrenamiento>();
        this.partidos = new ArrayList<Partido>();
        this.competencias = new ArrayList<Competencia>();
        this.torneosInternos = new ArrayList<TorneoInterno>();
        this.articulosTienda = new ArrayList<ArticuloTienda>();
        this.ubicacionesInventario = new ArrayList<UbicacionInventario>();
        this.productosCafeteria = new ArrayList<ProductoCafeteria>();
        this.ventas = new ArrayList<Venta>();
        this.turnos = new ArrayList<TurnoSemanal>();
        this.solicitudesCambioTurno = new ArrayList<SolicitudCambioTurno>();
        this.sugerencias = new ArrayList<SugerenciaProducto>();
    }

    public Usuario autenticarUsuario(String login, String pass) {
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            if (u.getLogin().equalsIgnoreCase(login) && u.autenticar(pass)) {
                return u;
            }
        }
        return null;
    }

    public boolean verificarDisponibilidadTurnos() {
        for (int i = 0; i < turnos.size(); i++) {
            if (!turnos.get(i).cumpleMin2EntrenadoresY1Fisio()) {
                return false;
            }
        }
        return true;
    }

    public boolean empleadoEstaDeTurno(Empleado emp, String dia) {
        if (emp == null || dia == null) {
            return false;
        }
        for (int i = 0; i < turnos.size(); i++) {
            TurnoSemanal t = turnos.get(i);
            if (t.getDia().equalsIgnoreCase(dia) && t.getEmpleadosAsignados().contains(emp)) {
                return true;
            }
        }
        return false;
    }

    public void agregarUsuario(Usuario u) {
        this.usuarios.add(u);
    }

    public Usuario buscarUsuario(String login) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getLogin().equalsIgnoreCase(login)) {
                return usuarios.get(i);
            }
        }
        return null;
    }

    public void agregarDeporte(Deporte d) {
        this.deportes.add(d);
    }

    public Deporte buscarDeporte(String nombreDep) {
        for (int i = 0; i < deportes.size(); i++) {
            if (deportes.get(i).getNombre().equalsIgnoreCase(nombreDep)) {
                return deportes.get(i);
            }
        }
        return null;
    }

    public Equipo crearEquipo(String nombreEq, DeporteConjunto deporte, String categoria, int cupo) {
        Equipo eq = new Equipo(nombreEq, deporte, categoria, cupo);
        this.equipos.add(eq);
        return eq;
    }

    public boolean darDeBajaEquipo(String nombreEquipo) {
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getNombre().equalsIgnoreCase(nombreEquipo)) {
                equipos.remove(i);
                return true;
            }
        }
        return false;
    }

    public Equipo buscarEquipo(String nombreEq) {
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getNombre().equalsIgnoreCase(nombreEq)) {
                return equipos.get(i);
            }
        }
        return null;
    }

    public void agregarInstalacion(Instalacion inst) {
        this.instalaciones.add(inst);
    }

    public Instalacion buscarInstalacion(String nombreInst) {
        for (int i = 0; i < instalaciones.size(); i++) {
            if (instalaciones.get(i).getNombre().equalsIgnoreCase(nombreInst)) {
                return instalaciones.get(i);
            }
        }
        return null;
    }

    public boolean registrarReserva(Usuario solicitante, Instalacion inst, String bloqueHorario, int cantJugadores, String modalidad) {
        if (solicitante == null || inst == null || !solicitante.puedeReservarUsoPersonal()) {
            return false;
        }
        if (!inst.admiteModalidadYJugadores(modalidad, cantJugadores)) {
            return false;
        }
        ReservaInstalacion nueva = new ReservaInstalacion(solicitante, inst, bloqueHorario, cantJugadores, modalidad);
        if (solicitante instanceof Socio && !nueva.validarSinBebidaCaliente((Socio) solicitante)) {
            return false;
        }
        Deporte d = buscarDeporte(inst.getNombreDisciplina());
        int maxSimultaneas = 2;
        if (d instanceof DisciplinaIndividual) {
            maxSimultaneas = ((DisciplinaIndividual) d).getMaxInstalacionesSimultaneas();
        }
        if (!nueva.hayCupoSimultaneoEnDisciplina(reservas, maxSimultaneas)) {
            return false;
        }
        reservas.add(nueva);
        return true;
    }

    public void agregarArticuloTienda(ArticuloTienda a) {
        this.articulosTienda.add(a);
    }

    public ArticuloTienda buscarArticuloTienda(String nombreArt) {
        for (int i = 0; i < articulosTienda.size(); i++) {
            if (articulosTienda.get(i).getNombre().equalsIgnoreCase(nombreArt)) {
                return articulosTienda.get(i);
            }
        }
        return null;
    }

    public void agregarUbicacionInventario(UbicacionInventario u) {
        this.ubicacionesInventario.add(u);
    }

    public UbicacionInventario buscarUbicacionInventario(String nombreUbic) {
        for (int i = 0; i < ubicacionesInventario.size(); i++) {
            if (ubicacionesInventario.get(i).getNombreUbicacion().equalsIgnoreCase(nombreUbic)) {
                return ubicacionesInventario.get(i);
            }
        }
        return null;
    }

    /**
     * Descuenta las unidades vendidas primero del Mostrador Tienda y, si faltan, de la Bodega Principal
     * para mantener sincronizado el stock general con las ubicaciones fisicas.
     */
    public void descontarDeUbicacionesInventario(ArticuloTienda art, int cantidad) {
        if (art == null || cantidad <= 0 || ubicacionesInventario.isEmpty()) {
            return;
        }
        int pendiente = cantidad;
        UbicacionInventario mostrador = buscarUbicacionInventario("Mostrador Tienda");
        if (mostrador != null) {
            pendiente = pendiente - mostrador.descontarExistencia(art, pendiente);
        }
        for (int i = 0; i < ubicacionesInventario.size() && pendiente > 0; i++) {
            UbicacionInventario u = ubicacionesInventario.get(i);
            if (u != mostrador) {
                pendiente = pendiente - u.descontarExistencia(art, pendiente);
            }
        }
    }

    public void agregarProductoCafeteria(ProductoCafeteria p) {
        this.productosCafeteria.add(p);
    }

    public ProductoCafeteria buscarProductoCafeteria(String nombreProd) {
        for (int i = 0; i < productosCafeteria.size(); i++) {
            if (productosCafeteria.get(i).getNombre().equalsIgnoreCase(nombreProd)) {
                return productosCafeteria.get(i);
            }
        }
        return null;
    }

    public void registrarVenta(Venta v) {
        if (v == null) {
            return;
        }
        if (v instanceof VentaTienda) {
            VentaTienda vt = (VentaTienda) v;
            if (!vt.isUbicacionesSincronizadas()) {
                for (int i = 0; i < vt.getDetalles().size(); i++) {
                    DetalleVenta det = vt.getDetalles().get(i);
                    if (det.getArticuloTienda() != null) {
                        descontarDeUbicacionesInventario(det.getArticuloTienda(), det.getCantidad());
                    }
                }
                vt.setUbicacionesSincronizadas(true);
            }
        }
        v.acreditarPuntosSocio();
        this.ventas.add(v);
    }

    public TurnoSemanal obtenerOCrearTurno(String dia, String horario) {
        for (int i = 0; i < turnos.size(); i++) {
            TurnoSemanal t = turnos.get(i);
            if (t.getDia().equalsIgnoreCase(dia) && t.getHorarioAtencion().equalsIgnoreCase(horario)) {
                return t;
            }
        }
        TurnoSemanal nuevo = new TurnoSemanal(dia, horario);
        turnos.add(nuevo);
        return nuevo;
    }

    public TorneoRecreativo crearTorneoRecreativo(String nombreTorneo, Deporte deporte, String categoriaONivel,
                                                  String modalidad, int maxCupos, String diaTorneo, double porcentajeBono) {
        TorneoRecreativo t = new TorneoRecreativo(nombreTorneo, deporte, categoriaONivel, modalidad, maxCupos, diaTorneo, porcentajeBono);
        this.torneosInternos.add(t);
        return t;
    }

    public TorneoCompetitivo crearTorneoCompetitivo(String nombreTorneo, Deporte deporte, String categoriaONivel,
                                                    String modalidad, int maxCupos, String diaTorneo, double tarifaInscripcion) {
        TorneoCompetitivo t = new TorneoCompetitivo(nombreTorneo, deporte, categoriaONivel, modalidad, maxCupos, diaTorneo, tarifaInscripcion);
        this.torneosInternos.add(t);
        return t;
    }

    public TorneoInterno buscarTorneoInterno(String nombreTorneo) {
        for (int i = 0; i < torneosInternos.size(); i++) {
            if (torneosInternos.get(i).getNombre().equalsIgnoreCase(nombreTorneo)) {
                return torneosInternos.get(i);
            }
        }
        return null;
    }

    public Competencia buscarCompetencia(String nombreComp) {
        for (int i = 0; i < competencias.size(); i++) {
            if (competencias.get(i).getNombre().equalsIgnoreCase(nombreComp)) {
                return competencias.get(i);
            }
        }
        return null;
    }

    public String getNombre() { return nombre; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Deporte> getDeportes() { return deportes; }
    public List<Equipo> getEquipos() { return equipos; }
    public List<Instalacion> getInstalaciones() { return instalaciones; }
    public List<ReservaInstalacion> getReservas() { return reservas; }
    public List<SesionEntrenamiento> getSesionesEntrenamiento() { return sesionesEntrenamiento; }
    public List<Partido> getPartidos() { return partidos; }
    public List<Competencia> getCompetencias() { return competencias; }
    public List<TorneoInterno> getTorneosInternos() { return torneosInternos; }
    public List<ArticuloTienda> getArticulosTienda() { return articulosTienda; }
    public List<UbicacionInventario> getUbicacionesInventario() { return ubicacionesInventario; }
    public List<ProductoCafeteria> getProductosCafeteria() { return productosCafeteria; }
    public List<Venta> getVentas() { return ventas; }
    public List<TurnoSemanal> getTurnos() { return turnos; }
    public List<SolicitudCambioTurno> getSolicitudesCambioTurno() { return solicitudesCambioTurno; }
    public List<SugerenciaProducto> getSugerencias() { return sugerencias; }
}
