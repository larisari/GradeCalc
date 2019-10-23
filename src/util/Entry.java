package util;

public class Entry {

  private String name;
  private double note;
  private Integer ects;
  private boolean discounted;


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

  public void setDiscounted() {
    discounted = true;
  }

  public boolean isDiscounted() {
    if (discounted) {
      return true;
    } else {
      return false;
    }
  }



}
