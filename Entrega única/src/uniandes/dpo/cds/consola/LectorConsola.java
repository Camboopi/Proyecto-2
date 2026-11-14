package uniandes.dpo.cds.consola;

import java.util.Scanner;

public class LectorConsola {
    private Scanner scanner;

    public LectorConsola() {
        this.scanner = new Scanner(System.in, "UTF-8");
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                return "";
            }
            String linea = scanner.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("  [!] Entrada vacia. Por favor ingrese un valor valido.");
        }
    }

    public String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                return 0;
            }
            String linea = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("  [!] Por favor ingrese un numero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Entrada invalida. Debe ingresar un numero entero.");
            }
        }
    }

    public double leerDoublePositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                return 0.0;
            }
            String linea = scanner.nextLine().trim();
            try {
                double valor = Double.parseDouble(linea);
                if (valor >= 0) {
                    return valor;
                }
                System.out.println("  [!] El valor no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Entrada invalida. Debe ingresar un valor numerico.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            if (!scanner.hasNextLine()) {
                return false;
            }
            String r = scanner.nextLine().trim().toLowerCase();
            if ("s".equals(r) || "si".equals(r) || "sí".equals(r)) {
                return true;
            }
            if ("n".equals(r) || "no".equals(r)) {
                return false;
            }
            System.out.println("  [!] Responda 's' para Si o 'n' para No.");
        }
    }
}
