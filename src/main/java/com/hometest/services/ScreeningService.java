package com.hometest.services;

import com.hometest.database.entities.ScreeningEntity;
import com.hometest.database.repositories.ScreeningRepository;
import com.hometest.dto.MovieDto;
import com.hometest.dto.ScreeningDto;
import com.hometest.exception.InvalidScreeningTimeRange;
import com.hometest.exception.MovieNotFoundException;
import com.hometest.exception.ScreeningNotFoundException;
import com.hometest.exception.ScreeningsOverLapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    public static DateTimeFormatter formatter;

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    /**
     * creates a new screening in the DB
     * @param dto - screening details
     * @return - created screening id
     * @throws ScreeningsOverLapException - when screening time over-laps with an existing screening in the same theater
     * @throws InvalidScreeningTimeRange - when there's no start/end time, or the start time is after the end-time
     */
    public long createScreening(ScreeningDto dto) throws ScreeningsOverLapException, InvalidScreeningTimeRange {
        validateTimeRange(dto, true);
        LocalDateTime startTime = LocalDateTime.parse(dto.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(dto.getEndTime(), formatter);
        validateScreeningsDontOverLap(dto.getTheaterId(), startTime, endTime);
        validateMovieTitle(dto.getMovieTitle());
        ScreeningEntity screeningEntity = ScreeningEntity.builder()
                .movieTitle(dto.getMovieTitle())
                .theaterId(dto.getTheaterId())
                .startTime(startTime)
                .endTime(endTime)
                .build();
        ScreeningEntity saved = screeningRepository.save(screeningEntity);
        return saved.getId();
    }

    /**
     * updates an existing screening
     * @param dto - the screening details
     * @throws ScreeningNotFoundException - screening not found
     * @throws ScreeningsOverLapException - when screening time over-laps with an existing screening in the same theater
     * @throws InvalidScreeningTimeRange - when there's no start/end time, or the start time is after the end-time
     */
    public void updateScreening(ScreeningDto dto) throws ScreeningNotFoundException, ScreeningsOverLapException, InvalidScreeningTimeRange {
        validateTimeRange(dto, false);
        ScreeningEntity screeningEntity = findScreeningById(dto.getId());
        boolean changed = false;
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (dto.getStartTime() != null) {
            startTime = LocalDateTime.parse(dto.getStartTime(), formatter);
            screeningEntity.setStartTime(startTime);
            changed = true;
        } else {
            startTime = screeningEntity.getStartTime();
        }
        if (dto.getEndTime() != null) {
            endTime = LocalDateTime.parse(dto.getEndTime(), formatter);
            screeningEntity.setEndTime(endTime);
            changed = true;
        } else {
            endTime = screeningEntity.getEndTime();
        }
        if (changed) {
            validateScreeningsDontOverLap(dto.getTheaterId(), startTime, endTime);
        }
        if (dto.getMovieTitle() != null) {
            validateMovieTitle(dto.getMovieTitle());
            screeningEntity.setMovieTitle(dto.getMovieTitle());
            changed = true;
        }
        if (dto.getTheaterId() != null) {
            screeningEntity.setTheaterId(dto.getTheaterId());
            changed = true;
        }
        if (changed)
            screeningRepository.save(screeningEntity);
    }

    /**
     * deletes a screening
     * @param id - the screening id
     * @throws ScreeningNotFoundException - screening not found
     */
    public void deleteScreening(Long id) throws ScreeningNotFoundException {
        ScreeningEntity screeningEntity = findScreeningById(id);
        screeningRepository.delete(screeningEntity);
    }

    /**
     * returns all the screening of a given title
     * @param movieTitle - the title to search for
     * @return - list of screenings details
     */
    public List<ScreeningDto> findScreeningByMovie(String movieTitle) {
        List<ScreeningEntity> screeningEntityList = screeningRepository.findByMovieTitle(movieTitle);
        return screeningEntityList.stream().map(this::convertToDto).toList();
    }

    /**
     * returns all screenings in a given theater
     * @param theaterId - the theater-id
     * @return - list of screenings details
     */
    public List<ScreeningDto> findScreeningByTheater(long theaterId) {
        List<ScreeningEntity> screeningEntityList = screeningRepository.findByTheaterId(theaterId);
        return screeningEntityList.stream().map(this::convertToDto).toList();
    }

    /**
     * find a screening by theater-id, show-time and a title
     * @param theaterId - the theater id
     * @param showTime - the show-time
     * @param movieTitle - the movie-title
     * @return screening details
     * @throws ScreeningNotFoundException - when no such screening is found
     */
    public ScreeningDto findScreeningByTheaterAndTime(long theaterId, String showTime, String movieTitle) throws ScreeningNotFoundException {
        ScreeningEntity screeningEntity = screeningRepository.findByStartTimeAndTheaterIdAndMovieTitle(LocalDateTime.parse(showTime, formatter), theaterId, movieTitle)
                .orElseThrow(() -> new ScreeningNotFoundException(movieTitle, showTime, theaterId));
        return convertToDto(screeningEntity);
    }


    /*-- private methods --*/
    private ScreeningEntity findScreeningById(Long id) throws ScreeningNotFoundException {
        return screeningRepository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
    }

    private void validateMovieTitle(String movieTitle) {
        MovieDto movieDto = movieService.findByTitle(movieTitle);
        if (movieDto == null) {
            throw new MovieNotFoundException(movieTitle);
        }
    }

    private ScreeningDto convertToDto(ScreeningEntity entity) {
        String startTime = entity.getStartTime().format(formatter);
        String endTime = entity.getEndTime().format(formatter);
        return ScreeningDto.builder()
                .id(entity.getId())
                .movieTitle(entity.getMovieTitle())
                .theaterId(entity.getTheaterId())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    // TODO - validate screening start time is not in the past;
    //      - validate screening time matches movie's duration
    private void validateTimeRange(ScreeningDto dto, boolean bothMandatory) throws InvalidScreeningTimeRange {
        if (bothMandatory && (dto.getStartTime() == null || dto.getEndTime() == null))
            throw new InvalidScreeningTimeRange("start-time and end-time are both required");
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (dto.getStartTime() != null) {
            try {
                startTime = LocalDateTime.parse(dto.getStartTime(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidScreeningTimeRange("invalid start-time: " + dto.getStartTime());
            }
        }
        if (dto.getEndTime() != null) {
            try {
                endTime = LocalDateTime.parse(dto.getEndTime(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidScreeningTimeRange("invalid end-time: " + dto.getEndTime());
            }
        }

        if (startTime != null && endTime != null && isAfterOrEqual(startTime, endTime))
            throw new InvalidScreeningTimeRange("invalid time-range: start-time is before end-time");
    }

    private void validateScreeningsDontOverLap(Long theaterId, LocalDateTime startTime, LocalDateTime endTime) throws ScreeningsOverLapException {
        List<ScreeningEntity> screeningEntities = screeningRepository.findByTheaterId(theaterId);
        for (ScreeningEntity screeningEntity : screeningEntities) {
            checkForOverlap(screeningEntity, startTime, endTime);
        }
    }

    private void checkForOverlap(ScreeningEntity screeningEntity, LocalDateTime startTime, LocalDateTime endTime) throws ScreeningsOverLapException {
        LocalDateTime screeningStartTime = screeningEntity.getStartTime();
        LocalDateTime screeningEndTime = screeningEntity.getEndTime();

        if ((isBeforeOrEqual(startTime, screeningStartTime) && isAfterOrEqual(endTime, screeningEndTime)) ||
                (isAfterOrEqual(startTime, screeningStartTime) && isBeforeOrEqual(endTime, screeningEndTime)) ||
                (isBeforeOrEqual(startTime, screeningStartTime) && isAfterOrEqual(endTime, screeningStartTime) && isBeforeOrEqual(endTime, screeningEndTime)) ||
                isAfterOrEqual(startTime, screeningStartTime) && isBeforeOrEqual(startTime, screeningEndTime) && isAfterOrEqual(endTime, screeningEndTime)) {
            throw new ScreeningsOverLapException(screeningEntity.getId(), screeningStartTime.format(formatter), screeningEndTime.format(formatter));
        }
    }

    private boolean isBeforeOrEqual(LocalDateTime time1, LocalDateTime time2) {
        return time1.isBefore(time2) || time1.isEqual(time2);
    }

    private boolean isAfterOrEqual(LocalDateTime time1, LocalDateTime time2) {
        return time1.isAfter(time2) || time1.isEqual(time2);
    }

}
