package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A client implementation of the talk interface that prints messages from STDIN to the talk server
 * and prints messages from the talk server on STDOUT.
 */
public class TalkClient implements BasicTalkInterface {

  private Socket socket;
  private BufferedReader sockin;
  private PrintWriter sockout;
  private BufferedReader stdin;

  public TalkClient(String hostname, int portnumber) throws IOException {
    this(new Socket(hostname, portnumber));
  }

  private TalkClient(Socket socket) throws IOException {
    this.socket = socket;
    this.sockin = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.sockout = new PrintWriter(this.socket.getOutputStream(), true);
    this.stdin = new BufferedReader(new InputStreamReader(System.in));
  }

  public void asyncIO() throws IOException {
    while (true) {
      if (this.stdin.ready()) {
        String input = this.stdin.readLine();
        if (input.equals("STATUS")) {
          System.out.println(status());
        } else {
          this.sockout.println(input);
        }
      }
      if (this.sockin.ready()) {
        System.out.printf("[remote] %s\n", this.sockin.readLine());
      }
    }
  }

  public void close() throws IOException {
    this.stdin.close();
    this.sockout.close();
    this.sockin.close();
    this.socket.close();
  }

  public String status() {
    return socket.toString();
  }

  public void syncIO() throws IOException {
    while (true) {
      if (this.stdin.ready()) {
        this.sockout.println(this.stdin.readLine());
      }
      if (this.sockin.ready()) {
        System.out.printf("[remote] %s\n", this.sockin.readLine());
      }
    }
  }
}