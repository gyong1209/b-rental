package BikeRental;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Long voucherId;
    private Long bikeId;
    private String status;

    @PostPersist
    public void onPostPersist(){
        Rented rented = new Rented();
        BeanUtils.copyProperties(this, rented);
        rented.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // Bike Aggregate
        BikeRental.external.Bike bike = new BikeRental.external.Bike();
        bike.setUserId(this.getUserId());
        bike.setBikeId(this.getBikeId());
        bike.setStatus(this.getStatus());

        // mappings goes here
        RentalApplication.applicationContextBike.getBean(BikeRental.external.BikeService.class)
            .rent(bike);

        // Voucher Aggregate
        BikeRental.external.Voucher voucher = new BikeRental.external.Voucher();
        voucher.setUserId(this.getUserId());

        RentalApplication.applicationContextBike.getBean(BikeRental.external.VoucherService.class)
                .rent(voucher);

    }

    @PreUpdate
    public void onPreUpdate(){
        RentalCancelled rentalCancelled = new RentalCancelled();
        BeanUtils.copyProperties(this, rentalCancelled);
        rentalCancelled.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }
    public Long getBikeId() {
        return bikeId;
    }

    public void setBikeId(Long bikeId) {
        this.bikeId = bikeId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
