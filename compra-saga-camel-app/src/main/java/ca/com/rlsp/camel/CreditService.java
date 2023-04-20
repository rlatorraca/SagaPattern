package ca.com.rlsp.camel;

import org.apache.camel.Header;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreditService {
    
    private int totalCredit;
    private Map<Long, Integer> orderValue = new HashMap<>(); // <Order, Value>d

    public CreditService(){
        this.totalCredit = 100;
    }

    public void doCredit(@Header("orderId") Long orderId, @Header("value") int value){
        if (value > totalCredit){
            throw new IllegalStateException("Balance doesn`t have enough credit");
        }

        totalCredit = totalCredit - value;
        orderValue.put(orderId, value);
    }

    public void undoCredit(@Header("id")  Long id){
        //totalCredit = totalCredit + orderValue.get(id);
        //orderValue.remove(id);
        System.out.println("[RLSP] Order failed. Cancelling order");
    }

    public int getTotalCredit(){
        return totalCredit;
    }
}
