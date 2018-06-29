# -*- coding: utf-8 -*-
"""The Python implementation of the gRPC client."""
from __future__ import print_function
import grpc
import sys
from proto import reviews_pb2, reviews_pb2_grpc


_PORT = '9080'

def run():
    conn = grpc.insecure_channel(_HOST + ':' + _PORT)
    client = reviews_pb2_grpc.ReviewsStub(channel=conn)
    response = client.BookReviewsById(reviews_pb2.BookReviewsRequest(productId=123456))
    print(response.reviews)

## 
if __name__ == '__main__':

    if len(sys.argv)== 2:
        print (sys.argv[1])
        _HOST = sys.argv[1]
    else:
        _HOST = 'localhost'

    #    
    run()
