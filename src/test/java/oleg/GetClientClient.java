package oleg;

import com.jcabi.http.Response;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.wire.VerboseWire;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.jcabi.http.Request.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by okunets on 13.04.2017.
 */
public class AddClient extends BaseContainer {
    private MkContainer thisContainer;
    @Before
    public void setUp() throws IOException {
        this.container=this.getBasicContainer();
        thisContainer=new MkGrizzlyContainer();

    }

    @Test
    public void authenticationTest() throws IOException {
        authorize();
        MkQuery logInQuery = container.take();
        MatcherAssert.assertThat(
                logInQuery.headers(),
                Matchers.hasEntry(
                        Matchers.equalTo("Authorization"),
                        Matchers.hasItem("amVmZjoxMjM0NQ==")
                )
        );
    }

    @Test
    public void getClient() throws IOException {
        Response authorize = authorize();
        Client client=new Client(
                   1,
                   "Oleg",
                   "Kunetskyi",
                "login",
                "password"
           );
        thisContainer.next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK)
                .withBody(client.toString())
        );
        thisContainer.start();
        new JdkRequest(thisContainer.home())
                .through(VerboseWire.class)
                .header("id", 1)
                .method(POST)
                .body().set(client.toString()).back()
                .fetch();

        MkQuery query = thisContainer.take();
        MatcherAssert.assertThat(
                query.body(),
               Matchers.equalTo(client.toString())
                );


    }

    @Test
    public void getErrorTest() throws IOException {
        getErrors();
        String body = container.take().body();
        assertEquals(body,"{'id':5}");
    }

}
