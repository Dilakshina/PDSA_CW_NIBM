package busbookingtool;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

class Bus {
    String busNo;
    String destination;
    String from;
    int noOfSeats;
    boolean available;
    int availableSeats;
    double ticketPrice;
    String customerId;
    LocalDate bookingDate;
    LocalTime time;
    boolean[] seats;  // Array to keep track of seat bookings

    public Bus(String busNo, String destination, String from, int noOfSeats, double ticketPrice) {
        this.busNo = busNo;
        this.destination = destination;
        this.from = from;
        this.noOfSeats = noOfSeats;
        this.ticketPrice = ticketPrice;
        this.available = true;
        this.availableSeats = noOfSeats;
        this.seats = new boolean[noOfSeats];  // Initialize all seats to false (unbooked)
    }
}

class Customer {
    String customerId;
    String cusName;
    String cusEmail;
    String telephone;

    public Customer(String customerId, String cusName, String cusEmail, String telephone) {
        this.customerId = customerId;
        this.cusName = cusName;
        this.cusEmail = cusEmail;
        this.telephone = telephone;
    }
}

class CustomerNode {
    Customer cus;
    CustomerNode next;

    public CustomerNode(Customer cus) {
        this.cus = cus;
        this.next = null;
    }
}

class BusNode {
    Bus bus;
    BusNode prev;
    BusNode next;

    public BusNode(Bus bus) {
        this.bus = bus;
        this.prev = null;
        this.next = null;
    }
}

class BookingManagement {
    BusNode head = null;
    BusNode back = null;
    CustomerNode cusHead = null;
    int totalBus = 0;
    int totalCustomers = 0;
    Scanner sc = new Scanner(System.in);

    public BookingManagement() {
        menu();
    }

