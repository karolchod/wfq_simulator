package simulator.sourcetypes;

//źródło pakietów typu C
//napływ pakietów o stałej szybkości bitowej (CBR) ze stałym rozmiarem pakietów
public class ConstantTimeConstantSize implements SourceType {

    private double time;
    private int size;

    public ConstantTimeConstantSize(double time, int size) {
        this.time = time;
        this.size = size;
    }

    @Override
    public double getTimeToNextArrival() {
        return time;
    }

    @Override
    public int getPacketSize() {
        return size;
    }
}
