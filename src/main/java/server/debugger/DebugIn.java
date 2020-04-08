package server.debugger;

import java.util.Scanner;

public class DebugIn extends Thread{
    public DebugIn(){}

    /**
     * has to be started in CoordConsole
     */
    public void run(){
        Scanner scan = new Scanner(System.in);

        scan.nextLine();
    }


}
