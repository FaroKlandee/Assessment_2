import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class fanSimulator extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final double FAN_RADIUS = 100;
    private static final double BLADE_LENGTH = 80;
    private static final double BLADE_WIDTH = 10;
    private static final double ROTATE_SPEED = 1;

    private boolean isFanRunning = false;
    private boolean isFanReversed = false;

    private double bladeRotation = 0;

    private Button startButton;
    private Button reverseButton;
    private Button stopButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fan Simulator");

        StackPane root = new StackPane();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawFan(gc);

        startButton = new Button("Start");
        reverseButton = new Button("Reverse");
        stopButton = new Button("Stop");

        startButton.setOnAction(e -> startFan());
        reverseButton.setOnAction(e -> reverseFan());
        stopButton.setOnAction(e -> stopFan());

        root.getChildren().addAll(canvas, startButton, reverseButton, stopButton);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawFan(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.GRAY);
        gc.fillOval(WIDTH / 2 - FAN_RADIUS, HEIGHT / 2 - FAN_RADIUS, FAN_RADIUS * 2, FAN_RADIUS * 2);

        gc.setFill(Color.DARKGRAY);
        for (int i = 0; i < 4; i++) {
            double startAngle = 90 * i + bladeRotation;
            gc.fillArc(WIDTH / 2 - BLADE_LENGTH / 2, HEIGHT / 2 - BLADE_WIDTH / 2, BLADE_LENGTH, BLADE_WIDTH,
                    startAngle, 90, ArcType.ROUND);
        }
    }

    private void startFan() {
        if (!isFanRunning) {
            isFanRunning = true;
            GraphicsContext gc = ((Canvas) startButton.getScene().getRoot().getChildrenUnmodifiable().get(0))
                    .getGraphicsContext2D();
            animateFan(gc);
        }
    }

    private void reverseFan() {
        isFanReversed = !isFanReversed;
    }

    private void stopFan() {
        if (isFanRunning) {
            isFanRunning = false;
        }
    }

    private void animateFan(GraphicsContext gc) {
        new Thread(() -> {
            while (isFanRunning) {
                if (isFanReversed) {
                    bladeRotation -= ROTATE_SPEED;
                } else {
                    bladeRotation += ROTATE_SPEED;
                }
                Platform.runLater(() -> drawFan(gc)); // Update the UI on the JavaFX application thread
                try {
                    Thread.sleep(16); // Adjust this for the desired rotation speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
