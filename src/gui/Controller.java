package gui;

//TODO wenn man Tab drückt soll es horizontal weiter gehen nicht vertikal

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Entry;

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
  private List<Entry> entries;
  private String xml;

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
    saveEntries();
    /*
    double sumEcts = 0;
    double ectsWGrade = 0;
    for (int i = 0; i < ectsBox.getChildren().size() - 1; i++) {
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      if (isInteger(ectsTxt.getText())) {
        sumEcts += Integer.parseInt(ectsTxt.getText());
      }
      //If an entry has a grade (instead of "passed")
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      if (!noteTxt.getText().isEmpty() || isNote(noteTxt.getText())) {
        ectsWGrade += Integer.parseInt(ectsTxt.getText());
      }
    }
    if (checkBox.isSelected()) {
      double garbageECTS = Math.round((sumEcts * garbageFactor) * 100.00) / 100.00;
      discountGarbageECTS(garbageECTS);
    }
*/

  }

  private boolean isInteger(String number) {
    try {
      int validNumber = Integer.parseInt(number);
      if (validNumber >= 0) {
        return true;
      }
    } catch (NumberFormatException e) {
    }
    return false;
  }

  private boolean isNote(String number) {
    try {
      double validNumber = Double.valueOf(number);
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
        if (isNote(noteTxt.getText())) {
          double note = Double.valueOf(noteTxt.getText());
          if (note > max) {
            max = note;

          }
        }

      }
    }
  }

  //TODO note kann nicht in int geparsed werden!!!

  private void saveEntries() {
    entries = new ArrayList<>();
    XStream xStream = new XStream(new DomDriver());
    for (int i = 0; i < vorlesungBox.getChildren().size() - 1; i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      if (!vorTxt.getText().isEmpty() && isNote(noteTxt.getText()) && (
          isInteger(ectsTxt.getText()) || ectsTxt.getText()
              .isEmpty())) {
        Entry entry = new Entry(vorTxt.getText(), Double.valueOf(noteTxt.getText()),
            Integer.parseInt(ectsTxt.getText()));
        entries.add(entry);
      }
    }
    xStream.alias("Vorlesungen", List.class);
    xStream.alias("Eintrag", util.Entry.class);
    xml = xStream.toXML(entries);

  }


}
