package model;
public class Room {
    private String roomId;
    private int capacity;
    private boolean isBackup;
    public Room(String roomId, int capacity, boolean isBackup) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.isBackup = isBackup;
    }
    public String getRoomId() { return roomId; }
    public int getCapacity() { return capacity; }
    public boolean isBackup() { return isBackup; }
    @Override public String toString() { return roomId+" ("+capacity+") backup="+isBackup; }
}
