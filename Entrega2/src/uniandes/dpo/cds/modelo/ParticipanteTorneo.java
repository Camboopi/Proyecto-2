package uniandes.dpo.cds.modelo;

public class ParticipanteTorneo {
    private String nombreParticipante;
    private String tipoParticipante;
    private String categoriaONivel;
    private Equipo equipoClub;
    private Usuario usuarioPrincipal;
    private Usuario segundoUsuarioPareja;
    private boolean esCupoPreferencial;
    private double montoPagado;

    public ParticipanteTorneo(String nombreParticipante, String tipoParticipante, String categoriaONivel,
                              Equipo equipoClub, Usuario usuarioPrincipal, boolean esCupoPreferencial, double montoPagado) {
        this.nombreParticipante = nombreParticipante;
        this.tipoParticipante = tipoParticipante;
        this.categoriaONivel = categoriaONivel;
        this.equipoClub = equipoClub;
        this.usuarioPrincipal = usuarioPrincipal;
        this.esCupoPreferencial = esCupoPreferencial;
        this.montoPagado = montoPagado;
    }

    public boolean esEmpleado() {
        if (usuarioPrincipal instanceof Empleado || segundoUsuarioPareja instanceof Empleado) {
            return true;
        }
        return "Empleado".equalsIgnoreCase(tipoParticipante);
    }

    public boolean esMiembroDelClub() {
        return equipoClub != null || usuarioPrincipal instanceof Socio || usuarioPrincipal instanceof Empleado;
    }

    public void setSegundoUsuarioPareja(Usuario segundoUsuarioPareja) {
        this.segundoUsuarioPareja = segundoUsuarioPareja;
    }

    public String getNombreParticipante() {
        return nombreParticipante;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public String getCategoriaONivel() {
        return categoriaONivel;
    }

    public Equipo getEquipoClub() {
        return equipoClub;
    }

    public Usuario getUsuarioPrincipal() {
        return usuarioPrincipal;
    }

    public Usuario getSegundoUsuarioPareja() {
        return segundoUsuarioPareja;
    }

    public boolean isEsCupoPreferencial() {
        return esCupoPreferencial;
    }

    public double getMontoPagado() {
        return montoPagado;
    }
}
