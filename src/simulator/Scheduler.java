package simulator;

import simulator.events.*;
import simulator.sourcetypes.SourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//mechanizm szeregowania pakietów zgodnie z algorytmem WFQ
public class Scheduler {
    private EventList eventList;
    private Server server;
    private HashMap<Integer, WaitingQueue> queues;
    private HashMap<Integer, SourceType> sources;
    private Statistics statistics;

    public Scheduler(double serverBitrate) {

        this.server = new Server(serverBitrate);
        eventList = new EventList();
        queues = new HashMap<>();
        sources = new HashMap<>();
        statistics = new Statistics();
    }

    public void addQueue(int queueId, double bandwidth) throws IllegalArgumentException {
        if(queues.containsKey(queueId))
            throw new IllegalArgumentException("Queue with this ID already exists");
        else {
            queues.put(queueId, new WaitingQueue(bandwidth));
            statistics.registerQueue(queueId);
        }

    }

    public void addSource(int sourceId, SourceType source)
            throws IllegalArgumentException {
        if(sources.containsKey(sourceId))
            throw new IllegalArgumentException("Source with this ID already exists!");
        else {
            sources.put(sourceId, source);
            scheduleNextArrival(sourceId);
        }
    }

    private void addEvent(Event event) {
        eventList.addEvent(event);
    }

    private void addClient(int queueId, Packet packet) {
        queues.get(queueId).add(packet);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();

        double previousTime = Clock.getCurrentTime();
        Clock.setTime(event.getTime());
        double timeDelta = Clock.getCurrentTime() - previousTime;

        if(server.isBusy())
            statistics.increaseServerBusyTime(timeDelta);

        queues.forEach((id, queue) -> statistics.increaseQueueTime(id,queue.size() * timeDelta));

        if (event.getEventType() == EventType.ARRIVAL)
            processArrival(event);
        else
            processDeparture();


    }

    private void processArrival(Event event) {
        statistics.increaseNumberOfArrivals();
        int queueId = event.getQueueId();
        scheduleNextArrival(queueId);
        Packet packet = wfqArrivalAlgorithm(event);

        if(server.isBusy()) {
            addClient(queueId, packet);//dodanie pakietu do kolejki
        } else {
            server.setIsBusy(true);
            server.setSpacingTime(packet.getVirtualSpacingTimestamp());
            statistics.addDelayToStatistics(queueId, 0.0);
            scheduleNextDeparture(packet.getSize());
        }
    }

    private void processDeparture() {
        if(areAllQueuesEmpty()) {
            server.setIsBusy(false);
        } else {
            int queueId = getIdOfQueueWithTheLowestTimestampOfNextPacket();
            Packet handledPacket = queues.get(queueId).poll(); //wyciaga z kolejki zerowy element i od razu go usuwa

            double clientArrivalTime = handledPacket.getArrivalTime();
            double waitingTime = Clock.getCurrentTime() - clientArrivalTime;

            server.setSpacingTime(handledPacket.getVirtualSpacingTimestamp());
            statistics.addDelayToStatistics(queueId, waitingTime);
            scheduleNextDeparture(handledPacket.getSize());
        }
    }

    private void scheduleNextArrival(int id) {
        double timeToNextArrival = sources.get(id).getTimeToNextArrival();
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival; //czas aktualny + wylosowany lub staly
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, id));
    }

    private void scheduleNextDeparture(int packetSizeInBytes){
        double serviceTime = (packetSizeInBytes*8)/server.getServiceBitrate(); //czas za ile serwer sie zwolni
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

//


    private int getIdOfQueueWithTheLowestTimestampOfNextPacket() {
        double lowestValue = Double.POSITIVE_INFINITY;
        ArrayList<Integer> id = new ArrayList<>();

        for(Map.Entry<Integer, WaitingQueue> entry : queues.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                double entryLowestTimestamp = entry.getValue().peekLowestTimestamp();

                //porownanie losowych double, wiec raczej malo kiedy bedzie ==
                if(entryLowestTimestamp <= lowestValue) {
                    if (entryLowestTimestamp == lowestValue) {
                        id.add(entry.getKey());
                    } else {
                        lowestValue = entryLowestTimestamp;
                        id.clear();
                        id.add(entry.getKey());
                    }
                }
            }
        }

        if(id.size() == 1)
            return id.get(0);
        else {
            int index = (int)(id.size() * Math.random());
            return id.get(index);
        }
    }

    private Packet wfqArrivalAlgorithm(Event event) {
        int id = event.getQueueId();    //numer kolejki ktorej dotyczy zdarzenie przybycia
        double vsi = queues.get(id).getVirtualSpacingTimestamp();  //VSi - ostatnio obliczony znacznik czasowy
        double ri = queues.get(id).getBandwidth()/server.getServiceBitrate();   //waga polaczenia
        int packetSize = sources.get(id).getPacketSize();   //rozmiar pakietu - staly lub zmienna losowa

        double timestamp = Math.max(server.getSpacingTime(), vsi) + (packetSize/ri);
        queues.get(id).setVirtualSpacingTimestamp(timestamp);
        return new Packet(timestamp, packetSize);
    }

    private boolean areAllQueuesEmpty() { //uzyte przy processDeparture do ustalenia czy serwer jest zajety
        boolean flag = true;
        for(WaitingQueue queue : queues.values()) {
            if(!queue.isEmpty())
                flag = false;
        }
        return flag;
    }

    public Statistics getStatistics() { //zwraca statystyki symulacji
        return statistics;
    }

    public long getNumberOfArrivals() { //zwraca liczbe obsluzonych przyjsc pakietu
        return statistics.getNumberOfArrivals();
    }


    public void displayInfo() {
        double simTime = Clock.getCurrentTime();
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Czas: " + simTime);
        java.lang.System.out.println("B(t): " + statistics.getServerBusyTime());
        java.lang.System.out.println("Zajetosc serwera: " + server.isBusy());
        queues.forEach((id, queue) -> {
            java.lang.System.out.println("--");
            java.lang.System.out.println("Kolejka " + id);
            Statistics.QueueStatistics stats = statistics.getQueueStatistics(id);
            java.lang.System.out.println("Sumaryczna ilosc oczekujacych: " + stats.numberOfDelays);
            java.lang.System.out.println("Suma czasow oczekiwania: " + stats.totalDelay);
            java.lang.System.out.println("Q(t) : " + stats.queueTime);
            java.lang.System.out.println("Dlugosc kolejki: " + queue.size());
            if(!queue.isEmpty())
                java.lang.System.out.println("Znacznik cz. brzegowego elementu: " + queue.peekLowestTimestamp());
        });
        java.lang.System.out.println("Nastepne zdarzenie: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("Zrodlo: " + eventList.peekNextEvent().getQueueId());
        java.lang.System.out.println("--");
    }

}
