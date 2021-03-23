package com.discoodle.api.repository;

import com.discoodle.api.model.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Optional;


@Repository
public interface GroupsRepository extends JpaRepository<Groups,Integer> {

    @Query(value = "SELECT groups FROM Groups groups where groups.groups_id=?1")
    Optional<Groups> findGroupsById(Integer id);

    @Query(value = "SELECT groups FROM Groups groups where groups.name=?1")
    Optional<Groups> findAllGroupsByName(String name);

    @Query(value = "SELECT groups FROM Groups groups where groups.usersGroupName=?1")
    Optional<Groups> findAllGroupsByUserGroupName(String userGroupName);

    @Query(value = "SELECT groups FROM Groups groups where groups.depth=?1")
    Optional<Groups> findAllGroupsByDepth(Integer depth);

    @Modifying
    @Query(value = "insert into link_groups_to_user (user_id, groups_id) VALUES (:user_id,:groups_id)", nativeQuery = true)
    @Transactional
    void addNewMemberInGroup(@Param("user_id") Integer user_ID, @Param("groups_id") Integer groups_ID);

    @Modifying
    @Query(value = "insert into link_groups_to_group (groups_id, son_id) VALUES (:groups_id,:son_id)", nativeQuery = true)
    @Transactional
    void addNewGroupsInGroup(@Param("groups_id") Integer groups_ID, @Param("son_id") Integer son_ID);
}