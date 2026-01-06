public class Room {
    private String roomID;
    private String roomType;
    private int capacity;
    private boolean isAvailable;
    private double pricePerNight;

    public Room(String roomID, String roomType, int capacity, double pricePerNight) {
        this.roomID = roomID;
        this.roomType = roomType;
        this.capacity = capacity;
        this.isAvailable = true;
        this.pricePerNight = pricePerNight;
    }

    public void assignRoom() {
        if (isAvailable) {
            isAvailable = false;
            System.out.println("Room " + roomID + " has been assigned.");
        } else {
            System.out.println("Room " + roomID + " is not available.");
        }
    }

    public void releaseRoom() {
        isAvailable = true;
        System.out.println("Room " + roomID + " is now available.");
    }

    public void updateRoomInfo(String roomType, int capacity, double pricePerNight) {
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }


    public String getRoomID() { return roomID; }
    public String getRoomType() { return roomType; }
    public int getCapacity() { return capacity; }
    public boolean isAvailable() { return isAvailable; }
    public double getPricePerNight() { return pricePerNight; }

    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    @Override
    public String toString() {
        return roomID + " - " + roomType + " ($" + pricePerNight + "/night)";
    }
}