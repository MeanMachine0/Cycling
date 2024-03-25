package cycling;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
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
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
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
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
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
			ArrayList<Entity> stages = new ArrayList<>(((Race) race).getChildren());
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
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
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
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		if (checkpoints.length != stage.numCriticalPoints()) throw new InvalidCheckpointTimesException();
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		stage.addResult(rider, checkpoints);
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		Optional<LocalTime[]> riderResults = Optional.ofNullable(stage.getResults().get(rider));
		return riderResults.orElseGet(() -> new LocalTime[0]);
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		Map<Rider, LocalTime[]> results = stage.getResults();
		Optional<LocalTime[]> optionalRiderResults = Optional.ofNullable(results.get(rider));
		if (optionalRiderResults.isEmpty()) return null;
		LocalTime[] riderResults = optionalRiderResults.get();
		LocalTime riderStart = riderResults[0];
		LocalTime riderEnd = riderResults[riderResults.length - 1];
		Duration adjustedElapsedTime;
		if (stage.getType() == StageType.TT) {
			adjustedElapsedTime = Duration.between(riderStart, riderEnd);
		}
		else {
			Duration comparisonWindow = Duration.ofSeconds(results.size());
			Duration oneSecond = Duration.ofSeconds(1);
			LocalTime[] ends = results.values().stream()
					.map(times -> times[times.length - 1])
					.filter(time -> {
						Duration difference = Duration.between(time, riderEnd);
						if (difference.isNegative()) {
							return difference.abs().compareTo(Duration.ofDays(1).minus(comparisonWindow)) >= 0;
						}
						return difference.compareTo(comparisonWindow) <= 0;
					})
					.toArray(LocalTime[]::new);
			LocalTime[] beforeEnds = Arrays.stream(ends)
					.filter(end -> end.isBefore(riderEnd))
					.sorted(Comparator.reverseOrder())
					.toArray(LocalTime[]::new);
			LocalTime adjustedRiderEnd = riderEnd;
			for (LocalTime end : beforeEnds) {
				Duration difference = Duration.between(end, adjustedRiderEnd);
				if (difference.compareTo(oneSecond) <= 0) {
					adjustedRiderEnd = end;
				} else {
					break;
				}
			}
			LocalTime[] afterEnds = Arrays.stream(ends)
					.filter(end -> end.isAfter(riderEnd))
					.sorted(Comparator.reverseOrder())
					.toArray(LocalTime[]::new);
			for (LocalTime end : afterEnds) {
				Duration difference = Duration.between(adjustedRiderEnd, end);
 				if (difference.abs().compareTo(oneSecond) <= 0 ||
						difference.abs().compareTo(Duration.ofDays(1).minus(oneSecond)) >= 0) {
					 adjustedRiderEnd = end;
				} else {
					 break;
				}
			}
			if (riderStart.isBefore(adjustedRiderEnd)) {
				adjustedElapsedTime = Duration.between(riderStart, adjustedRiderEnd);
			} else {
				adjustedElapsedTime = Duration.between(riderStart, LocalTime.MAX)
						.plus(Duration.ofNanos(1))
						.plus(Duration.between(LocalTime.MIDNIGHT, adjustedRiderEnd));
			}
		}
		int hours = adjustedElapsedTime.toHoursPart();
		int minutes = adjustedElapsedTime.toMinutesPart();
		int seconds = adjustedElapsedTime.toSecondsPart();
		int nanoseconds = adjustedElapsedTime.toNanosPart();
		return LocalTime.of(hours, minutes, seconds, nanoseconds);
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		stage.getResults().remove(rider);
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
	private <T extends Entity> ArrayList<T> narrow(ArrayList<Entity> entities, Class<T> subEntity) {
		ArrayList<T> subEntities = new ArrayList<>();
		for (Entity entity : entities) {
			subEntities.add(subEntity.cast(entity));
		}
		return subEntities;
	}
	private Optional<Entity> getEntity(int id, ArrayList<Entity> entities) {
		return entities.stream().filter(entity -> entity.id == id).findFirst();
	}
	private <T extends Entity> T getEntity(int id, ArrayList<? extends HasChildren> entities, Class<T> subEntityClass) throws IDNotRecognisedException {
		for (HasChildren entity : entities) {
			ArrayList<Entity> entityChildren = new ArrayList<>(entity.getChildren());
			Optional<Entity> optionalEntity = getEntity(id, entityChildren);
			if (optionalEntity.isPresent()) return subEntityClass.cast(optionalEntity.get());
		}
		throw new IDNotRecognisedException();
	}
}
