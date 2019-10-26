package gui;

//TODO wenn man Tab drückt soll es horizontal weiter gehen nicht vertikal

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Entry;

public class Controller {

  @FXML
  private VBox window;
  @FXML
  private HBox hBox;
  @FXML
  private VBox vorlesungBox;
  @FXML
  private VBox ectsBox;
  @FXML
  private VBox noteBox;
  @FXML
  private CheckBox checkbox;
  @FXML
  private Label finalGrade;
  @FXML
  private Label promptEntry;
  private final double garbageFactor = 0.166666667;
  private double garbageECTS;
  private List<Entry> entries;
  private Entry garbageEntry;


  /**
   * Handles user pressing the "+"-Button to add a new Row of Textfields.
   */
  @FXML
  public void handleAddNewTxtField(MouseEvent mouseEvent) {
    addRow();
  }

  /**
   * Adds a new row of TextFields.
   */
  private void addRow() {
    promptEntry.setVisible(false);
    TextField txt1 = new TextField();
    TextField txt2 = new TextField();
    TextField txt3 = new TextField();
    vorlesungBox.getChildren().add(txt1);
    ectsBox.getChildren().add(txt2);
    noteBox.getChildren().add(txt3);
    int vBoxLength = vorlesungBox.getChildren().size() - 1;
    setTraversalOrder(txt1, vBoxLength);
    setTraversalOrder(txt2, vBoxLength);
    setTraversalOrder(txt3, vBoxLength);

  }

