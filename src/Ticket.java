import java.util.concurrent.ThreadLocalRandom;

public class Ticket {
    int id;
    String type;
    int uses;
    int maxUses;

    public Ticket (String type, int maxUses) {
        this.id = ThreadLocalRandom.current().nextInt(100000000,999999999);
        this.type = type;
        this.uses = 0;
        this.maxUses = maxUses;
    }

    /*
    * Create a new ticket
    * @param type The type of ticket to create
    * @param maxUses The maximum number of times this ticket can be used; must be at least 1
    * @return True if ticket creation was successful, false if not
     */
    public static Ticket createTicket(String type, int maxUses) throws IllegalArgumentException {
        if (maxUses < 1) { //if the maxUses parameter is less than 0, throw an exception
            throw new IllegalArgumentException();
        }
        return new Ticket(type, maxUses);
    }
}
