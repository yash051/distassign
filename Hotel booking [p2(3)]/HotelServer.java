import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

// Define the remote interface for the hotel server
interface HotelServerInterface extends Remote {
    boolean bookRoom(String guestName, int roomNumber) throws RemoteException;
    String cancelBooking(String guestName) throws RemoteException;
    boolean cancelBooking(String guestName, int roomNumber) throws RemoteException;
}

// Implement the remote interface
public class HotelServer extends UnicastRemoteObject implements HotelServerInterface {
    private static final int TOTAL_ROOMS = 10;
    private Map<Integer, String> roomBookingMap;

    public HotelServer() throws RemoteException {
        super();
        roomBookingMap = new HashMap<>();
    }

    @Override
    public synchronized boolean bookRoom(String guestName, int roomNumber) throws RemoteException {
        if (roomNumber < 1 || roomNumber > TOTAL_ROOMS) {
            return false; // Invalid room number
        }

        if (roomBookingMap.containsKey(roomNumber)) {
            return false; // Room is already booked
        }

        roomBookingMap.put(roomNumber, guestName);
        return true;
    }

    @Override
    public synchronized String cancelBooking(String guestName) throws RemoteException {
        for (Map.Entry<Integer, String> entry : roomBookingMap.entrySet()) {
            if (entry.getValue().equals(guestName)) {
                return "You had booked room number " + entry.getKey() + ". Would you like to cancel? (yes/no)";
            }
        }
        return "No booking found for the guest: " + guestName;
    }

    @Override
    public synchronized boolean cancelBooking(String guestName, int roomNumber) throws RemoteException {
        if (roomNumber < 1 || roomNumber > TOTAL_ROOMS) {
            return false; // Invalid room number
        }

        if (!roomBookingMap.containsKey(roomNumber)) {
            return false; // Room is not booked
        }

        String bookedGuest = roomBookingMap.get(roomNumber);
        if (bookedGuest.equals(guestName)) {
            roomBookingMap.remove(roomNumber);
            return true;
        } else {
            return false; // The room is not booked by the specified guest
        }
    }

    public static void main(String[] args) {
        try {
            HotelServer server = new HotelServer();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("rmi://localhost/HotelServer", server);
            System.out.println("HotelServer is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
