package uniandes.dpo.cds.modelo;

public class EncuentroTorneo {
    private String rondaOFase;
    private ParticipanteTorneo participanteA;
    private ParticipanteTorneo participanteB;
    private String marcador;
    private ParticipanteTorneo ganador;
    private boolean disputado;

    public EncuentroTorneo(String rondaOFase, ParticipanteTorneo participanteA, ParticipanteTorneo participanteB,
                           String marcador, ParticipanteTorneo ganador) {
        this.rondaOFase = rondaOFase;
        this.participanteA = participanteA;
        this.participanteB = participanteB;
        this.marcador = marcador;
        this.ganador = ganador;
        this.disputado = true;
    }

    public String obtenerResumenEncuentro() {
        String nomGanador = ganador != null ? ganador.getNombreParticipante() : "Empate";
        return "[" + rondaOFase + "] " + participanteA.getNombreParticipante() + " vs " +
               participanteB.getNombreParticipante() + " -> Marcador: " + marcador + " (Ganador: " + nomGanador + ")";
    }

    public String getRondaOFase() {
        return rondaOFase;
    }

    public ParticipanteTorneo getParticipanteA() {
        return participanteA;
    }

    public ParticipanteTorneo getParticipanteB() {
        return participanteB;
    }

    public String getMarcador() {
        return marcador;
    }

    public ParticipanteTorneo getGanador() {
        return ganador;
    }

    public boolean isDisputado() {
        return disputado;
    }
}