  private void setTraversalOrder(TextField txt, int vBoxLength) {
    txt.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (keyEvent.getCode() == KeyCode.TAB) {
        VBox lastVBox = (VBox) hBox.getChildren().get(hBox.getChildren().size() - 1);
        if (lastVBox.equals(txt.getParent())) {
          if (!lastVBox.getChildren().get(lastVBox.getChildren().size() - 1).equals(txt)) {
            VBox vBox = (VBox) hBox.getChildren().get(0);
            keyEvent.consume();
            vBox.getChildren().get(vBoxLength + 1).requestFocus();
          }
        } else {
          for (int i = 0; i < hBox.getChildren().size(); i++) {
            if (hBox.getChildren().get(i).equals(txt.getParent())) {
              VBox nextBox = (VBox) hBox.getChildren().get(i + 1);
              keyEvent.consume();
              nextBox.getChildren().get(vBoxLength).requestFocus();

            }
          }
        }
      }
    });
  }

  /**
   * Removes a row of TextFields.
   */
  @FXML
  private void handleRemoveTxtField(MouseEvent mouseEvent) {

    if (vorlesungBox.getChildren().size() > 1) {
      vorlesungBox.getChildren().remove(vorlesungBox.getChildren().size() - 1);
      ectsBox.getChildren().remove(ectsBox.getChildren().size() - 1);
      noteBox.getChildren().remove(noteBox.getChildren().size() - 1);
    } else {
      clear();
    }
  }

  /**
   * Deletes row currently focused and sets focus on TextField underneath.
   */
  @FXML
  private void deleteCurrRow(ActionEvent actionEvent) {
    Scene scene = window.getScene();
    if (scene.focusOwnerProperty().get() instanceof TextField) {
      TextField textField = (TextField) scene.focusOwnerProperty().get();
      VBox box = (VBox) textField.getParent();
      for (int i = 0; i < box.getChildren().size(); i++) {
        if (box.getChildren().get(i).equals(textField)) {
          vorlesungBox.getChildren().remove(i);
          ectsBox.getChildren().remove(i);
          noteBox.getChildren().remove(i);
          box.getChildren().get(i).requestFocus();
        }
      }
    }
  }

  /**
   * Handles user pressing calculate grade Button. Calculates regular average or grades with garbage
   * rule.
   */
  @FXML
  private void handleCalcGrade(MouseEvent mouseEvent) {

    saveEntries();
    for (int i = 0; i < entries.size(); i++) {
      setTextColor(i, "black");
    }
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
        if (!entry.isDiscounted()) {
          grade += entry.getNote() * entry.getECTS();
        }

      }
      highlightCountedGrades();
    } else {
      for (Entry entry : entries) {
        grade += entry.getNote() * entry.getECTS();

      }
    }
    finalGrade.setText("" + grade / ectsWGrade);
    reset();
    deleteSuperfluousFields();

  }

  private void deleteSuperfluousFields() {
    for (int i = 0; i < vorlesungBox.getChildren().size(); i++) {
      TextField textField = (TextField) vorlesungBox.getChildren().get(i);
      if (textField.getText().isEmpty()) {
        vorlesungBox.getChildren().remove(i);
        ectsBox.getChildren().remove(i);
        noteBox.getChildren().remove(i);
      }
    }
  }


  private void highlightCountedGrades() {
    for (int i = 0; i < entries.size(); i++) {
      if (entries.get(i).isDiscounted()) {
        setTextColor(i, "#9c9c9c");
      } else {
        setTextColor(i, "black");
      }
    }
  }

  //TODO Problem weil Text wird nur grau bzw schwarz zurückgesetzt wenn mülltonne abgehakelt ist.
  private void setTextColor(int index, String color) {
    TextField vorTxt = (TextField) vorlesungBox.getChildren().get(index);
    TextField ectsTxt = (TextField) ectsBox.getChildren().get(index);
    TextField noteTxt = (TextField) noteBox.getChildren().get(index);
    vorTxt.setStyle("-fx-text-fill: " + color);
    ectsTxt.setStyle("-fx-text-fill: " + color);
    noteTxt.setStyle("-fx-text-fill: " + color);

  }

  /**
   * Discounts lectures that fall under the garbage rule.
   */
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
      if (garbageEntry != null) {
        garbageEntry.setDiscounted();
        System.out.println(garbageEntry.getName() + " wird nicht gewertet");
        garbageECTS -= garbageEntry.getECTS();
        max = 0;
      }
    }

  }

  /**
   * Checks whether a String is a valid entry for ects.
   *
   * @param number - String containing ects.
   * @return true if String is valid ects.
   */
  private boolean isECTS(String number) {
    try {
      int validNumber = Integer.parseInt(number);
      if (validNumber > 0) {
        return true;
      }
    } catch (NumberFormatException e) {
    }
    return false;
  }

  /**
   * Checks whether a String is a valid grade entry.
   *
   * @param number - String containing grade.
   * @return true if String is valid grade.
   */
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

  /**
   * Saves user input into List.
   */
  private void saveEntries() {
    entries = new ArrayList<>();
    for (int i = 0; i < vorlesungBox.getChildren().size(); i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      if (!vorTxt.getText().isEmpty() && (isNote(noteTxt.getText()) || noteTxt.getText().isEmpty())
          && isECTS(ectsTxt.getText())) {
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
    for (int i = 0; i < entries.size(); i++) {
      setTextColor(i, "black");
    }
  }


  /**
   * Handles user pressing "Save as". Saves user input as xml file to local directory.
   */
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

  /**
   * Handles user pressing Upload. Uploads saved xml file.
   */
  @FXML
  private void handleUploadXML(ActionEvent mouseEvent) {
    reset();
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(new File("XML Files"));
    chooser.setTitle("Wähle Datei aus, die du laden möchtest");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showOpenDialog(stage);
    if (selectedFile != null) {
      insertEntries(selectedFile);
    }
  }

  /**
   * Converts uploaded data into list. Inserts data of uploaded xml file into Textfields.
   *
   * @param file - uploaded data
   */
  private void insertEntries(File file) {
    promptEntry.setVisible(false);
    XStream xStream = new XStream(new DomDriver());
    List<Entry> uEntries = (List<Entry>) xStream.fromXML(file);
    clear();
    while (vorlesungBox.getChildren().size() < uEntries.size()) {
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

  /**
   * Clears all Textfields.
   */
  @FXML
  private void clearEntries(ActionEvent event) {
    clear();
  }

  private void clear() {
    for (int i = 0; i < vorlesungBox.getChildren().size(); i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      vorTxt.clear();
      ectsTxt.clear();
      noteTxt.clear();
      finalGrade.setText("");
    }
  }

  //TODO for Testing only
  @FXML
  private void clearNote() {
    for (int i = 0; i < vorlesungBox.getChildren().size(); i++) {
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      noteTxt.clear();
    }
  }

  @FXML
  private void handleInfoComp(ActionEvent actionEvent) {
    uploadFile("InformatikComputerlinguistik.xml");
  }

  @FXML
  private void handleMMI(ActionEvent actionEvent) {
    System.out.println(new File(".").getAbsoluteFile());
    uploadFile("MedieninformatikMMI.xml");
  }

  @FXML
  private void handleMG(ActionEvent actionEvent) {
    uploadFile("MedieninformatikMG.xml");
  }

  @FXML
  private void handleMBWL(ActionEvent actionEvent) {
    uploadFile("MedieninformatikBWL.xml");
  }

  private void uploadFile(String filename) {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(("XML Files/" + filename));
    insertEntries(file);
  }

}
//TODO add clear button