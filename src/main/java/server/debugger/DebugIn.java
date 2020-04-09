package server.debugger;

import java.util.Scanner;

public class DebugIn extends Thread{
    public DebugIn(){}

    /**
     * has to be started in CoordConsole
     */
    public void run(){
        Scanner scan = new Scanner(System.in);
        String input;
        while(true) {
            if(DebugOut.debugMode){
                System.out.print("[DEBUG]: ");
            }
            input = scan.nextLine();
            if(input.equals("debug")){
                DebugOut.debugMode = !DebugOut.debugMode;
                System.out.println("Debug mode toggled! Set to "+ DebugOut.debugMode.toString());
            }

        }
    }


}
