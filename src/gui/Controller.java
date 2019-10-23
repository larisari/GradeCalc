package gui;

//TODO wenn man Tab drückt soll es horizontal weiter gehen nicht vertikal

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Entry;

public class Controller {

  @FXML
  private VBox window;
  @FXML
  private VBox vorlesungBox;
  @FXML
  private VBox ectsBox;
  @FXML
  private VBox noteBox;
  @FXML
  private HBox buttonBox;
  @FXML
  private CheckBox checkbox;
  @FXML
  private Label finalGrade;
  private final double garbageFactor = 0.166666667;
  private double garbageECTS;
  private List<Entry> entries;
  private Entry garbageEntry;


  @FXML
  public void handleAddNewTxtField(MouseEvent mouseEvent) {
    addRow();
  }

  private void addRow() {
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

  @FXML
  private void handleCalcGrade(MouseEvent mouseEvent) {

    saveEntries();
    double sumEcts = 0;
    double ectsWGrade = 0;
    double grade = 0;
    for (int i = 0; i < entries.size(); i++) {
      sumEcts += entries.get(i).getECTS();
      //If an entry has a grade (instead of "passed")
      if (entries.get(i).getNote() > 0) {
        ectsWGrade += entries.get(i).getECTS();
      }
    }

    if (checkbox.isSelected()) {
      garbageECTS = Math.round((sumEcts * garbageFactor) * 100.00) / 100.00;

      discountGarbageECTS();
      //if one lecture does not count fully
      if (garbageECTS < 0) {
        grade += garbageEntry.getNote() * (-garbageECTS);

      }
      ectsWGrade -= sumEcts * garbageFactor;
      for (Entry entry : entries) {
        System.out.println(entry.isDiscounted());
        if (!entry.isDiscounted()) {
          grade += entry.getNote() * entry.getECTS();
        }

      }
    } else {
      for (Entry entry : entries) {
        grade += entry.getNote() * entry.getECTS();

      }
    }
    finalGrade.setText("" + grade / ectsWGrade);
    reset();

  }


  private void discountGarbageECTS() {
    double max = 0;
    while (garbageECTS > 0) {
      for (int i = 0; i < entries.size(); i++) {
        Double note = entries.get(i).getNote();
        if (note > max && !entries.get(i).isDiscounted()) {
          garbageEntry = entries.get(i);
          max = note;
        }
      }
      garbageEntry.setDiscounted();
      System.out.println(garbageEntry.getName() + " wird nicht gewertet");
      garbageECTS -= garbageEntry.getECTS();
      max = 0;
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

  private boolean isNote(String number) {
    try {
      double validNumber = Double.valueOf(number);
      if (validNumber >= 0) {
        return true;
      }
    } catch (NumberFormatException e) {

    }
    return false;
  }


  private void saveEntries() {
    entries = new ArrayList<>();
    for (int i = 0; i < vorlesungBox.getChildren().size() - 1; i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      if (!vorTxt.getText().isEmpty() && (isNote(noteTxt.getText()) || noteTxt.getText().isEmpty())
          && isInteger(ectsTxt.getText())) {
        double note = 0;
        if (!noteTxt.getText().isEmpty()) {
          note = Double.valueOf(noteTxt.getText());
        }

        Entry entry = new Entry(vorTxt.getText(), note, Integer.parseInt(ectsTxt.getText()));
        entries.add(entry);
      }
    }
  }

  private void reset() {
    garbageECTS = 0;
    garbageEntry = null;
    entries = null;
  }


  @FXML
  private void handleDownloadXML(ActionEvent mouseEvent) {
    saveEntries();
    XStream xStream = new XStream(new DomDriver());
    //xStream.alias("Vorlesungen", List.class);
    //xStream.alias("Eintrag", util.Entry.class);
    //xStream.aliasField("gewertet", util.Entry.class, "discounted");
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Speichere deine Datei ab.");
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showSaveDialog(stage);
    if (selectedFile != null) {
      try {
        xStream.toXML(entries, new FileOutputStream(selectedFile + ".xml"));
        //  xStream.toXML(entries, new FileOutputStream(new File("XML Files/Vorlesungen1.xml")));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  //open finder or load latest saved xml file
  @FXML
  private void handleUploadXML(ActionEvent mouseEvent) {
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(new File("XML Files"));
    chooser.setTitle("Wähle Datei aus, die du laden möchtest");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showOpenDialog(stage);
    if (selectedFile != null) {
      XStream xStream = new XStream(new DomDriver());
      List<Entry> uEntries = (List<Entry>) xStream.fromXML(selectedFile);
      insertEntries(uEntries);
    }
  }

  private void insertEntries(List<Entry> uEntries) {
    clear();
    while (vorlesungBox.getChildren().size() - 1 < uEntries.size()) {
      addRow();
    }
    for (int i = 0; i < uEntries.size(); i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      vorTxt.setText(uEntries.get(i).getName());
      ectsTxt.setText(uEntries.get(i).getECTS().toString());
      noteTxt.setText(uEntries.get(i).getNote() + "");
    }

  }

  private void clear() {
    for (int i = 0; i < vorlesungBox.getChildren().size() - 1; i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      vorTxt.clear();
      ectsTxt.clear();
      noteTxt.clear();
    }
  }
}
//TODO add clear button
//TODO Berechnung Note funktioniert noch nicht richtig. Normaler Durchschnitt gibt bessere Note als Mülltonne