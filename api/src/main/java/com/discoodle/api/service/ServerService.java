package com.discoodle.api.service;

import com.discoodle.api.model.Room;
import com.discoodle.api.model.Server;
import com.discoodle.api.model.User;
import com.discoodle.api.repository.ServerRepository;
import com.discoodle.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServerService {

    ServerRepository serverRepository;
    UserRepository userRepository;
    RoomService roomService;

    public Server createNewServ(String server_name, List<Long> server_members) {
        Server serv = new Server(
                server_name
        );
        Server finalServ = serverRepository.save(serv);
        for (Long server_member : server_members) {
            if(userRepository.existsById(server_member))
                serverRepository.addNewMember(finalServ.getServer_id(), server_member);
        }
        Room room = roomService.createNewRoom("Géneral", server_members);
        room.setRoom_link(true);
        serverRepository.addNewRoomInServ(finalServ.getServer_id(), room.getRoom_id());
        return finalServ;
    }

    public Optional<Server> addNewMember(Long server_id, Long user_id) {
        if(serverRepository.existsById(server_id) && userRepository.existsById(user_id)){
            serverRepository.addNewMember(server_id, user_id);
            Server server = serverRepository.findById(server_id).get();
            for (Room room : server.getRooms()) {
                roomService.addNewMember(room.getRoom_id(), user_id);
            }
            return serverRepository.findById(server_id);
        }
        return Optional.empty();
    }

    public Optional<Server> addNewRoom(Long server_id, String name) {
        if (serverRepository.existsById(server_id)) {
            Optional<Server> server = serverRepository.findById(server_id);
            List<Long> users_id = new LinkedList<>();
            for (User user : server.get().getUsers()) {
                users_id.add(user.getId());
            }
            Room newRoom = roomService.createNewRoom(name, users_id);
            newRoom.setRoom_link(true);
            serverRepository.addNewRoomInServ(server_id, newRoom.getRoom_id());
            return server;
        }
        return Optional.empty();
    }
}
