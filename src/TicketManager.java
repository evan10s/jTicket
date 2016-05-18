import java.util.ArrayList;
import java.util.Scanner;

public class TicketManager {
    private static ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    private String eventName;
    private int entryRestrictErrors = 0;
    private int totalTicketsChecked = 0;
    private int validEntries = 0;
    private int notFoundTickets = 0;
    private int validationCodesRequested = 0;
    private int entryRestrictWrongEntrances = 0;
    private int entryRestrictWrongVCs = 0;
    private int alreadyRedeemed = 0;

    public TicketManager(String eventName) {
        this.eventName = eventName;
    }

    public static void main (String[] args) {
        TicketManager manager = new TicketManager("WES 2016");
        manager.addTickets("Regular",1,10,0);
        manager.addTickets("Regular - Group",10, 5,0);
        manager.addTickets("Premium",1,5,1234);
        manager.addTickets("VIP",1,3,12345);
        manager.addTickets("Premium - Group",15,2,0);
        for (Ticket ticket : tickets) {
            System.out.println("Ticket " + ticket.id + " valid for " + ticket.maxUses + " " + ticket.type + " entrance(s)");
        }
        ArrayList<String> limitTypes = new ArrayList<>();
        limitTypes.add("VIP");
        manager.validateTickets(limitTypes);
        limitTypes.clear();
        limitTypes.add("Regular - Group");
        limitTypes.add("Premium - Group");
        manager.validateTickets(limitTypes);
        limitTypes.clear();
        limitTypes.add("Premium");
        manager.validateTickets(limitTypes);
        limitTypes.clear();
        manager.validateTickets(limitTypes);
        manager.printStats();
    }

    /*
    *
    * @param validationCode Code required to redeem this ticket; enter a number >= 1000 to use or 0 to ignore
     */
    public void addTicket(String type, int maxUses, int validationCode) {
        try {
            tickets.add(Ticket.createTicket(type, maxUses, validationCode));
        } catch(Error ignored) {

        }
    }

    public void addTickets(String type, int maxUses, int number, int validationCode) {
        for (int i = 0; i < number; i++) {
            addTicket(type, maxUses, validationCode);
        }
    }

    private int getValidationCode() {
        Scanner codeScanner = new Scanner(System.in);
        int code;
        System.out.println("Enter validation code:");
        while (!codeScanner.hasNextInt()) {
            codeScanner.next();
        }
        return codeScanner.nextInt();
    }

    public void validateTickets(ArrayList<String> limitTicketTypes) {
        System.out.println("jTicket v0.1-beta");
        if (limitTicketTypes.size() > 0) {
            System.out.println("Entrance at this gate is restricted to " + limitTicketTypes.toString());
        }
        System.out.println("Enter ticket ID to validate");
        Scanner idScanner = new Scanner(System.in);
        int id = -1,
            code;
        String result;
        Ticket ticketToCheck;
        boolean found = false,
                checkingTicketType = false;
        while (idScanner.hasNextInt()) {

            while (!idScanner.hasNextInt()) {
                idScanner.next();
            }
            id = idScanner.nextInt();
            this.totalTicketsChecked++;
            if (id < 100000000) {
                this.notFoundTickets++;
                System.out.println("INVALID: invalid id format");
            } else {

                ticketToCheck = findTicket(id);
                if (ticketToCheck == null) {
                    this.notFoundTickets++;
                    System.out.println("INVALID: id " + id + " is not a valid ticket");
                } else {
                    if (limitTicketTypes.size() > 0) {
                        checkingTicketType = true;
                        for (String limitTicketType : limitTicketTypes) {
                            if (limitTicketType.equals(ticketToCheck.type)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (checkingTicketType && !found) { //check ticket type if we need to
                        this.entryRestrictErrors++;
                        this.entryRestrictWrongEntrances++;
                        System.out.println("EntryRestrict: entry prohibited at this entrance for ticket " + id);
                    } else if (ticketToCheck.requiresValidationCode()) {
                        this.validationCodesRequested++;
                        code = getValidationCode();
                        result = ticketToCheck.redeemTicket(code);
                        printTicketValidity(id, result, ticketToCheck);
                    } else {
                        result = ticketToCheck.redeemTicket(0);
                        printTicketValidity(id, result, ticketToCheck);
                    }

                    //reset variables
                    checkingTicketType = false;
                    found = false;
                }
            }
        }
    }

    private void printTicketValidity(int id, String result, Ticket ticketToCheck) {
        switch (result) {
            case "valid":
                this.validEntries++;
                System.out.println("Ticket " + id + " is valid. " + (ticketToCheck.maxUses - ticketToCheck.uses) + " use(s) remaining.");
                break;
            case "incorrect validation code":
                this.entryRestrictErrors++;
                this.entryRestrictWrongVCs++;
                System.out.println("EntryRestrict: incorrect validation code provided for ticket " + id);
                break;
            case "already redeemed":
                this.alreadyRedeemed++;
                System.out.println("INVALID: ticket " + id + " has already been redeemed");
                break;
        }
    }

    public void printStats() {
        System.out.println("Statistics for event " + this.eventName);
        System.out.println("Total tickets checked " + this.totalTicketsChecked);
        System.out.println("Valid entrances: " + this.validEntries);
        System.out.println("EntryRestrict blocks: " + this.entryRestrictErrors);
        System.out.println("  Wrong entrance: " + this.entryRestrictWrongEntrances);
        System.out.println("  Incorrect validation code: " + this.entryRestrictWrongVCs + " (of " + this.validationCodesRequested + " requested)");
        System.out.println("Invalid ticket IDs: " + this.notFoundTickets);
        System.out.println("Attempted excessive redemptions: " + this.alreadyRedeemed);
    }

    private Ticket findTicket(int id) {
        boolean found = false;
        int ticketIndex = -1;
        for (int i = 0; i < tickets.size(); i++){
            if (tickets.get(i).id == id) {
                found = true;
                ticketIndex = i;
                break;
            }
        }
        return found ? tickets.get(ticketIndex) : null;
    }
}
