package uniandes.dpo.cds.persistencia;

import uniandes.dpo.cds.modelo.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManejadorPersistencia {
    private String rutaData;

    public ManejadorPersistencia(String rutaData) {
        this.rutaData = rutaData;
        File carpeta = new File(rutaData);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    public void cargarTodo(ClubDeportivoSeneca club) {
        crearArchivosBaseSiNoExisten();
        cargarDeportes(club);
        cargarUsuarios(club);
        cargarEquipos(club);
        cargarInstalaciones(club);
        cargarTienda(club);
        cargarCafeteria(club);
        cargarTurnos(club);
        cargarReservas(club);
        cargarTorneos(club);
        inicializarDatosSemillaComplementarios(club);
    }

    public void guardarTodo(ClubDeportivoSeneca club) {
        guardarUsuarios(club.getUsuarios());
        guardarEquipos(club.getEquipos());
        guardarTienda(club);
        guardarTurnos(club.getTurnos());
        guardarReservas(club.getReservas());
        guardarVentas(club.getVentas());
        guardarTorneos(club.getTorneosInternos());
    }

    private void crearArchivosBaseSiNoExisten() {
        escribirSiNoExiste("deportes.txt",
            "CONJUNTO;Fútbol\n" +
            "CONJUNTO;Baloncesto\n" +
            "CONJUNTO;Voleibol\n" +
            "INDIVIDUAL;Tenis;2\n" +
            "INDIVIDUAL;Pádel;2\n" +
            "INDIVIDUAL;Tenis de Mesa;2\n");

        escribirSiNoExiste("usuarios.txt",
            "ADMIN;admin;admin123;Carlos Administrador;0\n" +
            "ENTRENADOR;profe_juan;pass123;Juan Perez;DESC_JUAN;1200\n" +
            "ENTRENADOR;profe_marta;pass123;Marta Gomez;DESC_MARTA;800\n" +
            "ENTRENADOR;profe_andres;pass123;Andres Castro;DESC_ANDRES;500\n" +
            "FISIOTERAPEUTA;fisio_laura;pass123;Laura Rios;DESC_LAURA;600\n" +
            "FISIOTERAPEUTA;fisio_camilo;pass123;Camilo Vega;DESC_CAMILO;400\n" +
            "SOCIO;socio_mateo;pass123;Mateo Silva;16;Tenis:Avanzado:1200.0,Pádel:Intermedio:1100.0;3500;false;BONO_BIENVENIDA:0.20:Copa Integracion CDS:false\n" +
            "SOCIO;socio_valeria;pass123;Valeria Ortiz;16;Tenis:Intermedio:1150.0,Tenis de Mesa:Avanzado:1220.0;2000;false;\n" +
            "SOCIO;socio_lucas;pass123;Lucas Diaz;16;Tenis:Principiante:1000.0,Tenis de Mesa:Principiante:1000.0;1000;false;\n" +
            "SOCIO;socio_santiago;pass123;Santiago Mora;16;Tenis:Intermedio:1120.0;500;false;\n" +
            "SOCIO;socio_nicolas;pass123;Nicolas Pardo;16;Pádel:Intermedio:1100.0;500;false;\n" +
            "SOCIO;socio_samuel;pass123;Samuel Rojas;16;Tenis:Principiante:1000.0;500;false;\n" +
            "SOCIO;socio_tomas;pass123;Tomas Vargas;16;Tenis:Principiante:1000.0;500;false;\n" +
            "SOCIO;socio_carlos;pass123;Carlos Mayor;21;Tenis:Avanzado:1250.0,Pádel:Avanzado:1210.0;4000;false;\n" +
            "SOCIO;socio_diego;pass123;Diego Herrera;22;Tenis:Intermedio:1140.0;1500;false;\n" +
            "SOCIO;socio_felipe;pass123;Felipe Mendoza;20;Tenis de Mesa:Intermedio:1110.0;1000;false;\n" +
            "SOCIO;socio_julian;pass123;Julian Restrepo;23;Tenis:Principiante:1020.0;1000;false;\n" +
            "SOCIO;socio_sebastian;pass123;Sebastian Lozano;21;Pádel:Intermedio:1100.0;1000;false;\n" +
            "SOCIO;socio_alejandro;pass123;Alejandro Gil;22;Tenis:Intermedio:1100.0;1000;false;\n" +
            "SOCIO;socio_daniel;pass123;Daniel Acosta;20;Tenis:Principiante:1000.0;1000;false;\n");

        escribirSiNoExiste("equipos.txt",
            "Halcones CDS;Fútbol;Sub-17;15;socio_mateo:Delantero:9,socio_valeria:Mediocampista:10,socio_lucas:Defensa:4,socio_santiago:Portero:1,socio_nicolas:Defensa:2,socio_samuel:Mediocampista:8,socio_tomas:Delantero:11\n" +
            "Cóndores CDS;Fútbol;Sub-17;15;\n" +
            "Titanes Mayores CDS;Fútbol;Mayores;18;socio_carlos:Delantero:9,socio_diego:Mediocampista:8,socio_felipe:Defensa:3,socio_julian:Portero:1,socio_sebastian:Defensa:4,socio_alejandro:Mediocampista:6,socio_daniel:Delantero:7\n" +
            "Panteras CDS;Voleibol;Sub-17;12;socio_mateo:Opuesto:9,socio_valeria:Armadora:7\n");

        escribirSiNoExiste("instalaciones.txt",
            "TENIS;Cancha de Tenis Principal\n" +
            "TENIS;Cancha de Tenis Auxiliar\n" +
            "PADEL;Cancha de Pádel Central\n" +
            "MESA_TM;Mesa Tenis de Mesa Salón Cubierto;true\n" +
            "MESA_TM;Mesa Tenis de Mesa Exterior;false\n");

        escribirSiNoExiste("tienda.txt",
            "Raqueta de Tenis;Raquetas;450000;10;5;5\n" +
            "Balón de Fútbol;Balones;120000;25;12;13\n" +
            "Malla Reglamentaria;Mallas;180000;8;4;4\n" +
            "Uniforme Oficial CDS;Uniformes;85000;40;20;20\n");

        escribirSiNoExiste("cafeteria.txt",
            "BEBIDA;Café Americano;4500;true\n" +
            "BEBIDA;Bebida Hidratante Fría;6000;false\n" +
            "SNACK;Barra de Cereal con Maní;5000;Maní,Gluten\n" +
            "SNACK;Croissant de Queso;4200;Gluten,Lácteos\n");

        escribirSiNoExiste("turnos.txt",
            "Lunes;08:00-12:00;profe_juan,profe_marta,profe_andres,fisio_laura\n" +
            "Martes;08:00-12:00;profe_juan,profe_marta,fisio_laura,fisio_camilo\n" +
            "Sabado;08:00-12:00;profe_marta,profe_andres,fisio_camilo\n");

        escribirSiNoExiste("reservas.txt",
            "socio_mateo;Cancha de Tenis Principal;14:00-15:00;2;Sencillos\n");

        escribirSiNoExiste("torneos.txt",
            "RECREATIVO;Abierto Recreativo Tenis CDS;Tenis;Libre;Sencillos;5;Sabado;Inscripciones Abiertas;0.20;false;socio_mateo:Socio:Avanzado:true:0.0,socio_valeria:Socio:Intermedio:false:0.0;\n" +
            "COMPETITIVO;Copa Competitiva Tenis Séneca;Tenis;Libre;Sencillos;5;Sabado;En curso;50000.0;true;socio_carlos:Socio:Avanzado:true:50000.0,profe_juan:Empleado:Avanzado:false:0.0,Camila Estudiante Uniandes:Estudiante Universidad:Intermedio:false:50000.0;Ronda 1|Carlos Mayor|Camila Estudiante Uniandes|6-3, 6-2|Carlos Mayor;\n" +
            "RECREATIVO;Torneo Relámpago Tenis de Mesa;Tenis de Mesa;Libre;Sencillos;5;Martes;Finalizado;0.15;true;socio_valeria:Socio:Avanzado:true:0.0,socio_lucas:Socio:Principiante:false:0.0;Final|Valeria Ortiz|Lucas Diaz|3-1|Valeria Ortiz;Valeria Ortiz\n");
    }

    private void escribirSiNoExiste(String nombreArchivo, String contenido) {
        File f = new File(rutaData, nombreArchivo);
        if (!f.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
                bw.write(contenido);
                bw.close();
            } catch (IOException e) {
                System.out.println("Error creando archivo: " + e.getMessage());
            }
        }
    }

    private void cargarDeportes(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "deportes.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 2) {
                        String nombre = partes[1];
                        if ("Fútbol".equalsIgnoreCase(nombre) || "Futbol".equalsIgnoreCase(nombre)) {
                            club.agregarDeporte(new Futbol());
                        } else if ("Baloncesto".equalsIgnoreCase(nombre)) {
                            club.agregarDeporte(new Baloncesto());
                        } else if ("Voleibol".equalsIgnoreCase(nombre)) {
                            club.agregarDeporte(new Voleibol());
                        } else if ("Tenis".equalsIgnoreCase(nombre)) {
                            int maxInst = partes.length > 2 ? Integer.parseInt(partes[2]) : 2;
                            club.agregarDeporte(new Tenis(maxInst));
                        } else if ("Pádel".equalsIgnoreCase(nombre) || "Padel".equalsIgnoreCase(nombre)) {
                            int maxInst = partes.length > 2 ? Integer.parseInt(partes[2]) : 2;
                            club.agregarDeporte(new Padel(maxInst));
                        } else if ("Tenis de Mesa".equalsIgnoreCase(nombre)) {
                            int maxInst = partes.length > 2 ? Integer.parseInt(partes[2]) : 2;
                            club.agregarDeporte(new TenisDeMesa(maxInst));
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar deportes: " + e.getMessage());
        }
    }

    private void cargarUsuarios(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "usuarios.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 4) {
                        String tipo = partes[0];
                        String login = partes[1];
                        String pass = partes[2];
                        String nombre = partes[3];

                        if ("ADMIN".equalsIgnoreCase(tipo)) {
                            club.agregarUsuario(new Administrador(login, pass, nombre));
                        } else if ("ENTRENADOR".equalsIgnoreCase(tipo)) {
                            String desc = partes.length > 4 ? partes[4] : "DESC_" + login.toUpperCase();
                            Entrenador ent = new Entrenador(login, pass, nombre, desc);
                            if (partes.length > 5 && !partes[5].trim().isEmpty()) {
                                ent.setPuntosFidelidad(Integer.parseInt(partes[5].trim()));
                            }
                            club.agregarUsuario(ent);
                        } else if ("FISIOTERAPEUTA".equalsIgnoreCase(tipo)) {
                            String desc = partes.length > 4 ? partes[4] : "DESC_" + login.toUpperCase();
                            Fisioterapeuta fis = new Fisioterapeuta(login, pass, nombre, desc);
                            if (partes.length > 5 && !partes[5].trim().isEmpty()) {
                                fis.setPuntosFidelidad(Integer.parseInt(partes[5].trim()));
                            }
                            club.agregarUsuario(fis);
                        } else if ("SOCIO".equalsIgnoreCase(tipo)) {
                            int edad = Integer.parseInt(partes[4]);
                            Socio socio = new Socio(login, pass, nombre, edad);
                            if (partes.length > 5 && !partes[5].trim().isEmpty()) {
                                String[] perfiles = partes[5].split(",");
                                for (int i = 0; i < perfiles.length; i++) {
                                    String[] datosP = perfiles[i].split(":");
                                    if (datosP.length == 3) {
                                        Deporte d = club.buscarDeporte(datosP[0].trim());
                                        if (d instanceof DisciplinaIndividual) {
                                            double elo = Double.parseDouble(datosP[2].trim());
                                            socio.obtenerOCrearPerfil((DisciplinaIndividual) d, datosP[1].trim(), elo);
                                        }
                                    }
                                }
                            }
                            if (partes.length > 6 && !partes[6].trim().isEmpty()) {
                                socio.setPuntosFidelidad(Integer.parseInt(partes[6].trim()));
                            }
                            if (partes.length > 7 && !partes[7].trim().isEmpty()) {
                                socio.setTieneBebidaCaliente(Boolean.parseBoolean(partes[7].trim()));
                            }
                            if (partes.length > 8 && !partes[8].trim().isEmpty()) {
                                String[] bonos = partes[8].split(",");
                                for (int i = 0; i < bonos.length; i++) {
                                    String[] db = bonos[i].split(":");
                                    if (db.length >= 4) {
                                        BonoDescuento b = new BonoDescuento(db[0].trim(), Double.parseDouble(db[1].trim()), db[2].trim());
                                        b.setRedimido(Boolean.parseBoolean(db[3].trim()));
                                        socio.agregarBonoDescuento(b);
                                    }
                                }
                            }
                            club.agregarUsuario(socio);
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void cargarEquipos(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "equipos.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 4) {
                        String nombre = partes[0];
                        String nomDep = partes[1];
                        String cat = partes[2];
                        int cupo = Integer.parseInt(partes[3]);

                        Deporte dep = club.buscarDeporte(nomDep);
                        if (dep instanceof DeporteConjunto) {
                            Equipo eq = club.crearEquipo(nombre, (DeporteConjunto) dep, cat, cupo);
                            if (partes.length > 4 && !partes[4].trim().isEmpty()) {
                                String[] miembros = partes[4].split(",");
                                for (int i = 0; i < miembros.length; i++) {
                                    String[] dm = miembros[i].split(":");
                                    if (dm.length >= 3) {
                                        Usuario u = club.buscarUsuario(dm[0].trim());
                                        if (u instanceof Socio) {
                                            eq.inscribirJugador((Socio) u, dm[1].trim(), Integer.parseInt(dm[2].trim()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar equipos: " + e.getMessage());
        }
    }

    private void cargarInstalaciones(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "instalaciones.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 2) {
                        String tipo = partes[0];
                        String nom = partes[1];
                        if ("TENIS".equalsIgnoreCase(tipo)) {
                            club.agregarInstalacion(new CanchaTenis(nom));
                        } else if ("PADEL".equalsIgnoreCase(tipo)) {
                            club.agregarInstalacion(new CanchaPadel(nom));
                        } else if ("MESA_TM".equalsIgnoreCase(tipo)) {
                            boolean techada = false;
                            if (partes.length > 2) {
                                techada = Boolean.parseBoolean(partes[2]);
                            }
                            club.agregarInstalacion(new MesaTenisDeMesa(nom, techada));
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar instalaciones: " + e.getMessage());
        }
    }

    private void cargarTienda(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "tienda.txt");
        if (!file.exists()) {
            return;
        }
        UbicacionInventario bodega = new UbicacionInventario("Bodega Principal");
        UbicacionInventario mostrador = new UbicacionInventario("Mostrador Tienda");
        club.agregarUbicacionInventario(bodega);
        club.agregarUbicacionInventario(mostrador);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 4) {
                        String nom = partes[0];
                        String cat = partes[1];
                        double precio = Double.parseDouble(partes[2]);
                        int stock = Integer.parseInt(partes[3]);
                        int cantBodega = partes.length > 4 ? Integer.parseInt(partes[4]) : (stock / 2);
                        int cantMostrador = partes.length > 5 ? Integer.parseInt(partes[5]) : (stock - cantBodega);
                        ArticuloTienda art = new ArticuloTienda(nom, cat, precio, stock);
                        club.agregarArticuloTienda(art);
                        bodega.registrarExistenciaInicial(art, cantBodega);
                        mostrador.registrarExistenciaInicial(art, cantMostrador);
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar tienda: " + e.getMessage());
        }
    }

    private void cargarCafeteria(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "cafeteria.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 3) {
                        String tipo = partes[0];
                        String nom = partes[1];
                        double precio = Double.parseDouble(partes[2]);
                        if ("BEBIDA".equalsIgnoreCase(tipo)) {
                            boolean caliente = false;
                            if (partes.length > 3) {
                                caliente = Boolean.parseBoolean(partes[3]);
                            }
                            club.agregarProductoCafeteria(new Bebida(nom, precio, caliente));
                        } else if ("SNACK".equalsIgnoreCase(tipo)) {
                            List<String> alergenos = new ArrayList<String>();
                            if (partes.length > 3 && !partes[3].trim().isEmpty()) {
                                String[] listaA = partes[3].split(",");
                                for (int i = 0; i < listaA.length; i++) {
                                    alergenos.add(listaA[i].trim());
                                }
                            }
                            club.agregarProductoCafeteria(new Snack(nom, precio, alergenos));
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar cafeteria: " + e.getMessage());
        }
    }

    private void cargarTurnos(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "turnos.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 3) {
                        String dia = partes[0];
                        String horario = partes[1];
                        TurnoSemanal turno = club.obtenerOCrearTurno(dia, horario);
                        String[] logins = partes[2].split(",");
                        for (int i = 0; i < logins.length; i++) {
                            Usuario u = club.buscarUsuario(logins[i].trim());
                            if (u instanceof Empleado) {
                                turno.asignarEmpleado((Empleado) u);
                            }
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar turnos: " + e.getMessage());
        }
    }

    private void cargarReservas(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "reservas.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 5) {
                        Usuario u = club.buscarUsuario(partes[0]);
                        Instalacion inst = club.buscarInstalacion(partes[1]);
                        String bloque = partes[2];
                        int cant = Integer.parseInt(partes[3]);
                        String mod = partes[4];
                        if (u != null && inst != null) {
                            club.registrarReserva(u, inst, bloque, cant, mod);
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar reservas: " + e.getMessage());
        }
    }

    private void cargarTorneos(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "torneos.txt");
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String linea = br.readLine();
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 9) {
                        String tipo = partes[0];
                        String nom = partes[1];
                        Deporte dep = club.buscarDeporte(partes[2]);
                        String catNivel = partes[3];
                        String mod = partes[4];
                        int maxCupos = Integer.parseInt(partes[5]);
                        String dia = partes[6];
                        String estadoGuardado = partes[7];
                        double valorParam = Double.parseDouble(partes[8]);
                        boolean prefLib = partes.length > 9 && Boolean.parseBoolean(partes[9]);

                        if (dep != null) {
                            TorneoInterno torneo = null;
                            if ("RECREATIVO".equalsIgnoreCase(tipo)) {
                                torneo = club.crearTorneoRecreativo(nom, dep, catNivel, mod, maxCupos, dia, valorParam);
                            } else if ("COMPETITIVO".equalsIgnoreCase(tipo)) {
                                torneo = club.crearTorneoCompetitivo(nom, dep, catNivel, mod, maxCupos, dia, valorParam);
                            }
                            if (torneo != null) {
                                if (prefLib) {
                                    torneo.liberarCuposPreferenciales();
                                }
                                if (partes.length > 10 && !partes[10].trim().isEmpty()) {
                                    String[] listaP = partes[10].split(",");
                                    for (int i = 0; i < listaP.length; i++) {
                                        String[] dp = listaP[i].split(":");
                                        if (dp.length >= 3) {
                                            String idONom = dp[0].trim();
                                            String tPart = dp[1].trim();
                                            String niv = dp[2].trim();
                                            Usuario u = club.buscarUsuario(idONom);
                                            Equipo eq = club.buscarEquipo(idONom);
                                            if (eq != null) {
                                                torneo.inscribirEquipoClub(eq);
                                            } else if (u != null) {
                                                torneo.inscribirMiembroIndividual(u, niv, club.getTurnos());
                                            } else if (dep instanceof DeporteConjunto) {
                                                torneo.inscribirEquipoExterno(idONom, niv, ((DeporteConjunto) dep).getMinJugadoresHabilitados());
                                            } else {
                                                torneo.inscribirParticipanteExterno(idONom, tPart, niv);
                                            }
                                        }
                                    }
                                }
                                if ("En curso".equalsIgnoreCase(estadoGuardado) || "Finalizado".equalsIgnoreCase(estadoGuardado)) {
                                    torneo.iniciarTorneo();
                                    if (partes.length > 11 && !partes[11].trim().isEmpty()) {
                                        String[] listaE = partes[11].split("~");
                                        for (int i = 0; i < listaE.length; i++) {
                                            String[] de = listaE[i].split("\\|");
                                            if (de.length >= 5) {
                                                torneo.registrarEncuentro(de[0], de[1], de[2], de[3], de[4]);
                                            }
                                        }
                                    }
                                }
                                if ("Finalizado".equalsIgnoreCase(estadoGuardado) && partes.length > 12 && !partes[12].trim().isEmpty()) {
                                    torneo.finalizarTorneo(partes[12].trim());
                                }
                            }
                        }
                    }
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar torneos: " + e.getMessage());
        }
    }

    private void inicializarDatosSemillaComplementarios(ClubDeportivoSeneca club) {
        if (club.getCompetencias().isEmpty()) {
            Equipo titanes = club.buscarEquipo("Titanes Mayores CDS");
            if (titanes != null && titanes.obtenerJugadoresHabilitados().size() >= 7) {
                CompetenciaLiga cerros = new CompetenciaLiga("Torneo de los Cerros Fútbol", "2026-II", 7);
                cerros.registrarNominaOficial(titanes, titanes.obtenerJugadoresHabilitados());
                club.getCompetencias().add(cerros);
            }
        }
        if (club.getSugerencias().isEmpty()) {
            Usuario u = club.buscarUsuario("profe_juan");
            if (u instanceof Empleado) {
                club.getSugerencias().add(((Empleado) u).sugerirArticuloOSnack("Banda Elástica Pro", "Articulo Tienda"));
            }
        }
        if (club.getSolicitudesCambioTurno().isEmpty() && club.getTurnos().size() >= 2) {
            Usuario u = club.buscarUsuario("profe_juan");
            if (u instanceof Empleado) {
                SolicitudCambioTurno sol = ((Empleado) u).solicitarCambioTurno(club.getTurnos().get(0), club.getTurnos().get(1));
                club.getSolicitudesCambioTurno().add(sol);
            }
        }
    }

    public void guardarUsuarios(List<Usuario> usuarios) {
        File file = new File(rutaData, "usuarios.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario u = usuarios.get(i);
                if (u instanceof Administrador) {
                    bw.write("ADMIN;" + u.getLogin() + ";" + u.getPassword() + ";" + u.getNombre() + ";0\n");
                } else if (u instanceof Entrenador) {
                    Entrenador e = (Entrenador) u;
                    bw.write("ENTRENADOR;" + e.getLogin() + ";" + e.getPassword() + ";" + e.getNombre() + ";" +
                             e.getCodigoDescuento() + ";" + e.getPuntosFidelidad() + "\n");
                } else if (u instanceof Fisioterapeuta) {
                    Fisioterapeuta f = (Fisioterapeuta) u;
                    bw.write("FISIOTERAPEUTA;" + f.getLogin() + ";" + f.getPassword() + ";" + f.getNombre() + ";" +
                             f.getCodigoDescuento() + ";" + f.getPuntosFidelidad() + "\n");
                } else if (u instanceof Socio) {
                    Socio s = (Socio) u;
                    StringBuilder perfSb = new StringBuilder();
                    for (int j = 0; j < s.getPerfilesAcademia().size(); j++) {
                        PerfilAcademiaIndividual p = s.getPerfilesAcademia().get(j);
                        if (j > 0) perfSb.append(",");
                        perfSb.append(p.getDisciplina().getNombre()).append(":")
                              .append(p.getNivel()).append(":")
                              .append(p.getPuntajeRankingELO());
                    }
                    StringBuilder bonosSb = new StringBuilder();
                    for (int j = 0; j < s.getBonosDescuento().size(); j++) {
                        BonoDescuento b = s.getBonosDescuento().get(j);
                        if (j > 0) bonosSb.append(",");
                        bonosSb.append(b.getCodigo()).append(":")
                               .append(b.getPorcentajeDescuento()).append(":")
                               .append(b.getNombreTorneoOrigen()).append(":")
                               .append(b.isRedimido());
                    }
                    bw.write("SOCIO;" + s.getLogin() + ";" + s.getPassword() + ";" + s.getNombre() + ";" +
                             s.getEdad() + ";" + perfSb.toString() + ";" + s.getPuntosFidelidad() + ";" +
                             s.isTieneBebidaCaliente() + ";" + bonosSb.toString() + "\n");
                }
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public void guardarEquipos(List<Equipo> equipos) {
        File file = new File(rutaData, "equipos.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < equipos.size(); i++) {
                Equipo eq = equipos.get(i);
                StringBuilder nomSb = new StringBuilder();
                for (int j = 0; j < eq.getNomina().size(); j++) {
                    InscripcionEquipo insc = eq.getNomina().get(j);
                    if (j > 0) nomSb.append(",");
                    nomSb.append(insc.getSocio().getLogin()).append(":")
                         .append(insc.getPosicion()).append(":")
                         .append(insc.getNumeroCamisetaUnico());
                }
                bw.write(eq.getNombre() + ";" + eq.getDeporte().getNombre() + ";" + eq.getCategoria() + ";" +
                         eq.getCupoMaximoJugadores() + ";" + nomSb.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar equipos: " + e.getMessage());
        }
    }

    public void guardarTienda(ClubDeportivoSeneca club) {
        File file = new File(rutaData, "tienda.txt");
        UbicacionInventario bodega = club.buscarUbicacionInventario("Bodega Principal");
        UbicacionInventario mostrador = club.buscarUbicacionInventario("Mostrador Tienda");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < club.getArticulosTienda().size(); i++) {
                ArticuloTienda a = club.getArticulosTienda().get(i);
                int cBod = (bodega != null && bodega.getCantidadesPorArticulo().containsKey(a))
                        ? bodega.getCantidadesPorArticulo().get(a) : (a.getCantidadDisponible() / 2);
                int cMos = (mostrador != null && mostrador.getCantidadesPorArticulo().containsKey(a))
                        ? mostrador.getCantidadesPorArticulo().get(a) : (a.getCantidadDisponible() - cBod);
                bw.write(a.getNombre() + ";" + a.getCategoria() + ";" + a.getPrecio() + ";" +
                         a.getCantidadDisponible() + ";" + cBod + ";" + cMos + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar tienda: " + e.getMessage());
        }
    }

    public void guardarTurnos(List<TurnoSemanal> turnos) {
        File file = new File(rutaData, "turnos.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < turnos.size(); i++) {
                TurnoSemanal t = turnos.get(i);
                StringBuilder empSb = new StringBuilder();
                for (int j = 0; j < t.getEmpleadosAsignados().size(); j++) {
                    if (j > 0) empSb.append(",");
                    empSb.append(t.getEmpleadosAsignados().get(j).getLogin());
                }
                bw.write(t.getDia() + ";" + t.getHorarioAtencion() + ";" + empSb.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar turnos: " + e.getMessage());
        }
    }

    public void guardarReservas(List<ReservaInstalacion> reservas) {
        File file = new File(rutaData, "reservas.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < reservas.size(); i++) {
                ReservaInstalacion r = reservas.get(i);
                String loginResp = r.getUsuarioResponsable() != null ? r.getUsuarioResponsable().getLogin() : "socio_mateo";
                bw.write(loginResp + ";" + r.getInstalacion().getNombre() + ";" + r.getBloqueHorario() + ";" +
                         r.getCantidadJugadores() + ";" + r.getModalidad() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar reservas: " + e.getMessage());
        }
    }

    public void guardarVentas(List<Venta> ventas) {
        File file = new File(rutaData, "ventas.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < ventas.size(); i++) {
                Venta v = ventas.get(i);
                bw.write(v.getFecha() + ";" + v.getRubroContable() + ";" + v.getComprador().getLogin() + ";" +
                         v.getSubtotal() + ";" + v.getDescuentoEmpleado() + ";" + v.getPuntosRedimidos() + ";" +
                         v.getValorTotal() + ";" + v.getPuntosFidelidadGenerados() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar ventas: " + e.getMessage());
        }
    }

    public void guardarTorneos(List<TorneoInterno> torneos) {
        File file = new File(rutaData, "torneos.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            for (int i = 0; i < torneos.size(); i++) {
                TorneoInterno t = torneos.get(i);
                String tipo = (t instanceof TorneoRecreativo) ? "RECREATIVO" : "COMPETITIVO";
                double param = (t instanceof TorneoRecreativo)
                        ? ((TorneoRecreativo) t).getPorcentajeBonoDescuento()
                        : ((TorneoCompetitivo) t).getTarifaInscripcion();

                StringBuilder partSb = new StringBuilder();
                for (int j = 0; j < t.getParticipantes().size(); j++) {
                    ParticipanteTorneo p = t.getParticipantes().get(j);
                    if (j > 0) partSb.append(",");
                    String idRef = p.getNombreParticipante();
                    if (p.getUsuarioPrincipal() != null) {
                        idRef = p.getUsuarioPrincipal().getLogin();
                    } else if (p.getEquipoClub() != null) {
                        idRef = p.getEquipoClub().getNombre();
                    }
                    partSb.append(idRef).append(":")
                          .append(p.getTipoParticipante()).append(":")
                          .append(p.getCategoriaONivel()).append(":")
                          .append(p.isEsCupoPreferencial()).append(":")
                          .append(p.getMontoPagado());
                }

                StringBuilder encSb = new StringBuilder();
                for (int j = 0; j < t.getEncuentros().size(); j++) {
                    EncuentroTorneo e = t.getEncuentros().get(j);
                    if (j > 0) encSb.append("~");
                    String nomGan = e.getGanador() != null ? e.getGanador().getNombreParticipante() : "Empate";
                    encSb.append(e.getRondaOFase()).append("|")
                         .append(e.getParticipanteA().getNombreParticipante()).append("|")
                         .append(e.getParticipanteB().getNombreParticipante()).append("|")
                         .append(e.getMarcador()).append("|")
                         .append(nomGan);
                }

                String nomCampeon = t.getCampeon() != null ? t.getCampeon().getNombreParticipante() : "";
                bw.write(tipo + ";" + t.getNombre() + ";" + t.getDeporte().getNombre() + ";" +
                         t.getCategoriaONivel() + ";" + t.getModalidad() + ";" + t.getMaxCupos() + ";" +
                         t.getDiaTorneo() + ";" + t.getEstado() + ";" + param + ";" +
                         t.isCuposPreferencialesLiberados() + ";" + partSb.toString() + ";" +
                         encSb.toString() + ";" + nomCampeon + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al guardar torneos: " + e.getMessage());
        }
    }
}
