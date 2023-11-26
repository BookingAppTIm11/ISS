package rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.util.ReservationStatus;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long accommodation;
    private String guest;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfGuests;
    private ReservationStatus status;
    private Double price;
}