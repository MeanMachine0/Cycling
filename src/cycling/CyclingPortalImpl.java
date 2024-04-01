package cycling;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		if (stage.isTimeTrial()) throw new InvalidStageTypeException();
		Checkpoint checkpoint = new Climb(nextId++, "", type, location, averageGradient, length);
		stage.addCheckpoint(checkpoint);
		return checkpoint.id;
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		validateStageState(stage);
		if (location > stage.getLength()) throw new InvalidLocationException();
		if (stage.isTimeTrial()) throw new InvalidStageTypeException();
		Checkpoint checkpoint = new Checkpoint(nextId++, "", CheckpointType.SPRINT, location);
		stage.addCheckpoint(checkpoint);
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
		if (name == null || name.isEmpty() || yearOfBirth < 1900) throw new IllegalArgumentException();
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
		stage.addResult(rider, Arrays.stream(checkpoints)
				.map(time -> {
					LocalDate date = stage.start.toLocalDate();
					return time.isBefore(stage.start.toLocalTime()) ?
							LocalDateTime.of(date.plusDays(1), time) :
							LocalDateTime.of(date, time);
				})
				.sorted(Comparator.naturalOrder())
				.toArray(LocalDateTime[]::new));
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		Optional<LocalDateTime[]> optionalRiderCriticalTimes = Optional.ofNullable(stage.getResults().get(rider));
		if (optionalRiderCriticalTimes.isEmpty()) return new LocalTime[0];
		ArrayList<LocalDateTime> riderCriticalTimes = new ArrayList<>(List.of(optionalRiderCriticalTimes.get()));
		LocalDateTime start = riderCriticalTimes.removeFirst();
		LocalDateTime end = riderCriticalTimes.removeLast();
		Duration elapsedTime = Duration.between(start, end);
		LocalTime elapsedTimeLocal = toLocalTime(elapsedTime);
		LocalTime[] results = riderCriticalTimes.stream()
				.sorted(Comparator.naturalOrder())
				.map(LocalDateTime::toLocalTime)
				.toArray(LocalTime[]::new);
		return Stream.concat(Arrays.stream(results), Stream.of(elapsedTimeLocal))
				.toArray(LocalTime[]::new);
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Rider rider = getEntity(riderId, narrow(teams, Team.class), Rider.class);
		Map<Rider, LocalDateTime[]> results = stage.getResults();
		Optional<LocalDateTime[]> optionalRiderResults = Optional.ofNullable(results.get(rider));
		if (optionalRiderResults.isEmpty()) return null;
		LocalDateTime[] riderResults = optionalRiderResults.get();
		LocalDateTime riderEnd = riderResults[riderResults.length - 1];
		if (stage.isTimeTrial()) return toLocalTime((stage.ttTimeElapsed(riderResults[0], riderEnd)));
		LocalDateTime[] sortedEnds = results.values().stream()
				.map(times -> times[times.length - 1])
				.filter(end -> end.isBefore(riderEnd))
				.sorted(Comparator.reverseOrder())
				.toArray(LocalDateTime[]::new);
		LocalDateTime adjustedRiderEnd = riderEnd;
		for (LocalDateTime end : sortedEnds) {
			LocalDateTime cutoff = end.plus(Duration.ofSeconds(1)).plusNanos(1);
			if (adjustedRiderEnd.isBefore(cutoff)) adjustedRiderEnd = end;
			else break;
		}
		return toLocalTime((stage.timeElapsed(adjustedRiderEnd)));
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
		Map<Rider, LocalDateTime[]> results = stage.getResults();
		return results.entrySet().stream()
				.map(entry -> {
					LocalDateTime[] times = entry.getValue();
					LocalDateTime riderEnd = times[times.length - 1];
					Duration elapsedTime;
					elapsedTime = stage.isTimeTrial() ?
							stage.ttTimeElapsed(times[0], riderEnd) :
							stage.timeElapsed(riderEnd);
					int riderId = entry.getKey().id;
					return new AbstractMap.SimpleEntry<>(riderId, elapsedTime);
				})
				.sorted(Map.Entry.comparingByValue())
				.mapToInt(Map.Entry::getKey)
				.toArray();
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		ArrayList<AbstractMap.SimpleEntry<LocalTime, LocalTime>> elapsedAdjustedElapsedTimes = new ArrayList<>();
		for (Map.Entry<Rider, LocalDateTime[]> entry : stage.getResults().entrySet()) {
			LocalDateTime[] times = entry.getValue();
			LocalDateTime riderEnd = times[times.length - 1];
			Duration elapsedTime;
			if (stage.isTimeTrial()) elapsedTime = stage.ttTimeElapsed(times[0], riderEnd);
			else elapsedTime = stage.timeElapsed(riderEnd);
			LocalTime adjustedElapsedTime = getRiderAdjustedElapsedTimeInStage(stageId, entry.getKey().id);
			elapsedAdjustedElapsedTimes.add(new AbstractMap.SimpleEntry<>(toLocalTime(elapsedTime), adjustedElapsedTime));
		}
		return elapsedAdjustedElapsedTimes.stream()
				.sorted(Map.Entry.comparingByValue())
				.map(AbstractMap.SimpleEntry::getValue)
				.toArray(LocalTime[]::new);
    }

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Map<Rider, LocalDateTime[]> results = stage.getResults();
		if (results.isEmpty()) return new int[0];
		int[] rankedRiderIds = getRidersRankInStage(stageId);
		ArrayList<Rider> rankedRiders = new ArrayList<>();
		for (int rankedRiderId : rankedRiderIds) {
			rankedRiders.add(getEntity(rankedRiderId, narrow(teams, Team.class), Rider.class));
		}
		ArrayList<Duration> elapsedTimes = rankedRiders.stream()
				.map(rider -> {
					LocalDateTime[] times = results.get(rider);
					return stage.isTimeTrial() ?
							stage.ttTimeElapsed(times[0], times[times.length - 1]) :
							stage.timeElapsed(times[times.length - 1]);
				})
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<Duration> rankedElapsedTimes = elapsedTimes.stream()
				.sorted(Comparator.naturalOrder())
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<Integer> stageFinishPoints = Stage.SPRINTER_POINTS.get(stage.type);
		int[] ridersSprinterPoints = elapsedTimes.stream()
			.mapToInt(elapsedTime -> {
				int riderFinishingPlace = rankedElapsedTimes.indexOf(elapsedTime);
				return riderFinishingPlace < stageFinishPoints.size() ?
						stageFinishPoints.get(riderFinishingPlace) :
						0;
			})
			.toArray();
		if (stage.isTimeTrial()) return ridersSprinterPoints;
		ArrayList<Checkpoint> checkpoints = stage.getChildren();
		ArrayList<Checkpoint> sprints = checkpoints.stream()
				.filter(checkpoint -> !(checkpoint instanceof Climb))
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<ArrayList<LocalDateTime>> sprintsTimes = rankedRiders.stream()
				.map(rider -> {
					LocalDateTime[] times = results.get(rider);
					ArrayList<LocalDateTime> relevantTimes = new ArrayList<>();
					for (Checkpoint sprint : sprints) relevantTimes.add(times[checkpoints.indexOf(sprint) + 1]);
					return relevantTimes;
				})
				.collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < sprints.size(); i++) {
			final int finalI = i;
			ArrayList<LocalDateTime> sprintTimes = sprintsTimes.stream()
					.map(times -> times.get(finalI))
					.collect(Collectors.toCollection(ArrayList::new));
			ArrayList<LocalDateTime> sprintRankedTimes = sprintTimes.stream()
					.sorted(Comparator.naturalOrder())
					.collect(Collectors.toCollection(ArrayList::new));
			int[] sprintPoints = sprintTimes.stream()
					.mapToInt(sprintTime -> {
						int riderFinishingPlace = sprintRankedTimes.indexOf(sprintTime);
						return riderFinishingPlace < Checkpoint.INTERMEDIATE_SPRINT_POINTS.length ?
								Checkpoint.INTERMEDIATE_SPRINT_POINTS[riderFinishingPlace] :
								0;
					})
					.toArray();
			for (int i1 = 0; i1 < ridersSprinterPoints.length; i1++) {
				ridersSprinterPoints[i1] = ridersSprinterPoints[i1] + sprintPoints[i1];
			}
		}
		return ridersSprinterPoints;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getEntity(stageId, narrow(races, Race.class), Stage.class);
		Map<Rider, LocalDateTime[]> results = stage.getResults();
		if (results.isEmpty()) return new int[0];
		int[] ridersMountainPoints;
		int[] rankedRiderIds = getRidersRankInStage(stageId);
		ridersMountainPoints = new int[rankedRiderIds.length];
		Arrays.fill(ridersMountainPoints, 0);
		if (stage.isTimeTrial()) return ridersMountainPoints;
		ArrayList<Rider> rankedRiders = new ArrayList<>();
		for (int rankedRiderId : rankedRiderIds) {
			rankedRiders.add(getEntity(rankedRiderId, narrow(teams, Team.class), Rider.class));
		}
		ArrayList<Checkpoint> checkpoints = stage.getChildren();
		ArrayList<Checkpoint> climbs = checkpoints.stream()
				.filter(checkpoint -> checkpoint instanceof Climb)
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<ArrayList<LocalDateTime>> climbsTimes = rankedRiders.stream()
				.map(rider -> {
					LocalDateTime[] times = results.get(rider);
					ArrayList<LocalDateTime> relevantTimes = new ArrayList<>();
					for (Checkpoint climb : climbs) relevantTimes.add(times[checkpoints.indexOf(climb) + 1]);
					return relevantTimes;
				})
				.collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < climbs.size(); i++) {
			final int finalI = i;
			ArrayList<LocalDateTime> climbTimes = climbsTimes.stream()
					.map(times -> times.get(finalI))
					.collect(Collectors.toCollection(ArrayList::new));
			ArrayList<LocalDateTime> climbRankedTimes = climbTimes.stream()
					.sorted(Comparator.naturalOrder())
					.collect(Collectors.toCollection(ArrayList::new));
			ArrayList<Integer> climbPoints = Climb.MOUNTAIN_POINTS.get(climbs.get(finalI).type);
			int[] pointsFromClimb = climbTimes.stream()
					.mapToInt(sprintTime -> {
						int riderFinishingPlace = climbRankedTimes.indexOf(sprintTime);
						return riderFinishingPlace < climbPoints.size() ?
								climbPoints.get(riderFinishingPlace) :
								0;
					})
					.toArray();
			for (int i1 = 0; i1 < ridersMountainPoints.length; i1++) {
				ridersMountainPoints[i1] = ridersMountainPoints[i1] + pointsFromClimb[i1];
			}
		}
		return ridersMountainPoints;
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
	private void removeRiderResults(Rider rider) {
		for (Entity race : races) {
			ArrayList<Stage> stages = ((Race) race).getChildren();
			for (Stage stage : stages) {
				stage.getResults().remove(rider);
			}
		}
	}
	private void validateName(ArrayList<Entity> entities, String name) throws InvalidNameException, IllegalNameException {
		if (name == null || name.isEmpty() || name.length() > 30) throw new InvalidNameException();
		for (char c : name.toCharArray()) {
			if (Character.isWhitespace(c)) {
				throw new InvalidNameException();
			}
		}
		for (Entity entity : entities) if (entity.getName().equals(name)) throw new IllegalNameException();
	}
	private void validateStageState(Stage stage) throws InvalidStageStateException {
		String currentState = stage.getState();
		if (currentState.equals("waiting for results")) throw new InvalidStageStateException();
	}
	private LocalTime toLocalTime(Duration duration) {
		int hours = duration.toHoursPart();
		int minutes = duration.toMinutesPart();
		int seconds = duration.toSecondsPart();
		int nanoseconds = duration.toNanosPart();
		return LocalTime.of(hours, minutes, seconds, nanoseconds);
	}
}
