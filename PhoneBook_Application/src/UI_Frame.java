import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class UI_Frame extends JFrame implements Runnable{
    private final ArrayList<Person> contacts;
    {
        contacts = new ArrayList<>();
        btnClicked = -1;
        load = false;
        endPoint = 1;
        updateIndex = -1;
        loadContacts();
    }
    UI_Frame() {
        ImageIcon icon = new ImageIcon("icons/logo.png");
        this.setIconImage(icon.getImage());
        this.setTitle("Phonebook");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(670, 420);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        cnt = this.getContentPane();
        cnt.setLayout(null);
        cnt.setBackground(new Color(255, 255, 255));

        addCreditLabel();
    }

    class ActionListenerManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == newContact) {
                load = true;
                btnClicked = 1;
                display.setText("");
                addContact();
            } else if (e.getSource() == showContact) {
                load = true;
                btnClicked = 2;
                display.setText("");
                if (contacts.isEmpty()) {
                    display.append("Phonebook is empty!\n");
                } else {
                    int i = 1;
                    for (Person contact : contacts) {
                        display.append("Contact " + (i++) + ",\n");
                        display.append("Name     : " + contact.name + "\n");
                        display.append("Phone No.: " + contact.phoneNo + "\n");
                        display.append("Address  : " + contact.address + "\n\n");
                    }
                }
            } else if (e.getSource() == clearDisplay) {
                load = true;
                btnClicked = 3;
                display.setText("");
            } else if (e.getSource() == exitFrame) {
                System.exit(0);
            } else if (e.getSource() == searchButton){
                load = true;
                btnClicked = -1;
                String searchedName = searchField.getText();
                display.setText("");
                int matched = 0;
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).name.equalsIgnoreCase(searchedName)) {
                        matched = 1;
                        display.append("Name     : " + contacts.get(i).name + "\n");
                        display.append("Phone No.: " + contacts.get(i).phoneNo + "\n");
                        display.append("Address  : " + contacts.get(i).address + "\n\n");
                        updateIndex = i;
                        break;
                    }
                }
                if (matched == 0) {
                    display.append("Contact not found!\n");
                } else menuAfterSearch();
            } else if (e.getSource() == save) {
                if (save.getText().equals("UPDATE")) {
                    saveAt(updateIndex);
                } else saveAt(contacts.size());
            } else if (e.getSource() == backBtn) {
                state.setText("");
                display.setText("");
                loadMenu();
                btnClicked = -1;
            } else if (e.getSource() == updateButton) {
                load = true;
                btnClicked = 1;
                display.setText("");
                addContact();
                state.setText("UPDATE ANY DATA");
                name.setText(contacts.get(updateIndex).name);
                phone.setText(contacts.get(updateIndex).phoneNo);
                address.setText(contacts.get(updateIndex).address);
                save.setText("UPDATE");
            } else if (e.getSource() == remove) {
                load = true;
                contacts.remove(updateIndex);
                display.setText("Removed Contact!");
                updateButton.setEnabled(false);
                remove.setEnabled(false);
                updateButton.setBackground(new Color(172, 172, 172));
                remove.setBackground(new Color(172, 172, 172));
                updateContacts();
            } else if (e.getSource() == back) {
                display.setText("");
                loadMenu();
                btnClicked = -1;
            }
        }
    }

    public void addCreditLabel() {
        JLabel creditLabel = new JLabel();
        creditLabel.setText((char) 169 + " Md. Rakibul Islam Sabid - [011 231 0179]");
        creditLabel.setLocation(20, 350);
        creditLabel.setSize(400, 30);
        creditLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        cnt.add(creditLabel);
    }
    public void launch() {
        topPanel = new JPanel();
        topPanel.setLocation(50, 20);
        topPanel.setSize(560, 50);
        topPanel.setBackground(new Color(255, 255, 255));
        topPanel.setLayout(null);
        cnt.add(topPanel);

        menuPanel = new JPanel();
        menuPanel.setLocation(50, 80);
        menuPanel.setSize(240, 220);
        menuPanel.setBackground(new Color(255, 255, 255));
        cnt.add(menuPanel);

        display = new JTextArea();
        display.setText("");
        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        display.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(display);
        scrollPane.setLocation(330, 80);
        scrollPane.setSize(280, 222);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cnt.add(scrollPane);

        loadMenu();
        th.start();
    }
    public void loadMenu() {
        topPanel.removeAll();
        topPanel.repaint();

        searchField = new JTextField();
        searchField.setLocation(0, 5);
        searchField.setSize(410, 40);
        searchField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        topPanel.add(searchField);

        searchButton = new JButton();
        searchButton.setText("SEARCH");
        searchButton.setLocation(430, 4);
        searchButton.setSize(130, 40);
        searchButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        searchButton.setBackground(new Color(158, 41, 253));
        searchButton.setForeground(new Color(255, 255, 255));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(searchButton);

        menuPanel.removeAll();
        menuPanel.repaint();
        menuPanel.setLayout(new GridLayout(4, 1, 0, 20));

        newContact = new JButton();
        newContact.setText("ADD CONTACT");
        newContact.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        newContact.setForeground(new Color(255, 255, 255));
        newContact.setBackground(new Color(0, 255, 111));
        newContact.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(newContact);

        showContact = new JButton();
        showContact.setText("SHOW PHONEBOOK");
        showContact.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        showContact.setForeground(new Color(255, 255, 255));
        showContact.setBackground(new Color(0, 233, 255));
        showContact.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(showContact);

        clearDisplay = new JButton();
        clearDisplay.setText("CLEAR SCREEN");
        clearDisplay.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        clearDisplay.setForeground(new Color(255, 255, 255));
        clearDisplay.setBackground(new Color(255, 128, 0));
        clearDisplay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(clearDisplay);

        exitFrame = new JButton();
        exitFrame.setText("EXIT PROGRAM");
        exitFrame.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        exitFrame.setForeground(new Color(255, 255, 255));
        exitFrame.setBackground(new Color(255, 20, 0));
        exitFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(exitFrame);

        loadLabel = new JLabel();
        loadLabel.setLocation(50, 320);
        loadLabel.setSize(0, 25);
        loadLabel.setOpaque(true);
        loadLabel.setBackground(new Color(255, 235, 0));
        cnt.add(loadLabel);
        loadLabel.setVisible(false);

        display.setText("");

        searchButton.addActionListener(new ActionListenerManager());
        newContact.addActionListener(new ActionListenerManager());
        showContact.addActionListener(new ActionListenerManager());
        clearDisplay.addActionListener(new ActionListenerManager());
        exitFrame.addActionListener(new ActionListenerManager());
    }
    public void addContact() {
        topPanel.removeAll();
        topPanel.repaint();

        state = new JLabel();
        state.setText("FILL THE DETAILS BELOW..");
        state.setLocation(0, 5);
        state.setSize(350, 40);
        state.setOpaque(true);
        state.setBackground(new Color(255, 255, 255));
        state.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        topPanel.add(state);

        backBtn = new JButton();
        backBtn.setText("< BACK TO MENU");
        backBtn.setLocation(360, 5);
        backBtn.setSize(200, 40);
        backBtn.setBackground(new Color(158, 41, 253));
        backBtn.setForeground(new Color(255, 255, 255));
        backBtn.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(backBtn);

        menuPanel.removeAll();
        menuPanel.repaint();
        menuPanel.setLayout(null);

        JLabel namePrompt = new JLabel();
        namePrompt.setText("Enter name:");
        namePrompt.setLocation(0, 0);
        namePrompt.setSize(240, 20);
        namePrompt.setOpaque(true);
        namePrompt.setBackground(new Color(255, 255, 255));
        menuPanel.add(namePrompt);

        name = new JTextField();
        name.setLocation(0, 25);
        name.setSize(240, 30);
        menuPanel.add(name);

        JLabel phonePrompt = new JLabel();
        phonePrompt.setText("Phone Number:");
        phonePrompt.setLocation(0, 65);
        phonePrompt.setSize(240, 20);
        phonePrompt.setOpaque(true);
        phonePrompt.setBackground(new Color(255, 255, 255));
        menuPanel.add(phonePrompt);

        phone = new JTextField();
        phone.setLocation(0, 90);
        phone.setSize(240, 30);
        menuPanel.add(phone);

        JLabel addressPrompt = new JLabel();
        addressPrompt.setText("Address:");
        addressPrompt.setLocation(0, 125);
        addressPrompt.setSize(240, 20);
        addressPrompt.setOpaque(true);
        addressPrompt.setBackground(new Color(255, 255, 255));
        menuPanel.add(addressPrompt);

        address = new JTextField();
        address.setLocation(0, 150);
        address.setSize(240, 30);
        menuPanel.add(address);

        display.setText("");

        save = new JButton();
        save.setText("ADD NEW");
        save.setLocation(0, 190);
        save.setSize(240, 30);
        save.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        save.setBackground(new Color(0, 255, 111));
        save.setForeground(new Color(255, 255, 255));
        save.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(save);

        backBtn.addActionListener(new ActionListenerManager());
        save.addActionListener(new ActionListenerManager());
    }
    public void saveAt(int idx) {
        btnClicked = -1;
        String tempName = name.getText();
        String tempPhone = phone.getText();
        String tempAddress = address.getText();
        if (tempName.isEmpty() || tempPhone.isEmpty() || tempAddress.isEmpty()) {
            state.setText("PLEASE FILL ALL THE DATA");
        } else {
            load = true;
            if (idx != contacts.size()) {
                contacts.remove(idx);
            }
            contacts.add(idx, new Person(tempName, tempPhone, tempAddress));
            updateContacts();
            display.append("Saved Successfully!\n");
            name.setText("");
            phone.setText("");
            address.setText("");
            state.setText("FILL THE DETAILS BELOW..");
        }}
    public void updateContacts() {
        File myPhonebook = new File("phonebook.txt");
        try (PrintWriter pw = new PrintWriter(myPhonebook)) {
            for (Person contact : contacts) {
                pw.append(contact.name).append("\n");
                pw.append(contact.phoneNo).append("\n");
                pw.append(contact.address).append("\n\n");
            }
            pw.close();
        } catch (IOException ie) {
            assert true;
        }
    }
    public void loadContacts() {
        File myPhonebook = new File("phonebook.txt");
        if (myPhonebook.exists()) {
            try {
                Scanner sc = new Scanner(myPhonebook);
                while (sc.hasNext()) {
                    contacts.add(new Person(sc.nextLine(), sc.nextLine(), sc.nextLine()));
                    sc.nextLine();
                }
                sc.close();
            } catch (Exception e) {
                assert true;
            }
        } else {
            try {
                myPhonebook.createNewFile();
            } catch (IOException ie) {
                assert true;
            }
        }
    }
    public void menuAfterSearch() {
        menuPanel.removeAll();
        menuPanel.repaint();
        menuPanel.setLayout(new GridLayout(3, 1, 0, 40));

        updateButton = new JButton();
        updateButton.setText("UPDATE CONTACT");
        updateButton.setBackground(new Color(0, 255, 111));
        updateButton.setForeground(new Color(255, 255, 255));
        updateButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        updateButton.setEnabled(true);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(updateButton);

        remove = new JButton();
        remove.setText("DELETE CONTACT");
        remove.setBackground(new Color(158, 41, 53));
        remove.setForeground(new Color(255, 255, 255));
        remove.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        remove.setEnabled(true);
        remove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(remove);

        back = new JButton();
        back.setText("< BACK TO MENU");
        back.setBackground(new Color(158, 41, 253));
        back.setForeground(new Color(255, 255, 255));
        back.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuPanel.add(back);

        updateButton.addActionListener(new ActionListenerManager());
        remove.addActionListener(new ActionListenerManager());
        back.addActionListener(new ActionListenerManager());
    }
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }
    private final Container cnt;
    private JLabel loadLabel, state;
    private JPanel menuPanel, topPanel;
    private JTextField searchField, name, phone, address;
    private JButton searchButton, newContact, showContact,
            clearDisplay, exitFrame, save, backBtn, updateButton,
            remove, back;
    private JTextArea display;
    @Override
    public void run() {
        while (btnClicked != 4) {
            if (btnClicked == 2) {
                newContact.setBackground(new Color(0, 255, 111));
                showContact.setBackground(new Color(172, 172, 172));
                clearDisplay.setBackground(new Color(255, 128, 0));
                exitFrame.setBackground(new Color(255, 20, 0));

                showContact.setEnabled(false);
            } else {
                newContact.setBackground(new Color(0, 255, 111));
                showContact.setBackground(new Color(0, 233, 255));
                clearDisplay.setBackground(new Color(255, 128, 0));
                exitFrame.setBackground(new Color(255, 20, 0));

                showContact.setEnabled(true);
            }
            if (load) {
                loadLabel.setVisible(true);
                loadLabel.setSize(endPoint++, 25);
                sleep(1);
                if (endPoint == 560) {
                    load = false;
                    endPoint = 1;
                    sleep(100);
                    loadLabel.setVisible(false);
                }
            }
        }
    }
    private int updateIndex;
    short btnClicked, endPoint;
    boolean load;
    Thread th = new Thread(this);
}