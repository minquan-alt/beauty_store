package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        Pending,
        Completed,
        Cancelled
    }

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderDetail> orderDetails = new ArrayList<>();
}
