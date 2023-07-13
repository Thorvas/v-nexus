package com.example.demo.Mapper;

import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.DummyObject.Volunteer;

public class VolunteerMapper {

    public static VolunteerDTO mapVolunteerToDTO(Volunteer volunteerToMap) {

        VolunteerDTO newDTO = new VolunteerDTO();

        newDTO.setName(volunteerToMap.getName());
        newDTO.setSurname(volunteerToMap.getSurname());
        newDTO.setDateOfBirth(volunteerToMap.getDateOfBirth());
        newDTO.setContact(volunteerToMap.getContact());
        newDTO.setSkills(volunteerToMap.getSkills());
        newDTO.setProjects(volunteerToMap.getParticipatingProjects());
        newDTO.setReputation(volunteerToMap.getReputation());
        newDTO.setInterests(volunteerToMap.getInterests());

        return newDTO;
    }

    public static void mapPropertiesToVolunteer(Volunteer sourceVolunteer, Volunteer targetVolunteer) {

        targetVolunteer.setName(sourceVolunteer.getName());
        targetVolunteer.setSurname(sourceVolunteer.getSurname());
        targetVolunteer.setDateOfBirth(sourceVolunteer.getDateOfBirth());
        targetVolunteer.setContact(sourceVolunteer.getContact());
        targetVolunteer.setSkills(sourceVolunteer.getSkills());
        targetVolunteer.setParticipatingProjects(sourceVolunteer.getParticipatingProjects());
        targetVolunteer.setReputation(sourceVolunteer.getReputation());
        targetVolunteer.setInterests(sourceVolunteer.getInterests());

    }
}
