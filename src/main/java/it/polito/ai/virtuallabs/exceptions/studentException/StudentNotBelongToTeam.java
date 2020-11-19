package it.polito.ai.virtuallabs.exceptions.studentException;

public class StudentNotBelongToTeam extends StudentException {
    public StudentNotBelongToTeam(String studentId, String teamId){
        super(studentId + " not belong to " + teamId + " team");
    }
}
