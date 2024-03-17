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
        MiniCyclingPortal portal1 = new CyclingPortalImpl();
        int teamId = portal1.createTeam("Ape", "Escapees from the zoo");
        assertEquals(1, teamId);
    }
    @org.junit.jupiter.api.Test
    void createTeam_EmptyTeamNameThrowsException() {
        MiniCyclingPortal portal1 = new CyclingPortalImpl();
        assertThrows(InvalidNameException.class, () -> {
            portal1.createTeam("", "Escapees from the zoo");
        });
    }

    @org.junit.jupiter.api.Test
    void removeTeam() {
    }

    @org.junit.jupiter.api.Test
    void getTeams() {
    }
}