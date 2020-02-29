package gui;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
  private Label finalGrade;
  @FXML
  private Label promptEntry;
  @FXML
  private Label factorDisplay;
  @FXML
  private CheckBox selectAllCBoxes;
  private double garbageFactor = 0;
  private List<Entry> entries;
  private VBox garbageCheck;
  private double infoMMIFactor = 0.166666667;
  private double info150Factor = 0.2;
  private double info120Factor = 0.15;



  @FXML
  public void handleAddNewTxtField() {
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
    if (garbageFactor > 0) {
      setGarbageFactor(garbageFactor);
      CheckBox box = new CheckBox();
      box.getStyleClass().add("checkBox");
      garbageCheck.getChildren().add(box);
    }

  }

  /**
   * Changes traversal order for every Textfield to horizontal.
   *
   * @param txt - TextField for which traversal order should be changed
   * @param vBoxLength - vertical index of TextField
   */
  private void setTraversalOrder(TextField txt, int vBoxLength) {
    txt.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (keyEvent.getCode() == KeyCode.TAB) {
        VBox lastVBox = (VBox) hBox.getChildren().get(hBox.getChildren().size() - 1);
        int lastIndex = lastVBox.getChildren().size() - 1;
        if (lastVBox.equals(txt.getParent())) {
          if (!lastVBox.getChildren().get(lastIndex).equals(txt)) {
            VBox vBox;
            if (hBox.getChildren().size() > 3) {
              vBox = (VBox) hBox.getChildren().get(1);
            } else {
              vBox = (VBox) hBox.getChildren().get(0);
            }
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
  private void handleRemoveTxtField() {

    if (vorlesungBox.getChildren().size() > 0) {
      vorlesungBox.getChildren().remove(vorlesungBox.getChildren().size() - 1);
      ectsBox.getChildren().remove(ectsBox.getChildren().size() - 1);
      noteBox.getChildren().remove(noteBox.getChildren().size() - 1);
      if (garbageFactor > 0) {
        garbageCheck.getChildren().remove(garbageCheck.getChildren().size() - 1);
      }
    }
  }

  /**
   * Deletes row currently focused and sets focus on TextField underneath.
   */
  @FXML
  private void deleteCurrRow() {
    Scene scene = window.getScene();
    if (scene.focusOwnerProperty().get() instanceof TextField) {
      TextField textField = (TextField) scene.focusOwnerProperty().get();
      VBox box = (VBox) textField.getParent();
      if (getIndexInParent(textField).isPresent()) {
        int index = getIndexInParent(textField).getAsInt();
        vorlesungBox.getChildren().remove(index);
        ectsBox.getChildren().remove(index);
        noteBox.getChildren().remove(index);
        if (garbageFactor > 0) {
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
  private void addUnderCurrRow() {
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

        if (garbageFactor > 0) {
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


  /**
   * Handles user pressing calculate grade Button. Calculates regular average or grades with garbage
   * rule.
   */
  @FXML
  private void handleCalcGrade() {
    saveEntries();
    for (int i = 0; i < entries.size(); i++) {
      setTextColor(i, "black");
    }

    Calculator calculator = new Calculator(entries);
    double grade = calculator.calculate(garbageFactor);
    if (garbageFactor > 0) {
      highlightCountedGrades();
    }

    finalGrade.setText("" + grade);
    entries = null;
    deleteSuperfluousFields();

  }

  /**
   * Deletes empty rows.
   */
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

  /**
   * Greys out discounted grades.
   */
  private void highlightCountedGrades() {
    for (int i = 0; i < entries.size(); i++) {
      if (entries.get(i).isDiscounted()) {
        setTextColor(i, "#9c9c9c");
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
      e.printStackTrace();
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
      double validNumber = Double.parseDouble(number);
      if (validNumber >= 0) {
        return true;
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();

    }
    return false;
  }

  /*
  Resets discounted entries to black.
  Resets entries, garbagefactor and therefore checkboxes.
   */
  private void resetData() {
    if (!vorlesungBox.getChildren().isEmpty() && entries != null) {
      for (int i = 0; i < entries.size(); i++) {
        setTextColor(i, "black");
      }
    }
    entries = null;
    resetGarbageFactor();

  }


  /**
   * Handles user pressing "Save as". Saves user input as json file to local directory.
   */
  @FXML
  private void handleSaveFile() {
    deleteSuperfluousFields();
    saveEntries();
    if (entries.isEmpty()) {
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Speichere deine Datei ab.");
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showSaveDialog(stage);
    if (selectedFile != null) {
      String counter = checkForDuplicate(selectedFile);
      Gson gson = new Gson();
      try (FileWriter fileWriter = new FileWriter(selectedFile + counter
          + ".json")) {
        JsonObject obj = new JsonObject();
        JsonArray arr = new Gson().toJsonTree(entries).getAsJsonArray();
        JsonPrimitive prim = new JsonPrimitive(garbageFactor);
        obj.add("entries", arr);
        obj.add("factor", prim);
        gson.toJson(obj, fileWriter);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Checks if filename already exists.
   * Adds a counter if it does.
   */
  private String checkForDuplicate(File file) {
    int counter = 0;
    File temp = new File(file.getPath() + ".json");

    while (temp.exists()) {
      counter++;
      String filename = file.getPath() + counter + ".json";
      temp = new File(filename);
    }
    if (counter > 0) {
      System.out.println(counter);
      return counter + "";

    }
    return "";
  }

  /**
   * Handles user pressing Upload. Uploads saved json file.
   */
  @FXML
  private void handleLoadFile() {
    resetData();
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(new File("JSONs"));
    chooser.setTitle("Wähle Datei aus, die du laden möchtest");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSONs", "*.json"));
    Stage stage = (Stage) window.getScene().getWindow();
    File selectedFile = chooser.showOpenDialog(stage);
    if (selectedFile != null) {
      insertEntries(selectedFile);
      deleteSuperfluousFields();
    }
  }

  /**
   * Converts uploaded data into list. Inserts data of uploaded json file into Textfields.
   *
   * @param file - uploaded data
   */
  private void insertEntries(File file) {
    promptEntry.setVisible(false);
    Gson gson = new Gson();
    try (FileReader fileReader = new FileReader("JSONs/" + file.getName())) {
      JsonObject obj = gson.fromJson(fileReader, JsonObject.class);
      JsonElement ele = obj.get("factor");
      setGarbageFactor(ele.getAsDouble());
      Type entryListType = new TypeToken<ArrayList<Entry>>() {
      }.getType();
      entries = gson.fromJson(obj.get("entries"), entryListType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!vorlesungBox.getChildren().isEmpty()) {
      clear();
    }
    if (entries != null) {
      while (vorlesungBox.getChildren().size() < entries.size()) {
        addRow();
      }

      for (int i = 0; i < entries.size(); i++) {
        TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
        TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
        TextField noteTxt = (TextField) noteBox.getChildren().get(i);
        vorTxt.setText(entries.get(i).getName());
        ectsTxt.setText(entries.get(i).getECTS().toString());
        noteTxt.setText(entries.get(i).getNote() + "");
        if (entries.get(i).isGarbageEligible()) {
          CheckBox box = (CheckBox) garbageCheck.getChildren().get(i);
          box.setSelected(true);

        }
      }
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
      if (noteTxt.getText()
          .isEmpty() || !vorTxt.getText().isEmpty() && (isNote(noteTxt.getText()))
          && isECTS(ectsTxt.getText())) {
        double note = 0;
        if (!noteTxt.getText().isEmpty()) {
          note = Double.parseDouble(noteTxt.getText());
        }

        Entry entry = new Entry(vorTxt.getText(), note, Integer.parseInt(ectsTxt.getText()));

        if (garbageFactor > 0) {
          CheckBox box = (CheckBox) garbageCheck.getChildren().get(i);
          if (box.isSelected()) {
            entry.setGarbEligible();
          }
        }
        entries.add(entry);

      }
    }
  }


  @FXML
  private void resetGui() {
    clear();
  }

  /**
   * Clears all gui elements.
   */
  private void clear() {
    for (int i = 0; i < vorlesungBox.getChildren().size(); i++) {
      TextField vorTxt = (TextField) vorlesungBox.getChildren().get(i);
      TextField ectsTxt = (TextField) ectsBox.getChildren().get(i);
      TextField noteTxt = (TextField) noteBox.getChildren().get(i);
      vorTxt.clear();
      ectsTxt.clear();
      noteTxt.clear();
      removeCheckboxes();
    }
    finalGrade.setText("");
    entries = null;
    resetGarbageFactor();

  }

  @FXML
  private void handleInfoComp() {
    uploadFile("InfoPlusComp.xml");
    setGarbageFactor(infoMMIFactor);
  }

  @FXML
  private void handleMMI() {
    uploadFile("MMI.xml");
    setGarbageFactor(infoMMIFactor);
  }

  @FXML
  private void handleMG() {
    uploadFile("MedienGest.xml");
    setGarbageFactor(infoMMIFactor);
  }

  @FXML
  private void handleMBWL() {
    uploadFile("MedienBWL.xml");
    setGarbageFactor(infoMMIFactor);
  }

  private void uploadFile(String filename) {
    File file = new File(("JSONs/" + filename));
    insertEntries(file);
    saveEntries();
  }

  @FXML
  private void openGarbageInfo() {
    String url = "http://www2.tcs.ifi.lmu.de/~letz/informationen.shtml#Muelltonnenregelung";
    try {
      Desktop.getDesktop().browse(new URL(url).toURI());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void setCheckBoxesVisible() {
    if (hBox.getChildren().size() < 4) {
      garbageCheck = new VBox();
      while (garbageCheck.getChildren().size() < vorlesungBox.getChildren().size()) {
        CheckBox box = new CheckBox();
        box.getStyleClass().add("checkBox");
        box.setSelected(true);
        garbageCheck.getChildren().add(box);
      }
      hBox.getChildren().add(0, garbageCheck);
      selectAllCBoxes.setVisible(true);
      //}
    }
  }

  private void removeCheckboxes() {
    if (hBox.getChildren().size() > 3) {
      hBox.getChildren().remove(garbageCheck);
      selectAllCBoxes.setVisible(false);
    }
  }

  @FXML
  private void setFactorMI() {
    setGarbageFactor(infoMMIFactor);

  }

  @FXML
  private void setFactorInfo() {
    setGarbageFactor(infoMMIFactor);
  }

  @FXML
  private void setFactorInfo150() {
    setGarbageFactor(info150Factor);
  }

  @FXML
  private void setFactorInfo120() {
    setGarbageFactor(info120Factor);
  }

  @FXML
  private void handleResetGarbageFac() {
    resetGarbageFactor();

  }

  private void resetGarbageFactor() {
    setGarbageFactor(0);
  }

  private void setGarbageFactor(double gFactor) {
    garbageFactor = gFactor;
    if (garbageFactor == infoMMIFactor) {
      factorDisplay.setText("Faktor: 1/6");
    } else if (garbageFactor == info150Factor) {
      factorDisplay.setText("Faktor: 1/5");
    } else if (garbageFactor == info120Factor) {
      factorDisplay.setText("Faktor: 0.15");
    } else {
      factorDisplay.setText("");
    }
    if (garbageFactor != 0) {
      setCheckBoxesVisible();
      selectAllCBoxes.setSelected(true);
    } else {
      removeCheckboxes();
    }
  }

  //TODO mit Pfeilen durch Felder navigieren

  @FXML
  private void handleSelectAllCheckBoxes() {
    selectAllCheckBoxes();
  }

  private void selectAllCheckBoxes() {
    if (selectAllCBoxes.isSelected()) {
      for (int i = 0; i < garbageCheck.getChildren().size(); i++) {
        CheckBox check = (CheckBox) garbageCheck.getChildren().get(i);
        check.setSelected(true);
      }
    } else {
      for (int i = 0; i < garbageCheck.getChildren().size(); i++) {
        CheckBox check = (CheckBox) garbageCheck.getChildren().get(i);
        check.setSelected(false);
      }
    }
  }
}