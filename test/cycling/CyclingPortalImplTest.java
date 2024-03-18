package cycling;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CyclingPortalImplTest {
    CyclingPortalImpl portal;
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
    void createTeam_emptyTeamNameThrowsException() {
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
        Team team = portal.getTeam(teamId).orElseThrow(IDNotRecognisedException::new);
        ArrayList<Rider> riders = team.getRiders();
        // act
        int riderId = portal.createRider(teamId, "Daniel", 1999);
        // assert
        assertEquals(riderId, riders.getFirst().getId());
    }
   @org.junit.jupiter.api.Test
    void removeRider() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        // arrange
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        portal.createRider(teamId, "Daniel", 1999);
        portal.createRider(teamId, "Joel", 2001);
        int marcusId = portal.createRider(teamId, "Marcus", 2004);
        Team zooEscapees = portal.getTeam(teamId).orElseThrow(IDNotRecognisedException::new);
        ArrayList<Rider> apes = zooEscapees.getRiders();
        // act
        portal.removeRider(2);
        // assert
        assertEquals(3, marcusId);
   }
}