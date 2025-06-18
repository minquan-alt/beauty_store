package com.beautystore.adeline.repository;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.beautystore.adeline.dto.response.InventoryReportResponse;
import com.beautystore.adeline.dto.response.InventoryReportResultResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import oracle.jdbc.OracleTypes;

@Repository
public class InventoryReportRepositoryImpl implements InventoryReportRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<InventoryReportResponse> inventoryReportRowMapper = (rs, rowNum) -> {
        return new InventoryReportResponse(
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getLong("total_quantity"),
            rs.getBigDecimal("total_amount")
        );
    };

    @Override
    public InventoryReportResultResponse generateInventoryReport(LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.execute((Connection connection) -> {
            String sql = "{call BEAUTY_STORE.generate_inventory_report(?, ?, ?, ?, ?, ?)}";
            
            try (CallableStatement cs = connection.prepareCall(sql)) {
                // Set input parameters
                cs.setDate(1, Date.valueOf(startDate));
                cs.setDate(2, Date.valueOf(endDate));
                
                // Register output parameters (cursors)
                cs.registerOutParameter(3, OracleTypes.CURSOR); // cur_opening
                cs.registerOutParameter(4, OracleTypes.CURSOR); // cur_in
                cs.registerOutParameter(5, OracleTypes.CURSOR); // cur_out
                cs.registerOutParameter(6, OracleTypes.CURSOR); // cur_closing
                
                // Execute the procedure
                cs.execute();
                
                // Process results
                List<InventoryReportResponse> openingInventory = processCursor((ResultSet) cs.getObject(3));
                List<InventoryReportResponse> incomingInventory = processCursor((ResultSet) cs.getObject(4));
                List<InventoryReportResponse> outgoingInventory = processCursor((ResultSet) cs.getObject(5));
                List<InventoryReportResponse> closingInventory = processCursor((ResultSet) cs.getObject(6));
                
                return new InventoryReportResultResponse(
                    openingInventory,
                    incomingInventory,
                    outgoingInventory,
                    closingInventory
                );
            }
        });
    }

    private List<InventoryReportResponse> processCursor(ResultSet rs) throws SQLException {
        List<InventoryReportResponse> result = new ArrayList<>();
        
        if (rs != null) {
            while (rs.next()) {
                InventoryReportResponse dto = new InventoryReportResponse(
                    rs.getLong("product_id"),
                    rs.getString("name"),
                    rs.getLong("total_quantity"),
                    rs.getBigDecimal("total_amount")
                );
                result.add(dto);
            }
            rs.close();
        }
        
        return result;
    }
}