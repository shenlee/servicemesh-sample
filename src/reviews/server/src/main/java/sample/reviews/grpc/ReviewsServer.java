package sample.reviews.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReviewsServer {
	  private static final int DEFAULT_PORT = 9080;

	  public static void main(String[] args) throws Exception {

	    int port = DEFAULT_PORT;

	    final ReviewsServer server = new ReviewsServer();
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        try {
	          System.out.println("Shutting down");
	          server.stop();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	      });
	    server.start(port);
	    System.out.format("Bookstore service listening on %d\n", port);
	    server.blockUntilShutdown();
	  }

	  private Server server;

	  private void start(int port) throws IOException {
	    server = ServerBuilder.forPort(port)
	        .addService(new ReviewsService())
	        .build().start();
	  }

	  private void stop() throws Exception {
	    server.shutdownNow();
	    if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
	      System.err.println("Timed out waiting for server shutdown");
	    }
	  }

	  private void blockUntilShutdown() throws InterruptedException {
	    if (server != null) {
	      server.awaitTermination();
	    }
	  }
}
