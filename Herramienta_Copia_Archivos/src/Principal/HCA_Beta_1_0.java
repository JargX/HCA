package Principal;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class HCA_Beta_1_0 extends Application {
    private TextField usuarioField;
    private TextField unidadDestinoField;
    private ProgressBar progressBar;
    private Label statusLabel;
    private DoubleProperty progreso = new SimpleDoubleProperty(0);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Copia de Archivos");

        Label usuarioLabel = new Label("Nombre de usuario de Windows:");
        usuarioField = new TextField();

        Label unidadDestinoLabel = new Label("Unidad de destino:");
        unidadDestinoField = new TextField();
        Button seleccionarDestinoBtn = new Button("Seleccionar carpeta");
        seleccionarDestinoBtn.setOnAction(e -> seleccionarDestino(primaryStage));

        Button iniciarBtn = new Button("Iniciar Copia");
        iniciarBtn.setOnAction(e -> iniciarCopia());

        progressBar = new ProgressBar(0);
        progressBar.progressProperty().bind(progreso);

        statusLabel = new Label("Esperando inicio...");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                usuarioLabel, usuarioField,
                unidadDestinoLabel, unidadDestinoField, seleccionarDestinoBtn,
                iniciarBtn, progressBar, statusLabel
        );

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seleccionarDestino(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar unidad de destino");
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            unidadDestinoField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void iniciarCopia() {
        String usuario = usuarioField.getText().trim();
        String unidadDestino = unidadDestinoField.getText().trim();

        if (usuario.isEmpty() || unidadDestino.isEmpty()) {
            statusLabel.setText("Por favor, completa todos los campos.");
            return;
        }

        String destinoBase = unidadDestino + "\\Backup_" + usuario + "\\";

        List<String> carpetas = List.of("Downloads", "Desktop", "Documents", "Videos", "Pictures");
        for (String carpeta : carpetas) {
            Path origen = Paths.get("C:\\Users\\" + usuario + "\\" + carpeta);
            Path destino = Paths.get(destinoBase + carpeta);

            if (!Files.exists(origen)) {
                Platform.runLater(() -> statusLabel.setText("Carpeta de origen no encontrada: " + carpeta));
                continue;
            }

            new Thread(() -> {
                try {
                    copiarConProgreso(origen, destino);
                    Platform.runLater(() -> statusLabel.setText("Copia completada."));
                } catch (IOException e) {
                    Platform.runLater(() -> statusLabel.setText("Error en la copia de " + carpeta + ": " + e.getMessage()));
                }
            }).start();
        }

        // Exportar claves de Registro
        exportarRegistro("HKEY_CURRENT_USER\\Network", destinoBase + "Network.reg");
        exportarRegistro("HKEY_CURRENT_USER\\Software\\Microsoft\\Office\\16.0", destinoBase + "Office16.reg");

        // Copiar perfil de Firefox
        String perfilFirefox = "C:\\Users\\" + usuario + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles";
        Path origenFirefox = Paths.get(perfilFirefox);
        Path destinoFirefox = Paths.get(destinoBase + "Firefox_Profiles");
        if (Files.exists(origenFirefox)) {
            new Thread(() -> {
                try {
                    copiarConProgreso(origenFirefox, destinoFirefox);
                    Platform.runLater(() -> statusLabel.setText("Copia de perfil de Firefox completada."));
                } catch (IOException e) {
                    Platform.runLater(() -> statusLabel.setText("Error en la copia del perfil de Firefox: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void copiarConProgreso(Path origen, Path destino) throws IOException {
        long totalArchivos = Files.walk(origen).filter(Files::isRegularFile).count();
        final long[] archivosCopiados = {0};

        Files.walkFileTree(origen, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(destino.resolve(origen.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path destinoRuta = destino.resolve(origen.relativize(file));
                Files.copy(file, destinoRuta, StandardCopyOption.REPLACE_EXISTING);
                archivosCopiados[0]++;
                double progressValue = (double) archivosCopiados[0] / totalArchivos;
                Platform.runLater(() -> progreso.set(progressValue));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void exportarRegistro(String clave, String archivoDestino) {
        try {
            ProcessBuilder pb = new ProcessBuilder("reg", "export", clave, archivoDestino, "/y");
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            proceso.waitFor();
        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> statusLabel.setText("Error al exportar registro: " + e.getMessage()));
        }
    }
}
