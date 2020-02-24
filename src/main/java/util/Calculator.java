package util;

import java.util.List;

public class Calculator {

  private List<Entry> entries;
  private double garbageECTS;
  private Entry garbageEntry;

  public Calculator(List<Entry> entries) {
    this.entries = entries;
  }

  public double calculate(double garbageFactor) {
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
    if (garbageFactor > 0) {
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
    } else {
      for (Entry entry : entries) {
        grade += entry.getNote() * entry.getECTS();

      }
    }
    return grade/ectsWGrade;
  }

  /**
   * Discounts lectures that fall under the garbage rule.
   */
  private void discountGarbageECTS() {
    double max = 0;
    while (garbageECTS > 0) {
      for (Entry entry : entries) {
        Double note = entry.getNote();
        if (note > max && !entry.isDiscounted() && entry.isGarbageEligible()) {
          garbageEntry = entry;
          max = note;
        }
      }
      if (garbageEntry != null) {
        garbageEntry.setDiscounted();
        garbageECTS -= garbageEntry.getECTS();
        max = 0;
      }
    }

  }
}
