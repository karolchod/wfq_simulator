package simulator;

import java.util.HashMap;

public class Statistics {

    public class QueueStatistics {
        public int numberOfDelays = 0;
        public double totalDelay = 0.0;
        public double queueTime = 0.0; //Q(t)
    }
    private long numberOfArrivals = 0;
    private double serverBusyTime = 0.0;
    private long totalBytesServiced = 0;
    private HashMap<Integer, QueueStatistics> queueStatistics = new HashMap<>();//tablica statystyk

    public void registerQueue(int queueId) {    //dodanie nowej kolejki do tablicy statystyk
        queueStatistics.put(queueId, new QueueStatistics());

    }

    public void addDelayToStatistics(int queueId, double delay) { //dodanie czasu oczekiwania na obsluge
        queueStatistics.get(queueId).totalDelay += delay;
        queueStatistics.get(queueId).numberOfDelays++;
    }

    public long getNumberOfArrivals() {     //uzyte do sprawdzania w main czy jeszcze symulowac czy nie
        return numberOfArrivals;
    }

    public void increaseNumberOfArrivals() { //dodanie 1 do liczby przybytych pakietow
        this.numberOfArrivals++;
    }

    public void increaseQueueTime(int queueId, double queueTime) {
        this.queueStatistics.get(queueId).queueTime += queueTime;
    }

    public double getServerBusyTime() {
        return serverBusyTime;
    }

    public void increaseServerBusyTime(double serverBusyTime) {
        this.serverBusyTime += serverBusyTime;
    }

    public QueueStatistics getQueueStatistics(int queueId) {
        return queueStatistics.get(queueId);
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        //slajdy calosc strona 99
        HashMap<Integer, Double> qn = new HashMap<>();  //liczba klientow w kolejce
        HashMap<Integer, Double> dn = new HashMap<>();  //sredni czas oczekiwania w kolejce

        double un = serverBusyTime/totalSimTime;//srednie obciazenie serwera

        queueStatistics.forEach((id, stat) -> { //dla kazdej kolejki
            dn.put(id, stat.totalDelay/stat.numberOfDelays);//sredni czas oczekiwania w kolejce
            qn.put(id, stat.queueTime/totalSimTime);        //liczba klientow w kolejce
            ////Q(t)suma czasow oczekiwania kazdego pakietu/czas symulacji
        });


        java.lang.System.out.println("----------");
        java.lang.System.out.println("Wyniki symulacji");
        java.lang.System.out.println("----------");
        java.lang.System.out.println("Czas symulacji: " + String.format("%.5f", totalSimTime));
        java.lang.System.out.println("Liczba przyjsc pakietu: " + numberOfArrivals);
        java.lang.System.out.println("----------");
        queueStatistics.forEach((id, qs) -> {
            java.lang.System.out.println("Kolejka " + id);
            java.lang.System.out.println("Srednia liczba pakietow w kolejce:\t" + String.format("%.3f", qn.get(id)));
            java.lang.System.out.println("Sredni czas oczekiwania:\t" + String.format("%.3f", dn.get(id)));
            java.lang.System.out.println("--------------------");
        });
        java.lang.System.out.println("Srednie obciazenie serwera: " + String.format("%.3f", un));
        java.lang.System.out.println("------------------------");
    }
}
