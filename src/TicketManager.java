import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class TicketManager {
    public ArrayList<Ticket> tickets = new ArrayList<Ticket>();
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

    private void addTicketCommandLine() {
        System.out.println("Enter ticket type");
        Scanner userInput = new Scanner(System.in);
        String ticketType,
               ticketVCAnswer;
        int ticketUses,
            numTickets;
        boolean ticketVC;
        while (!userInput.hasNext()) {
            userInput.next();
        }
        ticketType = userInput.next();

        System.out.println("Number of tickets:");
        while (!userInput.hasNext()) {
            userInput.next();
        }
        numTickets = userInput.nextInt();

        System.out.println("Number of uses allowed per ticket:");
        while (!userInput.hasNext()) {
            userInput.next();
        }
        ticketUses = userInput.nextInt();

        System.out.println("Require PIN to redeem these tickets?  y/n");
        while (!userInput.hasNext()) {
            userInput.next();
        }
        ticketVCAnswer = userInput.next();
        ticketVC = ticketVCAnswer.equals("y");

        addTickets(ticketType,ticketUses,numTickets,ticketVC);

    }

    public void functionManager() {
        System.out.println("Welcome to jTicket v0.1-beta");
        Scanner functionGetter = new Scanner(System.in);
        String functionRequested = "";
        ArrayList<String> limitTicketTypes = new ArrayList<>();
        while(!functionRequested.equals("quit")) {
            System.out.println("Enter function name to continue or quit to exit");
            while (!functionGetter.hasNext()) {
                functionGetter.next();
            }
            functionRequested = functionGetter.next();
            switch (functionRequested) {
                case "create":
                    addTicketCommandLine();
                    break;
                case "validate":
                    validateTickets(limitTicketTypes);
                    break;
                case "print":
                    printTickets();
                    break;
            }
        }
    }

    /*
    *
    * @param validationCode Code required to redeem this ticket; enter a number >= 1000 to use or 0 to ignore
     */
    public void addTicket(String type, int maxUses, boolean useValidationCode) {
        int validationCode;
        if (useValidationCode) {
            validationCode = ThreadLocalRandom.current().nextInt(1000,9999);
        } else {
            validationCode = 0;
        }

        try {
            tickets.add(Ticket.createTicket(type, maxUses, validationCode));
        } catch(Error ignored) {

        }
    }

    public void addTickets(String type, int maxUses, int number, boolean validationCode) {
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

    private void validateTickets(ArrayList<String> limitTicketTypes) {
        System.out.println("jTicket v0.1-beta");
        System.out.println(this.eventName);
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
        System.out.println("Total tickets checked: " + this.totalTicketsChecked);
        System.out.println("Valid entrances: " + this.validEntries);
        System.out.println("EntryRestrict blocks: " + this.entryRestrictErrors);
        System.out.println("  Wrong entrance: " + this.entryRestrictWrongEntrances);
        System.out.println("  Incorrect validation code: " + this.entryRestrictWrongVCs + " (of " + this.validationCodesRequested + " requested)");
        System.out.println("Invalid ticket IDs: " + this.notFoundTickets);
        System.out.println("Attempted excessive redemptions: " + this.alreadyRedeemed);
    }

    private void printTickets() {
        for (Ticket ticket : tickets) {
            if (ticket.validationCode >= 1000) {
                System.out.println("Ticket " + ticket.id + " valid for " + ticket.maxUses + " " + ticket.type + " entrance(s) with PIN " + ticket.validationCode);
            } else {
                System.out.println("Ticket " + ticket.id + " valid for " + ticket.maxUses + " " + ticket.type + " entrance(s)");
            }
        }
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
