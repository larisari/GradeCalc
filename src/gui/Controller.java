package gui;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import javafx.event.ActionEvent;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Calculator;
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
  private double garbageFactor = 0;
  private List<Entry> entries;
  private VBox garbageCheck;
  private Calculator calculator;

//TODO checkboxen anpassen für delete zeile oder add zeile

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
    if (checkbox.isSelected()) {
      CheckBox box = new CheckBox();
      box.getStyleClass().add("checkBox");
      garbageCheck.getChildren().add(box);
    }

  }

  //TODO anpassen für wenn checkboxen aktiv sind

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
      if (checkbox.isSelected()) {
        garbageCheck.getChildren().remove(garbageCheck.getChildren().size() - 1);
      }
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
      if (getIndexInParent(textField).isPresent()) {
        int index = getIndexInParent(textField).getAsInt();
        vorlesungBox.getChildren().remove(index);
        ectsBox.getChildren().remove(index);
        noteBox.getChildren().remove(index);
        if (checkbox.isSelected()) {
          garbageCheck.getChildren().remove(index);
        }
        box.getChildren().get(index).requestFocus();
        entries.remove(index);
      }
    }


  }

  /**
   * Returns an Optional of the index of a TextField in its parent, if the TextField exists.
   *
   * @param textField from which index should be found
   */
  private OptionalInt getIndexInParent(TextField textField) {
    VBox box = (VBox) textField.getParent();
    for (int i = 0; i < box.getChildren().size(); i++) {
      if (box.getChildren().get(i).equals(textField)) {
        return OptionalInt.of(i);
      }
    }
    return OptionalInt.empty();
  }

  /**
   * Adds new row under currently focused row.
   */
  @FXML
  private void addUnderCurrRow(ActionEvent actionEvent) {
    Scene scene = window.getScene();
    if (scene.focusOwnerProperty().get() instanceof TextField) {
      TextField textField = (TextField) scene.focusOwnerProperty().get();
      if (getIndexInParent(textField).isPresent()) {
        int i = getIndexInParent(textField).getAsInt();
        TextField txt1 = new TextField();
        TextField txt2 = new TextField();
        TextField txt3 = new TextField();
        vorlesungBox.getChildren().add(i + 1, txt1);
        ectsBox.getChildren().add(i + 1, txt2);
        noteBox.getChildren().add(i + 1, txt3);

        if (checkbox.isSelected()) {
          CheckBox box = new CheckBox();
          box.getStyleClass().add("checkBox");
          garbageCheck.getChildren().add(i + 1, box);
        }

        int vBoxLength = vorlesungBox.getChildren().size() - 1;
        setTraversalOrder(txt1, vBoxLength);
        setTraversalOrder(txt2, vBoxLength);
        setTraversalOrder(txt3, vBoxLength);
        txt1.requestFocus();
      }
    }
  }

  //TODO anpassen für checkboxen

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

    calculator = new Calculator(entries);
    double grade = calculator.calculate(garbageFactor);
    if (garbageFactor > 0){
      highlightCountedGrades();
    }

    finalGrade.setText("" + grade);
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
        if (!garbageCheck.getChildren().isEmpty()) {
          garbageCheck.getChildren().remove(i);
        }
        i -= 1;
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

  private void setTextColor(int index, String color) {
    TextField vorTxt = (TextField) vorlesungBox.getChildren().get(index);
    TextField ectsTxt = (TextField) ectsBox.getChildren().get(index);
    TextField noteTxt = (TextField) noteBox.getChildren().get(index);
    vorTxt.setStyle("-fx-text-fill: " + color);
    ectsTxt.setStyle("-fx-text-fill: " + color);
    noteTxt.setStyle("-fx-text-fill: " + color);

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


  private void reset() {
    if (!vorlesungBox.getChildren().isEmpty()) {
      for (int i = 0; i < entries.size(); i++) {
        setTextColor(i, "black");
      }
    }
    entries = null;

  }


  /**
   * Handles user pressing "Save as". Saves user input as xml file to local directory.
   */
  @FXML
  private void handleDownloadXML(ActionEvent mouseEvent) {
    saveEntries();
    XStream xStream = new XStream(new DomDriver());
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Speichere deine Datei ab.");
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showSaveDialog(stage);
    if (selectedFile != null) {
      String counter = checkForDuplicate(selectedFile);
      try {
        xStream.toXML(entries, new FileOutputStream(selectedFile + counter + ".xml"));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private String checkForDuplicate(File file) {
    int counter = 0;
    File temp = new File(file.getPath() + ".xml");

    while (temp.exists()) {
      counter++;
      String filename = file.getPath() + counter + ".xml";
      temp = new File(filename);
    }
    if (counter > 0) {
      System.out.println(counter);
      return counter + "";

    }
    return "";
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
      saveEntries();
      deleteSuperfluousFields();
    }
  }

  //TODO anpassen für checkboxen, muss iwie erkennen wenn mülltonnenwerte mitabgespeichert wurden

  /**
   * Converts uploaded data into list. Inserts data of uploaded xml file into Textfields.
   *
   * @param file - uploaded data
   */
  private void insertEntries(File file) {
    promptEntry.setVisible(false);
    XStream xStream = new XStream(new DomDriver());
    List<Entry> uEntries = (List<Entry>) xStream.fromXML(file);

    if (!vorlesungBox.getChildren().isEmpty()) {
      clear();
    }
    while (vorlesungBox.getChildren().size() < uEntries.size()) {
      addRow();
    }
    setCheckBoxesVisible();
    int counter = 0;
    for (int i = 0; i < uEntries.size(); i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      vorTxt.setText(uEntries.get(i).getName());
      ectsTxt.setText(uEntries.get(i).getECTS().toString());
      noteTxt.setText(uEntries.get(i).getNote() + "");
      if (uEntries.get(i).isGarbageEligible()) {
        CheckBox box = (CheckBox) garbageCheck.getChildren().get(i);
        box.setSelected(true);
        counter++;
        checkbox.setSelected(true);
      }
    }
    if (counter == 0) {
      removeCheckboxes();
    }

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

        if (checkbox.isSelected()) {
          CheckBox box = (CheckBox) garbageCheck.getChildren().get(i);
          if (box.isSelected()) {
            entry.setGarbEligible();
          }
        }
        entries.add(entry);

      }
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
      removeCheckboxes();
      /*if (checkbox.isSelected()) {
        CheckBox box = (CheckBox) garbageCheck.getChildren().get(i);
        box.setSelected(false);
      }*/
    }
    finalGrade.setText("");
    checkbox.setSelected(false);
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
    File file = new File(("XML Files/" + filename));
    insertEntries(file);
    saveEntries();
  }

  @FXML
  private void openGarbageInfo(MouseEvent mouseEvent) {
    String url = "http://www2.tcs.ifi.lmu.de/~letz/informationen.shtml#Muelltonnenregelung";
    try {
      Desktop.getDesktop().browse(new URL(url).toURI());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void enableGarbageBoxes(MouseEvent mouseEvent) {
    if (checkbox.isSelected()) {
      setCheckBoxesVisible();
    } else {
      removeCheckboxes();
      garbageFactor = 0;
    }
  }

  private void setCheckBoxesVisible() {
    //  if (garbageCheck == null || garbageCheck.getChildren()
    //    .isEmpty()) {     //könnte exception schmeißen, geht bei UPLOAD nicht hier rein weil garbageCheck nicht leer.
    garbageCheck = new VBox();
    while (garbageCheck.getChildren().size() < vorlesungBox.getChildren().size()) {
      CheckBox box = new CheckBox();
      box.getStyleClass().add("checkBox");
      garbageCheck.getChildren().add(box);
    }
    hBox.getChildren().add(0, garbageCheck);
    //}
  }

  private void removeCheckboxes() {
    if (hBox.getChildren().size() > 3) {
      hBox.getChildren().remove(garbageCheck);
    }
  }

  @FXML
  private void setFactorMI(ActionEvent actionEvent) {
    setGarbageFactor(0.166666667);
  }

  @FXML
  private void setFactorInfo(ActionEvent actionEvent) {
    setGarbageFactor(0.166666667);
  }

  @FXML
  private void setFactorInfo150(ActionEvent actionEvent) {
    setGarbageFactor(0.2);
  }

  @FXML
  private void setFactorInfo120(ActionEvent actionEvent) {
    setGarbageFactor(0.15);

  }

  private void setGarbageFactor(double factor) {
    garbageFactor = factor;
    checkbox.setSelected(true);
  }
}