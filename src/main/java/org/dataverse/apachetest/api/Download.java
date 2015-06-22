package org.dataverse.apachetest.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.json.Json;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("download")
public class Download {

    @GET
    public Response meta() {
        return Response.ok()
                .entity(
                        Json.createObjectBuilder()
                        .add("foo", "bar")
                        .build()
                )
                .type(MediaType.APPLICATION_JSON).
                build();
    }

    @Path("streamBytes/{numBytes}")
    @GET
    @Produces({"application/octet-stream"})
    public Response streamBytes(@PathParam("numBytes") Long numBytes) {
        final long totalBytes = numBytes;

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {

                try (FileInputStream instream = new FileInputStream("/dev/urandom")) {
                    byte[] data = new byte[8192];

                    int i = 0;
                    long total = 0;
                    while (((i = instream.read(data))) > 0 && (total < totalBytes)) {
                        long numBytesOut = total + i < totalBytes ? i : totalBytes - total;
                        os.write(data, 0, (int) numBytesOut);
                        total += numBytesOut;

                        os.flush();
                    }
                }

                os.flush();
                os.close();
            }
        };
        return Response.ok(stream).build();
    }
}
