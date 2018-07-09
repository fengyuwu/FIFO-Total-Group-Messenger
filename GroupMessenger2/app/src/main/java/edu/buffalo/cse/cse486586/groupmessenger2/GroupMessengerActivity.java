//Intensive testing passed 10/10 Every time it runs

//UI code disabled for performance
//Final Version
//UI/UN-need Exception handing codes can be comment out/delete to improve performance.

//ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
//ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
//ISIS algorithm code  mainly based on Lecture slides
//Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
//Also used to detect failed avd / process so that it will remove bad message.

//Socket message passing code  mainly based on old PA1,PA2A and https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
//https://developer.android.com/reference/java/net/Socket.html
//https://developer.android.com/reference/java/net/ServerSocket.html
//Sockets mainly based on https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java
//Server Sockets mainly based on https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java

//Socket message passing code mainly base on https://docs.oracle.com/javase/7/docs/api/java/io/ObjectOutputStream.html
//https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html

//Debug using Toast, Toast code mainly based on https://developer.android.com/guide/topics/ui/notifiers/toasts.html
//URI,content provider code mainly base on PA1,PA2A.


package edu.buffalo.cse.cse486586.groupmessenger2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * All Source:
 * https://developer.android.com/guide/components/activities/activity-lifecycle.html
 * https://developer.android.com/reference/android/view/View.html
 * https://developer.android.com/reference/android/text/method/MovementMethod.html
 * https://developer.android.com/reference/android/text/method/ScrollingMovementMethod.html
 * https://developer.android.com/reference/android/widget/Button.html
 * https://developer.android.com/reference/android/content/ContentResolver.html
 * https://developer.android.com/reference/android/app/Activity.html
 * https://developer.android.com/reference/android/view/View.OnClickListener.html
 * https://developer.android.com/guide/topics/ui/menus.html
 * https://developer.android.com/reference/android/view/MenuInflater.html
 * http://www.gecg.in/papers/ds5thedn.pdf
 * https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
 * https://docs.google.com/document/d/1xgXwZ6GYA152WT3K0B1MPP7F0mf0sPCPzfqr528pO5Y/edit
 * https://www.cse.buffalo.edu/~eblanton/course/cse586/materials/2018S/12-multicast2.pdf
 * https://www.youtube.com/watch?v=yHRYetSvyjU&t=461s&list=LLDLR_M72v5WJbeR8k5Vw1Ow&index=3
 * https://www.cse.buffalo.edu/~stevko/courses/cse486/spring17/lectures/12-multicast2.pdf
 * https://developer.android.com/reference/java/io/ObjectOutputStream.html
 * https://developer.android.com/reference/java/io/ObjectInputStream.html
 * https://developer.android.com/guide/topics/ui/notifiers/toasts.html
 * Some Code From PA1 PA2A Steve's template code/hacks (Sockets, array, array list, content provider,content resolver,uri, cursor,sqlite, The book, Piaaza etc...
 * https://developer.android.com/reference/java/io/Serializable.html
 * https://developer.android.com/reference/java/util/Comparator.html
 * https://developer.android.com/reference/java/util/PriorityQueue.html
 * https://developer.android.com/reference/java/util/concurrent/PriorityBlockingQueue.html
 * https://developer.android.com/reference/java/util/Collection.html
 * https://developer.android.com/reference/java/lang/Exception.html
 * https://developer.android.com/reference/android/os/AsyncTask.html
 * https://developer.android.com/reference/java/lang/Enum.html
 * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
 * https://www.cse.buffalo.edu/~eblanton/course/cse586/materials/2018S/group-messenger-2.pdf
 * https://docs.google.com/document/d/1xgXwZ6GYA152WT3K0B1MPP7F0mf0sPCPzfqr528pO5Y/edit
 */

public class GroupMessengerActivity extends Activity {

    //Code from PA2A
    public Uri wode_uri(String... link) {// Construct uri.
        String together = "";

        for (int i = 0; i < link.length; i++) {
            together = together + link[i];


        }
        return Uri.parse(together);//given in PA2a


    }


