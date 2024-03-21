package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Egg
 * @author Marcus Carter
 */
public class CyclingPortalImpl implements MiniCyclingPortal {
	private int nextId = 1;
	private final ArrayList<Entity> teams = new ArrayList<>();
	private final ArrayList<Entity> races = new ArrayList<>();

	@Override
	public int[] getRaceIds() {
		return races.stream().mapToInt(Entity::getId).toArray();
	}

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		validateName(races, name);
		Race race = new Race(nextId++, name, description);
		races.add(race);
		return race.id;
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(raceId, races).orElseThrow(IDNotRecognisedException::new);
		return race.toString();
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		if (races.removeIf(race -> race.id == raceId)) return;
		throw new IDNotRecognisedException();
	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(raceId, races).orElseThrow(IDNotRecognisedException::new);
		return race.getChildren().size();
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
							  StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		validateName(races, stageName);
		if (length < 5) throw new InvalidLengthException();
		Entity race = getEntity(raceId, races).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Stage> stages = ((Race) race).getChildren();
		Stage stage = new Stage(nextId++, stageName, description, length, startTime, type);
		stages.add(stage);
		return stage.id;
	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		Race race = (Race) getEntity(raceId, races).orElseThrow(IDNotRecognisedException::new);
		return race.getChildren().stream().mapToInt(Stage::getId).toArray();
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		return stage.getLength();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		for (Entity race : races) {
			ArrayList<Stage> stages = ((Race) race).getChildren();
			if (stages.removeIf(stage -> stage.id == stageId)) return;
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
		InvalidStageTypeException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		if (location > stage.getLength()) throw new InvalidLocationException();
		if (stage.getType() == StageType.TT) throw new InvalidStageTypeException();
		Climb checkpoint = new Climb(nextId++, "", type, location, averageGradient, length);
		ArrayList<Checkpoint> checkpoints = stage.getChildren();
		checkpoints.add(checkpoint);
		return checkpoint.id;
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		if (location > stage.getLength()) throw new InvalidLocationException();
		if (stage.getType() == StageType.TT) throw new InvalidStageTypeException();
		Checkpoint checkpoint = new Checkpoint(nextId++, "", CheckpointType.SPRINT, location);
		ArrayList<Checkpoint> checkpoints = stage.getChildren();
		checkpoints.add(checkpoint);
		return checkpoint.id;
	}

	@Override
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		for (Entity race : races) {
			ArrayList<Entity> stages = objectsToEntities(((Race) race).getChildren());
			for (Entity stage : stages) {
				ArrayList<Checkpoint> checkpoints = ((Stage) stage).getChildren();
				if (checkpoints.removeIf(checkpoint -> checkpoint.id == checkpointId)) return;
			}
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		return stage.getChildren().stream().mapToInt(Checkpoint::getId).toArray();
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		validateName(teams, name);
		Entity team = new Team(nextId++, name, description);
		teams.add(team);
		return team.id;
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
		if (teams.removeIf(team -> team.id == teamId)) return;
		throw new IDNotRecognisedException();
	}

	@Override
	public int[] getTeams() {
        return teams.stream().mapToInt(Entity::getId).toArray();
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		Team team = (Team) getEntity(teamId, teams).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Rider> riders = team.getChildren();
        return riders.stream().mapToInt(Rider::getId).toArray();
	}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		Team team = (Team) getEntity(teamID, teams).orElseThrow(IDNotRecognisedException::new);
		ArrayList<Rider> riders = team.getChildren();
		Rider rider = new Rider(nextId++, name, yearOfBirth);
		riders.add(rider);
		return rider.id;
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
		for (Entity team : teams) {
			ArrayList<Rider> riders = ((Team) team).getChildren();
			if (riders.removeIf(rider -> rider.id == riderId)) return;
		}
		throw new IDNotRecognisedException();
	}
	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		if (checkpoints.length != stage.numCriticalPoints()) throw new InvalidCheckpointTimesException();
		for (Entity team : teams) {
			ArrayList<Entity> riders = objectsToEntities(((Team) team).getChildren());
			Optional<Entity> optionalRider = getEntity(riderId, riders);
			if (optionalRider.isPresent()) {
				Rider rider = (Rider) optionalRider.get();
				stage.addResult(rider, checkpoints);
				return;
			}
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntityOrThrow(stageId, entitiesToSubEntities(races, Race.class));
		for (Entity team : teams) {
			ArrayList<Entity> riders = objectsToEntities(((Team) team).getChildren());
			Optional<Entity> optionalRider = getEntity(riderId, riders);
			if (optionalRider.isPresent()) {
				Rider rider = (Rider) optionalRider.get();
				return stage.getResults().get(rider);
			}
		}
		throw new IDNotRecognisedException();
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
	private ArrayList<Entity> objectsToEntities(ArrayList<?> objects) {
		ArrayList<Entity> entities = new ArrayList<>();
		for (Object object : objects) {
			if (!(object instanceof Entity)) return new ArrayList<Entity>();
			Entity entity = (Entity) object;
			entities.add(entity);
		}
		return entities;
	}
	private <T extends Entity> ArrayList<T> entitiesToSubEntities(ArrayList<Entity> entities, Class<T> subEntity) {
		ArrayList<T> subEntities = new ArrayList<>();
		for (Entity entity : entities) {
			if (!(subEntity.isInstance(entity))) return new ArrayList<T>();
			subEntities.add(subEntity.cast(entity));
		}
		return subEntities;
	}
	private Optional<Entity> getEntity(int id, ArrayList<Entity> entities) {
		return entities.stream().filter(entity -> entity.id == id).findFirst();
	}
	private <T extends Entity> T getEntityOrThrow(int id, ArrayList<? extends HasChildren> entities) throws IDNotRecognisedException {
		for (HasChildren entity : entities) {
			ArrayList<Entity> entityChildren = objectsToEntities(entity.getChildren());
			Optional<Entity> optionalEntity = getEntity(id, entityChildren);
			if (optionalEntity.isPresent()) {
                return (T) optionalEntity.get();
			}
		}
		throw new IDNotRecognisedException();
	}
}
