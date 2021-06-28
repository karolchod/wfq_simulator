package simulator.sourcetypes;

import java.util.Random;

public class Generator {

    private Random random;

    public Generator() {
        random = new Random();
    }

    public double getExpRandom(double mean) {
        return Math.log(1 - random.nextDouble())*(-mean);
        // Math.log(1-random.nextDouble())/(-lambda); ale srednia (wartosc oczekiwana) to 1/lambda
    }

    public double getPoissonRandom(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * random.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }
}
