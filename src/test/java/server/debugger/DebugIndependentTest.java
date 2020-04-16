package server.debugger;

public class DebugIndependentTest {

    /**
     * quick demo of how the debug suite would work. each stream gets its own thread.
     * of course assuming that at least one debug message is printed
     * @param args
     */
    public static void main(String[] args){
    //public static void notMain(String[] args){

        DebugOut dOut = new DebugOut();
        dOut.start();
        DebugIn dIn = new DebugIn();
        dIn.start();

        for(int i=0;i<100000;i++){
            DebugMessage dM = new DebugMessage("message #"+i);
            dOut.addMessageToQueue(dM);
        }
    }

}
