import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class fanSimulator extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final double FAN_RADIUS = 200;
    private static final double BLADE_LENGTH = 160;
    private static final double BLADE_WIDTH = 20;
    private static final double ROTATE_SPEED = 1;

    private boolean isFanRunning = false;
    private boolean isFanReversed = false;

    private double bladeRotation = 0;

    private Button startButton;
    private Button reverseButton;
    private Button stopButton;

    private void drawFan(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Inside your drawFan() method
        for (int i = 0; i < 4; i++) {
            double startAngle = 90 * i + bladeRotation;

            gc.setFill(Color.IVORY);
            gc.fillOval(WIDTH / 2 - FAN_RADIUS, HEIGHT / 2 - FAN_RADIUS, FAN_RADIUS * 2, FAN_RADIUS * 2);

            // Apply a perspective transformation
            Rotate rotation = new Rotate(startAngle, WIDTH / 2, HEIGHT / 2);
            gc.setTransform(rotation.getMxx(), rotation.getMyx(), rotation.getMxy(), rotation.getMyy(),
                    rotation.getTx(), rotation.getTy());

            // Draw the fan blade
            gc.setFill(Color.PURPLE);
            gc.fillArc(WIDTH / 2 - BLADE_LENGTH / 2, HEIGHT / 2 - BLADE_WIDTH / 2, BLADE_LENGTH, BLADE_WIDTH,
                    startAngle, 90, ArcType.ROUND);

            // Reset the transformation
            gc.setTransform(new Affine());
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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fan Simulator");

        Line line = new Line(200, 200, 200, 350);
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(line.startXProperty());
        rotation.pivotYProperty().bind(line.startYProperty());

        line.getTransforms().add(rotation);

        // Create a VBox to arrange buttons vertically at the top
        HBox buttonBox = new HBox(10); // 10 is the spacing between buttons
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawFan(gc);

        startButton = new Button("Start");
        reverseButton = new Button("Reverse");
        stopButton = new Button("Stop");

        startButton.setOnAction(e -> startFan());
        reverseButton.setOnAction(e -> reverseFan());
        stopButton.setOnAction(e -> stopFan());

        // Add buttons to the VBox
        buttonBox.getChildren().addAll(startButton, reverseButton, stopButton);

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, buttonBox);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
