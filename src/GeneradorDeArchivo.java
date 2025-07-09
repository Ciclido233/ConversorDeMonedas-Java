import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GeneradorDeArchivo {
    private final List<String> historial = new ArrayList<>();

    public void agregarConversion(String conversion) {
        historial.add(LocalDateTime.now() + " - " + conversion);
    }

    public void guardarHistorial() {
        try (FileWriter writer = new FileWriter("historial.json")) {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(historial);
            writer.write(json);
        } catch (Exception e) {
            System.out.println("Error al guardar historial: " + e.getMessage());
        }
    }
}