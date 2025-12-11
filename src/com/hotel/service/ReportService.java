package com.hotel.service;

import com.hotel.model.Client;

public interface ReportService {

    String generateOccupancyReport();


    String generateRevenueReport();


    String generateClientHistoryReport(Client client);
}