import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.*;

public class server extends JFrame{

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public server()
    {
        try {
            server =  new ServerSocket(8);
            System.out.println("Server is ready to accept connection..");
            System.out.println("waiting..");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
//            startWriting();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void createGUI()
    {
        this.setTitle("Server Messanger[END]");
        this.setSize(500,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setSize(600,100);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //frame ka layout set karenga
        this.setLayout(new BorderLayout());

        //adding the components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);

    }

    private void handleEvents()
    {
        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                // System.out.println("Key Released!!" + e.getKeyCode());
                if(e.getKeyCode()==10)
                {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : " + contentToSend+"\n");

                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }





    public void startReading()
    {
        Runnable r1 = () -> {
            System.out.println("Reader Started..");

                try {
                    while (true) {
                        String msg = br.readLine();
                        if (msg.equals("exit")) {
                            System.out.println("Client Terminated the chat.");
                            JOptionPane.showMessageDialog(this,"Server Terminated The Chat");
                            messageInput.setEnabled(false);

                            socket.close();
                            break;
                        }
                        //System.out.println("Client: " + msg);
                        messageArea.append("Server: " + msg+"\n");
                    }

                }catch (Exception e){
                    //e.printStackTrace();
                    System.out.println("Connection Closed..");
                }

        };

        new Thread(r1).start();
    }

    public void startWriting()
    {
        Runnable r2=()->{
            System.out.println("Writer Started..");
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit"))
                    {
                        socket.close();
                        break;
                    }

                }
            }catch (Exception e)
            {
                //e.printStackTrace();
                System.out.println("Connection Closed..");
            }
        };

        new Thread(r2).start();
    }


    public static void main(String[] args) {
        System.out.println("This is server.. Going to Start Server");
        new server();
    }
}
