package ca.com.rlsp.camel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

    @Inject
    OrderService orderService;

    @Inject
    CreditService creditService;

    @Override
    public void configure() throws Exception {

        CamelSagaService sagaService = new InMemorySagaService(); // Executa o SAGA em Memorr
        getContext().addService(sagaService); // Camel cria o seu proprio Contexto

        //Saga
        // Good Path
        from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("[RLSP] Starting transaction")
                .to("direct:doOrder").log("[RLSP] Order ${header.id} created. Saga ${body}.")
                .to("direct:doCredit").log("[RLSP] Order Credit ${header.id} - value[CAD]: ${header.value} reserved for saga ${body}")
                .to("direct:getDone").log("[RLSP] Done!");

        //Pedido service
        from("direct:doOrder").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:undoOrder") // Se houver Problema
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "doOrder").log("Building new order with id ${header.id}");

        from("direct:undoOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "undoOrder").log("Order ${body} done");

        //Credito service

        from("direct:doCredit").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:undoCredit")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditService, "doCredit").log("Getting credit");

        from("direct:undoCredit")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditService, "undoCredit").log("Credit reserved SAGA ${body}");


        // Ending
        from("direct:getDone").saga().propagation(SagaPropagation.MANDATORY)
                .choice()// Pode ter o cenario de FALHA ou SUCESSO
                .end();
    }

}