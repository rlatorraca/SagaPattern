package ca.com.rlsp.camel;

import org.apache.camel.Header;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

// Para poder Injetar a Classe dentro do Endpoint
@ApplicationScoped 
public class OrderService {
    
    private Set<Long> orders = new HashSet<>(); // Set é mais performatico que List

    public void doOrder(@Header("id") Long id){
        orders.add(id);
    }

    public void undoOrder(@Header("id") Long id){
        orders.remove(id);
    }
}