    //Code from PA2A
    public int get_avd_name(int temp) { //method to calculate AVD/Process ID


        int AVD_who = -10000;
        switch (temp) {
            case 11108:
                AVD_who = 0;


                break;

            case 11112:
                AVD_who = 1;


                break;

            case 11116:
                AVD_who = 2;


                break;

            case 11120:
                AVD_who = 3;


                break;

            case 11124:
                AVD_who = 4;


                break;
            default:

                AVD_who = -99;
                break;

        }
        return AVD_who;
    }

    //Code from PA2A
    int REMOTE_PORT0 = 11108;
    int REMOTE_PORT1 = 11112;
    int REMOTE_PORT2 = 11116;
    int REMOTE_PORT3 = 11120;
    int REMOTE_PORT4 = 11124;
    int SERVER_PORT = 10000;
    int si = 11108;  //seq #
    int counteri = 0;//global counter
    int avd_id = -10000;  //AVD Identifier
    int failedAVD_id = -10000; //failed AVD/Process ID
    int cleaning = -10000; //Clean the queue/content provider
    PriorityQueue<Handshake> MyPQ = new PriorityQueue<Handshake>(28, Handshake.tieBreaker); //dynamic queue that grow as more message need to be stored.
    ArrayList<Integer> Shutdown_AVDs = new ArrayList<Integer>(); //locate failed avd
    //https://developer.android.com/reference/java/util/PriorityQueue.html


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Source: PA1,PA2A,Steve's hack code
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        avd_id = Integer.parseInt(myPort);
        setContentView(R.layout.activity_group_messenger);
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new Server().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (Exception e) {
            //UI code, commented out to improved performance.

            //catch all possible exception that cause the program to stall.

        }


        //PA2A CODE
        final Button button = (Button) findViewById(R.id.button4);//From Android developer.com
        final EditText editText = (EditText) findViewById(R.id.editText1);

