import java.util.concurrent.ThreadLocalRandom;

public class Ticket {
    int id;
    String type;
    int uses;
    int maxUses;
    int validationCode;

/*
* @param validationCode A 4- or greater digit code required to redeem the ticket
 */
    public Ticket (String type, int maxUses, int validationCode) {
        this.id = ThreadLocalRandom.current().nextInt(100000000,999999999);
        this.type = type;
        this.uses = 0;
        this.maxUses = maxUses;
        this.validationCode = validationCode;
    }

    /*
    * Create a new ticket
    * @param type The type of ticket to create
    * @param maxUses The maximum number of times this ticket can be used; must be at least 1
    * @return A new Ticket
     */
    public static Ticket createTicket(String type, int maxUses, int validationCode) throws IllegalArgumentException {
        if (maxUses < 1) { //if the maxUses parameter is less than 0, throw an exception
            throw new IllegalArgumentException();
        }
        return new Ticket(type, maxUses, validationCode);
    }

    public boolean requiresValidationCode() {
        if (this.uses == this.maxUses) { //don't request a validation code if the ticket wouldn't be able to redeemed anyways because it's already been used
            return false;
        }
        return this.validationCode >= 1000;
    }

    public String redeemTicket (int validationCode) {
        if (this.requiresValidationCode() && this.validationCode != validationCode) {
            return "incorrect validation code";
        } else {
            if (this.uses < this.maxUses) {
                this.uses++;
                return "valid";
            }
            return "already redeemed";
        }
    }

    /*
    * Reset the number of times this ticket has been used to 0
     */
    public void resetTicket() {
        this.uses = 0;
    }
}
