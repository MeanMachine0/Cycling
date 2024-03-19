package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Egg
 * @author Marcus Carter
 */
public class CyclingPortalImpl implements MiniCyclingPortal {
	private int nextRaceId = 1;
	private int nextStageId = 1;
	private int nextTeamId = 1;
	private int nextRiderId = 1;
	private final ArrayList<Entity> teams = new ArrayList<>();
	private final ArrayList<Entity> races = new ArrayList<>();

	@Override
	public int[] getRaceIds() {
		return races.stream().mapToInt(Entity::getId).toArray();
	}

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		validateName(races, name);
		Race race = new Race(nextRaceId++, name, description);
		races.add(race);
		return race.getId();
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(races, raceId).orElseThrow(IDNotRecognisedException::new);
		return race.toString();
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		if (races.removeIf(race -> race.getId() == raceId)) return;
		throw new IDNotRecognisedException();
	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(races, raceId).orElseThrow(IDNotRecognisedException::new);
		return race.getStages().size();
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
							  StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		validateName(races, stageName);
		Entity race = getEntity(races, raceId).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Stage> stages = ((Race) race).getStages();
		Stage stage = new Stage(nextStageId, stageName, description, length, startTime, type);
		stages.add(stage);
		return nextStageId++;
	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(races, raceId).orElseThrow(IDNotRecognisedException::new);
		return race.getStages().stream().mapToInt(Stage::getId).toArray();
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		for (Entity race : races) {
			ArrayList<Stage> stages = ((Race) race).getStages();
			Optional<Stage> optionalStage = stages.stream()
					.filter(stage -> stage.getId() == stageId)
					.findFirst();
			if (optionalStage.isPresent()) return optionalStage.get().getLength();
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		validateName(teams, name);
		Entity team = new Team(nextTeamId++, name, description);
		teams.add(team);
		return team.getId();
	}

	private static void validateName(ArrayList<Entity> entities, String name) throws InvalidNameException, IllegalNameException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException();
		}
		for (char c : name.toCharArray()) {
			if (Character.isWhitespace(c)) {
				throw new InvalidNameException();
			}
		}
		for (Entity entity : entities) {
			if (entity.getName().equals(name)) throw new IllegalNameException();
		}
	}

	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		boolean teamExists = !teams.removeIf(entity -> entity.getId() == teamId);
		if (teamExists) throw new IDNotRecognisedException();
	}

	@Override
	public int[] getTeams() {
        return teams.stream().mapToInt(Entity::getId).toArray();
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		Team team = (Team) getEntity(teams, teamId).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Rider> riders = team.getRiders();
        return riders.stream().mapToInt(Rider::getId).toArray();
	}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		Team team = (Team) getEntity(teams, teamID).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Rider> riders = team.getRiders();
		Rider rider = new Rider(nextRiderId++, name, yearOfBirth);
		riders.add(rider);
		return rider.getId();
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
		for (Entity team : teams) {
			ArrayList<Rider> riders = ((Team) team).getRiders();
			if (riders.removeIf(rider -> rider.getId() == riderId)) return;
		}
		throw new IDNotRecognisedException();
	}
	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eraseCyclingPortal() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}
	// HELPER METHODS:
	private Optional<Entity> getEntity(ArrayList<Entity> entities, int id) {
		return entities.stream().filter(entity -> entity.getId() == id).findFirst();
	}
}