        button.setOnClickListener(new View.OnClickListener() {
            //Do not delete/comment!
            //Implement an OnClickListener UI Code that must exist for the button to be pressed and message to be send and store in queue, content provider.
            public void onClick(View v) { //what action to take when the Send button is clicked.
                // Code here executes on main thread after user presses button
                String msg = editText.getText().toString() + "\n";//Store the input box value in a String called msg.
                editText.setText(""); // Reset the input box to be empty.
//                tv.append(msg);//append the msg on the screen.//for now just clear input box...
                new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //PA2A CODE
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    @SuppressLint("StaticFieldLeak")

    public class Server extends AsyncTask<ServerSocket, String, Void> {
        //Code taken from  https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java
        //Also from PA2A, some modification was made to make it work with object,Serializable was used. it works.


        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];


            try {
                do { //this while loop enabled the ability to keep listening for incoming message.
                    try {

                        //Code taken from  https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java
                        //Also from PA2A, some modification was made to make it work with object,Serializable was used. it works.


                        Socket socket = serverSocket.accept(); //accept a connection.
//                        socket.setSoTimeout(0);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //Used to write object/proposed priority back to client
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());  //Used to read object/from to client
                        Handshake message_from_client = (Handshake) ois.readObject(); //used to read object from Client.
                        failedAVD_id = message_from_client.getClient_AVD_ID(); // Trick to identify failed AVD.

//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }


                        if (message_from_client.getseq() == -999) { // if the message is heard from the Server for the very First time. -999 is just a flag define in Handshake class. When a Handshake object is created, it will auto set to -999.

                            //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                            //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                            //ISIS algorithm code  mainly based on Lecture slides
                            //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                            //Also used to detect failed avd / process so that it will remove bad message.
                            Handshake pass = new Handshake(); //Create a object that store the proposed priority.
                            pass.setseq(si); //Set the proposed priority of this object.

                            si++; //Increment the seq. no. by 1 each time a message come in.
                            oos.writeObject(pass);//Each process/Server reply with proposed priority (seq. no.). 1 parameter The Proposed Priority By Server.
                            oos.flush(); //Force the message to go out immediately


                            pass.setMsgContent(message_from_client.getmsgContent()); //put the message content in the object and insert it into the queue.

                            pass.setClient_AVD_ID(message_from_client.getClient_AVD_ID()); //Used to remember which avd sent this message. (Used to identify failed port, and update message)

                            MyPQ.offer(pass); //Store message in a priority queue.


                        }


                        //The Following code is executed.
                        // if the message is heard from the Server for the Second time
                        // This is when the Sender/Client had chosen the Final/Agreed priority.
                        //Server is currently receiving/reading agreed priority
//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }


                        ObjectInputStream oois = new ObjectInputStream(socket.getInputStream()); //Read object sent From Client.

                        Handshake hehe = (Handshake) oois.readObject(); //Read agreed/ Final priority from Client It has 3 information to the Server. (Max/seq number/Agree/final priority),Message Content, suggesting process/AVD ID.
                        failedAVD_id = hehe.getClient_AVD_ID(); // Used to identify the failed AVD.
                        hehe.setAck(1);// Mark message as deliverable


                        for (Handshake message : MyPQ) { //Modify message on Hold back queue as follow
                            //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                            //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                            //ISIS algorithm code  mainly based on Lecture slides
                            //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                            //Also used to detect failed avd / process so that it will remove bad message.


                            if (hehe.getmsgContent().equals(message.getmsgContent()) && hehe.getClient_AVD_ID() == (message.getClient_AVD_ID())) { //located the old message received in Server step 1
                                {
                                    MyPQ.remove(message); //remove that old message
                                    MyPQ.offer(hehe); //insert a new message
                                    //This updated the process sequence number,
                                    //This marked this message as deliverable.
                                    //This also updated the process ID that suggested this process.
                                    //These update steps are Needed in the algorithm. Source :https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                                }

                            }
                        }


//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }

                        //Deliver any deliverable messages at the front of  priority queue
                        while (!MyPQ.isEmpty() && MyPQ.peek().getAck() == 1) { //if the message is deliverable and the make sure the queue is not empty, otherwise exception will be thrown.

//                            if (cleaning ==1 &&MyPQ.peek().getClient_AVD_ID()==Shutdown_AVD){
//                                MyPQ.poll();
//                            }
//                                publishProgress("AVD_" + Stringing.valueOf(out.getClient_AVD_ID()));
//                                publishProgress(out.getmsgContent());

//                            else {
                            //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                            //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                            //ISIS algorithm code  mainly based on Lecture slides
                            //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                            //Also used to detect failed avd / process so that it will remove bad message.

                            String conv = Integer.toString(counteri++);//Updated the global counter, each time there is a message that is published

                            ContentValues mybag = new ContentValues();//create a ContentValues type to hold seq # and received msg.

                            mybag.put("value", MyPQ.peek().getmsgContent());//Insert the Message into the Content provider
                            mybag.put("key", conv); //Insert the Global counter for this Message into the Content provider

                            getContentResolver().insert(wode_uri("content", "://edu.buffalo.cse.cse", "486586", ".groupmessenger2", ".provider"), mybag); //insert the seq # and msg into my content provider.

                            MyPQ.poll();  //remove this message from the hold back queue.
//                            }

                        }

                        socket.close();//release resource

                    } catch (EOFException e) {
                        //One AVD has fallen.
                        //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                        //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                        //ISIS algorithm code  mainly based on Lecture slides
                        //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                        //Also used to detect failed avd / process so that it will remove bad message.


                        Shutdown_AVDs.add(failedAVD_id);
                        cleaning = 1;
                        if (cleaning == 1) {
                            if (!Shutdown_AVDs.isEmpty()) {
                                for (Handshake out : MyPQ) {

                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0))) {
                                        MyPQ.remove(out); //remove this BAD message from the hold back queue.

                                    }

                                }
                            }
                        }
                        //catch This exception is used to detect a failure.
                        //https://developer.android.com/reference/java/io/EOFException.html


                    } catch (Exception e) {
                        //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                        //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                        //ISIS algorithm code  mainly based on Lecture slides
                        //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                        //Also used to detect failed avd / process so that it will remove bad message.
                        cleaning = 1;
                        //UI code, commented out to improved performance.
                        //handle all
                        // possible exceptions that may be thrown when there is a failure
                        //catch all possible exceptions that cause the program to stall.
//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }
                    }

                } while (true);
            } catch (Exception e) {
                //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                //ISIS algorithm code  mainly based on Lecture slides
                //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                //Also used to detect failed avd / process so that it will remove bad message.
                cleaning = 1;
                //UI code, commented out to improved performance.
                //handle all
                // possible exceptions that may be thrown when there is a failure
                //catch all possible exceptions that cause the program to stall.
//                if (cleaning ==1) {
//                    if (Shutdown_AVDs.isEmpty()==false) {
//                        for (Handshake out : MyPQ) {
//
//                            if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                MyPQ.remove(out);
//
//                            }
//
//                        }
//                    }
//                }


            }
            return null;
        }


        protected void onProgressUpdate(String... strings) {
            //UI code, commented out to improved performance.
            //https://developer.android.com/reference/android/widget/TextView.html
            //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
            //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
            //ISIS algorithm code  mainly based on Lecture slides
            //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
            //Also used to detect failed avd / process so that it will remove bad message.
            cleaning = 1;

//            String strReceived = strings[0].trim();
//
//            if (strReceived.length() == 5 && strReceived.charAt(3) == '_') {
//
//                TextView temp = (TextView) findViewById(R.id.textView1); //find the location of that text view.
//                temp.append(strReceived + " says : "); //append the msg on the screen from server end.
//
//
//            } else {
//
//                TextView temp = (TextView) findViewById(R.id.textView1); //find the location of that text view.
//                temp.append("\n");
//                temp.append(" " + strReceived + "\n" + "\n"); //append the msg on the screen from server end.
//
//
//            }

        }


    }

    @SuppressLint("StaticFieldLeak")
    public class Client extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... msgs) {
            int port = get_avd_name(avd_id); //Get the AVD ID which is in range of 0-4. Totally 5 of them.
            try {
                //code from PA2A
//                HashMap<Integer, Socket> hmap = new HashMap<Integer, Socket>();
                int destPort = -10000; //The port that Message goes.
                String msgToSend = msgs[0];  //Whatever the User input is, it is the Message we need to send out Store as a String.
                ArrayList<Integer> store_max = new ArrayList<Integer>(); //Data structure that store all the Proposed priority From the Server.
//                ArrayList<Socket> store_all = new ArrayList<Socket>();
                HashMap<Integer, Socket> hmap = new HashMap<Integer, Socket>();
                Handshake client_send_first = new Handshake(); //Create a object that send out to the server.
                client_send_first.setMsgContent(msgToSend); //This is the message content, attached to this object.
                client_send_first.setClient_AVD_ID(port);// Noted down the AVD/process ID that send out this message. Used to identify failed AVD.
                //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                //ISIS algorithm code  mainly based on Lecture slides
                //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                //Also used to detect failed avd / process so that it will remove bad message.

                for (int i = 0; i < 5; i++) { //Sender multi casts message to everyone including himself.
                    try {

                        switch (i) { //Used to identify AVD id number 0-4 code from PA2A
                            case 0:
                                destPort = REMOTE_PORT0; //Used to identify AVD id 0 as 11008


                                break;

                            case 1:
                                destPort = REMOTE_PORT1; //Used to identify AVD id 1 as 11112


                                break;

                            case 2:
                                destPort = REMOTE_PORT2; //Used to identify AVD id 2 as 11116


                                break;

                            case 3:
                                destPort = REMOTE_PORT3; //Used to identify AVD id 3 as 11120


                                break;

                            case 4:
                                destPort = REMOTE_PORT4; //Used to identify AVD id 4 as 11124

                                break;

                        }


                        //code from PA2A
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), destPort); // Create socket code from PA1,PA2A
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); //Used to Read msg From Server- Proposed priority
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //Used to write msg to Server - Client First time multi cast
                        hmap.put(i,socket);
//                        store_all.get(i).setSoTimeout(0);
                        oos.writeObject(client_send_first); // Write the Message object to to Server. Two parameter, Message content and Process that Send this message.
                        oos.flush(); //Force the Message to go out immediately.
                        Handshake server_reply = (Handshake) ois.readObject(); // Read the proposed priority from Servers. 1 parameter The Proposed Priority By Server.
                        store_max.add(server_reply.getseq());  //Store all the proposed priority in a array list ,then find the Max of them when needed.
                        //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                        //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                        //ISIS algorithm code  mainly based on Lecture slides
                        //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                        //Also used to detect failed avd / process so that it will remove bad message.


                    } catch (Exception e) {
                        cleaning = 1;
                        //UI code, commented out to improved performance.
                        //handle all
                        // possible exceptions that may be thrown when there is a failure
                        //catch all possible exceptions that cause the program to stall.
//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }

                    }
                }
                Handshake agree = new Handshake(); //Create a object to send out the information to the server.
                agree.setMsgContent(msgToSend); //Attach the User Message Content to this object
                agree.setseq(Collections.max(store_max)); //Find the maximum Proposed priority in the array list and Attach it to this object.
                agree.setClient_AVD_ID(port); // Noted down the AVD/process ID that suggest this seq number/Agree/final priority. Used to identify failed AVD.


                for (int i = 0; i < 5; i++) {  //Sender choose Final/agreed priority, re multi casts the message.
                    //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                    //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                    //ISIS algorithm code  mainly based on Lecture slides
                    //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                    //Also used to detect failed avd / process so that it will remove bad message.
                    try {

                        //code from PA2A
                        ObjectOutputStream oos = new ObjectOutputStream(hmap.get(i).getOutputStream()); //Used to write Object to Server
                        oos.writeObject(agree); //Send the Object that contained 3 information to the Server. (Max/seq number/Agree/final priority),Message Content, suggesting process/AVD ID.
                        oos.flush();//Force the Message to go out immediately.
//                        publishProgress(String.valueOf(cleaning));
                        oos.close();//release the resource.
//                        store_all.get(i).setSoTimeout(0);
                        hmap.get(i).close(); //release the resource.

                    } catch (Exception e) {
                        cleaning = 1;
                        ///wwwwwwasdasdsad
                        //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                        //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                        //ISIS algorithm code  mainly based on Lecture slides
                        //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                        //Also used to detect failed avd / process so that it will remove bad message.
                        //UI code, commented out to improved performance.
                        //handle all
                        // possible exceptions that may be thrown when there is a failure
                        //catch all possible exceptions that cause the program to stall.
//                        if (cleaning ==1) {
//                            if (Shutdown_AVDs.isEmpty()==false) {
//                                for (Handshake out : MyPQ) {
//
//                                    if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                        MyPQ.remove(out);
//
//                                    }
//
//                                }
//                            }
//                        }

                    }

                }

            } catch (Exception e) {
//                cleaning =0;
                cleaning = 1;
                //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
                //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
                //ISIS algorithm code  mainly based on Lecture slides
                //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
                //Also used to detect failed avd / process so that it will remove bad message.
                //UI code, commented out to improved performance.
                //handle all
                // possible exceptions that may be thrown when there is a failure
                //catch all possible exceptions that cause the program to stall.
//                if (cleaning ==1) {
//                    if (Shutdown_AVDs.isEmpty()==false) {
//                        for (Handshake out : MyPQ) {
//
//                            if (out.getClient_AVD_ID() == (Shutdown_AVDs.get(0)) && out.getAck() != 1) {
//                                MyPQ.remove(out);
//
//                            }
//
//                        }
//                    }
//                }

            }
            return null;
        }

        protected void onProgressUpdate(String... strings) {
            cleaning = 1;
            //ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
            //ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
            //ISIS algorithm code  mainly based on Lecture slides
            //Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
            //Also used to detect failed avd / process so that it will remove bad message.
            //UI code, commented out to improved performance.
            //https://developer.android.com/reference/android/widget/Toast.html


            Context context = getApplicationContext();
            CharSequence text = " message sent";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();


        }


    }
}





































