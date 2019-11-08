package controllers;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.util.Arrays;
import java.util.Optional;
import static play.mvc.Results.ok;

public class StreamController {
    StreamController(){
        streamed();
    }

     private void streamed() {
        Source<ByteString, NotUsed> body = Source.from(Arrays.asList(ByteString.fromString("first"), ByteString.fromString("second")));
        System.out.println("STREAM::=>");
        System.out.println(ok().streamed(body, Optional.empty(), Optional.empty()).toString());
        System.out.println("STREAM::=|");
         ok().streamed(body, Optional.empty(), Optional.empty());
     }

}
