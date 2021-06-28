package simulator.sourcetypes;
//import simulator.Source;

public interface SourceType {
    double getTimeToNextArrival();
    int getPacketSize();
}
