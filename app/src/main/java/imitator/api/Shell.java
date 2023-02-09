package imitator.api;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Shell extends ApiHandler {

    protected Shell() {
        super();
    }

    void help() {
        System.out.println("\nAVAILABLE COMMANDS"
                + "\n   start: Starting all imitators."
                + "\n   stop: Stopping all imitators."
                + "\n   start imitatorName: Starting the selected imitator."
                + "\n   stop imitatorName: Stopping the selected imitator."
                + "\n   exit: Exit app."
                + "\n   help: Display help about available commands."
                + "\nAVAILABLE IMITATORS"
                + "\n   " + map.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    @Override
    public void run() {

        // wait until all services are loaded
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        help();

        Scanner scanner = new Scanner(System.in);
        System.out.print("imitator:>");
        try {
            do {
                String[] inputLine = scanner.nextLine().split(" ");

                if (inputLine.length == 1) {
                    switch (inputLine[0]) {
                        case "stop":
                            stop();
                            break;
                        case "start":
                            start();
                            break;
                        case "help":
                            help();
                            break;
                        case "exit":
                            System.exit(0); // SIGTERM
                            break;
                    }
                } else if (inputLine.length == 2) {

                    if (map.entrySet().stream().anyMatch(i -> i.getKey().equals(inputLine[1]))) {
                        if (inputLine[0].equals("stop")) {
                            stopImitator(inputLine[1]);
                        } else if (inputLine[0].equals("start")) {
                            startImitator(inputLine[1]);
                        } else {
                            System.out.println("Command not found. Use help");
                        }
                    }
                } else {
                    System.out.println("Command not found. Use help");
                }
                if (Thread.currentThread().isInterrupted() || isStop) {
                    Thread.currentThread().interrupt();
                    break;
                }
                System.out.print("imitator:>");
            } while (scanner.hasNext());
        } catch (NoSuchElementException ex) {
            if(!isStop)
                logger.warning(ex.getMessage());
        }
    }
}
