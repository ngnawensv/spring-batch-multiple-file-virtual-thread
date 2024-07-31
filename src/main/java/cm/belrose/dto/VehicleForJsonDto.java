package cm.belrose.dto;

public record VehicleForJsonDto(String referenceNumber,
                                String brand,
                                String model,
                                String type,
                                String customerFullName,
                                Double price) {}
