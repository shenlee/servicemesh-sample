package sample.reviews.grpc;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannelBuilder;

public final class ReviewsClient {

	private static final String DEFAULT_ADDRESS = "localhost:9080";

	public static void main(String[] args) throws Exception {
		String address = DEFAULT_ADDRESS;
		// Create gRPC stub.
		ReviewsGrpc.ReviewsBlockingStub stub = createReviewsGrpcStub(address);
		BookReviewsRequest request = BookReviewsRequest.newBuilder()
				.setProductId(111111).build();
		BookReviewsResponse response = stub.bookReviewsById(request);
		System.out.println(response);
		response.getReviews(1).getAllFields();
	}

	static ReviewsGrpc.ReviewsBlockingStub createReviewsGrpcStub(String address) {
		Channel channel = ManagedChannelBuilder.forTarget(address)
				.usePlaintext(true).build();

		return ReviewsGrpc.newBlockingStub(channel);
	}
}
