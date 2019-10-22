package gui;

//TODO wenn man Tab drückt soll es horizontal weiter gehen nicht vertikal

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
  private CheckBox checkbox;
  @FXML
  private Label finalGrade;
  private final double garbageFactor = 0.166666667;
  private double garbageECTS;
  private List<Entry> entries;
  private Entry garbageEntry;
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

  //TODO gesamtdurchschnitt ohne mülltonne berücksichtigt vorlesungen ohne noten nicht
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
      System.out.println(garbageECTS);
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
        if (note > max) {
          garbageEntry = entries.get(i);
          max = note;
        }
      }
      garbageEntry.setDiscounted();
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
    XStream xStream = new XStream(new DomDriver());
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
    xStream.alias("Vorlesungen", List.class);
    xStream.alias("Eintrag", util.Entry.class);
    xStream.aliasField("gewertet", util.Entry.class, "discounted");
    //xml = xStream.toXML(entries);
    try {
      xStream.toXML(entries, new FileOutputStream(new File("XML Files/Vorlesungen.xml")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  private void reset() {
    garbageECTS = 0;
    garbageEntry = null;
    entries = null;
  }


}
