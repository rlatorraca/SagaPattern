package ca.com.rlsp.camel;


import org.apache.camel.CamelContext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("order-camel")
public class OrderCamelResource {

    @Inject
    CamelContext context;

    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response saga(){

        try{
            Long id = 0L;

            order(++id, 20);
            order(++id, 30);
            order(++id, 35);
            order(++id, 25);

            return Response.ok().build();

        } catch (Exception e){
            return Response.status(500).build();
        }
    }

    private void order(Long id, int valor){
        System.out.println("Order: " + id + " value: " + valor + "\n");

        try{
            context.createFluentProducerTemplate()
                    .to("direct:saga")
                    .withHeader("id", id)
                    .withHeader("value", valor)
                    .request();
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }


    }

}
