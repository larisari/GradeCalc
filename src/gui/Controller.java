package gui;

//TODO wenn man Tab drückt soll es horizontal weiter gehen nicht vertikal

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Controller {

  @FXML
  private VBox vorlesungBox;
  @FXML
  private VBox ectsBox;
  @FXML
  private VBox noteBox;
  @FXML
  private HBox buttonBox;
  @FXML
  private CheckBox checkBox;
  private double garbageFactor = 0.166666667;

  @FXML
  public void handleAddNewTxtField(MouseEvent mouseEvent) {
    vorlesungBox.getChildren().add(new TextField());
    buttonBox.toFront();
    ectsBox.getChildren().add(new TextField());
    noteBox.getChildren().add(new TextField());
  }

  @FXML
  private void handleRemoveTxtField(MouseEvent mouseEvent) {
    vorlesungBox.getChildren().remove(vorlesungBox.getChildren().size() - 2);
    ectsBox.getChildren().remove(ectsBox.getChildren().size() - 1);
    noteBox.getChildren().remove(noteBox.getChildren().size() - 1);

  }

  //Loads all lectures for Medieninformatik mit MMI
  @FXML
  private void handleMMI(ActionEvent actionEvent) {

  }

  //TODO eventuell Vorlesung/ECTS/NOTE als Kombi abspeichern? -> leichter zum Note berechnen
  @FXML
  private void handleCalcGrade(MouseEvent mouseEvent) {
//save all entries into xml file?
    double sumEcts = 0;
    double ectsWGrade = 0;
    for (int i = 0; i < ectsBox.getChildren().size() - 1; i++) {
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      if (isInteger(ectsTxt.getText())) {
        sumEcts += Integer.parseInt(ectsTxt.getText());
      }
      //If an entry has a grade (instead of "passed")
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      if (!noteTxt.getText().isEmpty() || isInteger(noteTxt.getText())) {
        ectsWGrade += Integer.parseInt(ectsTxt.getText());
      }
    }
    if (checkBox.isSelected()) {
      double garbageECTS = Math.round((sumEcts * garbageFactor) * 100.00) / 100.00;
      discountGarbageECTS(garbageECTS);

    }


  }

  private boolean isInteger(String number) {
    try {
      int validNumber = Integer.parseInt(number);
      if (validNumber > 0) {
        return true;
      }
    } catch (NumberFormatException e) {
    }
    return false;
  }

  private void discountGarbageECTS(double garbageECTS) {
    double max = 0;
    while (garbageECTS > 0) {
      for (int i = 0; i < noteBox.getChildren().size() - 1; i++) {
        TextField noteTxt = (TextField) noteBox.getChildren().get(i);
        if (isInteger(noteTxt.getText())) {
          int note = Integer.parseInt(noteTxt.getText());
          if (note > max) {
            max = note;

          }
        }

      }
    }
  }

  private void saveEntries() {

  }


}
