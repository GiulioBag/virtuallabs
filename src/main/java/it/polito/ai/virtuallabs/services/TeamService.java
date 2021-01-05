package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.ProposedTeamDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.TeamDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    List<StudentDTO> getTeamStudentByTeam (String teamId, Principal principal) throws IOException;
    TeamDTO proposeTeam(ProposedTeamDTO team, String courseName, Principal principal);
    void confirmTeamParticipation(String teamID, Principal principal);
    void rejectTeamParticipation(String teamID, Principal principal);

    //vm
    List<VMDTO> getVMsByTeam(String teamId, Principal principal);

}
