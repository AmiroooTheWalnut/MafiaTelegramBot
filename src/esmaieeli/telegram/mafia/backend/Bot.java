/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esmaieeli.telegram.mafia.backend;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 *
 * @author Amir Mohammad Esmaieeli
 */
public class Bot implements LongPollingSingleThreadUpdateConsumer  {

    public Session parentSession;
    TelegramClient telegramClient;

    public Bot(Session session, String token) {
        parentSession = session;
        telegramClient = new OkHttpTelegramClient(token);
    }

    private String reportSession() {
        if (parentSession != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Session create on: ").append(parentSession.startSessionDate.toString()).append("\n");
            sb.append("Players:").append("\n");
            for (int i = 0; i < parentSession.players.size(); i++) {
                sb.append("    ").append(parentSession.players.get(i).name).append("\n");
            }
            sb.append("Roles:").append("\n");
            for (int i = 0; i < parentSession.roles.size(); i++) {
                sb.append("    ").append(parentSession.roles.get(i).name).append("\n");
            }
            return sb.toString();
        } else {
            return "No session is available!";
        }
    }

    private void sendMessage(Update update, String message) {
        SendMessage sendMessage = SendMessage.builder() // Create a SendMessage object with mandatory fields
                .chatId(update.getMessage().getChatId())
                .text(message).build();
        try {
            telegramClient.execute(sendMessage); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageWithPic(Update update, String message, String imgName) {
        SendMessage sendMessage = SendMessage.builder() // Create a SendMessage object with mandatory fields
                .chatId(update.getMessage().getChatId())
                .text(message).build();
        System.out.println(update.getMessage().getText());
        File file = new File(imgName);
        SendPhoto photo = SendPhoto.builder().chatId(update.getMessage().getChatId()).photo(new InputFile(file)).build();
        try {
            telegramClient.execute(sendMessage); // Call method to send the message
            telegramClient.execute(photo); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        System.out.println(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            Instant messageInstant = Instant.ofEpochSecond(update.getMessage().getDate());
            Date messageDate = Date.from(messageInstant);
            System.out.println(messageDate.toString());
            Instant nowInstant = Instant.now();
//            Date now = Date.from(nowInstant);
            if (nowInstant.minusSeconds(120).isAfter(messageInstant)) {
                sendMessage(update, "Your request is stale, please try a new request!");
            } else {
                String message = update.getMessage().getText();
                if (message.toLowerCase().equals("session")) {
                    sendMessage(update, reportSession());
                } else {
                    if (parentSession == null) {
                        sendMessage(update, "No game session is active!");
                    } else {
                        boolean isPlayerFound = false;
                        for (int i = 0; i < parentSession.players.size(); i++) {
                            if (parentSession.players.get(i).name.toLowerCase().equals(message.toLowerCase())) {
                                if (parentSession.players.get(i).hasSeenRole == false) {
                                    if (parentSession.players.get(i).role != null) {
                                        if (parentSession.players.get(i).role.picName.length() > 1) {
                                            try {
                                                String roleMessage = "Your role is: " + parentSession.players.get(i).role.name;
                                                sendMessageWithPic(update, roleMessage, parentSession.players.get(i).role.picName);
                                                parentSession.players.get(i).hasSeenRole = true;
                                                isPlayerFound = true;
                                                sendMessage(update, "Good luck!");
                                                parentSession.updateSessionGUI();
                                                break;
                                            } catch (Exception ex) {
                                                System.out.println("SEVERE ERROR: FAILED TO SEND ROLE WITH PIC! NOW TRYING WITHOUT PIC.");
                                                try {
                                                    String roleMessage = "Your role is: " + parentSession.players.get(i).role.name;
                                                    sendMessage(update, roleMessage);
                                                    parentSession.players.get(i).hasSeenRole = true;
                                                    isPlayerFound = true;
                                                    sendMessage(update, "Good luck!");
                                                    parentSession.updateSessionGUI();
                                                    break;
                                                } catch (Exception ex2) {
                                                    System.out.println("SEVERE ERROR: FAILED TO SEND ROLE TO PLAYER!");
                                                }
                                            }
                                        } else {
                                            try {
                                                String roleMessage = "Your role is: " + parentSession.players.get(i).role.name;
                                                sendMessage(update, roleMessage);
                                                parentSession.players.get(i).hasSeenRole = true;
                                                isPlayerFound = true;
                                                sendMessage(update, "Good luck!");
                                                parentSession.updateSessionGUI();
                                                break;
                                            } catch (Exception ex2) {
                                                System.out.println("SEVERE ERROR: FAILED TO SEND ROLE TO PLAYER!");
                                            }
                                        }
                                    } else {
                                        System.out.println("SEVERE ERROR: A PLAYER'S ROLE IS NULL!");
                                    }
                                } else {
                                    System.out.println("PLAYER " + parentSession.players.get(i).name + " WAS REQUESTED FOR ROLE MULTIPLE TIMES!");
                                    sendMessage(update, "You have already seen your role!!!");
                                    isPlayerFound = true;
                                }
                            }
                        }
                        if (isPlayerFound == false) {
                            sendMessage(update, "Unknown command, player not found, or player has no role assigned to!");
                        }
                    }
                }
            }
        }
    }

}
