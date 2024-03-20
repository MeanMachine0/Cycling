package cycling;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CyclingPortalImplTest {
    MiniCyclingPortal portal;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        portal = new CyclingPortalImpl();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }
    @org.junit.jupiter.api.Test
    void createTeam_oneValidTeam() throws InvalidNameException, IllegalNameException {
        int teamId = portal.createTeam("Ape", "Zoo escapees");
        assertEquals(1, teamId);
    }
    @org.junit.jupiter.api.Test
    void createTeam_emptyNameThrowsException() {
        assertThrows(InvalidNameException.class, () -> {
            portal.createTeam("", "Zoo escapees");
        });
    }
    @org.junit.jupiter.api.Test
    void removeTeam_idDoesNotExistThrowsException() {
        assertThrows(IDNotRecognisedException.class, () -> {
            portal.removeTeam(1);
        });
    }
    @org.junit.jupiter.api.Test
    void getTeams_threeTeams() throws InvalidNameException, IllegalNameException {
        portal.createTeam("Ape", "Zoo escapees");
        portal.createTeam("Chimp", "Zoo escapees");
        portal.createTeam("Egg", "Zoo escapees");
        assertEquals(3, portal.getTeams().length);
        assertEquals(1, portal.getTeams()[0]);
        assertEquals(2, portal.getTeams()[1]);
        assertEquals(3, portal.getTeams()[2]);
    }
    @org.junit.jupiter.api.Test
    void getTeams_empty() {
        assertEquals(0, portal.getTeams().length);
    }
    @org.junit.jupiter.api.Test
    void createRider() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        // arrange
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        // act
        int riderId = portal.createRider(teamId, "Daniel", 1999);
        // assert
        int[] riders = portal.getTeamRiders(teamId);
        assertEquals(1, riders.length);
        assertEquals(1, riders[0]);
    }
   @org.junit.jupiter.api.Test
    void removeRider() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        // arrange
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        portal.createRider(teamId, "Daniel", 1999);
        portal.createRider(teamId, "Joel", 2001);
        portal.createRider(teamId, "Marcus", 2004);
        // act
        portal.removeRider(2);
        // assert
        int[] riderIds = portal.getTeamRiders(teamId);
        assertEquals(2, riderIds.length);
   }
   @org.junit.jupiter.api.Test
    void createRace_oneValidRace() throws InvalidNameException, IllegalNameException {
        // act
       int raceId = portal.createRace("Egg&Spoon", "...on a bike");
       // assert
       assertEquals(1, raceId);
   }
    @org.junit.jupiter.api.Test
    void createRace_emptyNameThrowsException() {
        assertThrows(InvalidNameException.class, () -> {
            portal.createRace("", "Zoo escapees");
        });
    }
    @org.junit.jupiter.api.Test
    void addStageToRace() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime spoonStartTime = eggStartTime.plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        // act
        int eggStageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        int spoonStageId = portal.addStageToRace(raceId, "Spoon",
                "Carry a spoon", 2.718 + 3, spoonStartTime, StageType.HIGH_MOUNTAIN);
        // assert
        int[] stageIds = portal.getRaceStages(raceId);
        assertEquals(1, eggStageId);
        assertEquals(2, spoonStageId);
        assertEquals(stageIds[0], eggStageId);
        assertEquals(stageIds[1], spoonStageId);
    }
    @org.junit.jupiter.api.Test
    void addStageToRace_invalidLength() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        // assert
        assertThrows(InvalidLengthException.class, () -> {
            portal.addStageToRace(raceId, "Egg",
                    "Carry an egg", 4.999999999, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        });
    }
    @org.junit.jupiter.api.Test
    void getStageLength() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime spoonStartTime = eggStartTime.plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        int stageId = portal.addStageToRace(raceId, "Spoon",
                "Carry a spoon", 2.718 + 3, spoonStartTime, StageType.HIGH_MOUNTAIN);
        // act
        double stageLength = portal.getStageLength(stageId);
        // assert
        assertEquals(2.718 + 3, stageLength);
    }
    @org.junit.jupiter.api.Test
    void getRaceIds() throws InvalidNameException, IllegalNameException {
        // arrange
        int raceId1 = portal.createRace("Egg&Spoon", "...on a bike");
        int raceId2 = portal.createRace("Chimps&Gorillas", "...are apes");
        // act
        int[] raceIds = portal.getRaceIds();
        // assert
        assertEquals(1, raceId1);
        assertEquals(2, raceId2);
        assertEquals(raceId1, raceIds[0]);
        assertEquals(raceId2, raceIds[1]);
    }
    @org.junit.jupiter.api.Test
    void removeRaceById() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        // arrange
        portal.createRace("Egg&Spoon", "...on a bike");
        portal.createRace("Chimps&Gorillas", "...are apes");
        // act
        portal.removeRaceById(1);
        // assert
        assertEquals(2, portal.getRaceIds()[0]);
    }
    @org.junit.jupiter.api.Test
    void removeStageById() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime spoonStartTime = eggStartTime.plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        portal.addStageToRace(raceId, "Spoon",
                "Carry a spoon", 2.718 + 3, spoonStartTime, StageType.HIGH_MOUNTAIN);
        // act
        portal.removeStageById(2);
        // assert
        assertEquals(1, portal.getRaceStages(raceId).length);
    }
    @org.junit.jupiter.api.Test
    void addCategorizedClimbToStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        // act
        portal.addCategorizedClimbToStage(stageId, 6.141, CheckpointType.C2, 0.8, 5.0);
        //assert
        int[] checkpointIds = portal.getStageCheckpoints(stageId);
        assertEquals(1, checkpointIds.length);
        assertEquals(1, checkpointIds[0]);
    }
    @org.junit.jupiter.api.Test
    void removeCheckpoint() throws InvalidStageStateException, InvalidLocationException, IDNotRecognisedException, InvalidStageTypeException, InvalidNameException, IllegalNameException, InvalidLengthException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        portal.addIntermediateSprintToStage(stageId, 6.141);
        // act
        portal.removeCheckpoint(1);
        // assert
        assertEquals(0, portal.getStageCheckpoints(stageId).length);
    }
}