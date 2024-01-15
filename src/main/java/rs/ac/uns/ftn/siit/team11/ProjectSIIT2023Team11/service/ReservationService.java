package rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.domain.*;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.dto.ReservationDTO.AccommodationNumberOfReservations;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.repository.IAccommodationRepository;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.repository.IReservationRepository;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.util.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReservationService implements IReservationService {

    @Autowired
    IReservationRepository reservationRepository;

    @Autowired
    IAccommodationRepository accommodationRepository;
    @Autowired
    AvailabilityService availabilityService;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public <S extends Reservation> S save(S entity) {
        return reservationRepository.save(entity);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Collection<Reservation> findAllByGuestEmail(String email) {
        return reservationRepository.findAllByGuestEmail(email);
    }
    public Collection<Reservation> findAllByAccommodationOwnerEmail(String email) {
        return reservationRepository.findAllByAccommodationOwnerEmail(email);
    }

    @Override
    public Collection<Reservation> searchGuestReservations(Long startDate, Long endDate, String accommodationName,String email) {
        return reservationRepository.searchGuestReservations(startDate,endDate,accommodationName,email);
    }

    @Override
    public Collection<Reservation> searchOwnerReservations(Long startDate, Long endDate, String accommodationName, String email) {
        return reservationRepository.searchOwnerReservations(startDate,endDate,accommodationName,email);
    }

    public Collection<Reservation> findByStatusAndGuestEmail(ReservationStatus status, String email) {
        return reservationRepository.findByStatusAndGuestEmail(status, email);
    }

    public List<Reservation> findByStatusAndOwnerEmail(ReservationStatus status, String email) {
        return reservationRepository.findByStatusAndOwnerEmail(status, email);
    }

    @Override
    public boolean anyReservationInFuture(Accommodation accommodation) {
        for(Reservation reservation : reservationRepository.findAll()){
            if(reservation.getEndDate() > (Instant.now().getEpochSecond()) && reservation.getStatus().equals(ReservationStatus.Accepted)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean guestHasActiveReservations(String email) {
        for(Reservation reservation : reservationRepository.findAll()){
            if(email.equals(reservation.getGuest().getEmail()) && reservation.getStatus().equals(ReservationStatus.Accepted)
                    && reservation.getEndDate() > Instant.now().getEpochSecond()){
                return true;
            }
        }
        return false;
    }


    @Override
    public int countCancellationsForGuest(String guestId) {
        return (int) reservationRepository.findAll().stream()
                .filter(reservation -> guestId.equals(reservation.getGuest().getEmail()) && ReservationStatus.Cancelled.equals(reservation.getStatus()))
                .count();
    }

    @Override
    public void declineWaitingReservations(Long startDate, Long endDate, Long accommodationId) {
        for (Reservation reservation : reservationRepository.findAll()) {
            if ((Objects.equals(reservation.getAccommodation().getId(), accommodationId)) &&
                    ((reservation.getStartDate() < endDate && reservation.getEndDate() > startDate) ||
                            (startDate < reservation.getEndDate() && endDate > reservation.getStartDate()))) {

                if(reservation.getStatus() == ReservationStatus.Waiting){
                    reservation.setStatus(ReservationStatus.Declined);
                    save(reservation);
                }
            }
        }
    }

    @Override
    public boolean hasUnCancelledReservation(String guestEmail, String ownerEmail) {
        return reservationRepository.hasUnCancelledReservationWithOwnerAndStatus(guestEmail,ownerEmail,ReservationStatus.Accepted,ReservationStatus.Finished);
    }

    @Override
    public boolean canReviewAccommodation(String guestEmail, Long accommodationId) {
        Collection<Reservation> reservations = reservationRepository.findReservationsForAccommodationReview(guestEmail,accommodationId,ReservationStatus.Accepted,ReservationStatus.Finished) ;
        for (Reservation reservation: reservations) {
            Instant endDate = Instant.ofEpochSecond(reservation.getEndDate());
            if (Instant.now().isAfter(endDate) && Instant.now().isBefore(endDate.plus(7, ChronoUnit.DAYS))) {
                return true;
            }
        }
        return false;

    }

    @Override
    public Optional<Reservation> createNewReservation(Reservation newReservationEntry) {
        newReservationEntry.calculatePrice();
        newReservationEntry.setStatus(ReservationStatus.Waiting);

        if(newReservationEntry.getAccommodation().isAutomaticApproval()){
            availabilityService.fitAcceptedReservation(newReservationEntry.getStartDate(),newReservationEntry.getEndDate(),newReservationEntry.getAccommodation());
            newReservationEntry.setStatus(ReservationStatus.Accepted);
            return  Optional.ofNullable(save(newReservationEntry));
        }

        return  Optional.ofNullable(save(newReservationEntry));
    }

    @Override
    public Collection<AccommodationNumberOfReservations> getStatisticNumberOfReservations(Long startDate, Long endDate, String username) {
        List<AccommodationNumberOfReservations> accommodationsNumberOfReservations = new ArrayList<>();
        for(Accommodation accommodation: accommodationRepository.findAll()){
            if(!accommodation.getOwner().getEmail().equals(username)) continue;

            AccommodationNumberOfReservations accommodationNumberOfReservations = new AccommodationNumberOfReservations(accommodation.getName(),0);

            for(Reservation reservation: findAll()){
                if(!reservation.getAccommodation().getId().equals(accommodation.getId())) continue;

                if(reservation.getEndDate()<= endDate && reservation.getEndDate() >= startDate) accommodationNumberOfReservations.increaseNumberOfReservations();

            }
            accommodationsNumberOfReservations.add(accommodationNumberOfReservations);
        }
        return accommodationsNumberOfReservations;
    }
}
