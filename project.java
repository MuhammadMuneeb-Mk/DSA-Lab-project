import java.util.NoSuchElementException;
import java.util.Scanner;

class POSSystem {
    private MyArrayList<Product> inventory = new MyArrayList<>();
    private MyQueue<Customer> checkoutQueue = new MyQueue<>();
    private MyLinkedList<Transaction> transactionLog = new MyLinkedList<>();

    public static void main(String[] args) {
        POSSystem pos = new POSSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- POS System Dashboard ---");
            System.out.println("1. Add Item");
            System.out.println("2. Update Inventory");
            System.out.println("3. Cart Option");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    pos.addItem(scanner);
                    break;
                case 2:
                    pos.updateInventory(scanner);
                    break;
                case 3:
                    pos.manageCart(scanner);
                    break;
                case 4:
                    System.out.println("Exiting POS System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Add a new item to the inventory
    private void addItem(Scanner scanner) {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter product stock: ");
        int stock = scanner.nextInt();

        int id = inventory.size() + 1; // Generate a unique ID
        inventory.add(new Product(id, name, price, stock));
        System.out.println("Product added successfully.");
    }

    // Update inventory stock for a specific product
    private void updateInventory(Scanner scanner) {
        if (inventory.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }

        System.out.println("\n--- Inventory ---");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i + 1) + ". " + inventory.get(i));
        }

        System.out.print("Select a product to update (enter index): ");
        int index = scanner.nextInt() - 1;
        if (index < 0 || index >= inventory.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.print("Enter new stock quantity: ");
        int newStock = scanner.nextInt();
        inventory.get(index).quantity += newStock;
        System.out.println("Stock updated successfully.");
    }

    // Manage cart and checkout process
    private void manageCart(Scanner scanner) {
        if (inventory.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }

        MyArrayList<CartItem> cart = new MyArrayList<>();
        while (true) {
            System.out.println("\n--- Products ---");
            for (int i = 0; i < inventory.size(); i++) {
                System.out.println((i + 1) + ". " + inventory.get(i));
            }
            System.out.println("0. Exit Cart");

            System.out.print("Select a product to add to cart (enter index): ");
            int index = scanner.nextInt() - 1;
            if (index == -1) {
                break;
            }
            if (index < 0 || index >= inventory.size()) {
                System.out.println("Invalid selection.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            Product product = inventory.get(index);
            if (quantity > product.quantity) {
                System.out.println("Insufficient stock available.");
            } else {
                cart.add(new CartItem(product, quantity));
                System.out.println("Added to cart: " + product.name + " x" + quantity);
            }
        }

        if (!cart.isEmpty()) {
            System.out.print("Enter customer name: ");
            scanner.nextLine(); // Consume newline
            String customerName = scanner.nextLine();

            double total = 0;
            for (int i = 0; i < cart.size(); i++) {
                CartItem item = cart.get(i);
                Product product = item.product;
                product.quantity -= item.quantity;
                total += product.price * item.quantity;
            }

            checkoutQueue.enqueue(new Customer(customerName, cart));
            transactionLog.add(new Transaction(customerName, total));

            System.out.println("\n--- Cart Summary ---");
            System.out.println("Customer: " + customerName);
            System.out.println("Total: PKR " + total);
        } else {
            System.out.println("Cart is empty.");
        }
    }

    // Product class
    static class Product {
        int id;
        String name;
        double price;
        int quantity;

        public Product(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return id + ": " + name + " - PKR " + price + " (Stock: " + quantity + ")";
        }
    }

    // Customer class
    static class Customer {
        String name;
        MyArrayList<CartItem> cart;

        public Customer(String name, MyArrayList<CartItem> cart) {
            this.name = name;
            this.cart = cart;
        }
    }

    // Transaction class
    static class Transaction {
        String customerName;
        double total;

        public Transaction(String customerName, double total) {
            this.customerName = customerName;
            this.total = total;
        }

        @Override
        public String toString() {
            return "Customer: " + customerName + ", Total: PKR " + total;
        }
    }

    // CartItem class
    static class CartItem {
        Product product;
        int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    // Custom ArrayList implementation
    static class MyArrayList<T> {
        private Object[] elements;
        private int size;

        public MyArrayList() {
            elements = new Object[10];
            size = 0;
        }

        public void add(T element) {
            if (size == elements.length) {
                resize();
            }
            elements[size++] = element;
        }

        @SuppressWarnings("unchecked")
        public T get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            return (T) elements[index];
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void resize() {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    // Custom Queue implementation
    static class MyQueue<T> {
        private MyLinkedList<T> list = new MyLinkedList<>();

        public void enqueue(T element) {
            list.add(element);
        }

        public T dequeue() {
            if (list.isEmpty()) {
                throw new NoSuchElementException("Queue is empty");
            }
            return list.removeFirst();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    // Custom LinkedList implementation
    static class MyLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size;

        private static class Node<T> {
            T data;
            Node<T> next;

            public Node(T data) {
                this.data = data;
            }
        }

        public void add(T element) {
            Node<T> newNode = new Node<>(element);
            if (tail == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
            size++;
        }

        public T removeFirst() {
            if (head == null) {
                throw new NoSuchElementException("List is empty");
            }
            T data = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return data;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }
}