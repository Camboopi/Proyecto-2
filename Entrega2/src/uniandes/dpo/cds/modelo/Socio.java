package uniandes.dpo.cds.modelo;

import java.util.ArrayList;
import java.util.List;

public class Socio extends Usuario {
    private int edad;
    private String categoriaEdad;
    private boolean tieneBebidaCaliente;
    private List<PerfilAcademiaIndividual> perfilesAcademia;
    private List<InscripcionEquipo> inscripcionesEquipo;
    private List<BonoDescuento> bonosDescuento;

    public Socio(String login, String password, String nombre, int edad) {
        super(login, password, nombre);
        this.edad = edad;
        this.categoriaEdad = definirCategoriaEdad(edad);
        this.tieneBebidaCaliente = false;
        this.perfilesAcademia = new ArrayList<PerfilAcademiaIndividual>();
        this.inscripcionesEquipo = new ArrayList<InscripcionEquipo>();
        this.bonosDescuento = new ArrayList<BonoDescuento>();
    }

    private String definirCategoriaEdad(int edadSocio) {
        if (edadSocio <= 13) {
            return "Sub-13";
        }
        if (edadSocio <= 15) {
            return "Sub-15";
        }
        if (edadSocio <= 17) {
            return "Sub-17";
        }
        return "Mayores";
    }

    @Override
    public boolean puedeReservarUsoPersonal() {
        return true;
    }

    public boolean correspondeACategoria(String cat) {
        return this.categoriaEdad.equalsIgnoreCase(cat);
    }

    public boolean perteneceAEquipoEnDeporte(DeporteConjunto dep) {
        for (int i = 0; i < inscripcionesEquipo.size(); i++) {
            InscripcionEquipo insc = inscripcionesEquipo.get(i);
            if (insc.getEquipo().getDeporte().getNombre().equalsIgnoreCase(dep.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void agregarInscripcionEquipo(InscripcionEquipo insc) {
        this.inscripcionesEquipo.add(insc);
    }

    public PerfilAcademiaIndividual obtenerOCrearPerfil(DisciplinaIndividual disciplina, String nivelInicial, double eloInicial) {
        for (int i = 0; i < perfilesAcademia.size(); i++) {
            PerfilAcademiaIndividual p = perfilesAcademia.get(i);
            if (p.getDisciplina().getNombre().equalsIgnoreCase(disciplina.getNombre())) {
                return p;
            }
        }
        PerfilAcademiaIndividual nuevo = new PerfilAcademiaIndividual(this, disciplina, nivelInicial, eloInicial);
        perfilesAcademia.add(nuevo);
        return nuevo;
    }

    public PerfilAcademiaIndividual buscarPerfil(String nombreDisciplina) {
        for (int i = 0; i < perfilesAcademia.size(); i++) {
            PerfilAcademiaIndividual p = perfilesAcademia.get(i);
            if (p.getDisciplina().getNombre().equalsIgnoreCase(nombreDisciplina)) {
                return p;
            }
        }
        return null;
    }

    public void agregarBonoDescuento(BonoDescuento bono) {
        if (bono != null) {
            this.bonosDescuento.add(bono);
        }
    }

    public BonoDescuento buscarBonoDisponible(String codigo) {
        for (int i = 0; i < bonosDescuento.size(); i++) {
            BonoDescuento b = bonosDescuento.get(i);
            if (b.getCodigo().equalsIgnoreCase(codigo) && b.puedeRedimirse()) {
                return b;
            }
        }
        return null;
    }

    public List<BonoDescuento> getBonosDescuento() {
        return bonosDescuento;
    }

    public int getEdad() {
        return edad;
    }

    public String getCategoriaEdad() {
        return categoriaEdad;
    }

    public boolean isTieneBebidaCaliente() {
        return tieneBebidaCaliente;
    }

    public void setTieneBebidaCaliente(boolean tieneBebidaCaliente) {
        this.tieneBebidaCaliente = tieneBebidaCaliente;
    }

    public List<PerfilAcademiaIndividual> getPerfilesAcademia() {
        return perfilesAcademia;
    }

    public List<InscripcionEquipo> getInscripcionesEquipo() {
        return inscripcionesEquipo;
    }
}
