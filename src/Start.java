import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO add VM configuration for javafx 11

public class Start extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("gui/Gui_HVBox.fxml"));
      Parent root = loader.load();
      primaryStage.setTitle("GradeCalculator");
      primaryStage.setScene(new Scene(root));
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
