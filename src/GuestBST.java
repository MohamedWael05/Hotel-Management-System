
import java.util.*;

public class GuestBST {
    private class Node {
        Guest guest;
        Node left, right;

        Node(Guest guest) {
            this.guest = guest;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public GuestBST() {
        this.root = null;
    }

    public void insert(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Cannot insert null guest");
        }
        root = insertRec(root, guest);
        System.out.println("Inserted guest: " + guest.getName() + " (ID: " + guest.getGuestID() + ")");
    }

    private Node insertRec(Node root, Guest guest) {
        if (root == null) {
            return new Node(guest);
        }

        if (guest.compareTo(root.guest) < 0) {
            root.left = insertRec(root.left, guest);
        } else if (guest.compareTo(root.guest) > 0) {
            root.right = insertRec(root.right, guest);
        }

        return root;
    }

    public Guest search(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Guest result = searchRec(root, name);
        System.out.println("Search for '" + name + "': " + (result != null ? "Found" : "Not found"));
        return result;
    }

    private Guest searchRec(Node root, String name) {
        if (root == null) {
            return null;
        }

        int cmp = name.compareToIgnoreCase(root.guest.getName());

        if (cmp == 0) {
            return root.guest;
        } else if (cmp < 0) {
            return searchRec(root.left, name);
        } else {
            return searchRec(root.right, name);
        }
    }

    public Guest searchByID(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }
        Guest result = searchByIDRec(root, guestID);
        System.out.println("Search by ID '" + guestID + "': " + (result != null ? "Found" : "Not found"));
        return result;
    }

    private Guest searchByIDRec(Node root, String guestID) {
        if (root == null) {
            return null;
        }

        if (root.guest.getGuestID().equals(guestID)) {
            return root.guest;
        }

        Guest leftResult = searchByIDRec(root.left, guestID);
        if (leftResult != null) {
            return leftResult;
        }

        return searchByIDRec(root.right, guestID);
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        inorderTraversal(root, guests);
        System.out.println("Total guests in BST: " + guests.size());
        return guests;
    }

    private void inorderTraversal(Node root, List<Guest> guests) {
        if (root != null) {
            inorderTraversal(root.left, guests);
            guests.add(root.guest);
            inorderTraversal(root.right, guests);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getHeight() {
        return getHeightRec(root);
    }

    private int getHeightRec(Node root) {
        if (root == null) {
            return 0;
        }
        int leftHeight = getHeightRec(root.left);
        int rightHeight = getHeightRec(root.right);
        return Math.max(leftHeight, rightHeight) + 1;
    }

    public void printTree() {
        System.out.println("=== BST Structure ===");
        printTreeRec(root, 0);
        System.out.println("====================");
    }

    private void printTreeRec(Node root, int level) {
        if (root != null) {
            printTreeRec(root.right, level + 1);
            System.out.println(" ".repeat(level * 4) + root.guest.getName() + " (" + root.guest.getGuestID() + ")");
            printTreeRec(root.left, level + 1);
        }
    }
}