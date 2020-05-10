/*
 * Creates an Wifi Java Server for data transfer with client
 */
package server_PAM_SRV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PAMSRV_serversocket extends Thread {
    private PAMSRV_model bm;
    private PAMSRV_controller controller;
    private int port = 5555;
    private ServerSocket server;
    private Socket client;
    private OutputStream socketoutstr;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private InputStream socketinstr;
    private InputStreamReader isr;
    private BufferedReader br;
    private OutgoingStream os;
    private IncomingStream is;
    
    public PAMSRV_serversocket(PAMSRV_controller controller, PAMSRV_model bm) {
        this.controller = controller;
        this.bm = bm;
        //Öffne Server
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            bm.debugPrint("ERROR: Unable to open server...");
            bm.debugErrorPrint(ex);
        }
        bm.debugPrint("Server gestartet.");
    }//End: Konstruktor
    
    public void run() {
        while (true) {
            try {
                //Creating Wifi-Server
                bm.debugPrint("Warte auf Client...");
                client = server.accept();
                bm.debugPrint("Verbunden mit Client " + client.getInetAddress() + " .");
            } catch (IOException ex) {
                bm.debugPrint("ERROR: Unable to accept Client...");
                bm.debugErrorPrint(ex);
            }
            try {
                //Creating Reader/Writer for Wifi-Server
                bm.debugPrint("Versuche Streamwriter/ -reader zu öffnen...");
                
                socketoutstr = client.getOutputStream();
                osw = new OutputStreamWriter(socketoutstr);
                bw = new BufferedWriter(osw);
                socketinstr = client.getInputStream();
                isr = new InputStreamReader(socketinstr);
                br = new BufferedReader(isr);
                
                bm.debugPrint("Writer / Reader geöffnet.");
            } catch (IOException ex) {
                bm.debugPrint("ERROR: Unable to open streamreader/ -writer...");
                bm.debugErrorPrint(ex);
            }
            bm.debugPrint("Versuche Stream-Threads zu öffnen...");
            is = new IncomingStream(client, br);
            os = new OutgoingStream(client, bw);
            
            controller.setIncomingStream(is);
            controller.setOutgoingStream(os);

            bm.setServerRunning(true);
            bm.debugPrint("Erfolgreich mit Client verbunden.");
        }//End: while
    }//End: Konstruktor
    
    
    class OutgoingStream extends Thread {
        Socket client;
        BufferedWriter bw;
        public OutgoingStream(Socket client, BufferedWriter bw) {
            this.client = client;
            this.bw = bw;
        }//End: Konstruktor
        @Override
        public void run() {
            String msg;
            bm.debugPrint("Outgoingstream gestartet.");
            while (true) {
                    if (bm.isOutgoingMsgAvailable()) {
                        try {
                            msg = bm.getOldestMessage();
                            if (msg != null) {
                                bw.write(msg);
                                bw.newLine();
                                bw.flush();
                            }
                        } catch (IOException ex) {
                            bm.debugPrint("ERROR: Unable to write outgoing stream...");
                            bm.debugErrorPrint(ex);
                        }
                    }
            }
        }//End: run()
    }//End: INNER class OutgoingStream
    
    
    class IncomingStream extends Thread {
        Socket client;
        BufferedReader br;
        public IncomingStream(Socket client, BufferedReader br) {
            this.client = client;
            this.br = br;
        }//End: Konstruktor
        @Override
        public void run() {
            bm.debugPrint("Incomingstream gestartet.");
            while (true) {
                try {
                    if (br.ready()) {
                            controller.handleClient(br.readLine());
                    }
                } catch (IOException ex) {
                    bm.debugPrint("ERROR: Unable to read incoming stream...");
                    bm.debugErrorPrint(ex);
                }
            }
        }//End: run()
    }//End: INNER class IncomingStream
    
    
}//End: class PAMSRV_serversocket