    public boolean isBusExisist(String busNo) {
        BusNode current = head;
        while (current != null) {
            if (current.bus.busNo.equals(busNo)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean isCustomerExsist(String customerId) {
        CustomerNode current = cusHead;
        while (current != null) {
            if (current.cus.customerId.equals(customerId)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void addBus(String busNo, String destination, String from, int noOfSeats, double ticketPrice) {
        Bus newBus = new Bus(busNo, destination, from, noOfSeats, ticketPrice);
        BusNode newBusNode = new BusNode(newBus);

        if (head == null) {
            head = newBusNode;
            back = newBusNode;
        } else {
            back.next = newBusNode;
            newBusNode.prev = back;
            back = newBusNode;
        }
        totalBus++;
    }

    public void deleteBus(String busNo) {
        BusNode current = head;

        while (current != null) {
            if (current.bus.busNo.equals(busNo)) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }

                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    back = current.prev;
                }
                totalBus--;
                return;
            }
            current = current.next;
        }
    }

    public void editBus(String busNo, String destination, String from, int noOfSeats, double ticketPrice) {
        BusNode current = head;
        while (current != null) {
            if (current.bus.busNo.equals(busNo)) {
                current.bus.destination = destination;
                current.bus.from = from;
                current.bus.noOfSeats = noOfSeats;
                current.bus.ticketPrice = ticketPrice;
                System.out.println("Bus Edited Successfully!");
                System.out.println("------------------------------");
                break;
            }
            current = current.next;
        }
    }

    public void addCustomer(String cusId, String cusName, String cusEmail, String cusTp) {
        if (isCustomerExsist(cusId)) {
            System.err.println("This ID: " + cusId + " already exists.");
            return;
        }

        Customer newCus = new Customer(cusId, cusName, cusEmail, cusTp);
        CustomerNode cusNode = new CustomerNode(newCus);
        if (cusHead == null) {
            cusHead = cusNode;
        } else {
            CustomerNode current = cusHead;
            while (current.next != null) {
                current = current.next;
            }
            current.next = cusNode;
        }
        totalCustomers++;
    }

    public void bookSeats(String customerId, String busNo, int numSeats, LocalDate bookingDate) {
    if (!isCustomerExsist(customerId)) {
        System.err.println("Customer ID not found.");
        return;
    }

    BusNode currentBus = head;
    boolean busFound = false;
    while (currentBus != null) {
        if (currentBus.bus.busNo.equals(busNo)) {
            busFound = true;
            if (!currentBus.bus.available) {
                System.err.println("Bus is not available.");
                return;
            }

            int bookedSeats = 0;
            for (int i = 0; i < currentBus.bus.seats.length; i++) {
                if (!currentBus.bus.seats[i] && bookedSeats < numSeats) {
                    currentBus.bus.seats[i] = true;
                    bookedSeats++;
                }
                if (bookedSeats == numSeats) {
                    break;
                }
            }

            if (bookedSeats < numSeats) {
                System.err.println("Not enough seats available.");
                // Rollback the booking if not enough seats are found
                for (int i = 0; i < bookedSeats; i++) {
                    currentBus.bus.seats[i] = false;
                }
                return;
            }

            currentBus.bus.availableSeats -= bookedSeats;
            currentBus.bus.bookingDate = bookingDate;
            currentBus.bus.customerId = customerId;

            if (currentBus.bus.availableSeats == 0) {
                currentBus.bus.available = false;
            }

            System.out.println("Successfully booked " + numSeats + " seats on Bus " + busNo + " for customer " + customerId + " on " + bookingDate);
            break;
        }
        currentBus = currentBus.next;
    }

    if (!busFound) {
        System.err.println("Bus not found.");
    }
}


    public String getCusName(String customerId) {
        CustomerNode current = cusHead;
        while (current != null) {
            if (current.cus.customerId.equals(customerId)) {
                return current.cus.cusName;
            }
            current = current.next;
        }
        return "No Name Found";
    }

    public String getCusPhone(String customerId) {
        CustomerNode current = cusHead;
        while (current != null) {
            if (current.cus.customerId.equals(customerId)) {
                return current.cus.telephone;
            }
            current = current.next;
        }
        return "No Phone number Found";
    }

    public void showAvailableBuses() {
        BusNode current = head;
        boolean availables = false;
        while (current != null) {
            if (current.bus.available) {
                availables = true;
                System.out.println("Bus Number: " + current.bus.busNo);
                System.out.println("Destination: " + current.bus.destination + " - " + current.bus.from);
                System.out.println("Available Seats: " + current.bus.availableSeats);
                System.out.println("---------------------");
            }
            current = current.next;
        }
        if (!availables) {
            System.err.println("All buses are fully booked.");
        }
    }

    public void showBookedBuses() {
        System.out.println("Booking details: \n");
        BusNode current = head;
        while (current != null) {
            if (!current.bus.available || current.bus.availableSeats < current.bus.noOfSeats) {
                System.out.println("Bus Number: " + current.bus.busNo);
                System.out.println("Destination: " + current.bus.destination);
                System.out.println("Start: " + current.bus.from);
                System.out.println("Customer Name: " + getCusName(current.bus.customerId));
                System.out.println("Customer Phone: " + getCusPhone(current.bus.customerId));
                System.out.println("Booking Date: " + current.bus.bookingDate);
                System.out.println("Available Seats: " + current.bus.availableSeats);
                System.out.println("---------------------");
            }
            current = current.next;
        }
    }

    public void showCustomers() {
        System.out.println("All Customers:\n");
        CustomerNode current = cusHead;
        while (current != null) {
            System.out.println("Customer ID: " + current.cus.customerId);
            System.out.println("Name: " + current.cus.cusName);
            System.out.println("Email: " + current.cus.cusEmail);
            System.out.println("Phone: " + current.cus.telephone);
            System.out.println("---------------------");
            current = current.next;
        }
        if (totalCustomers == 0) {
            System.err.println("No customers available.");
        }
    }

    public void checkBusAvailability(String busNo, LocalDate date) {
        BusNode current = head;
        boolean busFound = false;
        while (current != null) {
            if (current.bus.busNo.equals(busNo)) {
                busFound = true;
                if (current.bus.bookingDate != null && current.bus.bookingDate.equals(date) && current.bus.availableSeats > 0) {
                    System.out.println("Bus Number: " + current.bus.busNo);
                    System.out.println("Destination: " + current.bus.destination);
                    System.out.println("Start: " + current.bus.from);
                    System.out.println("Available Seats: " + current.bus.availableSeats);
                    System.out.println("Booking Date: " + current.bus.bookingDate);
                    System.out.println("---------------------");
                } else if (current.bus.bookingDate == null || !current.bus.bookingDate.equals(date)) {
                    System.out.println("Bus Number: " + current.bus.busNo);
                    System.out.println("Destination: " + current.bus.destination);
                    System.out.println("Start: " + current.bus.from);
                    System.out.println("All seats are available for the date: " + date);
                    System.out.println("---------------------");
                } else {
                    System.err.println("No available seats on Bus " + busNo + " for the date " + date);
                }
                break;
            }
            current = current.next;
        }
        if (!busFound) {
            System.err.println("Bus not found.");
        }
    }

    public void getChoices() {
        System.out.println("1. Add Bus");
        System.out.println("2. Delete Bus");
        System.out.println("3. Edit Bus");
        System.out.println("4. Add Customer");
        System.out.println("5. Book Seat");
        System.out.println("6. View All Customers");
        System.out.println("7. View Booked Buses");
        System.out.println("8. View Available Buses");
        System.out.println("9. Check Bus Availability by Date");  // New option added
        System.out.println("0. Exit");
        System.out.println("---------------------");
        System.out.println("Enter your choice: ");
    }

    public void menu() {
        while (true) {
            getChoices();
            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline character

            switch (choice) {
                case 1:
                    System.out.println("Enter Bus Number: ");
                    String busNo = sc.nextLine();
                    if (isBusExisist(busNo)) {
                        System.err.println("Bus number already exists. Try again.");
                        break;
                    }
                    System.out.println("Enter Bus Destination: ");
                    String destination = sc.nextLine();
                    System.out.println("Enter Bus Start: ");
                    String from = sc.nextLine();
                    System.out.println("Enter No. of Seats: ");
                    int noOfSeats = sc.nextInt();
                    System.out.println("Enter Ticket Price: ");
                    double ticketPrice = sc.nextDouble();
                    addBus(busNo, destination, from, noOfSeats, ticketPrice);
                    System.out.println("Bus Added Successfully!");
                    System.out.println("------------------------------");
                    break;

                case 2:
                    System.out.println("Enter Bus Number to delete: ");
                    String delBusNo = sc.nextLine();
                    deleteBus(delBusNo);
                    System.out.println("Bus Deleted Successfully!");
                    System.out.println("------------------------------");
                    break;

                case 3:
                    System.out.println("Enter Bus Number to edit: ");
                    String editBusNo = sc.nextLine();
                    System.out.println("Enter new Bus Destination: ");
                    String editDestination = sc.nextLine();
                    System.out.println("Enter new Bus Start: ");
                    String editFrom = sc.nextLine();
                    System.out.println("Enter new No. of Seats: ");
                    int editNoOfSeats = sc.nextInt();
                    System.out.println("Enter new Ticket Price: ");
                    double editTicketPrice = sc.nextDouble();
                    editBus(editBusNo, editDestination, editFrom, editNoOfSeats, editTicketPrice);
                    break;

                case 4:
                    System.out.println("Enter Customer ID: ");
                    String cusId = sc.nextLine();
                    System.out.println("Enter Customer Name: ");
                    String cusName = sc.nextLine();
                    System.out.println("Enter Customer Email: ");
                    String cusEmail = sc.nextLine();
                    System.out.println("Enter Customer Phone: ");
                    String cusTp = sc.nextLine();
                    addCustomer(cusId, cusName, cusEmail, cusTp);
                    System.out.println("Customer Added Successfully!");
                    System.out.println("------------------------------");
                    break;

               case 5:
                    System.out.println("Enter Customer ID: ");
                    String bookCusId = sc.nextLine();
                    System.out.println("Enter Bus Number: ");
                    String bookBusNo = sc.nextLine();
                    System.out.println("Enter Number of Seats: ");
                    int numSeats = sc.nextInt();
                    System.out.println("Enter Booking Date (YYYY-MM-DD): ");
                    LocalDate bookingDate = LocalDate.parse(sc.next());
                    bookSeats(bookCusId, bookBusNo, numSeats, bookingDate);
                    break;


                case 6:
                    showCustomers();
                    break;

                case 7:
                    showBookedBuses();
                    break;

                case 8:
                    showAvailableBuses();
                    break;

                case 9:
                    System.out.println("Enter Bus Number: ");
                    String checkBusNo = sc.nextLine();
                    System.out.println("Enter Date (YYYY-MM-DD): ");
                    LocalDate checkDate = LocalDate.parse(sc.next());
                    checkBusAvailability(checkBusNo, checkDate);
                    break;

                case 0:
                    System.out.println("Thank you for using the Bus Seat Booking System. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.err.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}

public class BusBookingTool {
    public static void main(String[] args) {
        new BookingManagement();
    }
}