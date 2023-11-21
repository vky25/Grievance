package org.upsmf.grievance.enums;

public enum TicketStatus {

    OPEN(1), CLOSED(2), INVALID(3);

    private int id;

    TicketStatus(int id) {
        this.id = id;
    }
}
