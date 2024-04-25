package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class SupportTicket {
    public enum Type { HARDWARE, SOFTWARE, NETWORK }

    private static int ticketCounter = 0;

    private int id;
    private String description;
    private int priority;
    private Type type;

    public SupportTicket(String description, int priority, Type type) {
        this.id = ++ticketCounter;
        this.description = description;
        this.priority = priority;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public Type getType() {
        return type;
    }
}

interface SupportHandler {
    void handleRequest(SupportTicket ticket);
}

class HardwareSupportHandler implements SupportHandler {
    private SupportHandler nextHandler;

    public HardwareSupportHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(SupportTicket ticket) {
        if (ticket.getType() == SupportTicket.Type.HARDWARE) {
            System.out.println();
            System.out.println("Hardware team is handling ticket #" + ticket.getId() + ": " + ticket.getDescription());
            System.out.println();
        } else if (nextHandler != null) {
            nextHandler.handleRequest(ticket);
        } else {
            System.out.println();
            System.out.println("No handler available for ticket #" + ticket.getId());
            System.out.println();
        }
    }
}

class SoftwareSupportHandler implements SupportHandler {
    private SupportHandler nextHandler;

    public SoftwareSupportHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(SupportTicket ticket) {
        if (ticket.getType() == SupportTicket.Type.SOFTWARE) {
            System.out.println();
            System.out.println("Software team is handling ticket #" + ticket.getId() + ": " + ticket.getDescription());
            System.out.println();
        } else if (nextHandler != null) {
            nextHandler.handleRequest(ticket);
        } else {
            System.out.println();
            System.out.println("No handler available for ticket #" + ticket.getId());
            System.out.println();
        }
    }
}

class NetworkSupportHandler implements SupportHandler {
    private SupportHandler nextHandler;

    public NetworkSupportHandler(SupportHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(SupportTicket ticket) {
        if (ticket.getType() == SupportTicket.Type.NETWORK) {
            System.out.println();
            System.out.println("Network team is handling ticket #" + ticket.getId() + ": " + ticket.getDescription());
            System.out.println();
        } else if (nextHandler != null) {
            nextHandler.handleRequest(ticket);
        } else {
            System.out.println();
            System.out.println("No handler available for ticket #" + ticket.getId());
            System.out.println();
        }
    }
}

public class HelpDesk {
    private List<SupportHandler> handlerChain;
    private Map<SupportTicket.Type, SupportHandler> handlerMap;

    public HelpDesk() {
        handlerChain = new ArrayList<>();
        handlerMap = new HashMap<>();
    }

    public void addHandler(SupportHandler handler) {
        handlerChain.add(handler);
        handlerMap.put(getHandlerType(handler), handler);
    }

    public void removeHandler(SupportHandler handler) {
        handlerChain.remove(handler);
        handlerMap.remove(getHandlerType(handler));
    }

    public void handleRequest(SupportTicket ticket) {
        SupportHandler handler = handlerMap.get(ticket.getType());
        if (handler != null) {
            handler.handleRequest(ticket);
        } else {
            System.out.println("No handler available for ticket #" + ticket.getId());
        }
    }

    private SupportTicket.Type getHandlerType(SupportHandler handler) {
        if (handler instanceof HardwareSupportHandler) {
            return SupportTicket.Type.HARDWARE;
        } else if (handler instanceof SoftwareSupportHandler) {
            return SupportTicket.Type.SOFTWARE;
        } else if (handler instanceof NetworkSupportHandler) {
            return SupportTicket.Type.NETWORK;
        } else {
            throw new IllegalArgumentException("Unsupported handler type");
        }
    }

    public static void main(String[] args) {
        HelpDesk helpDesk = new HelpDesk();

        helpDesk.addHandler(new HardwareSupportHandler(null));
        helpDesk.addHandler(new SoftwareSupportHandler(null));
        helpDesk.addHandler(new NetworkSupportHandler(null));

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println("Welcome to the Help Desk!");
        while (true) {
            System.out.println("Enter ticket description:");
            String description = scanner.nextLine();
            System.out.println("Enter ticket priority (1-3):");
            int priority = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter ticket type (HARDWARE, SOFTWARE, NETWORK):");
            String typeStr = scanner.nextLine().toUpperCase();
            SupportTicket.Type type = SupportTicket.Type.valueOf(typeStr);

            SupportTicket ticket = new SupportTicket(description, priority, type);
            helpDesk.handleRequest(ticket);

            System.out.println("Do you want to create another ticket? (yes/no)");
            String answer = scanner.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                break;
            }
        }
        scanner.close();
    }
}
