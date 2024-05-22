//package com.advantal.model;
//
//
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import java.math.BigDecimal;
//
//public class UserTrade {
//
//    private final long id;
//    private final BigDecimal price;
//
//    // Other properties...
//
//    public UserTrade(
//            @JsonProperty("id") long id,
//            @JsonProperty("price") BigDecimal price
//            // Other properties...
//    ) {
//        this.id = id;
//        this.price = price;
//        // Initialize other properties...
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    // Other getter methods...
//
//    // Override toString() for easy debugging
//    @Override
//    public String toString() {
//        return "BinanceTrade{" +
//                "id=" + id +
//                ", price=" + price +
//                // Include other properties in the string representation...
//                '}';
//    }
//}
//
