package client.gui;

import client.ClientController;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;


public class ChatWindow {

    public JPanel mainChatPanel;
    private JTextArea chatWindowOutput;
    private JTextArea chatWindowInput;
    private JButton sendButton;
    private JPanel contactListPanel;
    private JComboBox<String> comboBoxContactList;

    private JList listaContactos;
    private JScrollPane listaContactosScrollPane;
    private DefaultListModel listaContactosModel;

    private DefaultComboBoxModel comboBoxModel;

    private Timer timer;
    private ClientController controller;

    private ConcurrentHashMap<String, LinkedList<String>> chatHistory;
    private ConcurrentHashMap<String, LinkedList<String>> chatBuffer;

    public ChatWindow(final ClientController controller) {
        $$$setupUI$$$();
        this.timer = new Timer(true);
        this.contactListPanel = new JPanel();
        this.comboBoxModel = new DefaultComboBoxModel();
        this.comboBoxContactList = new JComboBox(comboBoxModel);
        this.listaContactos = new JList();
        this.listaContactosModel = new DefaultListModel();
        this.listaContactos.setModel(listaContactosModel);
        this.timer.scheduleAtFixedRate(new CheckContacts(), 0, 3000);
        this.controller = controller;
        this.chatHistory = new ConcurrentHashMap<>();
        this.chatBuffer = new ConcurrentHashMap<>();
        this.chatWindowOutput.setCaretPosition(chatWindowOutput.getDocument().getLength());
        this.chatWindowOutput.setEditable(false);
        this.chatWindowOutput.setLineWrap(true);


        new Thread(new ReceiveMessageThread()).start();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUserStr = (String) listaContactos.getSelectedValue();
                if (!selectedUserStr.equals("")) {
                    String inputMessage = chatWindowInput.getText();
                    if (!inputMessage.equals("")) {
                        chatWindowInput.setText("");
                        controller.sendMessage(inputMessage, selectedUserStr, ClientController.username);
                        chatWindowOutput.append(ClientController.username + ": " + inputMessage + "\n");
                        LinkedList<String> userChatHistoryList = chatHistory.get(selectedUserStr);
                        userChatHistoryList.add(ClientController.username + ": " + inputMessage + "\n");
                        chatHistory.put(selectedUserStr, userChatHistoryList);
                    }
                }
            }
        });

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selectedUserStr = (String) listaContactos.getSelectedValue();
                //CHAT
                LinkedList<String> userChatHistoryList = chatHistory.get(selectedUserStr);
                chatWindowOutput.setText("");
                for (String line : userChatHistoryList) {
                    chatWindowOutput.append(line);
                }
                if (chatBuffer.containsKey(selectedUserStr)) {
                    LinkedList<String> unreadMessages = chatBuffer.get(selectedUserStr);
                    for (String line : unreadMessages) {
                        chatWindowOutput.append(line);
                        userChatHistoryList.add(line);
                    }
                    chatBuffer.put(selectedUserStr, new LinkedList<String>());
                }
                chatHistory.put(selectedUserStr, userChatHistoryList);
                System.out.println("[CLIENT] Changed the (focused) contact=" + selectedUserStr);
            }
        };
        listaContactos.addMouseListener(mouseListener);
    }


    class ReceiveMessageThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                String[] message = controller.receiveMessage().split(":");
                //dst : src : message
                if (!chatBuffer.containsKey(message[0])) {
                    chatBuffer.put(message[0], new LinkedList<String>());
                }
                String selectedUser = (String) listaContactos.getSelectedValue();
                if (selectedUser == null || !selectedUser.equals(message[0])) {
                    LinkedList<String> buffer = chatBuffer.get(message[0]);
                    buffer.add(message[0] + ": " + message[2] + "\n");
                    chatBuffer.put(message[0], buffer);
                } else {
                    if (!chatHistory.containsKey(message[0])) {
                        chatHistory.put(message[0], new LinkedList<String>());
                    }
                    LinkedList<String> userChatHistoryList = chatHistory.get(message[0]);
                    userChatHistoryList.add(message[0] + ": " + message[2] + "\n");
                    chatWindowOutput.append(message[0] + ": " + message[2] + "\n");
                }
            }
        }
    }

    int size = 0;

    class CheckContacts extends TimerTask {
        @Override
        public void run() {
            if (controller.updateContactList().keySet().size() > size) {
                for (String key : controller.updateContactList().keySet()) {
                    if (!chatHistory.containsKey(key)) {
                        chatHistory.put(key, new LinkedList<String>());
                        listaContactosModel.addElement(key);
                    }
                }
                listaContactos.setModel(listaContactosModel);
                listaContactosScrollPane.setViewportView(listaContactos);
                size = controller.updateContactList().keySet().size();
            }
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainChatPanel = new JPanel();
        mainChatPanel.setLayout(new GridLayoutManager(13, 2, new Insets(0, 5, 0, 0), -1, -1));
        chatWindowInput = new JTextArea();
        chatWindowInput.setBackground(new Color(-12236470));
        chatWindowInput.setForeground(new Color(-1));
        chatWindowInput.setText("");
        mainChatPanel.add(chatWindowInput, new GridConstraints(10, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(500, 50), new Dimension(500, 50), new Dimension(500, 50), 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-1));
        mainChatPanel.add(scrollPane1, new GridConstraints(0, 0, 10, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 300), new Dimension(500, 300), new Dimension(500, 300), 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        chatWindowOutput = new JTextArea();
        chatWindowOutput.setBackground(new Color(-12236470));
        chatWindowOutput.setEditable(false);
        chatWindowOutput.setForeground(new Color(-1));
        chatWindowOutput.setLineWrap(true);
        chatWindowOutput.setText("");
        chatWindowOutput.setWrapStyleWord(true);
        chatWindowOutput.putClientProperty("html.disable", Boolean.FALSE);
        scrollPane1.setViewportView(chatWindowOutput);
        sendButton = new JButton();
        sendButton.setText("Send");
        mainChatPanel.add(sendButton, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listaContactosScrollPane = new JScrollPane();
        mainChatPanel.add(listaContactosScrollPane, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listaContactos = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        listaContactos.setModel(defaultListModel1);
        listaContactosScrollPane.setViewportView(listaContactos);
        final JLabel label1 = new JLabel();
        label1.setText("Contact List");
        mainChatPanel.add(label1, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainChatPanel.add(spacer1, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainChatPanel;
    }

}
