import java.util.*;
import java.time.LocalDate;

public class ReportGenerator {
    private String reportType;
    private List<?> data;

    public ReportGenerator(String reportType) {
        this.reportType = reportType;
        this.data = new ArrayList<>();
    }

    public String generateReservationReport(List<Reservation> reservations) {
        this.data = new ArrayList<>(reservations);

        List<Reservation> sortedReservations = mergeSort(reservations);

        StringBuilder report = new StringBuilder();
        report.append("=== RESERVATION REPORT ===\n");
        report.append("Total Reservations: ").append(sortedReservations.size()).append("\n\n");

        int active = 0, completed = 0, cancelled = 0;

        for (Reservation res : sortedReservations) {
            report.append(res.toString()).append("\n");

            switch (res.getStatus()) {
                case "Active": active++; break;
                case "Completed": completed++; break;
                case "Cancelled": cancelled++; break;
            }
        }

        report.append("\nStatus Summary:\n");
        report.append("Active: ").append(active).append("\n");
        report.append("Completed: ").append(completed).append("\n");
        report.append("Cancelled: ").append(cancelled).append("\n");

        return report.toString();
    }

    public String generateRevenueReport(List<Billing> billings) {
        List<Billing> sortedBillings = quickSort(new ArrayList<>(billings), 0, billings.size() - 1);

        StringBuilder report = new StringBuilder();
        report.append("=== REVENUE REPORT ===\n");

        double totalRevenue = 0;
        double paidRevenue = 0;
        double pendingRevenue = 0;

        for (Billing bill : sortedBillings) {
            totalRevenue += bill.getTotalAmount();
            if (bill.isPaid()) {
                paidRevenue += bill.getTotalAmount();
            } else {
                pendingRevenue += bill.getRemainingBalance();
            }
            report.append(bill.toString()).append("\n");
        }

        report.append("\nRevenue Summary:\n");
        report.append(String.format("Total Revenue: $%.2f\n", totalRevenue));
        report.append(String.format("Paid Revenue: $%.2f\n", paidRevenue));
        report.append(String.format("Pending Revenue: $%.2f\n", pendingRevenue));

        return report.toString();
    }

    public String generateOccupancyReport(List<Room> rooms) {
        StringBuilder report = new StringBuilder();
        report.append("=== OCCUPANCY REPORT ===\n");
        report.append("Total Rooms: ").append(rooms.size()).append("\n");

        int occupied = 0;
        int available = 0;

        for (Room room : rooms) {
            if (room.isAvailable()) {
                available++;
            } else {
                occupied++;
            }
        }

        double occupancyRate = (rooms.size() > 0) ? (occupied * 100.0 / rooms.size()) : 0;

        report.append("Occupied Rooms: ").append(occupied).append("\n");
        report.append("Available Rooms: ").append(available).append("\n");
        report.append(String.format("Occupancy Rate: %.2f%%\n", occupancyRate));

        return report.toString();
    }

    private List<Reservation> mergeSort(List<Reservation> list) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        List<Reservation> left = mergeSort(new ArrayList<>(list.subList(0, mid)));
        List<Reservation> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())));

        return merge(left, right);
    }

    private List<Reservation> merge(List<Reservation> left, List<Reservation> right) {
        List<Reservation> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getCheckInDate().isBefore(right.get(j).getCheckInDate())) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }

        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }

    private List<Billing> quickSort(List<Billing> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
        return list;
    }

    private int partition(List<Billing> list, int low, int high) {
        double pivot = list.get(high).getTotalAmount();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).getTotalAmount() <= pivot) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}