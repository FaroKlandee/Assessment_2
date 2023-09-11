import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class pointApp extends Application {

    public static class Point {
        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "x=" + x + "; y=" + y;
        }
    }

    /**
     * Set variable names
     */
    private List<Point> points = new ArrayList<>();
    private Canvas canvas;
    private TextField coordinateTextField;
    private Point selectedPoint;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Show Points");

        // Read points from a text file and display them
        points = readPointsFromFile("points.txt");

        StackPane root = new StackPane();

        canvas = new Canvas(400, 300);
        root.getChildren().add(canvas);

        // Create text field with custom font size & style
        coordinateTextField = new TextField();
        coordinateTextField.setFont(new Font("Arial", 12));
        coordinateTextField.setEditable(false);
        coordinateTextField.setPrefHeight(30);

        // Set text field position to be bottom of stage
        coordinateTextField.setTranslateX(0);
        coordinateTextField.setTranslateY(150);

        root.getChildren().add(coordinateTextField);

        /**
         * Creates new contextMenu and deleteMenuItem with option shown as Delete
         * Context menu displayed when user right clicks
         * Option to delete is displayed
         * redraw canvas when delete function is requested
         */
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                selectedPoint = getPointAt(mouseX, mouseY);
                if (selectedPoint != null) {
                    canvas.setOnMouseDragged(dragEvent -> {
                        redrawCanvas();
                    });

                    canvas.setOnMouseReleased(releaseEvent -> {
                        redrawCanvas();
                        canvas.setOnMouseDragged(null);
                        canvas.setOnMouseReleased(null);
                    });
                }
            }
        });

        contextMenu.getItems().add(deleteMenuItem);

        // Function is remove the point when delete option is selected from a point's
        // context menu
        canvas.setOnContextMenuRequested(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            Point pointToDelete = getPointAt(mouseX, mouseY);
            if (pointToDelete != null) {
                contextMenu.show(canvas, event.getScreenX(), event.getScreenY());
                deleteMenuItem.setOnAction(deleteEvent -> {
                    points.remove(pointToDelete);
                    redrawCanvas();
                    contextMenu.hide();
                });
            }
        });

        // Function to display the coordinates of the point the cursor is hovering over
        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            Point hoveredPoint = getPointAt(mouseX, mouseY);
            if (hoveredPoint != null) {
                coordinateTextField.setText("Hovered Point: " + hoveredPoint.toString());
            } else {
                coordinateTextField.setText("");
            }
        });

        // Function to click and drag an existing point to a new point
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                Point selectedPoint = getPointAt(mouseX, mouseY);
                if (selectedPoint != null) {
                    canvas.setOnMouseDragged(dragEvent -> {
                        double newX = dragEvent.getX();
                        double newY = dragEvent.getY();
                        selectedPoint.x = newX;
                        selectedPoint.y = newY;
                        redrawCanvas();
                    });

                    canvas.setOnMouseReleased(releaseEvent -> {
                        redrawCanvas();
                        canvas.setOnMouseDragged(null);
                        canvas.setOnMouseReleased(null);
                    });
                }
            }
        });

        redrawCanvas();

        Scene scene = new Scene(root, 400, 330);
        primaryStage.setScene(scene);

        // Save the points when the window is closed
        primaryStage.setOnCloseRequest(event -> savePointsToFile("points.txt"));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Method to read points from the points.txt file
    // String splits special characters and passes the double into the class
    // variables
    private List<Point> readPointsFromFile(String fileName) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader("C:\\Users\\mrbee\\OneDrive\\Desktop\\Java 2\\Assessment_2\\pointApp\\points.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming lines are in the format: "x=5; y=10"
                String[] parts = line.split(";");
                String xStr = parts[0].trim().split("=")[1];
                String yStr = parts[1].trim().split("=")[1];
                double x = Double.parseDouble(xStr);
                double y = Double.parseDouble(yStr);
                points.add(new Point(x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    // redraws the canvas
    private void redrawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Point point : points) {
            double x = point.x;
            double y = point.y;
            gc.setFill(Color.BLUE);
            gc.fillOval(x - 5, y - 5, 10, 10);
        }
    }

    // Returns point at the current location
    private Point getPointAt(double x, double y) {
        for (Point point : points) {
            double pointX = point.x;
            double pointY = point.y;
            if (x >= pointX - 5 && x <= pointX + 5 && y >= pointY - 5 && y <= pointY + 5) {
                return point;
            }
        }
        return null;
    }

    // method to write new coordinates into existing points file
    private void savePointsToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("C:\\Users\\mrbee\\OneDrive\\Desktop\\Java 2\\Assessment_2\\pointApp\\points.txt"))) {
            for (Point point : points) {
                writer.write("x=" + point.x + "; y=" + point.y);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
