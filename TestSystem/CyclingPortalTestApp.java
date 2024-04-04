import cycling.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CyclingPortalTestApp {
	static final String filename = "miniCyclingPortal.ser";
	public static void main(String[] args) throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException, IOException, ClassNotFoundException {
		MiniCyclingPortal portal = new CyclingPortalImpl();
		File file = new File(filename);
		if (file.exists()) {
			System.out.println("Loading portal...");
			portal.loadCyclingPortal(filename);
			System.out.println("Loaded portal.");
			System.out.println(Arrays.toString(portal.getRidersPointsInStage(2)));
			System.out.println(portal.getNumberOfStages(1));
		}
		else {
			LocalDateTime start = LocalDateTime.now().plusDays(1);
			int raceId = portal.createRace("Egg&Spoon", "...on a bike");
			int stageId = portal.addStageToRace(raceId, "Egg",
					"Carry an egg", 3.141 + 3, start, StageType.MEDIUM_MOUNTAIN);
			System.out.println(portal.viewRaceDetails(raceId));
			portal.addCategorizedClimbToStage(stageId, 3.0, CheckpointType.C2, 0.8, 5.0);
			portal.addIntermediateSprintToStage(stageId, 4);
			portal.addIntermediateSprintToStage(stageId, 1.25);
			int teamId = portal.createTeam("Apes", "Zoo escapees");
			int parentsId = portal.createTeam("Great_Apes", "The founding zoo escapees");
			int petsId = portal.createTeam("Humans", "Zookeepers");
			Pair<Integer, Integer> bouncer = new Pair<>(portal.createRider(petsId, "Bouncer", 1970), 0);
			Pair<Integer, Integer> fluffy = new Pair<>(portal.createRider(petsId, "Fluffy", 1970), 0);
			Pair<Integer, Integer> stormy = new Pair<>(portal.createRider(petsId, "Stormy", 1970), 0);
			Pair<Integer, Integer> dan = new Pair<>(portal.createRider(teamId, "Daniel", 1999), 0);
			Pair<Integer, Integer> joel = new Pair<>(portal.createRider(teamId, "Joel", 2001), 0);
			Pair<Integer, Integer> me = new Pair<>(portal.createRider(teamId, "Marcus", 2004), 0);
			Pair<Integer, Integer> dad = new Pair<>(portal.createRider(parentsId, "Tim", 1970), 0);
			Pair<Integer, Integer> mum = new Pair<>(portal.createRider(parentsId, "Annie", 1973), 0);
			LocalTime[] bouncerCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(237), start.plusMinutes(400), start.plusMinutes(557), start.plusMinutes(600).plusSeconds(2)});
			LocalTime[] fluffyCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(236), start.plusMinutes(415), start.plusMinutes(556), start.plusMinutes(600).plusSeconds(2)});
			LocalTime[] stormyCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(235), start.plusMinutes(385), start.plusMinutes(555), start.plusMinutes(600).minusSeconds(6)});
			LocalTime[] dadCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(234), start.plusMinutes(400), start.plusMinutes(554), start.plusMinutes(600).minusSeconds(4)});
			LocalTime[] mumCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(233), start.plusMinutes(400), start.plusMinutes(553), start.plusMinutes(600).minusSeconds(2)});
			LocalTime[] danCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(232), start.plusMinutes(400), start.plusMinutes(552), start.plusMinutes(600).minusSeconds(1)});
			LocalTime[] joelCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(231), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600)});
			LocalTime[] myCriticalTimes = toLocalTimeArray(new LocalDateTime[]{start, start.plusMinutes(229), start.plusMinutes(400), start.plusMinutes(551), start.plusMinutes(600).plusSeconds(3)});
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
			bouncer.setTwo(8 + 8 + 15);
			fluffy.setTwo(9 + 9 + 15);
			stormy.setTwo(10 + 10 + 30);
			dad.setTwo(11 + 11 + 25);
			mum.setTwo(13 + 13 + 22);
			dan.setTwo(15 + 15 + 19);
			joel.setTwo(17 + 20 + 17);
			me.setTwo(20 + 20 + 11);
			ArrayList<Pair<Integer, Integer>> riders = new ArrayList<>(List.of(bouncer, fluffy, stormy, dan, joel, me, dad, mum));
			int[] rankedRiderIds = portal.getRidersRankInStage(stageId);
			int[] expectedRankedPoints = Arrays.stream(rankedRiderIds)
					.map(riderId -> riders.stream().filter(pair -> riderId == pair.getOne()).findFirst().orElseThrow().getTwo()).toArray();
			if (Arrays.equals(expectedRankedPoints, rankedPoints)) System.out.println(Arrays.toString(rankedPoints));
			portal.saveCyclingPortal(filename);
			portal.eraseCyclingPortal();
			portal.loadCyclingPortal(filename);
			rankedPoints = portal.getRidersPointsInStage(stageId);
			if (Arrays.equals(expectedRankedPoints, rankedPoints)) System.out.println(Arrays.toString(rankedPoints));
		}
	}
	private static LocalTime[] toLocalTimeArray(LocalDateTime[] times) {
		return Arrays.stream(times)
				.map(LocalDateTime::toLocalTime)
				.toArray(LocalTime[]::new);
	}
}