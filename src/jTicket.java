import java.util.ArrayList;

/**
 * Created by Evan on 5/18/2016.
 */
public class jTicket {
    public static void main (String[] args) {
        TicketManager manager = new TicketManager("WES 2016");
        manager.functionManager();
        //manager.addTickets("Regular",1,5,true);
        //manager.addTickets("Regular - Group",10, 2, true);
        //manager.addTickets("Premium",1,15,true);
        //manager.addTickets("VIP",1,10,true);
        //manager.addTickets("Premium - Group",1,15,true);
        ArrayList<String> limitTypes = new ArrayList<>();
        //limitTypes.add("Regular");
        //manager.validateTickets(limitTypes);
        /*limitTypes.clear();
        limitTypes.add("Regular - Group");
        limitTypes.add("Premium - Group");
        manager.validateTickets(limitTypes);
        limitTypes.clear();
        limitTypes.add("Premium");
        manager.validateTickets(limitTypes);
        limitTypes.clear();
        manager.validateTickets(limitTypes);*/
        manager.printStats();
    }


}
