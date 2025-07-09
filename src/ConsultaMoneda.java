import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaMoneda {
    private static final String API_KEY = "5d3748f0cb9146d8941634ec";
    private static final String URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    public Moneda obtenerTasas() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), Moneda.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tasas de cambio: " + e.getMessage());
        }
    }
}