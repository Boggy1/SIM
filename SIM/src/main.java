import java.util.List;
import javax.smartcardio.*;

public class main {

	/*
	 * Look in rt.jar for "javax.smartcardio" to get further information
	 * Defined in: JSR 268
	 */
    public static int listCounted() {
        
        /* we use the default TerminalFactory */
        TerminalFactory factory = TerminalFactory.getDefault();
        
        try {
            /* We can have multiple terminals on one System, so we get a list */
            List<CardTerminal> terminals = factory.terminals().list();
                        
            for (CardTerminal terminal : terminals) {
                System.out.println("Card_Terminal_Name: "+ terminal.getName());
                System.out.println("Card_in_Terminal_present: "+terminal.isCardPresent());
                System.out.println("--------------------------------------------");
            }
            return terminals.size();
            
        } catch (CardException e) {
            e.printStackTrace();
        }
        return 0;
    }
     
    /**
     * 
     * @param index is the array index of the terminal list (0..x)
     * @throws CardException if there are problems accessing the smartcard
     */
    public static void connectCard(int index) throws CardException {
        
        /* Is a Reader connected we can access? */
        if (TerminalFactory.getDefault().terminals().list().size() == 0) {
            System.err.println("No reader present");
            return;
        }
        
        /* Terminal we are working on */
         CardTerminal terminal = TerminalFactory.getDefault().terminals().list().get(index);
        
        /* Is a card present? */
        if (!terminal.isCardPresent()) {
            System.err.println("No Card present!");
            return;
        }
        
        /* Here you have to choose "T=0","T=1", "T=2", check documentation of your smart card */
        //Mostly it's "T=1", for older cards its "T=0"
        Card card = terminal.connect("T=1");
        
        System.out.println("Card_Info: "+card.toString());
        System.out.println("Card Protocol: "+ card.getProtocol());
        
        //Reset the card for use
        ATR atr = card.getATR();
 
        System.out.println("ATR: " + atr.getBytes());
        System.out.println("ATR historical bytes: "+ atr.getHistoricalBytes());
        
        /* Get the basic channel. This one can't be closed */
        CardChannel channel = card.getBasicChannel();
        
        /* Try to send a command. This one won't work! */
        byte[] command = { 0, 0, 0, 0};
        
        CommandAPDU someApdu = new CommandAPDU(command);
        
        ResponseAPDU r = channel.transmit(someApdu);
        /* Response encoded in bytes */
        byte[] response = r.getBytes();
        
        System.out.println("response: "+ response);
            
        card.disconnect(false);
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = listCounted();		
		
		try {
			if(size > 0)
				//First Terminal = 0
				connectCard(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

}
