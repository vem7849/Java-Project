package main.java;

public class HttpHandler implements Runnable {

  public static void spawn(HttpServer server) {
    // Spawns a new thread to run a handler.
    Thread thread = new Thread(new HttpHandler(server));
    thread.start();
  }

  private HttpServer server;

  public HttpHandler(HttpServer server) {
    // Constructs a handler for http requests to the server.
    this.server = server;
  }

  public void run() {
    // TODO: implement run()
    throw new UnsupportedOperationException("run() not yet implemented");
  }
}
