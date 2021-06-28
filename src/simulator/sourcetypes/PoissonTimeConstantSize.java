package simulator.sourcetypes;


//import simulator.Source;

//źródło pakietów typu A
//napływ o rozkładzie Poissona ze stalym rozmiarem pakietów
public class PoissonTimeConstantSize implements SourceType {
    private double meanTime;
    private int size;
    Generator generator;

    public PoissonTimeConstantSize(double meanTime, int size) {
        this.meanTime = meanTime;
        this.size = size;
        this.generator = new Generator();
    }

    @Override
    public double getTimeToNextArrival() {
        double n = generator.getPoissonRandom(meanTime);

//        System.out.println(n);

        if(n!=0)
            return n;
        else
            return 0.000001;
    }

    @Override
    public int getPacketSize() {
        return size;
    }
}
