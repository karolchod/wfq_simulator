package simulator.events;

import java.util.Comparator;
import java.util.PriorityQueue;

//struktura przechowująca zaplanowane kolejne zdarzenia
public class EventList {
    private PriorityQueue<Event> events;

    public EventList() {
        this.events = new PriorityQueue<>(5, Comparator.comparingDouble(Event::getTime));
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public Event popNextEvent() { //usuwa i zwraca brzegowy element z tablicy
        return events.poll();
    }

    public Event peekNextEvent() {  //zwraca brzegowy ale nie usuwa
        return events.peek();
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }
}
