package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer implements BasicTalkInterface {

  private Socket client;
  private ServerSocket server;
  private BufferedReader sockin;
  private PrintWriter sockout;
  private BufferedReader stdin;

  public TalkServer(int portnumber) throws IOException {
    this(new ServerSocket(portnumber));
  }

  public TalkServer(ServerSocket server) throws IOException {
    this.server = server;
    this.client = this.server.accept();
    this.sockin = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
    this.sockout = new PrintWriter(this.client.getOutputStream(), true);
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
    this.client.close();
    this.server.close();
  }

  public String status() {
    return client.toString();
  }

  public void syncIO() throws IOException {
    while (true) {
      if (this.sockin.ready()) {
        System.out.printf("[remote] %s\n", this.sockin.readLine());
      }
      if (this.stdin.ready()) {
        this.sockout.println(this.stdin.readLine());
      }
    }
  }
}