package oleg;

import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.wire.BasicAuthWire;
import com.jcabi.http.wire.VerboseWire;

import java.io.IOException;
import java.net.HttpURLConnection;


import static com.jcabi.http.Request.GET;
import static com.jcabi.http.Request.POST;

/**
 * Created by okunets on 13.04.2017.
 */
public class BaseContainer {
    protected MkContainer container;

    public MkContainer getBasicContainer() throws IOException {
        MkContainer container = new MkGrizzlyContainer();
        container.next(
                new MkAnswer.Simple("Authentication is successful!")
                        .withStatus(HttpURLConnection.HTTP_OK)
        );
        container.next(
                new MkAnswer.Simple("Look.U've got some error!")
                        .withStatus(HttpURLConnection.HTTP_OK)
        );
        container.start();
        return container;
    }

    public Response authorize() throws IOException {
        getBasicContainer();
//        container.start();
        Response response = new JdkRequest(container.home())
                .through(BasicAuthWire.class)
                .method(POST)
                .header("Authorization","amVmZjoxMjM0NQ==")
                .fetch();
        return response;
    }

    public Response getErrors() throws IOException {
        getBasicContainer();
        return new JdkRequest(container.home())
                .through(VerboseWire.class)
                .method(POST)
                .body().set("{'id':5}")
                .back()
                .fetch();
    }
}
