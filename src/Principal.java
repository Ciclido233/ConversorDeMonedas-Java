import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Principal {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String HISTORIAL_FILE = "historial_conversiones.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Moneda tasas = obtenerTasasCambio();

        if (tasas == null) {
            System.out.println("No se pudieron obtener las tasas de cambio. Saliendo...");
            return;
        }

        int opcion;
        do {
            mostrarMenuCompleto();
            opcion = scanner.nextInt();

            if (opcion >= 1 && opcion <= 14) {
                System.out.println("Ingrese el valor que desea convertir:");
                double monto = scanner.nextDouble();
                convertirMoneda(opcion, monto, tasas);
            } else if (opcion == 15) {
                System.out.println("Conversión entre monedas");
                System.out.println("Ingrese el código de la moneda de origen (ej: USD, EUR, ARS):");
                String from = scanner.next().toUpperCase();
                System.out.println("Ingrese el código de la moneda de destino:");
                String to = scanner.next().toUpperCase();
                System.out.println("Ingrese el monto a convertir:");
                double monto = scanner.nextDouble();
                convertirCruzada(from, to, monto, tasas);
            }
        } while (opcion != 16);

        System.out.println("Gracias por usar el conversor!");
    }

    private static Moneda obtenerTasasCambio() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://v6.exchangerate-api.com/v6/5d3748f0cb9146d8941634ec/latest/USD"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), Moneda.class);
        } catch (Exception e) {
            System.out.println("Error al obtener tasas de cambio: " + e.getMessage());
            return null;
        }
    }

    private static void mostrarMenuCompleto() {
        System.out.println("""
            ****************************************************
            Sea bienvenido/a al Conversor de Moneda PLUS =]
            
            1) Dólar (USD) => Peso argentino (ARS)
            2) Peso argentino (ARS) => Dólar (USD)
            3) Dólar (USD) => Real brasileño (BRL)
            4) Real brasileño (BRL) => Dólar (USD)
            5) Dólar (USD) => Peso colombiano (COP)
            6) Peso colombiano (COP) => Dólar (USD)
            7) Dólar (USD) => Euro (EUR)
            8) Euro (EUR) => Dólar (USD)
            9) Dólar (USD) => Peso mexicano (MXN)
            10) Peso mexicano (MXN) => Dólar (USD)
            11) Dólar (USD) => Peso chileno (CLP)
            12) Peso chileno (CLP) => Dólar (USD)
            13) Euro (EUR) => Peso argentino (ARS)
            14) Peso argentino (ARS) => Euro (EUR)
            15) Conversión entre cualquier moneda
            16) Salir
            
            Elija una opción válida:
            ****************************************************
            """);
    }

    private static void convertirMoneda(int opcion, double monto, Moneda tasas) {
        double resultado = 0;
        String from = "", to = "";

        switch(opcion) {
            case 1: // USD a ARS
                resultado = monto * tasas.conversion_rates().get("ARS");
                from = "USD"; to = "ARS";
                break;
            case 2: // ARS a USD
                resultado = monto / tasas.conversion_rates().get("ARS");
                from = "ARS"; to = "USD";
                break;
            case 3: // USD a BRL
                resultado = monto * tasas.conversion_rates().get("BRL");
                from = "USD"; to = "BRL";
                break;
            case 4: // BRL a USD
                resultado = monto / tasas.conversion_rates().get("BRL");
                from = "BRL"; to = "USD";
                break;
            case 5: // USD a COP
                resultado = monto * tasas.conversion_rates().get("COP");
                from = "USD"; to = "COP";
                break;
            case 6: // COP a USD
                resultado = monto / tasas.conversion_rates().get("COP");
                from = "COP"; to = "USD";
                break;
            case 7: // USD a EUR
                resultado = monto * tasas.conversion_rates().get("EUR");
                from = "USD"; to = "EUR";
                break;
            case 8: // EUR a USD
                resultado = monto / tasas.conversion_rates().get("EUR");
                from = "EUR"; to = "USD";
                break;
            case 9: // USD a MXN
                resultado = monto * tasas.conversion_rates().get("MXN");
                from = "USD"; to = "MXN";
                break;
            case 10: // MXN a USD
                resultado = monto / tasas.conversion_rates().get("MXN");
                from = "MXN"; to = "USD";
                break;
            case 11: // USD a CLP
                resultado = monto * tasas.conversion_rates().get("CLP");
                from = "USD"; to = "CLP";
                break;
            case 12: // CLP a USD
                resultado = monto / tasas.conversion_rates().get("CLP");
                from = "CLP"; to = "USD";
                break;
            case 13: // EUR a ARS
                resultado = monto * (tasas.conversion_rates().get("ARS") / tasas.conversion_rates().get("EUR"));
                from = "EUR"; to = "ARS";
                break;
            case 14: // ARS a EUR
                resultado = monto * (tasas.conversion_rates().get("EUR") / tasas.conversion_rates().get("ARS"));
                from = "ARS"; to = "EUR";
                break;
        }

        String mensaje = String.format("El valor %s [%s] corresponde al valor final de =>>> %s [%s]",
                df.format(monto), from, df.format(resultado), to);

        System.out.println(mensaje);
        guardarEnHistorial(mensaje);
    }

    private static void convertirCruzada(String from, String to, double monto, Moneda tasas) {
        if (!tasas.conversion_rates().containsKey(from) || !tasas.conversion_rates().containsKey(to)) {
            System.out.println("Moneda no válida. Por favor use códigos de moneda válidos (USD, EUR, ARS, etc.)");
            return;
        }

        // Conversión: montoDestino = montoOrigen * (tasaDestino / tasaOrigen)
        double resultado = monto * (tasas.conversion_rates().get(to) / tasas.conversion_rates().get(from));

        String mensaje = String.format("El valor %s [%s] corresponde al valor final de =>>> %s [%s]",
                df.format(monto), from, df.format(resultado), to);

        System.out.println(mensaje);
        guardarEnHistorial(mensaje);
    }

    private static void guardarEnHistorial(String conversion) {
        try (FileWriter writer = new FileWriter(HISTORIAL_FILE, true)) {
            String timestamp = LocalDateTime.now().format(dtf);
            writer.write(String.format("[%s] %s%n", timestamp, conversion));
        } catch (IOException e) {
            System.out.println("Error al guardar en el historial: " + e.getMessage());
        }
    }
}