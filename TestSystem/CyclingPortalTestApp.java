import cycling.*;

import java.time.LocalDateTime;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortal interface -- note you
 * will want to increase these checks, and run it on your CyclingPortalImpl class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Diogo Pacheco
 * @version 2.0
 */
public class CyclingPortalTestApp {

	/**
	 * Test method.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException {
		MiniCyclingPortal portal = new CyclingPortalImpl();
		LocalDateTime eggStartTime = LocalDateTime.now().plusDays(1);
		LocalDateTime spoonStartTime = eggStartTime.plusDays(1);
		int raceId = portal.createRace("Egg&Spoon", "...on a bike");
		int eggStageId = portal.addStageToRace(raceId, "Egg",
				"Carry an egg", 3.141, eggStartTime, StageType.MEDIUM_MOUNTAIN);
		int spoonStageId = portal.addStageToRace(raceId, "Spoon",
				"Carry a spoon", 2.718, spoonStartTime, StageType.HIGH_MOUNTAIN);
		System.out.println(portal.viewRaceDetails(raceId));
	}
}