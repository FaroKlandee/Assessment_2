import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

public class sales extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new Group());
        stage.setTitle("XYZ sales");
        stage.setWidth(500);
        stage.setHeight(500);

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("ACT", 10),
                new PieChart.Data("NSW", 20),
                new PieChart.Data("VIC", 15),
                new PieChart.Data("QLD", 15),
                new PieChart.Data("SA", 15),
                new PieChart.Data("WA", 15),
                new PieChart.Data("NT", 5),
                new PieChart.Data("TAS", 5));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Imported Fruits");

        ((Group) scene.getRoot()).getChildren().add(chart);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    
    }
    
}
