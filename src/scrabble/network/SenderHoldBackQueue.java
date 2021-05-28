package scrabble.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderHoldBackQueue {
  /**
   * A normal ObjectOutputStream cause problems with deserialization by the client protocol. I
   * predict it is caused by to fast sequential message sending. This class is used to make sure
   * that every message will only be send after a other one finished its sending and in the same
   * order they are added to the sender queue. The class mimics the syntax of the ObjectOutputStream
   * to be easily changeable with other approaches.
   * 
   * @author hendiehl
   */
  private ObjectOutputStream out;
  private boolean isRunning;
  private Queue<Message> holder;
  private Sender sender;

  /**
   * Constructor for the SenderHoldBackQueue which get the ObjectOutputStream of an
   * LobbyServerProtocol or an LobbyClientProtocol. Will create a sender thread which has the
   * function to send network messages in sequence.
   * 
   * @param out ObjectOutputStream of an protocol Socket.
   * @author hendiehl
   */
  public SenderHoldBackQueue(ObjectOutputStream out) {
    this.out = out;
    this.isRunning = true;
    this.holder = new ConcurrentLinkedQueue<Message>();
    this.sender = new Sender();
    this.sender.start();
  }

  /**
   * Mimic function which will add the Message to a sending Queue and notify the sender thread if he
   * isn't sending at the moment.
   * 
   * @param msg Message which will be sended.
   * @author hendiehl
   */
  public void writeObject(Message msg) {
    System.out.println("SENDER : Added");
    this.holder.add(msg); // adding to queue
    this.sender.inform();;
  }

  /**
   * Method just to make safe a message is not in the queue anymore.
   * 
   * @author hendiehl
   */
  public void flush() {
    this.sender.inform();
  }

  /**
   * Method to shutdown the sender thread.
   * 
   * @author hendiehl
   */
  public void shutdwon() {
    this.isRunning = false;
    this.sender.inform();
  }



  private class Sender extends Thread {
    /**
     * The private sender class has only the function to send messages when there are messages in
     * the queue.
     * 
     * @author hendiehl
     */


    /**
     * Run method of the thread class.
     * 
     * @author hendiehl
     */
    public void run() {
      while (isRunning) {
        send();
      }
      System.err.println("SENDER OUTRUN");
    }

    /**
     * Method to send messages if the sender queue isn't empty.
     * 
     * @author hendiehl
     */
    private void send() {
      if (!holder.isEmpty()) {
        try {
          out.writeObject(holder.poll());
          out.flush();
          System.out.println("SENDER : sended");
          System.out.println("SENDER : size : " + holder.size());
        } catch (SocketException e) {
          System.err.println("SENDER : Socket closed");
          isRunning = false;
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } else {
        this.waitUntilFill();
      }
    }

    /**
     * Method to set the sender thread on wait until he is notified about new messages.
     * 
     * @author hendiehl
     */
    private synchronized void waitUntilFill() {
      try {
        this.wait(); // waiting
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    /**
     * Method to inform the sender thread about new messages. Is also used after every sending to
     * reduce the possibility of messages in the queue after the thread waits.
     * 
     * @author hendiehl
     */
    private synchronized void inform() {
      sender.notify();
    }
  }
}
