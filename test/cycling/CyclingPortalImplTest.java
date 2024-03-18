package cycling;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CyclingPortalImplTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void createTeam_oneValidTeam() throws InvalidNameException, IllegalNameException {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        int teamId = portal.createTeam("Ape", "Zoo escapees");
        assertEquals(1, teamId);
    }
    @org.junit.jupiter.api.Test
    void createTeam_emptyTeamNameThrowsException() {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        assertThrows(InvalidNameException.class, () -> {
            portal.createTeam("", "Zoo escapees");
        });
    }
    @org.junit.jupiter.api.Test
    void removeTeam_idDoesNotExistThrowsException() {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        assertThrows(IDNotRecognisedException.class, () -> {
            portal.removeTeam(1);
        });
    }
//    @org.junit.jupiter.api.Test
//    void removeTeam_oneTeam() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
//        MiniCyclingPortal portal = new CyclingPortalImpl();
//        int teamId = portal.createTeam("Ape", "Escapees from the zoo");
//        assert portal.getTeams(teamId) == teamId;
//    }
    @org.junit.jupiter.api.Test
    void getTeams_ThreeTeams() throws InvalidNameException, IllegalNameException {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        portal.createTeam("Ape", "Zoo escapees");
        portal.createTeam("Chimp", "Zoo escapees");
        portal.createTeam("Egg", "Zoo escapees");
        assertEquals(3, portal.getTeams().length);
        assertEquals(1, portal.getTeams()[0]);
        assertEquals(2, portal.getTeams()[1]);
        assertEquals(3, portal.getTeams()[2]);
    }
    @org.junit.jupiter.api.Test
    void getTeams_Empty() {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        assertEquals(0, portal.getTeams().length);
    }
   @org.junit.jupiter.api.Test
    void createRider_simple() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        CyclingPortalImpl portal = new CyclingPortalImpl();
        int teamId = portal.createTeam("Apes", "Zoo escapees");
        int riderId = portal.createRider(teamId, "Daniel", 1999);
        int rider1Id = portal.createRider(teamId, "Joel", 2001);
        int rider2Id = portal.createRider(teamId, "Marcus", 2004);
        Team zooEscapees = portal.getTeam(teamId).orElseThrow();
        ArrayList<Rider> apes = zooEscapees.getRiders();
        System.out.println(apes);
        portal.removeRider(rider1Id);
        System.out.println(apes);
   }
}