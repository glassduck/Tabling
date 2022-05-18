package tabling.domain;

import tabling.domain.OrderCanceled;
import tabling.domain.OrderCompleted;
import tabling.ManagementApplication;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date; 


@Entity
@Table(name="OrderMng_table")
public class OrderMng  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    private Long id;
    
    
    private String storeName;
    
    
    private Long customerId;
    
    
    private Long customerNum;
    
    
    private Date updatedAt;

    @PostPersist
    public void onPostPersist(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // tabling.external.Seat seat = new tabling.external.Seat();
        // mappings goes here
        // ManagementApplication.applicationContext.getBean(tabling.external.SeatService.class)
            // .checkSeat(seat);

        OrderCompleted orderCompleted = new OrderCompleted();
        BeanUtils.copyProperties(this, orderCompleted);
        orderCompleted.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // tabling.external.Seat seat = new tabling.external.Seat();
        // mappings goes here
        // ManagementApplication.applicationContext.getBean(tabling.external.SeatService.class)
            // .checkSeat(seat);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(Long customerNum) {
        this.customerNum = customerNum;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }



    public static OrderMngRepository repository(){
        OrderMngRepository orderMngRepository = ManagementApplication.applicationContext.getBean(OrderMngRepository.class);
        return orderMngRepository;
    }


    public static void cancelRecept(ReceptCanceled receptCanceled){

        OrderMng orderMng = new OrderMng();
        /*
        LOGIC GOES HERE
        */
        repository().save(orderMng);


    }

        public static void sendRecept(ReceptAdded receptAdded){

        OrderMng orderMng = new OrderMng();
        /*
        LOGIC GOES HERE
        */
        repository().save(orderMng);


    }


}
