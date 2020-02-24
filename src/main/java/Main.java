/*import java.util.ArrayList;
import java.util.List;

public class Main {


  public static void main(String[] args) {
    List<util.Entry> list = new ArrayList<>();
    list.add(new util.Entry("EIP", 1.7, 9));
    list.add(new util.Entry("Promo", 3.3, 6));
    list.add(new util.Entry("Algo", 3.7, 6));
    list.add(new util.Entry("RA", 1, 6));
    list.add(new util.Entry("BS", 1, 6));
    list.add(new util.Entry("Rechnernetze", 1, 6));
    list.add(new util.Entry("Digitale Medien", 1, 6));
    list.add(new util.Entry("Medientechnik", 0, 6));
    list.add(new util.Entry("Computergrafik", 1, 6));
    list.add(new util.Entry("LinAlg", 2.7, 6));
    list.add(new util.Entry("Timi", 2.3, 3));
    list.add(new util.Entry("Seminar", 2, 3));
    list.add(new util.Entry("Analysis", 4, 9));
    list.add(new util.Entry("SEP", 1.3, 12));
    list.add(new util.Entry("Psycho", 1.7, 6));
    list.add(new util.Entry("UX2", 1.3, 6));
    list.add(new util.Entry("Statistik", 2.3, 6));
    list.add(new util.Entry("UX1", 1.3, 6));
    list.add(new util.Entry("Zeichnen", 0, 3));
    //list.add(new util.Entry("UX3", 0, 6));
//    list.add(new util.Entry("Arbeit", 0, 3));
//    list.add(new util.Entry("Fachschaft", 0, 3));
//    list.add(new util.Entry("Englisch", 0, 3));
//    list.add(new util.Entry("VPS", 0, 3));
//    list.add(new util.Entry("Softwaretechnik", 2, 6));
//    list.add(new util.Entry("Datenbanken", 2, 6));
//    list.add(new util.Entry("It", 2, 6));
//    list.add(new util.Entry("swh", 2, 6));
//    list.add(new util.Entry("thema", 2, 6));
//    list.add(new util.Entry("ba", 2, 15));

    double gesamtECTS = 0;
    double ectsMitNote = 0;
    //Berechne GesamtEcts
    for (util.Entry entry : list) {
      gesamtECTS += entry.getECTS();
      if (entry.hasNote()) {
        ectsMitNote += entry.getECTS();
      }
    }

    double muellECTS = Math.round((gesamtECTS * 0.16666666667) * 100.00) / 100.00;
    System.out.println(muellECTS);
    //Ziehe ECTS von den 16% schlechtesten Noten ab und lösche sie aus der Liste
    double max = 0;
    util.Entry muellEntry = null;
    while (muellECTS > 0) {

      for (util.Entry entry : list) {
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
    for (util.Entry entry : list) {
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