package Test;

public class Operation {
    private static final String YELLOW ="\033[33m";
    private static final String GREEN ="\033[32m";
    private static final String RED ="\033[31m";
    private static final String BOLD ="\033[1m";
    private static final String RESET ="\033[0m";
    private static final String CYAN ="\033[36m";
    public static void yellowOp(String word){
        System.out.println(YELLOW+BOLD+"[i] "+RESET+ word);
    }
    public static void greenOp(String word){
        System.out.println(GREEN+BOLD+"[+] "+RESET+ word);
    }
    public static void redOp(String word){
        System.out.println(RED+BOLD+"[!] "+RESET+ word);
    }
    public static void cyanOp(String word){
        System.out.println(CYAN+BOLD+"[*] "+RESET+ word);
    }
    public static void client(String word){
        System.out.print("\n"+YELLOW+BOLD+"[CLIENT] "+RESET+ word+ "\n"+GREEN+BOLD+"[SERVER] "+RESET);
    }
    public static void clientmsg(){
        System.out.print(YELLOW+BOLD+"[CLIENT] "+RESET);
    }
    public static void server(String word){
        System.out.print("\n"+GREEN+BOLD+"[SERVER] "+RESET+ word+"\n"+YELLOW+BOLD+"[CLIENT] "+RESET);
    }
    public static void servermsg(){
        System.out.print(GREEN+BOLD+"[SERVER] "+RESET);
    }
}
