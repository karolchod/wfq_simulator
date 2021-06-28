package simulator.sourcetypes;

//źródło pakietów typu A
//napływ o rozkładzie Poissona z wykładniczym rozmiarem pakietów
public class PoissonTimeExponentialSize implements SourceType {

    private double meanTime;
    private int meanSize;
    Generator generator;

    public PoissonTimeExponentialSize(double meanTime, int meanSize) {
        this.meanTime = meanTime;
        this.meanSize = meanSize;
        this.generator = new Generator();
    }

    @Override
    public double getTimeToNextArrival() {
        double n = generator.getPoissonRandom(meanTime);

//        System.out.println("time "+n);

        if(n!=0)
            return n;
        else
            return 0.000001;

    }

    @Override
    public int getPacketSize() {
        int result = (int) generator.getExpRandom(meanSize);

//        System.out.println("size "+result);

        if (result != 0)
            return result;
        else
            return meanSize;
    }

}
