public class Entry {

  private String name;
  private double note;
  private Integer ects;


  public Entry(String name, double note, Integer ects) {
    this.setEntry(name, note, ects);
  }

  public void setEntry(String name, double note, Integer ects) {
    this.name = name;
    this.note = note;
    this.ects = ects;
  }

  public String getName() {
    return this.name;
  }

  public double getNote() {
    return this.note;
  }

  public Integer getECTS() {
    return this.ects;
  }

  public boolean hasNote() {
    if (this.note == 0) {
      return false;
    }
    return true;
  }


}
