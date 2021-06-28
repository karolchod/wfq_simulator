package simulator;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

//kolejki wejściowe
public class WaitingQueue {
    private Queue<Packet> queue;

    private double bandwidth; //reserved bandwidth in b/s
    private double virtualSpacingTimestamp; //VSi - ostatnio obliczony znacznik czasowy

    WaitingQueue(double bandwidth) {
        queue = new PriorityQueue<>(5, Comparator.comparingDouble(Packet::getVirtualSpacingTimestamp));

        this.bandwidth = bandwidth;
        this.virtualSpacingTimestamp = 0.0;
    }

    public void add(Packet packet) {    //dodanie pakietu do kolejki
        queue.add(packet);
    }

    public Packet poll() {  //wyrzucenie pierwszego z brzegu pakietu i return jego
        return queue.poll();
    }

    public boolean isEmpty() {  //czy kolejka jest pusta
        return queue.isEmpty();
    }

    public double getBandwidth() {  //zwraca przeplywnosc kolejki uzywana w wfq
        return bandwidth;
    }

    public double getVirtualSpacingTimestamp() { //VSi - ostatnio obliczony znacznik czasowy
        return virtualSpacingTimestamp;
    }

    public void setVirtualSpacingTimestamp(double virtualSpacingTimestamp) {
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
    }

    public double peekLowestTimestamp() { //zwraca znacznik czasowy zerowego elementu
        if(queue.isEmpty())
            return 0.0;
        else
            return queue.peek().getVirtualSpacingTimestamp();
    }

    public int size() {
        return queue.size();
    }
}
