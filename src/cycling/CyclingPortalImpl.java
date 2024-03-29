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
		validateStageState(stage);
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
		validateStageState(stage);
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
				Optional<Checkpoint> optionalCheckpoint = checkpoints.stream()
						.filter(checkpoint -> checkpoint.id == checkpointId)
						.findFirst();
				if (optionalCheckpoint.isPresent()) {
					validateStageState((Stage) stage);
					checkpoints.remove(optionalCheckpoint.get());
					return;
				}
			}
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		validateStageState(stage);
		stage.setState("waiting for results");
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

	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		Team team = (Team) getEntity(teamId, teams).orElseThrow(IDNotRecognisedException::new);
		for (Rider rider : team.getChildren()) removeRiderResults(rider);
		teams.remove(team);
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
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		removeRiderResults(rider);
		for (Entity team : teams) {
			if (((Team) team).getChildren().remove(rider)) return;
		}
		throw new IDNotRecognisedException();
	}

	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		validateStageState(stage);
		if (checkpoints.length != stage.numCriticalPoints()) throw new InvalidCheckpointTimesException();
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		stage.addResult(rider, checkpoints);
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		Optional<LocalTime[]> optionalRiderCriticalTimes = Optional.ofNullable(stage.getResults().get(rider));
		if (optionalRiderCriticalTimes.isEmpty()) return new LocalTime[0];
		ArrayList<LocalTime> riderCriticalTimes = new ArrayList<>(List.of(optionalRiderCriticalTimes.get()));
		LocalTime start = riderCriticalTimes.remove(0);
		LocalTime end = riderCriticalTimes.remove(riderCriticalTimes.size() - 1);
		riderCriticalTimes.add(timeElapsed(start, end));
		return riderCriticalTimes.toArray(LocalTime[]::new);
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
		if (stage.getType() == StageType.TT) return timeElapsed(riderStart, riderEnd);
		Duration comparisonWindow = Duration.ofSeconds(results.size());
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
		Duration oneSecond = Duration.ofSeconds(1);
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
		return timeElapsed(riderStart, adjustedRiderEnd);
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		stage.getResults().remove(rider);
	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Map<Rider, LocalTime[]> results = stage.getResults();
		return results.entrySet().stream()
				.map(entry -> {
					int riderId = entry.getKey().id;
					LocalTime[] times = entry.getValue();
					LocalTime timeElapsed = timeElapsed(times[0], times[times.length - 1]);
					return new AbstractMap.SimpleEntry<>(riderId, timeElapsed);
				})
				.sorted(Map.Entry.comparingByValue())
				.mapToInt(Map.Entry::getKey)
				.toArray();
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		ArrayList<AbstractMap.SimpleEntry<LocalTime, LocalTime>> elapsedAdjustedElapsedTimes = new ArrayList<>();
		for (Map.Entry<Rider, LocalTime[]> entry : stage.getResults().entrySet()) {
			LocalTime[] times = entry.getValue();
			LocalTime elapsedTime = timeElapsed(times[0], times[times.length - 1]);
			LocalTime adjustedElapsedTime = getRiderAdjustedElapsedTimeInStage(stageId, entry.getKey().id);
			elapsedAdjustedElapsedTimes.add(new AbstractMap.SimpleEntry<>(elapsedTime, adjustedElapsedTime));
		}
		return elapsedAdjustedElapsedTimes.stream()
				.sorted(Map.Entry.comparingByValue())
				.map(AbstractMap.SimpleEntry::getValue)
				.toArray(LocalTime[]::new);
    }

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Map<Rider, LocalTime[]> results = stage.getResults();
		ArrayList<Checkpoint> checkpoints = stage.getChildren();
		int[] rankedRiderIds = getRidersRankInStage(stageId);
		Checkpoint[] sortedCheckpoints = checkpoints.stream()
				.sorted(Comparator.comparingDouble(Checkpoint::getLocation))
				.toArray(Checkpoint[]::new);
		ArrayList<AbstractMap.SimpleEntry<Checkpoint, Integer>> sprints = new ArrayList<>();
		for (int i = 0; i < sortedCheckpoints.length; i++) {
			Checkpoint checkpoint = sortedCheckpoints[i];
			if (checkpoint.type.equals(CheckpointType.SPRINT)) {
				sprints.add(new AbstractMap.SimpleEntry<>(checkpoint, i + 1));
			}
		}
		ArrayList<AbstractMap.SimpleEntry<Rider, Pair<ArrayList<LocalTime>, Integer>>> riderMaps = new ArrayList<>();
        for (int rankedRiderId : rankedRiderIds) {
            Rider rider = getEntity(rankedRiderId, narrow(teams, Team.class), Rider.class);
            LocalTime[] times = results.get(rider);
            LocalTime elapsedTime = timeElapsed(times[0], times[times.length - 1]);
            ArrayList<LocalTime> relevantTimes = new ArrayList<>();
            relevantTimes.add(elapsedTime);
            for (AbstractMap.SimpleEntry<Checkpoint, Integer> sprint : sprints) {
                int sprintIndex = sprint.getValue();
                LocalTime end = times[sprintIndex];
                relevantTimes.add(end);
            }
            riderMaps.add(new AbstractMap.SimpleEntry<>(rider, new Pair<>(relevantTimes, 0)));
        }
		ArrayList<LocalTime> rankedElapsedTimes = (ArrayList<LocalTime>) List.of(riderMaps.stream()
				.map(entry -> entry.getValue().getOne().get(0))
				.sorted(Comparator.reverseOrder())
				.toArray(LocalTime[]::new));
		ArrayList<ArrayList<LocalTime>> sprintsRankedEnds = new ArrayList<>();
		for (AbstractMap.SimpleEntry<Rider, Pair<ArrayList<LocalTime>, Integer>> riderMap : riderMaps) {
			ArrayList<LocalTime> times = riderMap.getValue().getOne();
			if (times.size() > 1) {
				for (int i = 1; i < sprints.size() + 1; i++) {
					final int finalI = i;
					sprintsRankedEnds.add((ArrayList<LocalTime>) List.of(riderMaps
							.stream()
							.map(entry -> new Pair<>(entry.getValue().getOne().get(finalI),))
							.sorted(Comparator.reverseOrder())
							.toArray(LocalTime[]::new)));
				}
			}
		}
        for (AbstractMap.SimpleEntry<Rider, Pair<ArrayList<LocalTime>, Integer>> riderMap : riderMaps) {
			Pair<ArrayList<LocalTime>, Integer> timesPoints = riderMap.getValue();
            ArrayList<LocalTime> times = timesPoints.getOne();
            LocalTime elapsedTime = times.get(0);
            int finishingPosition = rankedElapsedTimes.indexOf(elapsedTime);
            if (finishingPosition < 15) {
				timesPoints.setTwo(Stage.SPRINTER_POINTS.get(stage.getType()).get(finishingPosition));
            }
            if (times.size() > 1) {
				for (int i = 0; i < sprints.size(); i++) {
					LocalTime sprintElapsedTime = riderMap.getValue().getOne().get(i + 1);
					ArrayList<LocalTime> sprintRankedEnds = sprintsRankedEnds.get(i);
					int sprintFinishingPosition = sprintRankedEnds.indexOf(sprintElapsedTime);
					if (sprintFinishingPosition < 15) {
						Integer currentPoints = timesPoints.getTwo();
						timesPoints.setTwo(currentPoints +
								Checkpoint.INTERMEDIATE_SPRINT_POINTS[sprintFinishingPosition]);
					}
				}
			}
        }
		return riderMaps.stream()
				.map(entry -> new Pair<>(entry.getValue().getOne().get(0), entry.getValue().getTwo()))
				.sorted(Comparator.comparing((Pair<LocalTime, Integer> pair) -> pair.getOne()).reversed())
				.mapToInt(Pair::getTwo)
				.toArray();
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
	private LocalTime timeElapsed(LocalTime start, LocalTime end) {
		Duration elapsedTime;
		if (start.isBefore(end)) {
			elapsedTime = Duration.between(start, end);
		} else {
			elapsedTime = Duration.between(start, LocalTime.MAX)
					.plus(Duration.ofNanos(1))
					.plus(Duration.between(LocalTime.MIDNIGHT, end));
		}
		int hours = elapsedTime.toHoursPart();
		int minutes = elapsedTime.toMinutesPart();
		int seconds = elapsedTime.toSecondsPart();
		int nanoseconds = elapsedTime.toNanosPart();
		return LocalTime.of(hours, minutes, seconds, nanoseconds);
	}
	private void removeRiderResults(Rider rider) {
		for (Entity race : races) {
			ArrayList<Stage> stages = ((Race) race).getChildren();
			for (Stage stage : stages) {
				stage.getResults().remove(rider);
			}
		}
	}
	private void validateName(ArrayList<Entity> entities, String name) throws InvalidNameException, IllegalNameException {
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
	private void validateStageState(Stage stage) throws InvalidStageStateException {
		String currentState = stage.getState();
		if (currentState.equals("waiting for results")) throw new InvalidStageStateException();
	}
}
