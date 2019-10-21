/*import java.util.ArrayList;
import java.util.List;

public class Main {


  public static void main(String[] args) {
    List<Entry> list = new ArrayList<>();
    list.add(new Entry("EIP", 1.7, 9));
    list.add(new Entry("Promo", 3.3, 6));
    list.add(new Entry("Algo", 3.7, 6));
    list.add(new Entry("RA", 1, 6));
    list.add(new Entry("BS", 1, 6));
    list.add(new Entry("Rechnernetze", 1, 6));
    list.add(new Entry("Digitale Medien", 1, 6));
    list.add(new Entry("Medientechnik", 0, 6));
    list.add(new Entry("Computergrafik", 1, 6));
    list.add(new Entry("LinAlg", 2.7, 6));
    list.add(new Entry("Timi", 2.3, 3));
    list.add(new Entry("Seminar", 2, 3));
    list.add(new Entry("Analysis", 4, 9));
    list.add(new Entry("SEP", 1.3, 12));
    list.add(new Entry("Psycho", 1.7, 6));
    list.add(new Entry("UX2", 1.3, 6));
    list.add(new Entry("Statistik", 2.3, 6));
    list.add(new Entry("UX1", 1.3, 6));
    list.add(new Entry("Zeichnen", 0, 3));
    //list.add(new Entry("UX3", 0, 6));
//    list.add(new Entry("Arbeit", 0, 3));
//    list.add(new Entry("Fachschaft", 0, 3));
//    list.add(new Entry("Englisch", 0, 3));
//    list.add(new Entry("VPS", 0, 3));
//    list.add(new Entry("Softwaretechnik", 2, 6));
//    list.add(new Entry("Datenbanken", 2, 6));
//    list.add(new Entry("It", 2, 6));
//    list.add(new Entry("swh", 2, 6));
//    list.add(new Entry("thema", 2, 6));
//    list.add(new Entry("ba", 2, 15));

    double gesamtECTS = 0;
    double ectsMitNote = 0;
    //Berechne GesamtEcts
    for (Entry entry : list) {
      gesamtECTS += entry.getECTS();
      if (entry.hasNote()) {
        ectsMitNote += entry.getECTS();
      }
    }

    double muellECTS = Math.round((gesamtECTS * 0.16666666667) * 100.00) / 100.00;
    System.out.println(muellECTS);
    //Ziehe ECTS von den 16% schlechtesten Noten ab und lösche sie aus der Liste
    double max = 0;
    Entry muellEntry = null;
    while (muellECTS > 0) {

      for (Entry entry : list) {
        if (entry.getNote() > 0 && entry.getNote() > max) {
          muellEntry = entry;
          max = muellEntry.getNote();
        }
      }
      System.out.println(muellEntry.getName() + " wird nicht gewertet.");
      list.remove(muellEntry);
      muellECTS -= muellEntry.getECTS();
      max = 0;
    }

//stimmt
    double note = 0;
    for (Entry entry : list) {
      note += entry.getNote() * entry.getECTS();
    }

    //Berechne abgeschnittene Mülltonnenects
    if (muellECTS < 0) {
      System.out.println(muellEntry.getName() + " wird nicht ganz gewertet.");
      note += muellEntry.getNote() * -muellECTS;
    }

    ectsMitNote -= gesamtECTS * 0.1667;
    System.out.println("Gesamtnote beträgt: " + note / ectsMitNote);


  }

}
*/