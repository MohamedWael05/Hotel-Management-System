import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceRequest {
    private String requestID;
    private String guestID;
    private String serviceType;
    private LocalDateTime requestDate;
    private String status;

    public ServiceRequest(String requestID, String guestID, String serviceType) {
        this.requestID = requestID;
        this.guestID = guestID;
        this.serviceType = serviceType;
        this.requestDate = LocalDateTime.now();
        this.status = "Pending";
    }

    public void addRequest() {
        System.out.println("Service request " + requestID + " added: " + serviceType);
    }

    public void completeRequest() {
        this.status = "Completed";
        System.out.println("Service request " + requestID + " completed.");
    }

    public void updateRequestStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Request " + requestID + " status updated to: " + newStatus);
    }


    public String getRequestID() { return requestID; }
    public String getGuestID() { return guestID; }
    public String getServiceType() { return serviceType; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public String getStatus() { return status; }

    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return requestID + " - " + serviceType + " | Guest: " + guestID +
                " | " + requestDate.format(formatter) + " | " + status;
    }
}