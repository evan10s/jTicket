import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by RandyRyanEvan on 5/15/2016.
 */
public class TicketManager {
    static ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    String eventName;

    public TicketManager (String eventName) {
        this.eventName = eventName;
    }

    public static void main (String[] args) {
        TicketManager manager = new TicketManager("WES 2016");
        manager.addTicket("VIP",1);
        manager.addTicket("VIP",1);
        manager.addTicket("Regular",1);
        manager.addTicket("Regular",10);
        manager.addTicket("Regular",1);
        for (int i = 0; i < tickets.size(); i++) {
            System.out.println(tickets.get(i).id);
        }
        ArrayList<String> limitTypes = new ArrayList<String>();
        limitTypes.add("Regular");
        manager.validateTickets(limitTypes);
    }

    public void addTicket(String type, int maxUses) {
        try {
            tickets.add(Ticket.createTicket(type, maxUses));
        } catch(Error error) {

        }
    }

    public void validateTickets(ArrayList<String> limitTicketTypes) {
        System.out.println("jTicket v0.1-beta");
        System.out.println("Enter ticket ID to validate");
        Scanner idScanner = new Scanner(System.in);
        int id = -1;
        Ticket ticketToCheck;
        boolean found = false,
                checkingTicketType = false;
        while (idScanner.hasNextInt()) {

            while (!idScanner.hasNextInt()) {
                idScanner.next();
            }
            id = idScanner.nextInt();
            if (id < 100000000) {
                System.out.println("INVALID: invalid id format");
            } else {

                ticketToCheck = findTicket(id);
                if (ticketToCheck == null) {
                    System.out.println("INVALID: id " + id + " is not a valid ticket");
                } else {
                    if (limitTicketTypes.size() > 0) {
                        checkingTicketType = true;
                        for (int i = 0; i < limitTicketTypes.size(); i++) {
                            if (limitTicketTypes.get(i).equals(ticketToCheck.type)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (checkingTicketType && !found) { //check ticket type if we need to
                        System.out.println("EntryRestrict: entry prohibited at this entrance for ticket " + id);
                    } else if (ticketToCheck.uses < ticketToCheck.maxUses) {
                        ticketToCheck.uses++;
                        System.out.println("Ticket " + id + " is valid. " + (ticketToCheck.maxUses - ticketToCheck.uses) + " use(s) remaining.");
                    } else {
                        System.out.println("INVALID: ticket " + id + " has already been redeemed");
                    }

                    //reset variables
                    checkingTicketType = false;
                    found = false;
                }
            }
        }
    }

    public Ticket findTicket (int id) {
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
