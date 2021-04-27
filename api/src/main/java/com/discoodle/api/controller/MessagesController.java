package com.discoodle.api.controller;

import com.discoodle.api.model.Message;

import com.discoodle.api.repository.RoomRepository;
import com.discoodle.api.repository.UserRepository;
import com.discoodle.api.request.MessageRequest;
import com.discoodle.api.service.MessagesService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class MessagesController {

    private final MessagesService messagesService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @GetMapping("/api/messages")
    public List<Message> findMessagesOfRoom(@RequestParam(value = "room_uuid") String room_uuid) {
        // If the room_uuid entered is a valid one, then look for it in the database
        if (room_uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") && roomRepository.existsById(room_uuid))
            return messagesService.findMessagesOfRoom(room_uuid);
        // Else, return an empty list.
        return List.of();
    }

    @PostMapping("api/messages/sendMessage")
    public Message sendMessage(@RequestParam(value = "room_uuid") String room_uuid, @RequestBody MessageRequest message) {
        // Checking if the room_uuid is the same than the message uuid (autogenerated) so you cannot cheat and send messages to another group.
        if (room_uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") && roomRepository.existsById(room_uuid) && room_uuid.equals(message.getConv_uuid())) {
            Message msg = new Message(
                  message.getContent(),
                  message.getSender(),
                  message.getMessage_date(),
                  message.getConv_uuid()
            );
            if(roomRepository.findById(room_uuid).get().getUsers().contains(userRepository.findUserByUserName(message.getSender()).get())) {
                return messagesService.sendMessage(msg);
            }
        }
        // Should not happen if everything goes well.
        return null;
    }

    @PutMapping(path = "/api/messages/pinMessage")
    public void pinMessage(@RequestParam(value = "message_id") Long message_id) {
        messagesService.pinMessage(message_id);
    }

    @PutMapping(path = "/api/messages/unpinMessage")
    public void unpinMessage(@RequestParam(value = "message_id") Long message_id) {
        messagesService.unpinMessage(message_id);
    }

    @DeleteMapping(path = "/api/messages/deleteMessage")
    public void deleteMessage(@RequestParam(value = "message_id") Long message_id) {
        messagesService.deleteMessage(message_id);
    }

    @PutMapping(path = "/api/messages/editMessage")
    public void editMessage(@RequestBody EditMessageRequest messageRequest) {
        // Edit the content of the message and also set the edited prop to true.
        messagesService.editMessage(messageRequest.getMessage_id(), messageRequest.getContent());
        messagesService.setEdited(messageRequest.getMessage_id());
    }

    @Getter
    @AllArgsConstructor
    static class EditMessageRequest {
        private final Long message_id;
        private final String content;
    }

}
