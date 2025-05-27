package com.beautystore.adeline.repository;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import oracle.jdbc.OracleConnection;
import org.springframework.stereotype.Repository;


@Repository
public class PurchaseOrderProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

     @Transactional
    public void processPurchaseOrder(
            Long supplierId,
            List<Long> productIds,
            List<Integer> quantities,
            List<BigDecimal> unitPrices
    ) {
        try {
            OracleConnection conn = entityManager.unwrap(OracleConnection.class);

            Struct[] itemStructs = new Struct[productIds.size()];

            for (int i = 0; i < productIds.size(); i++) {
                Object[] attributes = new Object[]{
                        productIds.get(i),
                        quantities.get(i),
                        unitPrices.get(i)
                };
                itemStructs[i] = conn.createStruct("PURCHASE_ITEM_TYPE", attributes);
            }

            Array itemArray = conn.createOracleArray("PURCHASE_ITEM_TABLE_TYPE", itemStructs);

            var call = conn.prepareCall("{ call PROCESS_PURCHASE_ORDER(?, ?) }");
            call.setLong(1, supplierId);
            call.setArray(2, itemArray);

            call.execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to call PROCESS_PURCHASE_ORDER", e);
        }
    }

}
