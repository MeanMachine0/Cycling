package cycling;

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
        int teamId = portal.createTeam("Ape", "Escapees from the zoo");
        assertEquals(1, teamId);
    }
    @org.junit.jupiter.api.Test
    void createTeam_EmptyTeamNameThrowsException() {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        assertThrows(InvalidNameException.class, () -> {
            portal.createTeam("", "Escapees from the zoo");
        });
    }

    @org.junit.jupiter.api.Test
    void removeTeam() {
    }

    @org.junit.jupiter.api.Test
    void getTeams_ThreeTeams() throws InvalidNameException, IllegalNameException {
        MiniCyclingPortal portal = new CyclingPortalImpl();
        portal.createTeam("Ape", "Escapees from the zoo");
        portal.createTeam("Chimp", "Escapees from the zoo");
        portal.createTeam("Egg", "Escapees from the zoo");
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
}