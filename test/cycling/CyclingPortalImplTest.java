package cycling;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CyclingPortalImplTest {
    final String filename = "miniCyclingPortal.ser";
    MiniCyclingPortal portal = new CyclingPortalImpl();
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        File file = new File(filename);
        if (file.exists() && file.delete()) System.out.println(filename + " deleted.");
        portal.eraseCyclingPortal();
    }

    @org.junit.jupiter.api.Test
    void getRaceIds_empty() {
        // act
        int[] raceIds = portal.getRaceIds();
        // assert
        assertArrayEquals(new int[0], raceIds);
    }
    @org.junit.jupiter.api.Test
    void createTeam_oneValidTeam() throws InvalidNameException, IllegalNameException {
        int teamId = portal.createTeam("Ape", "Zoo escapees");
        assertEquals(1, teamId);
    }
    @org.junit.jupiter.api.Test
    void createTeam_emptyNameThrowsException() {
        assertThrows(InvalidNameException.class, () -> portal.createTeam("", "Zoo escapees"));
    }
    @org.junit.jupiter.api.Test
    void removeTeam_idDoesNotExistThrowsException() {
        assertThrows(IDNotRecognisedException.class, () -> portal.removeTeam(1));
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
        assertEquals(riderId, riders[0]);
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
        assertThrows(InvalidNameException.class, () -> portal.createRace("", "Zoo escapees"));
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
        assertEquals(2, eggStageId);
        assertEquals(3, spoonStageId);
        assertEquals(stageIds[0], eggStageId);
        assertEquals(stageIds[1], spoonStageId);
    }
    @org.junit.jupiter.api.Test
    void addStageToRace_invalidLength() throws InvalidNameException, IllegalNameException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        // assert
        assertThrows(InvalidLengthException.class, () -> portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 4.999999999, eggStartTime, StageType.MEDIUM_MOUNTAIN));
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
        assertEquals(3, checkpointIds[0]);
    }
    @org.junit.jupiter.api.Test
    void addIntermediateSprintToStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        // act
        portal.addIntermediateSprintToStage(stageId, 6.141);
        //assert
        int[] checkpointIds = portal.getStageCheckpoints(stageId);
        assertEquals(1, checkpointIds.length);
        assertEquals(3, checkpointIds[0]);
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
        portal.removeCheckpoint(3);
        // assert
        assertEquals(0, portal.getStageCheckpoints(stageId).length);
    }
    @org.junit.jupiter.api.Test
    void registerRiderResultsInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        portal.createRider(teamId, "Daniel", 1999);
        portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        LocalTime[] criticalTimes = { start.toLocalTime(), start.plusMinutes(50).toLocalTime(), start.plusMinutes(62).toLocalTime(), start.plusMinutes(70).toLocalTime()};
        portal.concludeStagePreparation(stageId);
        // act
        portal.registerRiderResultsInStage(stageId, myId, criticalTimes);
        // assert
        assertArrayEquals(new LocalTime[] { criticalTimes[1], criticalTimes[2], LocalTime.of(1, 10) }, portal.getRiderResultsInStage(stageId, myId));
    }
    @org.junit.jupiter.api.Test
    void registerRiderResultsInStage_duplicateResult() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        portal.createRider(teamId, "Daniel", 1999);
        portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        portal.concludeStagePreparation(stageId);
        LocalTime[] criticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(50), start.plusMinutes(62), start.plusMinutes(70)});
        portal.registerRiderResultsInStage(stageId, myId, criticalTimes);
        // act
        assertThrows(DuplicatedResultException.class, () -> portal.registerRiderResultsInStage(stageId, myId, criticalTimes));
    }
    @org.junit.jupiter.api.Test
    void deleteRiderResultsInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        portal.createRider(teamId, "Daniel", 1999);
        portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        LocalTime now = LocalTime.now();
        LocalTime[] criticalTimes = { now, now.plusMinutes(50), now.plusMinutes(62), now.plusMinutes(70)};
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, myId, criticalTimes);
        // act
        portal.deleteRiderResultsInStage(stageId, myId);
        // assert
        assertEquals(0, portal.getRiderResultsInStage(stageId, myId).length);
    }
    @org.junit.jupiter.api.Test
    void duration_between() {
        LocalTime justBeforeMidnight = LocalTime.of(23, 59, 59);
        Duration duration = Duration.between(justBeforeMidnight, LocalTime.MIDNIGHT);
        assert duration.isNegative();
    }
    @org.junit.jupiter.api.Test
    void getRiderAdjustedElapsedTimeInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        int bouncerId = portal.createRider(petsId, "Bouncer", 1970);
        int fluffyId = portal.createRider(petsId, "Fluffy", 1970);
        int stormyId = portal.createRider(petsId, "Stormy", 1970);
        int danId = portal.createRider(teamId, "Daniel", 1999);
        int joelId = portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        int dadId = portal.createRider(parentsId, "Tim", 1970);
        int mumId = portal.createRider(parentsId, "Annie", 1973);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(1) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4).minusNanos(1) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(3) });
        Duration myDuration = Duration.ofMinutes(555).plusSeconds(3);
        LocalTime myElapsedTime = LocalTime.of(myDuration.toHoursPart(), myDuration.toMinutesPart(), myDuration.toSecondsPart(), myDuration.toNanosPart());
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncerId, bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffyId, fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, danId, danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dadId, dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joelId, joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormyId, stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, myId, myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mumId, mumCriticalTimes);
        // act
        LocalTime myAdjustedElapsedTime = portal.getRiderAdjustedElapsedTimeInStage(stageId, myId);
        // assert
        assertEquals(myElapsedTime.minusSeconds(5), myAdjustedElapsedTime);
    }
    @org.junit.jupiter.api.Test
    void getRiderAdjustedElapsedTimeInStage_TT() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.TT);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        int bouncerId = portal.createRider(petsId, "Bouncer", 1970);
        int fluffyId = portal.createRider(petsId, "Fluffy", 1970);
        int stormyId = portal.createRider(petsId, "Stormy", 1970);
        int danId = portal.createRider(teamId, "Daniel", 1999);
        int joelId = portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        int dadId = portal.createRider(parentsId, "Tim", 1970);
        int mumId = portal.createRider(parentsId, "Annie", 1973);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(1) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(3) });
        Duration myDuration = Duration.ofMinutes(555).plusSeconds(3);
        LocalTime myElapsedTime = LocalTime.of(myDuration.toHoursPart(), myDuration.toMinutesPart(), myDuration.toSecondsPart(), myDuration.toNanosPart());
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncerId, bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffyId, fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, danId, danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dadId, dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joelId, joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, myId, myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mumId, mumCriticalTimes);
        // act
        LocalTime myAdjustedElapsedTime = portal.getRiderAdjustedElapsedTimeInStage(stageId, myId);
        // assert
        assertEquals(myElapsedTime, myAdjustedElapsedTime);
        assertNull(portal.getRiderAdjustedElapsedTimeInStage(stageId, stormyId));
        assertEquals(0, portal.getRiderResultsInStage(stageId, stormyId).length);
    }
    @org.junit.jupiter.api.Test
    void getRidersRankInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        int bouncerId = portal.createRider(petsId, "Bouncer", 1970);
        int fluffyId = portal.createRider(petsId, "Fluffy", 1970);
        int stormyId = portal.createRider(petsId, "Stormy", 1970);
        int danId = portal.createRider(teamId, "Daniel", 1999);
        int joelId = portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        int dadId = portal.createRider(parentsId, "Tim", 1970);
        int mumId = portal.createRider(parentsId, "Annie", 1973);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(1) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4).minusNanos(1) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncerId, bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffyId, fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, danId, danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dadId, dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joelId, joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormyId, stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, myId, myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mumId, mumCriticalTimes);
        // act
        int[] riderIds = portal.getRidersRankInStage(stageId);
        // assert
        int[] expectedRiderIds = { stormyId, dadId, mumId, danId, joelId, bouncerId, fluffyId, myId };
        assertArrayEquals(expectedRiderIds, riderIds);
    }
    @org.junit.jupiter.api.Test
    void getRankedAdjustedElapsedTimesInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        int bouncerId = portal.createRider(petsId, "Bouncer", 1970);
        int fluffyId = portal.createRider(petsId, "Fluffy", 1970);
        int stormyId = portal.createRider(petsId, "Stormy", 1970);
        int danId = portal.createRider(teamId, "Daniel", 1999);
        int joelId = portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        int dadId = portal.createRider(parentsId, "Tim", 1970);
        int mumId = portal.createRider(parentsId, "Annie", 1973);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(1) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4).minusNanos(1) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(230), start.plusMinutes(400), start.plusMinutes(555).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncerId, bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffyId, fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, danId, danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dadId, dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joelId, joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormyId, stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, myId, myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mumId, mumCriticalTimes);
        LocalTime myAdjustedElapsedTime = portal.getRiderAdjustedElapsedTimeInStage(stageId, myId);
        LocalTime[] expectedRankedAdjustedElapsedTimes = {
                myAdjustedElapsedTime.minusSeconds(2).minusNanos(1),
                myAdjustedElapsedTime.minusSeconds(2).minusNanos(1),
                myAdjustedElapsedTime,
                myAdjustedElapsedTime,
                myAdjustedElapsedTime,
                myAdjustedElapsedTime,
                myAdjustedElapsedTime,
                myAdjustedElapsedTime
        };
        // act
        LocalTime[] rankedAdjustedElapsedTimes = portal.getRankedAdjustedElapsedTimesInStage(stageId);
        // assert
        assertArrayEquals(expectedRankedAdjustedElapsedTimes, rankedAdjustedElapsedTimes);
    }
    @org.junit.jupiter.api.Test
    void getRidersPointsInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
        portal.addIntermediateSprintToStage(stageId, 4);
        portal.addIntermediateSprintToStage(stageId, 1.25);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        Pair<Integer, Integer> bouncer = new Pair<>(portal.createRider(petsId, "Bouncer", 1970), 0);
        Pair<Integer, Integer> fluffy= new Pair<>(portal.createRider(petsId, "Fluffy", 1970), 0);
        Pair<Integer, Integer> stormy = new Pair<>(portal.createRider(petsId, "Stormy", 1970), 0);
        Pair<Integer, Integer> dan = new Pair<>(portal.createRider(teamId, "Daniel", 1999), 0);
        Pair<Integer, Integer> joel = new Pair<>(portal.createRider(teamId, "Joel", 2001), 0);
        Pair<Integer, Integer> me = new Pair<>(portal.createRider(teamId, "Marcus", 2004), 0);
        Pair<Integer, Integer> dad = new Pair<>(portal.createRider(parentsId, "Tim", 1970), 0);
        Pair<Integer, Integer> mum = new Pair<>(portal.createRider(parentsId, "Annie", 1973), 0);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(237), start.plusMinutes(400), start.plusMinutes(557), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(236), start.plusMinutes(415), start.plusMinutes(556), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(235), start.plusMinutes(385), start.plusMinutes(555), start.plusMinutes(600).minusSeconds(6) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(234), start.plusMinutes(400), start.plusMinutes(554), start.plusMinutes(600).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(233), start.plusMinutes(400), start.plusMinutes(553), start.plusMinutes(600).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(232), start.plusMinutes(400), start.plusMinutes(552), start.plusMinutes(600).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(231), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(229), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncer.getOne(), bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffy.getOne(), fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dan.getOne(), danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dad.getOne(), dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joel.getOne(), joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormy.getOne(), stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, me.getOne(), myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mum.getOne(), mumCriticalTimes);
        // act
        int[] rankedPoints = portal.getRidersPointsInStage(stageId);
        // assert
        bouncer.setTwo(8+8+15);
        fluffy.setTwo(9+9+15);
        stormy.setTwo(10+10+30);
        dad.setTwo(11+11+25);
        mum.setTwo(13+13+22);
        dan.setTwo(15+15+19);
        joel.setTwo(17+20+17);
        me.setTwo(20+20+11);
        ArrayList<Pair<Integer, Integer>> riders = new ArrayList<>(List.of(bouncer, fluffy, stormy, dan, joel, me, dad, mum));
        int[] rankedRiderIds = portal.getRidersRankInStage(stageId);
        int[] expectedRankedPoints = Arrays.stream(rankedRiderIds)
                .map(riderId -> riders.stream().filter(pair -> riderId == pair.getOne()).findFirst().orElseThrow().getTwo()).toArray();
        assertArrayEquals(expectedRankedPoints, rankedPoints);
    }
    @org.junit.jupiter.api.Test
    void getRidersPointsInStage_TT() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.TT);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        int bouncerId = portal.createRider(petsId, "Bouncer", 1970);
        int fluffyId = portal.createRider(petsId, "Fluffy", 1970);
        portal.createRider(petsId, "Stormy", 1970);
        int danId = portal.createRider(teamId, "Daniel", 1999);
        int joelId = portal.createRider(teamId, "Joel", 2001);
        int myId = portal.createRider(teamId, "Marcus", 2004);
        int dadId = portal.createRider(parentsId, "Tim", 1970);
        int mumId = portal.createRider(parentsId, "Annie", 1973);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(2) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(555).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncerId, bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffyId, fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, danId, danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dadId, dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joelId, joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, myId, myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mumId, mumCriticalTimes);
        int[] rankedPoints = portal.getRidersPointsInStage(stageId);
        // assert
        int[] expectedRankedPoints = { 20, 17, 15, 13, 11, 11, 9 };
        assertArrayEquals(expectedRankedPoints, rankedPoints);
    }
    @org.junit.jupiter.api.Test
    void getRidersMountainPointsInStage() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C4, 0.8, 5.0);
        portal.addCategorizedClimbToStage(stageId, 4.0, CheckpointType.C2, 0.2, 1.0);
        portal.addCategorizedClimbToStage(stageId, 1.25, CheckpointType.HC, 1.2, 6.0);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        Pair<Integer, Integer> bouncer = new Pair<>(portal.createRider(petsId, "Bouncer", 1970), 0);
        Pair<Integer, Integer> fluffy= new Pair<>(portal.createRider(petsId, "Fluffy", 1970), 0);
        Pair<Integer, Integer> stormy = new Pair<>(portal.createRider(petsId, "Stormy", 1970), 0);
        Pair<Integer, Integer> dan = new Pair<>(portal.createRider(teamId, "Daniel", 1999), 0);
        Pair<Integer, Integer> joel = new Pair<>(portal.createRider(teamId, "Joel", 2001), 0);
        Pair<Integer, Integer> me = new Pair<>(portal.createRider(teamId, "Marcus", 2004), 0);
        Pair<Integer, Integer> dad = new Pair<>(portal.createRider(parentsId, "Tim", 1970), 0);
        Pair<Integer, Integer> mum = new Pair<>(portal.createRider(parentsId, "Annie", 1973), 0);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(237), start.plusMinutes(400), start.plusMinutes(557), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(236), start.plusMinutes(415), start.plusMinutes(556), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(235), start.plusMinutes(385), start.plusMinutes(555), start.plusMinutes(600).minusSeconds(6) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(234), start.plusMinutes(400), start.plusMinutes(554), start.plusMinutes(600).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(233), start.plusMinutes(400), start.plusMinutes(553), start.plusMinutes(600).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(232), start.plusMinutes(400), start.plusMinutes(552), start.plusMinutes(600).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(231), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(229), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncer.getOne(), bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffy.getOne(), fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dan.getOne(), danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dad.getOne(), dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joel.getOne(), joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormy.getOne(), stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, me.getOne(), myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mum.getOne(), mumCriticalTimes);
        // act
        int[] rankedPoints = portal.getRidersMountainPointsInStage(stageId);
        // assert
        bouncer.setTwo(2);
        fluffy.setTwo(4);
        stormy.setTwo(6+1);
        dad.setTwo(8);
        mum.setTwo(10+1);
        dan.setTwo(12+2);
        joel.setTwo(15+5);
        me.setTwo(20+5);
        ArrayList<Pair<Integer, Integer>> riders = new ArrayList<>(List.of(bouncer, fluffy, stormy, dan, joel, me, dad, mum));
        int[] rankedRiderIds = portal.getRidersRankInStage(stageId);
        int[] expectedRankedPoints = Arrays.stream(rankedRiderIds)
                .map(riderId -> riders.stream().filter(pair -> riderId == pair.getOne()).findFirst().orElseThrow().getTwo()).toArray();
        assertArrayEquals(expectedRankedPoints, rankedPoints);
    }
    @org.junit.jupiter.api.Test
    void concludeStagePreparation() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException {
        // arrange
        LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime spoonStartTime = eggStartTime.plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, eggStartTime, StageType.MEDIUM_MOUNTAIN);
        int spoonStageId = portal.addStageToRace(raceId, "Spoon",
                "Carry a spoon", 2.718 + 3, spoonStartTime, StageType.HIGH_MOUNTAIN);
        // act
        portal.concludeStagePreparation(spoonStageId);
        // assert
        assertThrows(InvalidStageStateException.class, () -> portal.concludeStagePreparation(spoonStageId));
    }
    @org.junit.jupiter.api.Test
    void eraseCyclingPortal() throws InvalidNameException, IllegalNameException {
        // arrange
        portal.createTeam("Apes", "Zoo escapees");
        portal.createRace("Egg&Spoon", "...on a bike");
        // act
        portal.eraseCyclingPortal();
        portal.createTeam("Apes", "Zoo escapees");
        portal.createRace("Egg&Spoon", "...on a bike");
        // assert
        assertArrayEquals(new int[] { 1 }, portal.getTeams());
        assertArrayEquals(new int[] { 2 }, portal.getRaceIds());
    }
    @org.junit.jupiter.api.Test
    void saveEraseLoadCyclingPortal() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException, IOException, ClassNotFoundException {
        // arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        int raceId = portal.createRace("Egg&Spoon", "...on a bike");
        int stageId = portal.addStageToRace(raceId, "Egg",
                "Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
        portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C4, 0.8, 5.0);
        portal.addCategorizedClimbToStage(stageId, 4.0, CheckpointType.C2, 0.2, 1.0);
        portal.addCategorizedClimbToStage(stageId, 1.25, CheckpointType.HC, 1.2, 6.0);
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
        int petsId = portal.createTeam("Humans", "Zookeepers");
        Pair<Integer, Integer> bouncer = new Pair<>(portal.createRider(petsId, "Bouncer", 1970), 0);
        Pair<Integer, Integer> fluffy= new Pair<>(portal.createRider(petsId, "Fluffy", 1970), 0);
        Pair<Integer, Integer> stormy = new Pair<>(portal.createRider(petsId, "Stormy", 1970), 0);
        Pair<Integer, Integer> dan = new Pair<>(portal.createRider(teamId, "Daniel", 1999), 0);
        Pair<Integer, Integer> joel = new Pair<>(portal.createRider(teamId, "Joel", 2001), 0);
        Pair<Integer, Integer> me = new Pair<>(portal.createRider(teamId, "Marcus", 2004), 0);
        Pair<Integer, Integer> dad = new Pair<>(portal.createRider(parentsId, "Tim", 1970), 0);
        Pair<Integer, Integer> mum = new Pair<>(portal.createRider(parentsId, "Annie", 1973), 0);
        LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(237), start.plusMinutes(400), start.plusMinutes(557), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(236), start.plusMinutes(415), start.plusMinutes(556), start.plusMinutes(600).plusSeconds(2) });
        LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(235), start.plusMinutes(385), start.plusMinutes(555), start.plusMinutes(600).minusSeconds(6) });
        LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(234), start.plusMinutes(400), start.plusMinutes(554), start.plusMinutes(600).minusSeconds(4) });
        LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(233), start.plusMinutes(400), start.plusMinutes(553), start.plusMinutes(600).minusSeconds(2) });
        LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(232), start.plusMinutes(400), start.plusMinutes(552), start.plusMinutes(600).minusSeconds(1) });
        LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(231), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600) });
        LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[] { start, start.plusMinutes(229), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600).plusSeconds(3) });
        portal.concludeStagePreparation(stageId);
        portal.registerRiderResultsInStage(stageId, bouncer.getOne(), bouncerCriticalTimes);
        portal.registerRiderResultsInStage(stageId, fluffy.getOne(), fluffyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dan.getOne(), danCriticalTimes);
        portal.registerRiderResultsInStage(stageId, dad.getOne(), dadCriticalTimes);
        portal.registerRiderResultsInStage(stageId, joel.getOne(), joelCriticalTimes);
        portal.registerRiderResultsInStage(stageId, stormy.getOne(), stormyCriticalTimes);
        portal.registerRiderResultsInStage(stageId, me.getOne(), myCriticalTimes);
        portal.registerRiderResultsInStage(stageId, mum.getOne(), mumCriticalTimes);
        // act
        portal.saveCyclingPortal(filename);
        System.out.println(filename + " created.");
        portal.eraseCyclingPortal();
        portal.loadCyclingPortal(filename);
        // assert
    }
    private static LocalTime[] toLocalTimeArray(LocalDateTime[] times) {
        return Arrays.stream(times)
                .map(LocalDateTime::toLocalTime)
                .toArray(LocalTime[]::new);
    }
}