package simulator;


import simulator.sourcetypes.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

//        System.out.println("Liczba pakietow przychodzacych");
//        final int N = scan.nextInt();
        final int N = 10000000;

//        System.out.println("Bitrate serwera [b/s]");
//        final double C = scan.nextDouble();
        final double C = 1000;

        Scheduler scheduler = new Scheduler(C);

        for (int i = 1; i <= 2; i++) { //tworzenie dwóch kolejek
            System.out.println("  Kolejka " + i);
            System.out.println("bitrate:");

            int bitrate=scan.nextInt();

            scheduler.addQueue(i, bitrate);


            System.out.println("Rodzaj zrodla");
            System.out.println("1. Naplyw Poissona ze stalym rozmiarem pakietu");
            System.out.println("2. Naplyw Poissona z wykladniczym rozmiarem pakietu");
            System.out.println("3. Naplyw CBR ze stalym rozmiarem pakietu");
            int choice = scan.nextInt();
            if(choice==1) {
//                System.out.println("Sredni odstep i rozmiar pakietu [B]");
//                scheduler.addSource(i, new PoissonTimeConstantSize(scan.nextDouble(),scan.nextInt()));
                scheduler.addSource(i, new PoissonTimeConstantSize(0.1,bitrate/80));
            } else if (choice==2){
//                System.out.println("Sredni odstep i sredni rozmiar pakietu [B]");
//                scheduler.addSource(i, new PoissonTimeExponentialSize(scan.nextDouble(),scan.nextInt()));
                scheduler.addSource(i, new PoissonTimeExponentialSize(0.1,bitrate/80));
            }else if (choice==3){
//                System.out.println("Odstep i rozmiar pakietu [B]");
//                scheduler.addSource(i, new ConstantTimeConstantSize(scan.nextInt(), scan.nextInt()));
                scheduler.addSource(i, new ConstantTimeConstantSize(0.1, bitrate/80));
            }else{
                System.out.println("Niepoprawny typ zrodla, prosze zresetowac program");
                return;
            }

        }

            while (scheduler.getNumberOfArrivals() < N) {
                scheduler.processNextEvent();

//                scheduler.displayInfo();
            }

            Statistics statistics = scheduler.getStatistics();
            statistics.displayAllStatistics();

    }
}
