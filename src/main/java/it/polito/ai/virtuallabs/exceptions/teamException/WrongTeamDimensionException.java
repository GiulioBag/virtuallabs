package it.polito.ai.virtuallabs.exceptions.teamException;

public class WrongTeamDimensionException extends TeamException {
   public WrongTeamDimensionException(int min, int max, int size){
        super("The number of students in the team must be between " + min + " and " + max + ". The current" +
                " number of students is: " + size);
    }
}
