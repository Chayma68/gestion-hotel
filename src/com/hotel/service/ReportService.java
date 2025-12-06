package com.hotel.service;

import com.hotel.model.Client;

/**
 * Defines methods for generating textual reports from the system data.
 * Reports cover hotel occupancy, revenue and client reservation
 * histories.  In a more elaborate implementation these methods could
 * return domain objects or streams but simple strings suffice here.
 */
public interface ReportService {
    /**
     * Builds a report summarising the current occupancy of the hotel.  The
     * report should include the number of rooms, number of booked
     * rooms and percentage occupancy.
     *
     * @return textual report
     */
    String generateOccupancyReport();

    /**
     * Builds a report summarising revenue generated through the
     * payment service.  The report includes the total revenue.
     *
     * @return textual revenue report
     */
    String generateRevenueReport();

    /**
     * Builds a report detailing all reservations a given client has
     * made.
     *
     * @param client client whose history should be included
     * @return textual history report
     */
    String generateClientHistoryReport(Client client);
}