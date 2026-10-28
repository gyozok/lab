package dev.gyozok.virtualthread;

public class VirtualThreadInterruption {

    static void main() {
        Thread platformThread = Thread.ofVirtual().start(
                () -> {
                    try {
                        System.out.println("Virtual thread started");
                        for (int i=0; i<5; i++) {
                            System.out.println("Virtual thread working: " + i);
                            Thread.sleep(1000);
                        }
                        System.out.println("Virtual thread finished");
                    } catch (InterruptedException e) {
                        System.out.println("Virtual thread interrupted!");
                    }
                }
        );

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
        }

        platformThread.interrupt();
    }

}
