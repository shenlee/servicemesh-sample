package sample.reviews.grpc;

import io.grpc.stub.StreamObserver;

import com.google.auto.value.AutoValue.Builder;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.CookieParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ReviewsService extends ReviewsGrpc.ReviewsImplBase {

	private final static Boolean ratings_enabled = Boolean.valueOf(System
			.getenv("ENABLE_RATINGS"));

	private final static String star_color = System.getenv("STAR_COLOR") == null ? "black"
			: System.getenv("STAR_COLOR");

    private final static String ratings_service = "http://ratings:9080/ratings";

	private JsonObject getRatings(String productId){
      ClientBuilder cb = ClientBuilder.newBuilder();
      String timeout = star_color.equals("black") ? "10000" : "2500";
      cb.property("com.ibm.ws.jaxrs.client.connection.timeout", timeout);
      cb.property("com.ibm.ws.jaxrs.client.receive.timeout", timeout);
      Client client = cb.build();
      WebTarget ratingsTarget = client.target(ratings_service + "/" + productId);
      javax.ws.rs.client.Invocation.Builder builder = ratingsTarget.request(MediaType.APPLICATION_JSON);
      
      Response r = builder.get();
      int statusCode = r.getStatusInfo().getStatusCode();
      if (statusCode == Response.Status.OK.getStatusCode() ) {
        StringReader stringReader = new StringReader(r.readEntity(String.class));
        try (JsonReader jsonReader = Json.createReader(stringReader)) {
           JsonObject j = jsonReader.readObject();
           return j;
        }
      }else{
        System.out.println("Error: unable to contact "+ratings_service+" got status of "+statusCode);
        return null;
      }
    }

	@Override
	public void bookReviewsById(BookReviewsRequest request,
			StreamObserver<BookReviewsResponse> responseObserver) {
		long productId = request.getProductId();
		int starsReviewer1 = -1;
		int starsReviewer2 = -1;

		JsonObject ratingsResponse = getRatings(Long.toString(productId));
		if (ratingsResponse != null) {
          if (ratingsResponse.containsKey("ratings")) {
            JsonObject ratings = ratingsResponse.getJsonObject("ratings");
            if (ratings.containsKey("Reviewer1")){
          	  starsReviewer1 = ratings.getInt("Reviewer1");
            }
            if (ratings.containsKey("Reviewer2")){
              starsReviewer2 = ratings.getInt("Reviewer2");
            }
          }
        }

		sample.reviews.grpc.BookReviewsResponse.Builder builder = BookReviewsResponse
				.newBuilder();
		builder.setProductId(productId);
		Review review1 = Review
				.newBuilder()
				.setReviewer("Reviewer1")
				.setText(
						"An extremely entertaining play by Shakespeare. The slapstick humour is refreshing!")
				.build();

		if (ratings_enabled && starsReviewer1 != -1) {
			review1 = review1.toBuilder().setRatingstar(starsReviewer1).build();
		}
		builder.addReviews(review1);

		Review review2 = Review
				.newBuilder()
				.setReviewer("Reviewer2")
				.setText(
						"Absolutely fun and entertaining. The play lacks thematic depth when compared to other plays by Shakespeare.")
				.build();
		if (ratings_enabled && starsReviewer2 != -1) {
			review2 = review2.toBuilder().setRatingstar(starsReviewer2).build();
		}
		builder.addReviews(review2);
		BookReviewsResponse response = builder.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
